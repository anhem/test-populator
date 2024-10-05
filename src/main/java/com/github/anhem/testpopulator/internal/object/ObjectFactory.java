package com.github.anhem.testpopulator.internal.object;

public interface ObjectFactory {

    void constructor(Class<?> clazz, int expectedChildren);

    void setter(Class<?> clazz, int expectedChildren);

    void mutator(Class<?> clazz, int expectedChildren);

    void builder(Class<?> clazz, int expectedChildren);

    void method(String methodName, int expectedChildren);

    void set(Class<?> clazz);

    void setOf();

    void list(Class<?> clazz);

    void listOf();

    void map(Class<?> clazz);

    void mapOf();

    void array(Class<?> clazz);

    <T> void value(T value);

    <T> void nullValue(Class<T> clazz);

    ObjectResult build();

    void writeToFile();

}
