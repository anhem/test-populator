package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.ConstructorType;
import com.github.anhem.testpopulator.config.MethodType;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.internal.util.ImmutablesUtil.getImmutablesGeneratedClass;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingInt;

public class PopulateUtil {

    private static final Comparator<Method> PARAMETER_COUNT_COMPARATOR = comparingInt(Method::getParameterCount);
    private static final Comparator<Method> SIMPLEST_METHOD_COMPARATOR = comparingDouble(PopulateUtil::getAverageParameterComplexity)
            .thenComparingInt(Method::getParameterCount);
    static final String MATCH_FIRST_CHARACTER_UPPERCASE = "\\p{Lu}.*";
    private static final String JAVA_BASE = "java.base";
    private static final String NO_CONSTRUCTOR_FOUND = "Could not find public constructor for %s";

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
        List<String> setterMethodFormats = getMethodFormats(setterPrefixes);
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> isSetterMethod(method, setterMethodFormats))
                .collect(Collectors.toList());
    }

    public static <T> List<Method> getMutatorMethods(Class<T> clazz, List<String> blacklistedMethods) {
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> isMutatorMethod(method, clazz))
                .collect(Collectors.toList());
    }

    public static <T> List<Method> getMethodsForCustomBuilder(Class<T> clazz, List<String> blacklistedMethods) {
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> method.getReturnType().equals(clazz) && method.getParameters().length > 0)
                .collect(Collectors.toList());
    }

    public static <T> Method getStaticMethod(Class<T> clazz, List<String> blacklistedMethods, MethodType methodType) {
        List<Method> methods = getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> isMatchingStaticMethod(method, clazz))
                .sorted(Comparator.comparing(Method::getName))
                .collect(Collectors.toList());
        switch (methodType) {
            case LARGEST:
                return methods.stream().max(PARAMETER_COUNT_COMPARATOR).orElseThrow();
            case SMALLEST:
                return methods.stream().min(PARAMETER_COUNT_COMPARATOR).orElseThrow();
            case SIMPLEST:
                return methods.stream().min(SIMPLEST_METHOD_COMPARATOR).orElseThrow();
            default:
                throw new IllegalArgumentException("Unsupported MethodType: " + methodType);
        }
    }

    private static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    private static boolean hasSelfReferencingParameter(Method method, Class<?> clazz) {
        for (Type paramType : method.getGenericParameterTypes()) {
            if (isOrContainsType(paramType, clazz)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOrContainsType(Type type, Class<?> clazz) {
        if (clazz.equals(type)) {
            return true;
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                if (isOrContainsType(typeArgument, clazz)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static double getAverageParameterComplexity(Method method) {
        return Arrays.stream(method.getParameterTypes())
                .mapToInt(PopulateUtil::getParameterTypeScore)
                .average()
                .orElse(0);
    }

    private static int getParameterTypeScore(Class<?> type) {
        if (Iterator.class.isAssignableFrom(type) ||
                InputStream.class.isAssignableFrom(type) ||
                OutputStream.class.isAssignableFrom(type) ||
                Reader.class.isAssignableFrom(type) ||
                Stream.class.isAssignableFrom(type)) {
            return 100;
        }
        if (type.isPrimitive() || Number.class.isAssignableFrom(type) ||
                type.equals(Boolean.class) || type.equals(Character.class) ||
                type.equals(String.class)) {
            return 1;
        }
        if (type.getPackage() != null && (type.getPackage().getName().startsWith("java.util") || type.getPackage().getName().startsWith("java.time"))) {
            return 5;
        }
        if (type.isArray()) {
            return 10;
        }
        return 20;
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
            List<String> setterMethodFormats = getMethodFormats(setterPrefixes);
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

    public static <T> boolean isMatchingBuilderStrategy(Strategy strategy, Class<T> clazz, BuilderPattern builderPattern, String builderMethod) {
        if (strategy.equals(BUILDER)) {
            try {
                if (builderPattern.equals(IMMUTABLES)) {
                    getImmutablesGeneratedClass(clazz).getDeclaredMethod(builderMethod);
                } else {
                    clazz.getDeclaredMethod(builderMethod);
                }
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        return false;
    }

    public static <T> boolean isMatchingStaticMethodStrategy(Strategy strategy, Class<T> clazz) {
        if (strategy.equals(STATIC_METHOD)) {
            return getDeclaredMethods(clazz, new ArrayList<>()).stream()
                    .anyMatch(method -> isMatchingStaticMethod(method, clazz));
        }
        return false;
    }

    private static <T> boolean isMatchingStaticMethod(Method method, Class<T> clazz) {
        return isStatic(method) &&
                method.getReturnType().equals(clazz) &&
                hasAtLeastOneParameter(method) &&
                !hasSelfReferencingParameter(method, clazz);
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
                .min(comparingInt(Constructor::getParameterCount))
                .orElseGet(() -> getNoArgsConstructor(clazz, canAccessNonPublicConstructor));
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getLargestConstructor(Class<T> clazz, boolean canAccessNonPublicConstructor) {
        return (Constructor<T>) stream(clazz.getDeclaredConstructors())
                .filter(constructor -> canAccessNonPublicConstructor || Modifier.isPublic(constructor.getModifiers()))
                .filter(constructor -> constructor.getParameterCount() != 0)
                .max(comparingInt(Constructor::getParameterCount))
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
        return method.getName().matches(setMethodFormat) && method.getReturnType().equals(void.class) && method.getParameters().length == 1 && !isStatic(method);
    }

    private static <T> boolean isMutatorMethod(Method method, Class<T> clazz) {
        return (method.getReturnType().equals(void.class) || method.getReturnType().equals(clazz)) && method.getParameters().length > 0 && !isStatic(method);
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

    private static List<String> getMethodFormats(List<String> setterPrefixes) {
        return setterPrefixes.stream()
                .map(PopulateUtil::getMethodFormat)
                .collect(Collectors.toList());
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
}
