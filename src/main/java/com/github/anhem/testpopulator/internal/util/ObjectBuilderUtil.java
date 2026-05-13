package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;

import java.lang.reflect.Modifier;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.BuildType.*;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.isJavaBaseClass;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.isMapEntry;

public class ObjectBuilderUtil {

    private static final String QUALIFIED_NAME_FORMAT = "%s.%s";
    private static final String TRY_START = "\t\ttry {";
    private static final String THROW_RUNTIME_EXCEPTION = "\t\t\tthrow new RuntimeException(e);";
    private static final String BLOCK_END = "\t\t}";
    private static final String METHOD_END = "\t}";
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
            addImport(clazz.getComponentType(), value, false, imports, staticImports);
            return;
        }
        if (clazz.isPrimitive() || clazz.getName().startsWith("java.lang.")) {
            return;
        }
        if (clazz.isEnum()) {
            String className = clazz.getCanonicalName();
            if (value != null) {
                staticImports.add(String.format(QUALIFIED_NAME_FORMAT, className, value));
            } else {
                imports.add(className);
            }
        }
        else if (Modifier.isStatic(clazz.getModifiers()) && clazz.getEnclosingClass() != null) {
            staticImports.add(String.format(QUALIFIED_NAME_FORMAT, clazz.getEnclosingClass().getCanonicalName(), clazz.getSimpleName()));
            if (isMapEntry(clazz)) {
                imports.add("java.util.AbstractMap");
            }
        }
        else {
            imports.add(clazz.getCanonicalName());
        }
    }

    public static boolean isBasicValue(ObjectBuilder objectBuilder) {
        return objectBuilder.getBuildType() == BuildType.VALUE && (isJavaBaseClass(objectBuilder.getClazz()) || objectBuilder.getClazz().isEnum());
    }

    public static Stream<String> endBuilder(String buildMethodName) {
        return Stream.of(String.format("    .%s();", buildMethodName));
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
                    TRY_START,
                    "\t\t\treturn new java.net.URL(url);",
                    "\t\t} catch (java.net.MalformedURLException e) {",
                    THROW_RUNTIME_EXCEPTION,
                    BLOCK_END,
                    METHOD_END);
        }
        if (clazz != null && (clazz.equals(InetAddress.class) || clazz.equals(Inet4Address.class) || clazz.equals(Inet6Address.class) || clazz.equals(InetSocketAddress.class))) {
            return String.join(System.lineSeparator(),
                    "\tprivate static java.net.InetAddress toInetAddress(String host) {",
                    TRY_START,
                    "\t\t\treturn java.net.InetAddress.getByName(host);",
                    "\t\t} catch (java.net.UnknownHostException e) {",
                    THROW_RUNTIME_EXCEPTION,
                    BLOCK_END,
                    METHOD_END);
        }
        return null;
    }

    private static boolean requiresImport(Class<?> clazz) {
        return !"java.lang".equals(clazz.getPackageName());
    }

    public static String formatBytes(byte[] bytes) {
        StringJoiner joiner = new StringJoiner(", ");
        for (byte b : bytes) {
            joiner.add(String.format("(byte) %d", b));
        }
        return joiner.toString();
    }

}
