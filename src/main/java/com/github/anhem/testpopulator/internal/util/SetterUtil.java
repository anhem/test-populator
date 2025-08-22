package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.Strategy.SETTER;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;

public class SetterUtil {

    public static <T> boolean isMatchingSetterStrategy(Strategy strategy, Class<T> clazz, List<String> setterPrefixes, boolean accessNonPublicConstructor) {
        if (strategy.equals(SETTER) && hasConstructorWithoutArguments(clazz, accessNonPublicConstructor)) {
            List<String> setterMethodFormats = getMethodFormats(setterPrefixes);
            return getAllDeclaredMethods(clazz, new ArrayList<>()).stream()
                    .filter(method -> !isWaitMethod(method))
                    .anyMatch(method -> isSetterMethod(method, setterMethodFormats));
        }
        return false;
    }

    public static <T> List<Method> getSetterMethods(Class<T> clazz, List<String> blacklistedMethods, List<String> setterPrefixes) {
        List<String> setterMethodFormats = getMethodFormats(setterPrefixes);
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> !isWaitMethod(method))
                .filter(method -> isSetterMethod(method, setterMethodFormats))
                .collect(Collectors.toList());
    }

    private static List<String> getMethodFormats(List<String> setterPrefixes) {
        return setterPrefixes.stream()
                .map(PopulateUtil::getMethodFormat)
                .collect(Collectors.toList());
    }

    private static boolean isSetterMethod(Method method, List<String> setterMethodFormats) {
        return setterMethodFormats.stream().anyMatch(setMethodFormat -> isSetterMethod(method, setMethodFormat));
    }

    private static boolean isSetterMethod(Method method, String setMethodFormat) {
        if (setMethodFormat.isBlank()) {
            return method.getReturnType().equals(void.class) && method.getParameterCount() == 1;
        }
        return method.getName().matches(setMethodFormat) && method.getReturnType().equals(void.class) && method.getParameterCount() == 1 && !isStatic(method);
    }
}
