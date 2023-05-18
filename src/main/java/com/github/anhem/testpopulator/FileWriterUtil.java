package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.anhem.testpopulator.ObjectBuilder.PSF;
import static com.github.anhem.testpopulator.ObjectBuilderUtil.STATIC_BLOCK_END;
import static com.github.anhem.testpopulator.ObjectBuilderUtil.STATIC_BLOCK_START;

class FileWriterUtil {

    private static final String PATH = "target/generated-test-sources/test-populator/%s/%s_%s.java";

    private FileWriterUtil() {
    }

    public static Path getPath(ObjectResult objectResult, PopulateConfig populateConfig) {
        return Paths.get(String.format(PATH, objectResult.getPackageName(), objectResult.getClassName(), encode(populateConfig)));
    }

    public static void createOrOverwriteFile(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new ObjectException(String.format("Could not create or overwrite %s", path.toAbsolutePath()), e);
        }
    }

    public static void writePackage(ObjectResult objectResult, Path path) {
        writeLine(path, String.format("package %s;%s", objectResult.getPackageName(), System.lineSeparator()));
    }

    public static void writeImports(ObjectResult objectResult, Path path) {
        objectResult.getImports().stream()
                .sorted()
                .forEach(s -> writeLine(path, String.format("import %s;", s)));
        writeLine(path, "");
    }

    public static void writeStaticImports(ObjectResult objectResult, Path path) {
        objectResult.getStaticImports().stream()
                .sorted()
                .forEach(s -> writeLine(path, String.format("import static %s;", s)));
        writeLine(path, "");
    }

    public static void writeStartClass(ObjectResult objectResult, Path path, PopulateConfig populateConfig) {
        writeLine(path, String.format("public class %s_%s {%s", objectResult.getClassName(), encode(populateConfig), System.lineSeparator()));
    }

    public static void writeEndClass(Path path) {
        writeLine(path, "}");
    }

    public static void writeObjects(ObjectResult objectResult, Path path) {
        objectResult.getObjects().forEach(s -> {
            if (s.startsWith(STATIC_BLOCK_START)) {
                writeLine(path, String.format("%s\t%s", System.lineSeparator(), s));
            } else if (s.startsWith(STATIC_BLOCK_END)) {
                writeLine(path, String.format("\t%s%s", s, System.lineSeparator()));
            } else if (s.startsWith(PSF)) {
                writeLine(path, String.format("\t%s", s));
            } else {
                writeLine(path, String.format("\t\t%s", s));
            }
        });
    }

    private static void writeLine(Path path, String line) {
        String formattedLine = String.format("%s%s", line, System.lineSeparator());
        try {
            Files.writeString(path, formattedLine, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new ObjectException(String.format("Write %s to %s failed", formattedLine, path.toAbsolutePath()), e);
        }
    }

    private static String encode(PopulateConfig populateConfig) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(populateConfig.toString().getBytes());
            return IntStream.range(0, bytes.length)
                    .mapToObj(i -> String.format("%02x", bytes[i]))
                    .collect(Collectors.joining());
        } catch (NoSuchAlgorithmException e) {
            throw new ObjectException("Could not encode configuration", e);
        }
    }
}
