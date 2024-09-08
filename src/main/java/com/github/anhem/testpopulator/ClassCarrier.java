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

    public ClassCarrier<?> toClassCarrier(Parameter parameter) {
        return new ClassCarrier<>(parameter.getType(), objectFactory);
    }

    public TypeCarrier toTypeCarrier(Type type) {
        return new TypeCarrier(type, objectFactory);
    }

    public CollectionCarrier<?> toCollectionCarrier(Parameter parameter) {
        Class<?> clazz = parameter.getType();
        return new CollectionCarrier<>(clazz, parameter, objectFactory);
    }
}


