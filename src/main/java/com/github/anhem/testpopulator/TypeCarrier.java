package com.github.anhem.testpopulator;

import java.lang.reflect.Type;

public class TypeCarrier extends Carrier {

    private final Type type;

    public TypeCarrier(Type type, ObjectFactory objectFactory) {
        super(objectFactory);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> ClassCarrier<T> toClassCarrier(Type type) {
        Class<T> clazz = (Class<T>) type;
        return new ClassCarrier<>(clazz, objectFactory);
    }
}