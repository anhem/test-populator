package com.github.anhem.testpopulator;

import java.lang.reflect.Type;

abstract class Carrier {

    protected final ObjectFactory objectFactory;

    public static <T> ClassCarrier<T> initialize(Class<T> clazz, ObjectFactory objectFactory) {
        return new ClassCarrier<>(clazz, objectFactory);
    }

    protected Carrier(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> CollectionCarrier<T> toCollectionCarrier(Type type, Type[] typeArguments) {
        Class<T> clazz = (Class<T>) type;
        return new CollectionCarrier<>(clazz, typeArguments, objectFactory);
    }
}
