package com.github.anhem.testpopulator.object;

import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.PopulateFactory.BUILD_METHOD;
import static com.github.anhem.testpopulator.util.PopulateUtil.isJavaBaseClass;

public class ObjectBuilderUtil {

    public static final String STATIC_BLOCK_START = "static {";
    public static final String STATIC_BLOCK_END = "}";

    private ObjectBuilderUtil() {
    }

    public static String getPackageName(Class<?> clazz) {
        return clazz.getName().startsWith("java.") ? ObjectBuilder.class.getPackageName() : clazz.getPackageName();
    }

    public static String formatClassName(Class<?> clazz) {
        return String.format("%s_TestData", clazz.getSimpleName());
    }

    public static void addImport(Class<?> clazz, Object value, Set<String> imports, Set<String> staticImports) {
        if (clazz != null && !clazz.getName().startsWith("java.lang.")) {
            if (Modifier.isStatic(clazz.getModifiers()) && clazz.getEnclosingClass() != null) {
                staticImports.add(String.format("%s.%s", clazz.getEnclosingClass().getName(), clazz.getSimpleName()));
            } else if (value != null && clazz.isEnum()) {
                staticImports.add(String.format("%s.%s", clazz.getName(), value));
            } else {
                imports.add(clazz.getName());
            }
        }
    }

    public static boolean isBasicValue(ObjectBuilder objectBuilder) {
        return Objects.requireNonNull(objectBuilder.getBuildType()) == BuildType.VALUE && (isJavaBaseClass(objectBuilder.getClazz()) || objectBuilder.getClazz().isEnum());
    }

    public static Stream<String> endBuilder() {
        return Stream.of(String.format(".%s();", BUILD_METHOD));
    }

    public static Stream<String> startStaticBlock() {
        return Stream.of(STATIC_BLOCK_START);
    }

    public static Stream<String> endStaticBlock() {
        return Stream.of(STATIC_BLOCK_END);
    }

    @SafeVarargs
    public static <T> Stream<T> concatenate(Stream<T>... streams) {
        return Stream.of(streams).flatMap(s -> s);
    }

}
