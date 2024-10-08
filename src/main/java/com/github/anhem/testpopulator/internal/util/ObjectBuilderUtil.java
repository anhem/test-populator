package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.PopulateFactory.BUILD_METHOD;
import static com.github.anhem.testpopulator.internal.object.BuildType.*;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.isJavaBaseClass;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.isMapEntry;

public class ObjectBuilderUtil {

    static final String STATIC_BLOCK_START = "static {";
    static final String STATIC_BLOCK_END = "}";

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
            if (isMapEntry(clazz)) {
                staticImports.add(String.format("%s.%s", clazz.getEnclosingClass().getName(), clazz.getSimpleName()));
                imports.add("java.util.AbstractMap");
            }
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

    public static boolean collectionHasNullValues(ObjectBuilder objectBuilder) {
        if (List.of(LIST, SET, MAP).contains(objectBuilder.getBuildType())) {
            return objectBuilder.getChildren().stream()
                    .map(ObjectBuilder::getChildren)
                    .flatMap(Collection::stream)
                    .allMatch(ObjectBuilder::isNullValue);
        }
        return false;
    }

}
