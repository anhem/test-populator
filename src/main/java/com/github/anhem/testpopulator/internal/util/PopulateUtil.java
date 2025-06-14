package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.ConstructorType;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.PopulateFactory.BUILDER_METHOD;
import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.internal.util.ImmutablesUtil.getImmutablesGeneratedClass;
import static java.util.Arrays.stream;

public class PopulateUtil {

    static final String MATCH_FIRST_CHARACTER_UPPERCASE = "\\p{Lu}.*";
    private static final String JAVA_BASE = "java.base";
    private static final String NO_CONSTRUCTOR_FOUND = "Could not find public constructor for %s";
    public static final String KOTLIN_DEFAULT_CONSTRUCTOR_MARKER = "DefaultConstructorMarker";

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

    public static <T> List<Method> getSetterMethods(Class<T> clazz, List<String> blacklistedMethods, List<String> setterPrefixes) {
        List<String> setterMethodFormats = getSetterMethodFormats(setterPrefixes);
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> isSetterMethod(method, setterMethodFormats))
                .collect(Collectors.toList());
    }

    public static <T> List<Method> getMutatorMethods(Class<T> clazz, List<String> blacklistedMethods) {
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> isMutatorMethod(method, clazz))
                .collect(Collectors.toList());
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

    public static <T> boolean isCollection(Class<T> clazz) {
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
        return method.getParameters().length > 0;
    }

    public static <T> boolean isMatchingSetterStrategy(Strategy strategy, Class<T> clazz, List<String> setterPrefixes, boolean accessNonPublicConstructor) {
        if (strategy.equals(SETTER) && hasConstructorWithoutArguments(clazz, accessNonPublicConstructor)) {
            List<String> setterMethodFormats = getSetterMethodFormats(setterPrefixes);
            return getAllDeclaredMethods(clazz, new ArrayList<>()).stream()
                    .filter(method -> !isWaitMethod(method))
                    .anyMatch(method -> isSetterMethod(method, setterMethodFormats));
        }
        return false;
    }

    public static <T> boolean isMatchingMutatorStrategy(Strategy strategy, Class<T> clazz, boolean accessNonPublicConstructor, ConstructorType constructorType) {
        if (strategy.equals(MUTATOR) && hasAccessibleConstructor(clazz, accessNonPublicConstructor, constructorType)) {
            return getAllDeclaredMethods(clazz, new ArrayList<>()).stream()
                    .filter(method -> !isWaitMethod(method))
                    .anyMatch(method -> isMutatorMethod(method, clazz));
        }
        return false;
    }

    public static <T> boolean isMatchingConstructorStrategy(Strategy strategy, Class<T> clazz, boolean accessNonPublicConstructor) {
        return strategy.equals(CONSTRUCTOR) && hasConstructorWithArguments(clazz, accessNonPublicConstructor);
    }

    public static <T> boolean isMatchingFieldStrategy(Strategy strategy, Class<T> clazz, boolean accessNonPublicConstructor) {
        return strategy.equals(FIELD) && hasConstructorWithoutArguments(clazz, accessNonPublicConstructor);
    }

    public static <T> boolean isMatchingBuilderStrategy(Strategy strategy, Class<T> clazz, BuilderPattern builderPattern) {
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

    private static <T> boolean hasAccessibleConstructor(Class<T> clazz, boolean canAccessNonPublicConstructor, ConstructorType constructorType) {
        try {
            getConstructor(clazz, canAccessNonPublicConstructor, constructorType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, boolean canAccessNonPublicConstructor, ConstructorType constructorType) {
        switch (constructorType) {
            case SMALLEST:
                return getSmallestConstructor(clazz, canAccessNonPublicConstructor);
            case LARGEST:
                return getLargestConstructor(clazz, canAccessNonPublicConstructor);
            case NO_ARGS:
            default:
                return getNoArgsConstructor(clazz, canAccessNonPublicConstructor);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> getSmallestConstructor(Class<T> clazz, boolean canAccessNonPublicConstructor) {
        return (Constructor<T>) stream(clazz.getDeclaredConstructors())
                .filter(constructor -> canAccessNonPublicConstructor || Modifier.isPublic(constructor.getModifiers()))
                .filter(constructor -> constructor.getParameterCount() != 0)
                .min(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseGet(() -> getNoArgsConstructor(clazz, canAccessNonPublicConstructor));
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getLargestConstructor(Class<T> clazz, boolean canAccessNonPublicConstructor) {
        return (Constructor<T>) stream(clazz.getDeclaredConstructors())
                .filter(constructor -> canAccessNonPublicConstructor || Modifier.isPublic(constructor.getModifiers()))
                .filter(constructor -> constructor.getParameterCount() != 0)
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseGet(() -> getNoArgsConstructor(clazz, canAccessNonPublicConstructor));
    }

    private static <T> Constructor<T> getNoArgsConstructor(Class<T> clazz, boolean canAccessNonPublicConstructor) {
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

    private static boolean isSetterMethod(Method method, List<String> setterMethodFormats) {
        return setterMethodFormats.stream().anyMatch(setMethodFormat -> isSetterMethod(method, setMethodFormat));
    }

    private static boolean isSetterMethod(Method method, String setMethodFormat) {
        if (setMethodFormat.isBlank()) {
            return method.getReturnType().equals(void.class) && method.getParameters().length == 1;
        }
        return method.getName().matches(setMethodFormat) && method.getReturnType().equals(void.class) && method.getParameters().length == 1;
    }

    private static <T> boolean isMutatorMethod(Method method, Class<T> clazz) {
        return (method.getReturnType().equals(void.class) || method.getReturnType().equals(clazz)) && method.getParameters().length > 0;
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

    private static <T> boolean hasConstructorWithoutArguments(Class<T> clazz, boolean canAccessNonPublicConstructor) {
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

    private static <T> List<Method> getAllDeclaredMethods(Class<T> clazz, List<Method> declaredMethods) {
        declaredMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        if (clazz.getSuperclass() != null) {
            getAllDeclaredMethods(clazz.getSuperclass(), declaredMethods);
        }
        return declaredMethods;
    }

    private static List<String> getSetterMethodFormats(List<String> setterPrefixes) {
        return setterPrefixes.stream()
                .map(PopulateUtil::getSetterMethodFormat)
                .collect(Collectors.toList());
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
        boolean isFinal = Modifier.isFinal(method.getModifiers());
        boolean isWaitName = method.getName().equals("wait") || (method.getName().equals("wait0") && Modifier.isNative(method.getModifiers()));

        if (isFinal && isWaitName) {
            return method.getParameters().length == 0 ||
                    (method.getParameters().length == 1 && method.getParameters()[0].getType().equals(long.class)) ||
                    (method.getParameters().length == 2 && method.getParameters()[0].getType().equals(long.class) && method.getParameters()[1].getType().equals(int.class));
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

    public static boolean isKotlinConstructor(Class<?>[] parameterTypes) {
        return parameterTypes.length > 0 && parameterTypes[parameterTypes.length - 1].getSimpleName().equals(KOTLIN_DEFAULT_CONSTRUCTOR_MARKER);
    }

}
