package com.github.anhem.testpopulator.testutil;

import com.github.anhem.testpopulator.config.PopulateConfig;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.util.FileWriterUtil.getPath;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.formatClassName;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.getPackageName;
import static org.assertj.core.api.Assertions.assertThat;

public class GeneratedCodeUtil {

    private static final String JAVA = ".java";
    private static final String CLASS = ".class";

    public static <T> void assertGeneratedCode(Class<T> clazz, T object, PopulateConfig populateConfig) {
        String packageName = getPackageName(object.getClass());
        Path path = getPath(packageName, formatClassName(clazz), populateConfig);
        assertGeneratedCode(object, path, packageName, clazz.getSimpleName(), populateConfig);
    }

    public static <T> void assertGeneratedCode(T object, PopulateConfig populateConfig) {
        String packageName = getPackageName(object.getClass());
        Path path = getPath(packageName, formatClassName(object.getClass()), populateConfig);
        assertGeneratedCode(object, path, packageName, object.getClass().getSimpleName(), populateConfig);
    }

    public static <T> void assertGeneratedCodeContains(T object, PopulateConfig populateConfig, String... expectedSnippets) {
        String packageName = getPackageName(object.getClass());
        Path path = getPath(packageName, formatClassName(object.getClass()), populateConfig);
        try {
            String sourceCode = Files.readString(path);
            assertThat(sourceCode).contains(expectedSnippets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void assertGeneratedCode(T object, Path path, String packageName, String simpleName, PopulateConfig populateConfig) {
        try {
            compileGeneratedFile(path);
            Class<T> clazz = loadClass(path, packageName, path.getFileName().toString().replace(JAVA, ""), populateConfig);
            T value = getStaticObjectFromClass(clazz, simpleName);
            assertThat(value).usingRecursiveComparison()
                    .withEqualsForType((a, b) -> a.toString().contentEquals(b), StringBuilder.class)
                    .withEqualsForType((a, b) -> a.toString().contentEquals(b), StringBuffer.class)
                    .withEqualsForType((a, b) -> a.getMessage().equals(b.getMessage()) && a.getClass().equals(b.getClass()), Throwable.class)
                    .withEqualsForType((a, b) -> a.get() == b.get(), AtomicInteger.class)
                    .withEqualsForType((a, b) -> a.get() == b.get(), AtomicLong.class)
                    .withEqualsForType((a, b) -> a.get() == b.get(), AtomicBoolean.class)
                    .withEqualsForType((a, b) -> true, Stream.class)
                    .withEqualsForType((a, b) -> true, IntStream.class)
                    .withEqualsForType((a, b) -> true, LongStream.class)
                    .withEqualsForType((a, b) -> true, DoubleStream.class)
                    .withEqualsForType((a, b) -> true, Future.class)
                    .withEqualsForType((a, b) -> true, Scanner.class)
                    .withEqualsForType((a, b) -> true, Iterator.class)
                    .isEqualTo(object);
        } finally {
            removeGeneratedFiles(path);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> loadClass(Path path, String packageName, String className, PopulateConfig populateConfig) {
        assertThat(path.toFile()).exists();
        assertThat(new File(path.getParent().toFile(), path.getFileName().toString().replace(JAVA, CLASS))).exists();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{Path.of(populateConfig.getObjectFactoryPath()).toFile().toURI().toURL()})) {
            return (Class<T>) Class.forName(String.format("%s.%s", packageName, className), true, classLoader);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getStaticObjectFromClass(Class<T> clazz, String simpleName) {
        String variableName = String.format("%s_0", Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1));
        try {
            return (T) clazz.getDeclaredField(variableName).get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void compileGeneratedFile(Path path) {
        File file = new File(path.getParent().toFile(), path.getFileName().toString());
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, file.getPath());
        assertThat(result).as("compilation failed").isZero();
    }

    private static void removeGeneratedFiles(Path path) {
        Path classFilePath = path.resolveSibling(path.getFileName().toString().replace(JAVA, CLASS));
        try {
            Files.deleteIfExists(path);
            Files.deleteIfExists(classFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
