package com.github.anhem.testpopulator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.PopulateUtil.*;
import static java.util.Arrays.stream;

public class LombokUtil {
    private static final String CLEAR_METHOD_PATTERN = String.format("%s%s", "clear", MATCH_FIRST_CHARACTER_UPPERCASE);

    private LombokUtil() {
    }

    static Map<Integer, List<Method>> getMethodsForLombokBuilderGroupedByInvokeOrder(Class<?> clazz) {
        return getDeclaredMethods(clazz).stream()
                .filter(method -> !isDeclaringJavaBaseClass(method))
                .filter(method -> !isBlackListedMethod(method))
                .collect(Collectors.groupingBy(LombokUtil::lombokMethodInvokeOrder));
    }

    private static int lombokMethodInvokeOrder(Method method) {
        if (stream(method.getParameterTypes()).anyMatch(PopulateUtil::isCollection)) {
            return 3;
        }
        if (isClearMethod(method)) {
            return 2;
        }
        if (hasAtLeastOneParameter(method)) {
            return 1;
        }
        return 0;
    }

    private static boolean isClearMethod(Method method) {
        return method.getName().matches(CLEAR_METHOD_PATTERN) && method.getParameterTypes().length == 0;
    }

}
