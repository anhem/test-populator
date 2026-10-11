package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.ConstructorType;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.Strategy.MUTATOR;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;

public class MutatorUtil {

    private MutatorUtil() {
    }

    public static <T> boolean isMatchingMutatorStrategy(Strategy strategy, Class<T> clazz, boolean accessNonPublicConstructor, ConstructorType constructorType) {
        return isMatchingMutatorStrategy(strategy, clazz, accessNonPublicConstructor, constructorType, true, true, true);
    }

    public static <T> boolean isMatchingMutatorStrategy(
            Strategy strategy,
            Class<T> clazz,
            boolean accessNonPublicConstructor,
            ConstructorType constructorType,
            boolean mutatorMethods,
            boolean mutatorPublicFields,
            boolean mutatorInPlaceGetters
    ) {
        if (strategy.equals(MUTATOR) && hasAccessibleConstructor(clazz, accessNonPublicConstructor, constructorType)) {
            return (mutatorMethods && hasMutatorMethods(clazz))
                    || (mutatorPublicFields && hasPublicFields(clazz))
                    || (mutatorInPlaceGetters && hasMutatorGetters(clazz));
        }
        return false;
    }

    public static <T> List<Method> getMutatorMethods(Class<T> clazz, Set<String> blacklistedMethods) {
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> isMutatorMethod(method, clazz))
                .collect(Collectors.toList());
    }

    public static <T> List<Field> getPublicFields(Class<T> clazz, Set<String> blacklistedFields) {
        return stream(clazz.getFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .filter(field -> !blacklistedFields.contains(field.getName()))
                .sorted(Comparator.comparing(Field::getName))
                .collect(Collectors.toList());
    }

    public static <T> List<MutatorGetter> getMutatorGetters(
            T objectOfClass,
            Class<T> clazz,
            PopulateConfig populateConfig
    ) {
        return getMutatorGetters(objectOfClass, clazz, populateConfig, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    public static <T> List<Method> getMutatorGetters(Class<T> clazz, Set<String> blacklistedMethods) {
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> isMutatorGetter(method, new HashSet<>(List.of(clazz))))
                .sorted(Comparator.comparing(Method::getName))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <T> List<MutatorGetter> getMutatorGetters(
            T objectOfClass,
            Class<T> clazz,
            PopulateConfig populateConfig,
            Set<Object> visitedObjects
    ) {
        if (objectOfClass == null || visitedObjects.contains(objectOfClass)) {
            return Collections.emptyList();
        }
        visitedObjects.add(objectOfClass);

        List<Method> candidateGetters = getDeclaredMethods(clazz, populateConfig.getBlacklistedMethods()).stream()
                .filter(MutatorUtil::isMutatorGetterCandidate)
                .sorted(Comparator.comparing(Method::getName))
                .collect(Collectors.toList());

        List<MutatorGetter> mutatorGetters = new ArrayList<>();
        for (Method getter : candidateGetters) {
            try {
                setAccessible(getter, objectOfClass);
                Object targetObject = getter.invoke(objectOfClass);
                if (targetObject != null && !visitedObjects.contains(targetObject)) {
                    Class<?> targetClass = targetObject.getClass();
                    List<Method> childMutatorMethods = populateConfig.isMutatorMethods()
                            ? getMutatorMethods(targetClass, populateConfig.getBlacklistedMethods())
                            : Collections.emptyList();
                    List<Field> childPublicFields = populateConfig.isMutatorPublicFields()
                            ? getPublicFields(targetClass, populateConfig.getBlacklistedFields())
                            : Collections.emptyList();
                    List<MutatorGetter> childMutatorGetters = populateConfig.isMutatorInPlaceGetters()
                            ? getMutatorGetters(targetObject, (Class<Object>) targetClass, populateConfig, visitedObjects)
                            : Collections.emptyList();
                    int expectedChildren = childMutatorMethods.size() + childPublicFields.size() + childMutatorGetters.size();
                    if (expectedChildren > 0) {
                        mutatorGetters.add(new MutatorGetter(getter, targetObject, childMutatorMethods, childPublicFields, childMutatorGetters));
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return mutatorGetters;
    }

    private static <T> boolean hasMutatorGetters(Class<T> clazz) {
        return hasMutatorGetters(clazz, new HashSet<>(List.of(clazz)));
    }

    private static boolean hasMutatorGetters(Class<?> clazz, Set<Class<?>> visitedClasses) {
        if (isJavaBaseClass(clazz) || clazz.isPrimitive() || clazz.isEnum() || clazz.isArray() || isCollectionLike(clazz)) {
            return false;
        }
        return getDeclaredMethods(clazz, Collections.emptySet()).stream()
                .filter(MutatorUtil::isMutatorGetterCandidate)
                .map(Method::getReturnType)
                .filter(returnType -> !visitedClasses.contains(returnType))
                .anyMatch(returnType -> {
                    visitedClasses.add(returnType);
                    return hasMutatorMethods(returnType) || hasPublicFields(returnType) || hasMutatorGetters(returnType, visitedClasses);
                });
    }

    private static boolean isMutatorGetter(Method method, Set<Class<?>> visitedClasses) {
        if (!isMutatorGetterCandidate(method)) {
            return false;
        }
        Class<?> returnType = method.getReturnType();
        if (visitedClasses.contains(returnType)) {
            return false;
        }
        visitedClasses.add(returnType);
        return hasMutatorMethods(returnType) || hasPublicFields(returnType) || hasMutatorGetters(returnType, visitedClasses);
    }

    private static boolean isMutatorGetterCandidate(Method method) {
        if (method.getParameterCount() != 0 || method.getReturnType().equals(void.class) || isStatic(method) || !Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        if (method.getDeclaringClass().equals(Object.class) || isWaitMethod(method)) {
            return false;
        }
        String name = method.getName();
        if (!(name.startsWith("get") && name.length() > 3 && Character.isUpperCase(name.charAt(3)))
                && !(name.startsWith("is") && name.length() > 2 && Character.isUpperCase(name.charAt(2)))) {
            return false;
        }
        Class<?> returnType = method.getReturnType();
        return isEligibleReturnType(returnType);
    }

    private static boolean isEligibleReturnType(Class<?> returnType) {
        return !isJavaBaseClass(returnType)
                && !returnType.isPrimitive()
                && !returnType.isEnum()
                && !returnType.isArray()
                && !isCollectionLike(returnType);
    }

    private static <T> boolean hasMutatorMethods(Class<T> clazz) {
        return getAllDeclaredMethods(clazz, new ArrayList<>()).stream()
                .filter(method -> !isWaitMethod(method))
                .anyMatch(method -> isMutatorMethod(method, clazz));
    }

    private static <T> boolean hasPublicFields(Class<T> clazz) {
        boolean hasPublic = stream(clazz.getFields())
                .anyMatch(field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()));
        if (!hasPublic) {
            return false;
        }
        return getDeclaredFields(clazz, Collections.emptySet()).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
                .allMatch(field -> Modifier.isPublic(field.getModifiers()));
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

    private static <T> boolean isMutatorMethod(Method method, Class<T> clazz) {
        return (method.getReturnType().equals(void.class) || method.getReturnType().equals(clazz)) && method.getParameterCount() > 0 && !isStatic(method);
    }


}
