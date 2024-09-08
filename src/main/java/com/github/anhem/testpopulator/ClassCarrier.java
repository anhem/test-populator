package com.github.anhem.testpopulator;

public class ClassCarrier<T> {

    private final Class<T> clazz;
    private final ObjectFactory objectFactory;

    public ClassCarrier(Class<T> clazz, ObjectFactory objectFactory) {
        this.clazz = clazz;
        this.objectFactory = objectFactory;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }
}
