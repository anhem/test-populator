package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.MethodType;
import com.github.anhem.testpopulator.config.Strategy;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.config.Strategy.STATIC_METHOD;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingInt;

public class StaticMethodUtil {

    private static final Comparator<Method> PARAMETER_COUNT_COMPARATOR = comparingInt(Method::getParameterCount);
    private static final Comparator<Method> SIMPLEST_METHOD_COMPARATOR = comparingDouble(StaticMethodUtil::getAverageParameterComplexity)
            .thenComparingInt(Method::getParameterCount);

    private StaticMethodUtil() {
    }

    public static <T> boolean isMatchingStaticMethodStrategy(Strategy strategy, Class<T> clazz) {
        if (strategy.equals(STATIC_METHOD)) {
            return getDeclaredMethods(clazz, new ArrayList<>()).stream()
                    .anyMatch(method -> isMatchingStaticMethod(method, clazz));
        }
        return false;
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

    private static <T> boolean isMatchingStaticMethod(Method method, Class<T> clazz) {
        return isStatic(method) &&
                method.getReturnType().equals(clazz) &&
                hasAtLeastOneParameter(method) &&
                !hasSelfReferencingParameter(method, clazz);
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
                .mapToInt(StaticMethodUtil::getParameterTypeScore)
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
        if (type.isPrimitive() ||
                Number.class.isAssignableFrom(type) ||
                type.equals(Boolean.class) ||
                type.equals(Character.class)
        ) {
            return 1;
        }
        if (type.equals(String.class)) {
            return 2;
        }
        if (type.getPackage() != null && (type.getPackage().getName().startsWith("java.util") || type.getPackage().getName().startsWith("java.time"))) {
            return 5;
        }
        if (type.isArray()) {
            return 10;
        }
        return 20;
    }
}
