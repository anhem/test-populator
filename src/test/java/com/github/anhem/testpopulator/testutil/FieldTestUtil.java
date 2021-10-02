package com.github.anhem.testpopulator.testutil;

import java.lang.reflect.Field;
import java.util.Arrays;

public class FieldTestUtil {

    public static Field getField(String fieldName, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.getName().equals(fieldName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find field with name " + fieldName));
    }
}
