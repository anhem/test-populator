package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.ConstructorType;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.Strategy.MUTATOR;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;

public class MutatorUtil {

    public static <T> boolean isMatchingMutatorStrategy(Strategy strategy, Class<T> clazz, boolean accessNonPublicConstructor, ConstructorType constructorType) {
        if (strategy.equals(MUTATOR) && hasAccessibleConstructor(clazz, accessNonPublicConstructor, constructorType)) {
            return getAllDeclaredMethods(clazz, new ArrayList<>()).stream()
                    .filter(method -> !isWaitMethod(method))
                    .anyMatch(method -> isMutatorMethod(method, clazz));
        }
        return false;
    }

    public static <T> List<Method> getMutatorMethods(Class<T> clazz, List<String> blacklistedMethods) {
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> isMutatorMethod(method, clazz))
                .collect(Collectors.toList());
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
