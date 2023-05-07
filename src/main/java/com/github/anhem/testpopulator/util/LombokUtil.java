package com.github.anhem.testpopulator.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.util.PopulateUtil.*;
import static java.util.Arrays.stream;

public class LombokUtil {
    private static final String CLEAR_METHOD_PATTERN = String.format("%s%s", "clear", MATCH_FIRST_CHARACTER_UPPERCASE);

    private LombokUtil() {
    }

    public static Map<Integer, List<Method>> getMethodsForLombokBuilderGroupedByInvokeOrder(Class<?> clazz, List<String> blacklistedMethods) {
        return getDeclaredMethods(clazz, blacklistedMethods).stream()
                .filter(method -> !isDeclaringJavaBaseClass(method))
                .collect(Collectors.groupingBy(LombokUtil::lombokMethodInvokeOrder));
    }

    public static int calculateExpectedChildren(Map<Integer, List<Method>> builderObjectMethodsGroupedByInvokeOrder) {
        return builderObjectMethodsGroupedByInvokeOrder.entrySet().stream()
                .filter(e -> e.getKey() > 0)
                .map(e -> e.getValue().size())
                .reduce(0, Integer::sum);
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
