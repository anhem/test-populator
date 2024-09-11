package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.ImmutablesUtil.getImmutablesGeneratedClass;
import static com.github.anhem.testpopulator.PopulateFactory.BUILDER_METHOD;
import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static java.util.Arrays.stream;

class PopulateUtil {

    static final String MATCH_FIRST_CHARACTER_UPPERCASE = "\\p{Lu}.*";
    private static final String JAVA_BASE = "java.base";
    private static final String NO_CONSTRUCTOR_FOUND = "Could not find public constructor for %s";

    private PopulateUtil() {
    }

    static List<Type> toArgumentTypes(Parameter parameter) {
        return Arrays.stream(((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments())
                .map(type -> type instanceof WildcardType ? ((WildcardType) type).getUpperBounds()[0] : type)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    static <T> T getOverridePopulateValue(Class<T> clazz, Map<Class<?>, OverridePopulate<?>> overridePopulate) {
        return (T) (overridePopulate.get(clazz).create());
    }

    static List<Field> getDeclaredFields(Class<?> clazz, List<String> blacklistedFields) {
        List<Field> declaredFields = getAllDeclaredFields(clazz, new ArrayList<>());
        return removeUnwantedFields(declaredFields, blacklistedFields);
    }

    static List<Method> getDeclaredMethods(Class<?> clazz, List<String> blacklistedMethods) {
        List<Method> declaredMethods = getAllDeclaredMethods(clazz, new ArrayList<>());
        return removeUnwantedMethods(declaredMethods, blacklistedMethods);
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

    static <T> boolean isCollectionCarrier(ClassCarrier<T> classCarrier) {
        return classCarrier instanceof CollectionCarrier;
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

    static boolean isMatchingSetterStrategy(Strategy strategy, Class<?> clazz, String setterPrefix, boolean accessNonPublicConstructor) {
        return strategy.equals(SETTER) && hasConstructorWithoutArguments(clazz, accessNonPublicConstructor) && getAllDeclaredMethods(clazz, new ArrayList<>()).stream()
                .anyMatch(method -> isSetterMethod(method, setterPrefix));
    }

    static boolean isMatchingConstructorStrategy(Strategy strategy, Class<?> clazz, boolean accessNonPublicConstructor) {
        return strategy.equals(CONSTRUCTOR) && hasConstructorWithArguments(clazz, accessNonPublicConstructor);
    }

    static boolean isMatchingFieldStrategy(Strategy strategy, Class<?> clazz, boolean accessNonPublicConstructor) {
        return strategy.equals(FIELD) && hasConstructorWithoutArguments(clazz, accessNonPublicConstructor);
    }

    static boolean isMatchingBuilderStrategy(Strategy strategy, Class<?> clazz, BuilderPattern builderPattern) {
        if (strategy.equals(BUILDER)) {
            try {
                if (builderPattern.equals(IMMUTABLES)) {
                    getImmutablesGeneratedClass(clazz).getDeclaredMethod(BUILDER_METHOD);
                } else {
                    clazz.getDeclaredMethod(BUILDER_METHOD);
                }
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    static <T> Constructor<T> getLargestConstructor(Class<T> clazz, boolean canAccessNonPublicConstructor) {
        Constructor<?> constructor1 = stream(clazz.getDeclaredConstructors())
                .filter(constructor -> canAccessNonPublicConstructor || Modifier.isPublic(constructor.getModifiers()))
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new RuntimeException(String.format(NO_CONSTRUCTOR_FOUND, clazz.getName())));
        return (Constructor<T>) constructor1;
    }

    static boolean isBlackListed(Method method, List<String> blacklistedMethods) {
        return blacklistedMethods.contains(method.getName());
    }

    static boolean isBlackListed(Field field, List<String> blacklistedFields) {
        return blacklistedFields.contains(field.getName());
    }

    static boolean isSetterMethod(Method method, String setterPrefix) {
        String methodFormat = getSetterMethodFormat(setterPrefix);
        if (methodFormat.isBlank()) {
            return method.getReturnType().equals(void.class) && method.getParameters().length == 1;
        }
        return method.getName().matches(methodFormat) && method.getReturnType().equals(void.class) && method.getParameters().length == 1;
    }


    static <T> boolean isSameMethodParameterAsClass(Class<T> clazz, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(clazz);
    }

    static <T> void setAccessible(Constructor<T> constructor, boolean canAccessNonPublicConstructor) {
        if (canAccessNonPublicConstructor && !constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }
    }

    static <T> void setAccessible(Method method, T object) {
        if (!method.canAccess(object)) {
            method.setAccessible(true);
        }
    }

    static <T> void setAccessible(Field field, T object) {
        if (!field.canAccess(object)) {
            field.setAccessible(true);
        }
    }

    static <T> boolean hasConstructors(CollectionCarrier<T> collectionCarrier) {
        return collectionCarrier.getClazz().getConstructors().length > 0;
    }

    private static boolean hasConstructorWithoutArguments(Class<?> clazz, boolean canAccessNonPublicConstructor) {
        return stream(clazz.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .anyMatch(constructor -> isAccessible(canAccessNonPublicConstructor, constructor));
    }

    private static boolean hasConstructorWithArguments(Class<?> clazz, boolean canAccessNonPublicConstructor) {
        return stream(clazz.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() > 0)
                .anyMatch(constructor -> isAccessible(canAccessNonPublicConstructor, constructor));
    }

    private static List<Field> getAllDeclaredFields(Class<?> clazz, List<Field> declaredFields) {
        declaredFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            getAllDeclaredFields(clazz.getSuperclass(), declaredFields);
        }
        return declaredFields;
    }

    private static List<Method> getAllDeclaredMethods(Class<?> clazz, List<Method> declaredMethods) {
        declaredMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        if (clazz.getSuperclass() != null) {
            getAllDeclaredMethods(clazz.getSuperclass(), declaredMethods);
        }
        return declaredMethods;
    }

    private static String getSetterMethodFormat(String setterPrefix) {
        return setterPrefix.isEmpty() ? "" : String.format("%s%s", setterPrefix, MATCH_FIRST_CHARACTER_UPPERCASE);
    }

    private static List<Field> removeUnwantedFields(List<Field> declaredFields, List<String> blacklistedFields) {
        return declaredFields.stream()
                .filter(field -> !isBlackListed(field, blacklistedFields))
                .collect(Collectors.toList());
    }

    private static List<Method> removeUnwantedMethods(List<Method> declaredMethods, List<String> blacklistedMethods) {
        return declaredMethods.stream()
                .filter(method -> {
                    if (isBlackListed(method, blacklistedMethods)) {
                        return false;
                    }
                    if (isNativeMethod(method)) {
                        return false;
                    }
                    return !isWaitMethod(method);
                }).collect(Collectors.toList());
    }

    private static boolean isNativeMethod(Method method) {
        return Modifier.isNative(method.getModifiers());
    }

    private static boolean isWaitMethod(Method method) {
        if (Modifier.isFinal(method.getModifiers()) && method.getName().equals("wait")) {
            return method.getParameters().length == 0 || (method.getParameters().length == 1 && method.getParameters()[0].getType().equals(long.class));
        }
        return false;
    }

    private static boolean isAccessible(boolean canAccessNonPublicConstructor, Constructor<?> constructor) {
        if (canAccessNonPublicConstructor) {
            return true;
        } else {
            return Modifier.isPublic(constructor.getModifiers());
        }
    }

}
