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

import static com.github.anhem.testpopulator.util.FileWriterUtil.getPath;
import static com.github.anhem.testpopulator.util.ObjectBuilderUtil.formatClassName;
import static com.github.anhem.testpopulator.util.ObjectBuilderUtil.getPackageName;
import static org.assertj.core.api.Assertions.assertThat;

public class GeneratedCodeUtil {

    private static final String JAVA = ".java";
    private static final String CLASS = ".class";
    public static final Path PATH = Path.of("target/generated-test-sources/test-populator/");

    public static <T> void assertGeneratedCode(T object, PopulateConfig populateConfig) {
        String packageName = getPackageName(object.getClass());
        Path path = getPath(packageName, formatClassName(object.getClass()), populateConfig);
        try {
            compileGeneratedFile(path);
            Class<T> clazz = loadClass(path, packageName, path.getFileName().toString().replace(JAVA, ""));
            T value = getStaticObjectFromClass(clazz, object.getClass().getSimpleName());
            assertThat(value).usingRecursiveComparison().isEqualTo(object);
        } finally {
            removeGeneratedFiles(path);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> loadClass(Path path, String packageName, String className) {
        assertThat(path.toFile()).exists();
        assertThat(new File(path.getParent().toFile(), path.getFileName().toString().replace(JAVA, CLASS))).exists();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{PATH.toFile().toURI().toURL()})) {
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
