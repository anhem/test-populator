package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;

import static com.github.anhem.testpopulator.ObjectResult.EMPTY_OBJECT_RESULT;

class ObjectFactoryVoid implements ObjectFactory {
    @Override
    public void constructor(Class<?> clazz, int expectedChildren) {

    }

    @Override
    public void setter(Class<?> clazz, int expectedChildren) {

    }

    @Override
    public void builder(Class<?> clazz, int expectedChildren) {

    }

    @Override
    public void method(String methodName, int expectedChildren) {

    }

    @Override
    public void set(Class<?> clazz) {

    }

    @Override
    public void setOf() {

    }

    @Override
    public void list(Class<?> clazz) {

    }

    @Override
    public void listOf() {

    }

    @Override
    public void map(Class<?> clazz) {

    }

    @Override
    public void mapOf() {

    }

    @Override
    public void array(Class<?> clazz) {

    }

    @Override
    public <T> void overridePopulate(Class<?> clazz, OverridePopulate<T> overridePopulateValue) {

    }

    @Override
    public <T> void value(T value) {

    }

    @Override
    public ObjectResult build() {
        return EMPTY_OBJECT_RESULT;
    }

    @Override
    public void writeToFile() {

    }
}
