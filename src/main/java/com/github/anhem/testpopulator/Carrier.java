package com.github.anhem.testpopulator;

import java.lang.reflect.Type;

public abstract class Carrier {

    protected final ObjectFactory objectFactory;

    public static <T> ClassCarrier<T> initialize(Class<T> clazz, ObjectFactory objectFactory) {
        return new ClassCarrier<>(clazz, objectFactory);
    }

    public Carrier(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public CollectionCarrier<?> toCollectionCarrier(Type type, Type[] typeArguments) {
        Class<?> clazz = (Class<?>) type;
        return new CollectionCarrier<>(clazz, typeArguments, objectFactory);
    }
}
