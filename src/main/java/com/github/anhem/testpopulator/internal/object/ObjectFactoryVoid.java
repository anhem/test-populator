package com.github.anhem.testpopulator.internal.object;

import static com.github.anhem.testpopulator.internal.object.ObjectResult.EMPTY_OBJECT_RESULT;

public class ObjectFactoryVoid implements ObjectFactory {
    @Override
    public <T> void constructor(Class<T> clazz, int expectedChildren) {

    }

    @Override
    public <T> void setter(Class<T> clazz, int expectedChildren) {

    }

    @Override
    public <T> void mutator(Class<T> clazz, int expectedChildren) {

    }

    @Override
    public <T> void builder(Class<T> clazz, int expectedChildren) {

    }

    @Override
    public void method(String methodName, int expectedChildren) {

    }

    @Override
    public <T> void set(Class<T> clazz) {

    }

    @Override
    public void setOf() {

    }

    @Override
    public <T> void list(Class<T> clazz) {

    }

    @Override
    public void listOf() {

    }

    @Override
    public <T> void map(Class<T> clazz) {

    }

    @Override
    public void mapOf() {

    }

    @Override
    public <T> void mapEntry(Class<T> clazz) {

    }

    @Override
    public <T> void array(Class<T> clazz) {

    }

    @Override
    public <T> void value(T value) {

    }

    @Override
    public <T> void nullValue(Class<T> clazz) {

    }

    @Override
    public ObjectResult build() {
        return EMPTY_OBJECT_RESULT;
    }

    @Override
    public void writeToFile() {

    }
}
