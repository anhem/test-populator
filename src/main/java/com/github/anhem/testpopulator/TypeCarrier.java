package com.github.anhem.testpopulator;

import java.lang.reflect.Type;

class TypeCarrier extends Carrier {

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
        return new ClassCarrier<>((Class<T>) type, objectFactory);
    }
}
