package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class PopulateUtil {

    private static final String JAVA_BASE = "java.base";
    private static final String NO_CONSTRUCTOR_FOUND = "Could not find public constructor for %s";
    private static final String SETTER_PATTERN = "set\\p{Lu}.*";

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

    static boolean isValue(Class<?> clazz) {
        return clazz.isEnum() || isJavaBaseClass(clazz);
    }

    static boolean isJavaBaseClass(Class<?> clazz) {
        return clazz.getModule() != null && JAVA_BASE.equals(clazz.getModule().getName());
    }

    static boolean hasOnlyDefaultConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        return Arrays.stream(constructors).count() == 1 && constructors[0].getParameterCount() == 0;
    }

    static boolean hasAtLeastOneParameter(Method method) {
        return method.getParameters().length > 0;
    }

    static boolean isMatchingSetterStrategy(Strategy strategy, Class<?> clazz) {
        return strategy.equals(Strategy.SETTER) && hasOnlyDefaultConstructor(clazz);
    }

    static boolean isMatchingConstructorStrategy(Strategy strategy, Class<?> clazz) {
        return strategy.equals(Strategy.CONSTRUCTOR) && !hasOnlyDefaultConstructor(clazz);
    }

    static boolean isMatchingFieldStrategy(Strategy strategy, Class<?> clazz) {
        return strategy.equals(Strategy.FIELD) && hasOnlyDefaultConstructor(clazz);
    }

    @SuppressWarnings("unchecked")
    static <T> Constructor<T> getLargestPublicConstructor(Class<T> clazz) {
        return (Constructor<T>) stream(clazz.getDeclaredConstructors())
                .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new RuntimeException(String.format(NO_CONSTRUCTOR_FOUND, clazz.getName())));
    }

    static boolean isSetter(Method method) {
        return method.getName().matches(SETTER_PATTERN) && method.getReturnType().equals(void.class) && method.getParameters().length == 1;
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
