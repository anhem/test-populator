package com.github.anhem.testpopulator.internal.object;

import static com.github.anhem.testpopulator.internal.object.ObjectResult.EMPTY_OBJECT_RESULT;

public class ObjectFactoryVoid implements ObjectFactory {
    @Override
    public void constructor(Class<?> clazz, int expectedChildren) {
        //ignored
    }

    @Override
    public void setter(Class<?> clazz, int expectedChildren) {
        //ignored
    }

    @Override
    public void mutator(Class<?> clazz, int expectedChildren) {
        //ignored
    }

    @Override
    public void builder(Class<?> clazz, int expectedChildren) {
        //ignored
    }

    @Override
    public void method(String methodName, int expectedChildren) {
        //ignored
    }

    @Override
    public void set(Class<?> clazz) {
        //ignored
    }

    @Override
    public void setOf() {
        //ignored
    }

    @Override
    public void list(Class<?> clazz) {
        //ignored
    }

    @Override
    public void listOf() {
        //ignored
    }

    @Override
    public void map(Class<?> clazz) {
        //ignored
    }

    @Override
    public void mapOf() {
        //ignored
    }

    @Override
    public <T> void mapEntry(Class<T> clazz) {
        //ignored
    }

    @Override
    public void array(Class<?> clazz) {
        //ignored
    }

    @Override
    public <T> void value(T value) {
        //ignored
    }

    @Override
    public <T> void nullValue(Class<T> clazz) {
        //ignored
    }

    @Override
    public ObjectResult build() {
        return EMPTY_OBJECT_RESULT;
    }

    @Override
    public void writeToFile() {
        //ignored
    }
}
