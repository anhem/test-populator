package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.PopulateFactory.BUILDER_METHOD;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static java.util.Arrays.stream;

public class PopulateUtil {

    static final String MATCH_FIRST_CHARACTER_UPPERCASE = "\\p{Lu}.*";

    private static final List<String> BLACKLISTED_METHODS = List.of("$jacocoInit");
    private static final String JAVA_BASE = "java.base";
    private static final String NO_CONSTRUCTOR_FOUND = "Could not find public constructor for %s";

    private PopulateUtil() {
    }

    static List<Type> toArgumentTypes(Parameter parameter, Type[] typeArguments) {
        if (typeArguments != null) {
            return Arrays.asList(typeArguments);
        } else {
            return Arrays.stream(((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments())
                    .map(type -> type instanceof WildcardType ? ((WildcardType) type).getUpperBounds()[0] : type)
                    .collect(Collectors.toList());
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T getOverridePopulateValue(Class<?> clazz, Map<? extends Class<?>, OverridePopulate<?>> overridePopulate) {
        return (T) (overridePopulate.get(clazz).create());
    }

    static List<Field> getDeclaredFields(Class<?> clazz) {
        return getDeclaredFields(clazz, new ArrayList<>());
    }

    static List<Method> getDeclaredMethods(Class<?> clazz) {
        return getDeclaredMethods(clazz, new ArrayList<>());
    }

    static boolean isSet(Class<?> clazz) {
        return Set.class.isAssignableFrom(clazz);
    }

    static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) ||
                Map.class.isAssignableFrom(clazz) ||
                Iterable.class.isAssignableFrom(clazz);
    }

    static <T> boolean isMapEntry(Class<T> clazz) {
        return clazz.isAssignableFrom(Map.Entry.class);
    }

    static boolean isValue(Class<?> clazz) {
        return clazz.isEnum() || isJavaBaseClass(clazz);
    }

    static boolean isJavaBaseClass(Class<?> clazz) {
        return clazz.getModule() != null && JAVA_BASE.equals(clazz.getModule().getName());
    }

    static boolean isDeclaringJavaBaseClass(Method method) {
        return isJavaBaseClass(method.getDeclaringClass());
    }

    static boolean hasAtLeastOneParameter(Method method) {
        return method.getParameters().length > 0;
    }

    static boolean isMatchingSetterStrategy(Strategy strategy, Class<?> clazz) {
        return strategy.equals(SETTER) && hasOnlyDefaultConstructor(clazz);
    }

    static boolean isMatchingConstructorStrategy(Strategy strategy, Class<?> clazz) {
        return strategy.equals(CONSTRUCTOR) && !hasOnlyDefaultConstructor(clazz);
    }

    static boolean isMatchingFieldStrategy(Strategy strategy, Class<?> clazz) {
        return strategy.equals(FIELD) && hasOnlyDefaultConstructor(clazz);
    }

    static boolean isMatchingBuilderStrategy(Strategy strategy, Class<?> clazz) {
        if (strategy.equals(BUILDER)) {
            try {
                clazz.getDeclaredMethod(BUILDER_METHOD);
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    static <T> Constructor<T> getLargestPublicConstructor(Class<T> clazz) {
        return (Constructor<T>) stream(clazz.getDeclaredConstructors())
                .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new RuntimeException(String.format(NO_CONSTRUCTOR_FOUND, clazz.getName())));
    }

    static boolean isBlackListedMethod(Method method) {
        return BLACKLISTED_METHODS.contains(method.getName());
    }

    static boolean isSetterMethod(Method method, String setterPrefix) {
        String methodFormat = setterPrefix.equals("") ? "" : String.format("%s%s", setterPrefix, MATCH_FIRST_CHARACTER_UPPERCASE);
        return method.getName().matches(methodFormat) && method.getReturnType().equals(void.class) && method.getParameters().length == 1;
    }

    static <T> boolean isSameMethodParameterAsClass(Class<T> clazz, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(clazz);
    }

    private static boolean hasOnlyDefaultConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        return Arrays.stream(constructors).count() == 1 && constructors[0].getParameterCount() == 0;
    }

    private static List<Field> getDeclaredFields(Class<?> clazz, List<Field> declaredFields) {
        declaredFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            getDeclaredFields(clazz.getSuperclass(), declaredFields);
        }
        return declaredFields;
    }

    private static List<Method> getDeclaredMethods(Class<?> clazz, List<Method> declaredMethods) {
        declaredMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        if (clazz.getSuperclass() != null) {
            getDeclaredMethods(clazz.getSuperclass(), declaredMethods);
        }
        return declaredMethods;
    }

}
