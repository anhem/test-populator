package com.github.anhem.testpopulator.internal.object;

import static com.github.anhem.testpopulator.internal.object.ObjectResult.EMPTY_OBJECT_RESULT;

public class ObjectFactoryVoid implements ObjectFactory {
    @Override
    public <T> void constructor(Class<T> clazz, int expectedChildren) {
        //ignored
    }

    @Override
    public <T> void setter(Class<T> clazz, int expectedChildren) {
        //ignored
    }

    @Override
    public <T> void mutator(Class<T> clazz, int expectedChildren) {
        //ignored
    }

    @Override
    public <T> void builder(Class<T> clazz, int expectedChildren, String builderMethodName, String buildMethodName) {
        //ignored
    }

    @Override
    public void method(String methodName, int expectedChildren) {
        //ignored
    }

    @Override
    public <T> void set(Class<T> clazz) {
        //ignored
    }

    @Override
    public void setOf() {
        //ignored
    }

    @Override
    public <T> void list(Class<T> clazz) {
        //ignored
    }

    @Override
    public void listOf() {
        //ignored
    }

    @Override
    public <T> void map(Class<T> clazz) {
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
    public <T> void array(Class<T> clazz) {
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
