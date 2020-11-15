package com.github.anhem.testpopulator.testutil;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodTestUtil {

    public static Method getMethod(String methodName, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find method with name " + methodName));
    }
}
