package com.github.anhem.testpopulator.internal.object;

public interface ObjectFactory {

    <T> void constructor(Class<T> clazz, int expectedChildren);

    <T> void setter(Class<T> clazz, int expectedChildren);

    <T> void mutator(Class<T> clazz, int expectedChildren);

    <T> void builder(Class<T> clazz, int expectedChildren);

    void method(String methodName, int expectedChildren);

    <T> void set(Class<T> clazz);

    void setOf();

    <T> void list(Class<T> clazz);

    void listOf();

    <T> void map(Class<T> clazz);

    void mapOf();

    <T> void mapEntry(Class<T> clazz);

    <T> void array(Class<T> clazz);

    <T> void value(T value);

    <T> void nullValue(Class<T> clazz);

    ObjectResult build();

    void writeToFile();

}
