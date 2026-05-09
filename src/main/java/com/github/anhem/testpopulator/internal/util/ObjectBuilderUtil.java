package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

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

    public static void addImport(Class<?> clazz, Object value, boolean useFullyQualifiedName, Set<String> imports, Set<String> staticImports) {
        if (clazz == null || useFullyQualifiedName) {
            return;
        }
        if (clazz.isArray()) {
            addImport(clazz.getComponentType(), value, useFullyQualifiedName, imports, staticImports);
            return;
        }
        if (!clazz.isPrimitive() && !clazz.getName().startsWith("java.lang.")) {
            String className = clazz.getName().replace('$', '.');
            if (isMapEntry(clazz)) {
                staticImports.add(String.format("%s.%s", clazz.getEnclosingClass().getName().replace('$', '.'), clazz.getSimpleName()));
                imports.add("java.util.AbstractMap");
            }
            if (Modifier.isStatic(clazz.getModifiers()) && clazz.getEnclosingClass() != null) {
                String enclosingClassName = clazz.getEnclosingClass().getName().replace('$', '.');
                if (clazz.isEnum()) {
                    if (value != null) {
                        staticImports.add(String.format("%s.%s.%s", enclosingClassName, clazz.getSimpleName(), value));
                    } else {
                        imports.add(className);
                    }
                } else {
                    staticImports.add(String.format("%s.%s", enclosingClassName, clazz.getSimpleName()));
                }
            } else if (clazz.isEnum()) {
                if (value != null) {
                    staticImports.add(String.format("%s.%s", className, value));
                } else {
                    imports.add(className);
                }
            } else {
                imports.add(className);
            }
        }
    }

    public static boolean isBasicValue(ObjectBuilder objectBuilder) {
        return Objects.requireNonNull(objectBuilder.getBuildType()) == BuildType.VALUE && (isJavaBaseClass(objectBuilder.getClazz()) || objectBuilder.getClazz().isEnum());
    }

    public static Stream<String> endBuilder(String buildMethodName) {
        return Stream.of(String.format(".%s();", buildMethodName));
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
        if (List.of(LIST, SET, ENUM_SET, MAP, ENUM_MAP).contains(objectBuilder.getBuildType())) {
            return objectBuilder.getChildren().stream()
                    .map(ObjectBuilder::getChildren)
                    .flatMap(Collection::stream)
                    .allMatch(ObjectBuilder::isNullValue);
        }
        return false;
    }

    public static boolean useFullyQualifiedName(Class<?> clazz, Map<String, Class<?>> classNames) {
        if (requiresImport(clazz)) {
            Class<?> existingClass = classNames.get(clazz.getSimpleName());
            if (existingClass == null) {
                classNames.put(clazz.getSimpleName(), clazz);
                return false;
            } else return !existingClass.equals(clazz);
        }
        return false;
    }

    public static String getHelperMethod(Class<?> clazz) {
        if (clazz != null && clazz.equals(java.net.URL.class)) {
            return String.join(System.lineSeparator(),
                    "\tprivate static java.net.URL toUrl(String url) {",
                    "\t\ttry {",
                    "\t\t\treturn new java.net.URL(url);",
                    "\t\t} catch (java.net.MalformedURLException e) {",
                    "\t\t\tthrow new RuntimeException(e);",
                    "\t\t}",
                    "\t}");
        }
        if (clazz != null && (clazz.equals(java.net.InetAddress.class) || clazz.equals(java.net.Inet4Address.class) || clazz.equals(java.net.Inet6Address.class))) {
            return String.join(System.lineSeparator(),
                    "\tprivate static java.net.InetAddress toInetAddress(String host) {",
                    "\t\ttry {",
                    "\t\t\treturn java.net.InetAddress.getByName(host);",
                    "\t\t} catch (java.net.UnknownHostException e) {",
                    "\t\t\tthrow new RuntimeException(e);",
                    "\t\t}",
                    "\t}");
        }
        return null;
    }

    private static boolean requiresImport(Class<?> clazz) {
        return !"java.lang".equals(clazz.getPackageName());
    }

}
