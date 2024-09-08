package com.github.anhem.testpopulator;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class ClassCarrier<T> extends Carrier {

    private final Class<T> clazz;


    public ClassCarrier(Class<T> clazz, ObjectFactory objectFactory) {
        super(objectFactory);
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public <V> ClassCarrier<V> toClassCarrier(Class<V> clazz) {
        return new ClassCarrier<>(clazz, objectFactory);
    }

    @SuppressWarnings("unchecked")
    public <V> ClassCarrier<V> toClassCarrier(Parameter parameter) {
        return (ClassCarrier<V>) new ClassCarrier<>(parameter.getType(), objectFactory);
    }

    public TypeCarrier toTypeCarrier(Type type) {
        return new TypeCarrier(type, objectFactory);
    }

    @SuppressWarnings("unchecked")
    public <V> CollectionCarrier<V> toCollectionCarrier(Parameter parameter) {
        return new CollectionCarrier<>((Class<V>) parameter.getType(), parameter, objectFactory);
    }
}


