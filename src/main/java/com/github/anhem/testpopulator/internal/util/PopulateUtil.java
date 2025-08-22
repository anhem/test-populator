package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.config.Strategy.FIELD;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;

public class PopulateUtil {

    static final String MATCH_FIRST_CHARACTER_UPPERCASE = "\\p{Lu}.*";
    private static final String JAVA_BASE = "java.base";
    public static final String NO_CONSTRUCTOR_FOUND = "Could not find public constructor for %s";

    private PopulateUtil() {
    }

    public static List<Type> toArgumentTypes(Parameter parameter) {
        return Arrays.stream(((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments())
                .map(type -> type instanceof WildcardType ? ((WildcardType) type).getUpperBounds()[0] : type)
                .collect(Collectors.toList());
    }

    public static <T> List<Field> getDeclaredFields(Class<T> clazz, List<String> blacklistedFields) {
        List<Field> declaredFields = getAllDeclaredFields(clazz, new ArrayList<>());
        return removeUnwantedFields(declaredFields, blacklistedFields);
    }

    public static <T> List<Method> getDeclaredMethods(Class<T> clazz, List<String> blacklistedMethods) {
        List<Method> declaredMethods = getAllDeclaredMethods(clazz, new ArrayList<>());
        return removeUnwantedMethods(declaredMethods, blacklistedMethods);
    }

    static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static <T> boolean isSet(Class<T> clazz) {
        return Set.class.isAssignableFrom(clazz);
    }

    public static <T> boolean isMap(Class<T> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    public static <T> boolean isMapEntry(Class<T> clazz) {
        return Map.Entry.class.isAssignableFrom(clazz);
    }

    public static <T> boolean isCollectionLike(Class<T> clazz) {
        return Collection.class.isAssignableFrom(clazz) ||
                Map.class.isAssignableFrom(clazz) ||
                Iterable.class.isAssignableFrom(clazz) ||
                Map.Entry.class.isAssignableFrom(clazz);
    }

    public static <T> boolean isCollectionCarrier(ClassCarrier<T> classCarrier) {
        return classCarrier instanceof CollectionCarrier;
    }

    public static <T> boolean isJavaBaseClass(Class<T> clazz) {
        return clazz.getModule() != null && JAVA_BASE.equals(clazz.getModule().getName());
    }

    static boolean isDeclaringJavaBaseClass(Method method) {
        return isJavaBaseClass(method.getDeclaringClass());
    }

    static boolean hasAtLeastOneParameter(Method method) {
        return method.getParameterCount() > 0;
    }

    public static <T> boolean isMatchingConstructorStrategy(Strategy strategy, Class<T> clazz, boolean accessNonPublicConstructor) {
        return strategy.equals(CONSTRUCTOR) && hasConstructorWithArguments(clazz, accessNonPublicConstructor);
    }

    public static <T> boolean isMatchingFieldStrategy(Strategy strategy, Class<T> clazz, boolean accessNonPublicConstructor) {
        return strategy.equals(FIELD) && hasConstructorWithoutArguments(clazz, accessNonPublicConstructor);
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getLargestConstructor(Class<T> clazz, boolean canAccessNonPublicConstructor) {
        return (Constructor<T>) stream(clazz.getDeclaredConstructors())
                .filter(constructor -> canAccessNonPublicConstructor || Modifier.isPublic(constructor.getModifiers()))
                .filter(constructor -> constructor.getParameterCount() != 0)
                .max(comparingInt(Constructor::getParameterCount))
                .orElseGet(() -> getNoArgsConstructor(clazz, canAccessNonPublicConstructor));
    }

    static <T> Constructor<T> getNoArgsConstructor(Class<T> clazz, boolean canAccessNonPublicConstructor) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            if (canAccessNonPublicConstructor || Modifier.isPublic(constructor.getModifiers())) {
                return constructor;
            }
        } catch (NoSuchMethodException ignored) {
        }
        throw new RuntimeException(String.format(NO_CONSTRUCTOR_FOUND, clazz.getName()));
    }

    static boolean isBlackListed(Method method, List<String> blacklistedMethods) {
        return blacklistedMethods.contains(method.getName());
    }

    static boolean isBlackListed(Field field, List<String> blacklistedFields) {
        return blacklistedFields.contains(field.getName());
    }

    public static <T> void setAccessible(Constructor<T> constructor, boolean canAccessNonPublicConstructor) {
        if (canAccessNonPublicConstructor && !constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }
    }

    public static <T> void setAccessible(Method method, T object) {
        if (!method.canAccess(object)) {
            method.setAccessible(true);
        }
    }

    public static <T> void setAccessible(Field field, T object) {
        if (!field.canAccess(object)) {
            field.setAccessible(true);
        }
    }

    public static <T> boolean hasConstructors(CollectionCarrier<T> collectionCarrier) {
        return collectionCarrier.getClazz().getConstructors().length > 0;
    }

    public static <T> boolean alreadyVisited(ClassCarrier<T> classCarrier, boolean nullOnCircularDependency) {
        return nullOnCircularDependency && !isJavaBaseClass(classCarrier.getClazz()) && !classCarrier.addVisited();
    }

    static <T> boolean hasConstructorWithoutArguments(Class<T> clazz, boolean canAccessNonPublicConstructor) {
        return stream(clazz.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .anyMatch(constructor -> isAccessible(canAccessNonPublicConstructor, constructor));
    }

    private static <T> boolean hasConstructorWithArguments(Class<T> clazz, boolean canAccessNonPublicConstructor) {
        return stream(clazz.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() > 0)
                .anyMatch(constructor -> isAccessible(canAccessNonPublicConstructor, constructor));
    }

    private static <T> List<Field> getAllDeclaredFields(Class<T> clazz, List<Field> declaredFields) {
        declaredFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            getAllDeclaredFields(clazz.getSuperclass(), declaredFields);
        }
        return declaredFields;
    }

    static <T> List<Method> getAllDeclaredMethods(Class<T> clazz, List<Method> declaredMethods) {
        declaredMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        if (clazz.getSuperclass() != null) {
            getAllDeclaredMethods(clazz.getSuperclass(), declaredMethods);
        }
        return declaredMethods;
    }

    static String getMethodFormat(String setterPrefix) {
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

    static boolean isWaitMethod(Method method) {
        boolean isFinal = Modifier.isFinal(method.getModifiers());
        boolean isWaitName = method.getName().equals("wait") || (method.getName().equals("wait0") && Modifier.isNative(method.getModifiers()));

        if (isFinal && isWaitName) {
            int parameterCount = method.getParameterCount();
            return parameterCount == 0 ||
                    (parameterCount == 1 && method.getParameters()[0].getType().equals(long.class)) ||
                    (parameterCount == 2 && method.getParameters()[0].getType().equals(long.class) && method.getParameters()[1].getType().equals(int.class));
        }
        return false;
    }

    private static <T> boolean isAccessible(boolean canAccessNonPublicConstructor, Constructor<T> constructor) {
        if (canAccessNonPublicConstructor) {
            return true;
        } else {
            return Modifier.isPublic(constructor.getModifiers());
        }
    }
}
