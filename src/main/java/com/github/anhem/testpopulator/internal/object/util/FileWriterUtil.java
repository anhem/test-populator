package com.github.anhem.testpopulator.internal.object.util;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.ObjectException;
import com.github.anhem.testpopulator.internal.object.ObjectResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.anhem.testpopulator.internal.object.ObjectBuilder.PSF;
import static com.github.anhem.testpopulator.internal.object.util.ObjectBuilderUtil.STATIC_BLOCK_END;
import static com.github.anhem.testpopulator.internal.object.util.ObjectBuilderUtil.STATIC_BLOCK_START;

public class FileWriterUtil {

    private FileWriterUtil() {
    }

    public static Path getPath(ObjectResult objectResult, PopulateConfig populateConfig) {
        return getPath(objectResult.getPackageName(), objectResult.getClassName(), populateConfig);
    }

    public static Path getPath(String packageName, String className, PopulateConfig populateConfig) {
        String extension = populateConfig.isKotlinSupport() ? ".kt" : ".java";
        return Paths.get(populateConfig.getObjectFactoryPath(), toPackagePath(packageName), String.format("%s_%s%s", className, encode(populateConfig), extension));
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

    public static void writeStaticImports(ObjectResult objectResult, Path path, PopulateConfig populateConfig) {
        objectResult.getStaticImports().stream()
                .sorted()
                .forEach(s -> writeLine(path, String.format(populateConfig.isKotlinSupport() ? "import %s" : "import static %s;", s)));
        writeLine(path, "");
    }

    public static void writeStartClass(ObjectResult objectResult, Path path, PopulateConfig populateConfig) {
        String classType = populateConfig.isKotlinSupport() ? "object" : "public class";
        writeLine(path, String.format("%s %s_%s {%s", classType, objectResult.getClassName(), encode(populateConfig), System.lineSeparator()));
    }

    public static void writeEndClass(Path path) {
        writeLine(path, "}");
    }

    public static void writeMethods(ObjectResult objectResult, Path path, PopulateConfig populateConfig) {
        if (!objectResult.getMethods().isEmpty()) {
            writeLine(path, "");
            objectResult.getMethods().stream()
                    .sorted()
                    .forEach(method -> writeLine(path, method));
        }
    }

    public static void writeObjects(ObjectResult objectResult, Path path, PopulateConfig populateConfig) {
        List<String> staticBlockLines = new ArrayList<>();
        boolean inStaticBlock = false;

        for (String s : objectResult.getObjects()) {
            if (s.startsWith(STATIC_BLOCK_START)) {
                inStaticBlock = true;
            } else if (s.startsWith(STATIC_BLOCK_END)) {
                inStaticBlock = false;
            } else if (inStaticBlock) {
                staticBlockLines.add(s);
            } else {
                if (populateConfig.isKotlinSupport() && s.startsWith(PSF)) {
                    writeLine(path, String.format("\tval%s", s.substring(PSF.length())));
                } else if (s.startsWith(PSF)) {
                    writeLine(path, String.format("\t%s", s));
                } else {
                    writeLine(path, String.format("\t\t%s", s));
                }
            }
        }

        if (!staticBlockLines.isEmpty()) {
            if (populateConfig.isKotlinSupport()) {
                writeLine(path, String.format("%s\tinit {", System.lineSeparator()));
            } else {
                writeLine(path, String.format("%s\t%s", System.lineSeparator(), STATIC_BLOCK_START));
            }
            staticBlockLines.forEach(s -> writeLine(path, String.format("\t\t%s", s)));
            writeLine(path, String.format("\t}"));
        }
    }

    private static void writeLine(Path path, String line) {
        String formattedLine = String.format("%s%s", line, System.lineSeparator());
        try {
            Files.writeString(path, formattedLine, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new ObjectException(String.format("Write %s to %s failed", formattedLine, path.toAbsolutePath()), e);
        }
    }

    static String encode(PopulateConfig populateConfig) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = messageDigest.digest(populateConfig.toString().getBytes());
            return IntStream.range(0, bytes.length)
                    .mapToObj(i -> String.format("%02x", bytes[i]))
                    .collect(Collectors.joining())
                    .substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new ObjectException("Could not encode configuration", e);
        }
    }

    static String toPackagePath(String packageName) {
        return packageName.replace(".", "/");
    }
}
