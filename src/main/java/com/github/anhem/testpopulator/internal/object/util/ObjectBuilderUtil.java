package com.github.anhem.testpopulator.internal.object.util;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;

import java.lang.reflect.Field;
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
        if (clazz.isPrimitive() || "java.lang".equals(clazz.getPackageName())) {
            return;
        }
        if (clazz.isEnum()) {
            String className = clazz.getCanonicalName();
            if (value != null) {
                staticImports.add(String.format(QUALIFIED_NAME_FORMAT, className, value));
            } else {
                imports.add(className);
            }
        } else if (Modifier.isStatic(clazz.getModifiers()) && clazz.getEnclosingClass() != null) {
            staticImports.add(String.format(QUALIFIED_NAME_FORMAT, clazz.getEnclosingClass().getCanonicalName(), clazz.getSimpleName()));
            if (isMapEntry(clazz)) {
                imports.add("java.util.AbstractMap");
            }
        } else {
            imports.add(clazz.getCanonicalName());
        }
    }

    public static boolean isBasicValue(ObjectBuilder objectBuilder) {
        return objectBuilder.getBuildType() == BuildType.VALUE && (isJavaBaseClass(objectBuilder.getClazz()) || objectBuilder.getClazz().isEnum());
    }

    public static Stream<String> endBuilder(String buildMethodName) {
        return Stream.of(String.format("\t.%s();", buildMethodName));
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

    public static String getHelperMethod(Class<?> clazz, boolean isKotlinSupport) {
        if (clazz != null && clazz.equals(java.net.URL.class)) {
            if (isKotlinSupport) {
                return String.join(System.lineSeparator(),
                        "	private fun toUrl(url: String): java.net.URL {",
                        "		try {",
                        "			return java.net.URL(url)",
                        "		} catch (e: java.net.MalformedURLException) {",
                        "			throw RuntimeException(e)",
                        "		}",
                        "	}");
            }
            return String.join(System.lineSeparator(),
                    "	private static java.net.URL toUrl(String url) {",
                    TRY_START,
                    "			return new java.net.URL(url);",
                    "		} catch (java.net.MalformedURLException e) {",
                    THROW_RUNTIME_EXCEPTION,
                    BLOCK_END,
                    METHOD_END);
        }
        if (clazz != null && (clazz.equals(InetAddress.class) || clazz.equals(Inet4Address.class) || clazz.equals(Inet6Address.class) || clazz.equals(InetSocketAddress.class))) {
            if (isKotlinSupport) {
                return String.join(System.lineSeparator(),
                        "	private fun toInetAddress(host: String): java.net.InetAddress {",
                        "		try {",
                        "			return java.net.InetAddress.getByName(host)",
                        "		} catch (e: java.net.UnknownHostException) {",
                        "			throw RuntimeException(e)",
                        "		}",
                        "	}");
            }
            return String.join(System.lineSeparator(),
                    "	private static java.net.InetAddress toInetAddress(String host) {",
                    TRY_START,
                    "			return java.net.InetAddress.getByName(host);",
                    "		} catch (java.net.UnknownHostException e) {",
                    THROW_RUNTIME_EXCEPTION,
                    BLOCK_END,
                    METHOD_END);
        }
        return null;
    }

    public static String getPrivateConstructorHelperMethod(Class<?> clazz, String helperMethodName, Class<?>[] parameterTypes, Map<String, Class<?>> classNames, Set<String> imports, Set<String> staticImports, boolean isKotlinSupport) {
        if (isKotlinSupport) {
            String className = getClassName(clazz, classNames, imports, staticImports);
            String parameterDeclarations = getKotlinParameterDeclarations(parameterTypes, classNames, imports, staticImports);
            String parameterTypeClasses = getKotlinParameterTypeClasses(parameterTypes, classNames, imports, staticImports);
            String parameterArguments = getParameterArguments(parameterTypes);
            return String.join(System.lineSeparator(),
                    String.format(
                            "	private fun %s(%s): %s {",
                            helperMethodName,
                            parameterDeclarations,
                            className
                    ),
                    "		try {",
                    String.format(
                            "			val constructor = %s::class.java.getDeclaredConstructor(%s)",
                            className,
                            parameterTypeClasses
                    ),
                    "			constructor.isAccessible = true",
                    String.format("			return constructor.newInstance(%s)", parameterArguments),
                    "		} catch (e: Exception) {",
                    "			throw RuntimeException(e)",
                    "		}",
                    "	}"
            );
        }
        String className = getClassName(clazz, classNames, imports, staticImports);
        String constructorClassName = getClassName(java.lang.reflect.Constructor.class, classNames, imports, staticImports);
        String parameterDeclarations = getParameterDeclarations(parameterTypes, classNames, imports, staticImports);
        String parameterTypeClasses = getParameterTypeClasses(parameterTypes, classNames, imports, staticImports);
        String parameterArguments = getParameterArguments(parameterTypes);
        return String.join(System.lineSeparator(),
                String.format(
                        "	private static %s %s(%s) {",
                        className,
                        helperMethodName,
                        parameterDeclarations
                ),
                TRY_START,
                String.format(
                        "			%s<%s> constructor = %s.class.getDeclaredConstructor(%s);",
                        constructorClassName,
                        className,
                        className,
                        parameterTypeClasses
                ),
                "			constructor.setAccessible(true);",
                String.format("			return constructor.newInstance(%s);", parameterArguments),
                "		} catch (Exception e) {",
                THROW_RUNTIME_EXCEPTION,
                BLOCK_END,
                METHOD_END
        );
    }

    public static String getFieldHelperMethod(Class<?> clazz, String helperMethodName, List<java.lang.reflect.Field> fields, Map<String, Class<?>> classNames, Set<String> imports, Set<String> staticImports, boolean isKotlinSupport) {
        if (isKotlinSupport) {
            String className = getClassName(clazz, classNames, imports, staticImports);
            Class<?>[] parameterTypes = getParameterTypes(fields);
            String parameterDeclarations = getKotlinParameterDeclarations(parameterTypes, classNames, imports, staticImports);

            List<String> lines = new ArrayList<>();
            lines.add(String.format("	private fun %s(%s): %s {", helperMethodName, parameterDeclarations, className));
            lines.add("		try {");
            lines.add(String.format("			val constructor = %s::class.java.getDeclaredConstructor()", className));
            lines.add("			constructor.isAccessible = true");
            lines.add(String.format("			val obj = constructor.newInstance()"));

            for (int i = 0; i < fields.size(); i++) {
                java.lang.reflect.Field field = fields.get(i);
                String declaringClassName = getClassName(field.getDeclaringClass(), classNames, imports, staticImports);
                lines.add(String.format("			setField(obj, %s::class.java, \"%s\", p%d)", declaringClassName, field.getName(), i));
            }

            lines.add("			return obj");
            lines.add("		} catch (e: Exception) {");
            lines.add("			throw RuntimeException(e)");
            lines.add("		}");
            lines.add("	}");

            return String.join(System.lineSeparator(), lines);
        }
        String className = getClassName(clazz, classNames, imports, staticImports);
        String constructorClassName = getClassName(java.lang.reflect.Constructor.class, classNames, imports, staticImports);
        Class<?>[] parameterTypes = getParameterTypes(fields);
        String parameterDeclarations = getParameterDeclarations(parameterTypes, classNames, imports, staticImports);

        List<String> lines = new ArrayList<>();
        lines.add(String.format("	private static %s %s(%s) {", className, helperMethodName, parameterDeclarations));
        lines.add(TRY_START);
        lines.add(String.format("			%s<%s> constructor = %s.class.getDeclaredConstructor();", constructorClassName, className, className));
        lines.add("			constructor.setAccessible(true);");
        lines.add(String.format("			%s obj = constructor.newInstance();", className));

        for (int i = 0; i < fields.size(); i++) {
            java.lang.reflect.Field field = fields.get(i);
            String declaringClassName = getClassName(field.getDeclaringClass(), classNames, imports, staticImports);
            lines.add(String.format("			setField(obj, %s.class, \"%s\", p%d);", declaringClassName, field.getName(), i));
        }

        lines.add("			return obj;");
        lines.add("		} catch (Exception e) {");
        lines.add(THROW_RUNTIME_EXCEPTION);
        lines.add(BLOCK_END);
        lines.add(METHOD_END);

        return String.join(System.lineSeparator(), lines);
    }

    public static String getSetFieldMethod(Map<String, Class<?>> classNames, Set<String> imports, Set<String> staticImports, boolean isKotlinSupport) {
        if (isKotlinSupport) {
            return String.join(System.lineSeparator(),
                    "	private fun setField(obj: Any, clazz: Class<*>, fieldName: String, value: Any?) {",
                    "		try {",
                    "			val field = clazz.getDeclaredField(fieldName)",
                    "			field.isAccessible = true",
                    "			field.set(obj, value)",
                    "		} catch (e: Exception) {",
                    "			throw RuntimeException(e)",
                    "		}",
                    "	}");
        }
        String fieldClassName = getClassName(java.lang.reflect.Field.class, classNames, imports, staticImports);
        return String.join(System.lineSeparator(),
                "	private static void setField(Object obj, Class<?> clazz, String fieldName, Object value) {",
                TRY_START,
                String.format("			%s field = clazz.getDeclaredField(fieldName);", fieldClassName),
                "			field.setAccessible(true);",
                "			field.set(obj, value);",
                "		} catch (Exception e) {",
                THROW_RUNTIME_EXCEPTION,
                BLOCK_END,
                METHOD_END);
    }

    private static Class<?>[] getParameterTypes(List<Field> fields) {
        return fields.stream()
                .map(Field::getType)
                .toArray(Class<?>[]::new);
    }

    private static String getParameterDeclarations(Class<?>[] parameterTypes, Map<String, Class<?>> classNames, Set<String> imports, Set<String> staticImports) {
        if (parameterTypes.length > 1) {
            return System.lineSeparator() + "\t\t\t" + java.util.stream.IntStream.range(0, parameterTypes.length)
                    .mapToObj(i -> String.format("%s p%d", getClassName(parameterTypes[i], classNames, imports, staticImports), i))
                    .collect(java.util.stream.Collectors.joining("," + System.lineSeparator() + "\t\t\t")) +
                    System.lineSeparator() + "\t";
        }
        return java.util.stream.IntStream.range(0, parameterTypes.length)
                .mapToObj(i -> String.format("%s p%d", getClassName(parameterTypes[i], classNames, imports, staticImports), i))
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private static String getParameterTypeClasses(Class<?>[] parameterTypes, Map<String, Class<?>> classNames, Set<String> imports, Set<String> staticImports) {
        if (parameterTypes.length > 1) {
            return System.lineSeparator() + "\t\t\t\t\t" + Arrays.stream(parameterTypes)
                    .map(p -> String.format("%s.class", getClassName(p, classNames, imports, staticImports)))
                    .collect(java.util.stream.Collectors.joining("," + System.lineSeparator() + "\t\t\t\t\t")) +
                    System.lineSeparator() + "\t\t\t\t";
        }
        return Arrays.stream(parameterTypes)
                .map(p -> String.format("%s.class", getClassName(p, classNames, imports, staticImports)))
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private static String getParameterArguments(Class<?>[] parameterTypes) {
        if (parameterTypes.length > 1) {
            return System.lineSeparator() + "\t\t\t\t" + java.util.stream.IntStream.range(0, parameterTypes.length)
                    .mapToObj(i -> "p" + i)
                    .collect(java.util.stream.Collectors.joining("," + System.lineSeparator() + "\t\t\t\t")) +
                    System.lineSeparator() + "\t\t\t";
        }
        return java.util.stream.IntStream.range(0, parameterTypes.length)
                .mapToObj(i -> "p" + i)
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private static String getKotlinParameterDeclarations(Class<?>[] parameterTypes, Map<String, Class<?>> classNames, Set<String> imports, Set<String> staticImports) {
        if (parameterTypes.length > 1) {
            return System.lineSeparator() + "\t\t\t" + java.util.stream.IntStream.range(0, parameterTypes.length)
                    .mapToObj(i -> String.format("p%d: %s", i, getClassName(parameterTypes[i], classNames, imports, staticImports)))
                    .collect(java.util.stream.Collectors.joining("," + System.lineSeparator() + "\t\t\t")) +
                    System.lineSeparator() + "\t";
        }
        return java.util.stream.IntStream.range(0, parameterTypes.length)
                .mapToObj(i -> String.format("p%d: %s", i, getClassName(parameterTypes[i], classNames, imports, staticImports)))
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private static String getKotlinParameterTypeClasses(Class<?>[] parameterTypes, Map<String, Class<?>> classNames, Set<String> imports, Set<String> staticImports) {
        if (parameterTypes.length > 1) {
            return System.lineSeparator() + "\t\t\t\t\t" + Arrays.stream(parameterTypes)
                    .map(p -> String.format("%s::class.java", getClassName(p, classNames, imports, staticImports)))
                    .collect(java.util.stream.Collectors.joining("," + System.lineSeparator() + "\t\t\t\t\t")) +
                    System.lineSeparator() + "\t\t\t\t";
        }
        return Arrays.stream(parameterTypes)
                .map(p -> String.format("%s::class.java", getClassName(p, classNames, imports, staticImports)))
                .collect(java.util.stream.Collectors.joining(", "));
    }

    public static String getClassName(Class<?> clazz, Map<String, Class<?>> classNames, Set<String> imports, Set<String> staticImports) {
        if (clazz.isPrimitive()) {
            return clazz.getName();
        }
        if (clazz.isArray()) {
            return getClassName(clazz.getComponentType(), classNames, imports, staticImports) + "[]";
        }
        boolean useFqn = useFullyQualifiedName(clazz, classNames);
        if (!useFqn) {
            addImport(clazz, null, false, imports, staticImports);
            return clazz.getSimpleName();
        }
        return clazz.getCanonicalName();
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
