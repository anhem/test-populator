package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.Strategy.BUILDER;
import static com.github.anhem.testpopulator.internal.util.ImmutablesUtil.getImmutablesGeneratedClass;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.getDeclaredMethods;

public class BuilderUtil {

    private BuilderUtil() {
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

    public static <T> List<Method> getMethodsForCustomBuilder(Class<T> clazz, List<String> blacklistedMethods) {
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> method.getReturnType().equals(clazz) && method.getParameterCount() > 0)
                .collect(Collectors.toList());
    }
}
