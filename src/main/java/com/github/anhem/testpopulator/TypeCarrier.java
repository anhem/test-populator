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

    public ClassCarrier<?> toClassCarrier(Type type) {
        Class<?> clazz = (Class<?>) type;
        return new ClassCarrier<>(clazz, objectFactory);
    }
}
