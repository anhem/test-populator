package com.github.anhem.testpopulator.object;

import com.github.anhem.testpopulator.config.OverridePopulate;

public interface ObjectFactory {

    void constructor(Class<?> clazz, int expectedChildren);

    void setter(Class<?> clazz, int expectedChildren);

    void builder(Class<?> clazz, int expectedChildren);

    void method(String methodName, int expectedChildren);

    void set(Class<?> clazz);

    void setOf();

    void list(Class<?> clazz);

    void listOf();

    void map(Class<?> clazz);

    void mapOf();

    void array(Class<?> clazz);

    <T> void overridePopulate(Class<?> clazz, OverridePopulate<T> overridePopulateValue);

    <T> void value(T value);

    ObjectResult build();

    void writeToFile();
}
