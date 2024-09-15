package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.object.ObjectFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class TypeCarrier extends Carrier {

    private final Type type;

    public TypeCarrier(Type type, ObjectFactory objectFactory, List<String> visited) {
        super(objectFactory, visited);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> ClassCarrier<T> toClassCarrier(Type type) {
        return new ClassCarrier<>((Class<T>) type, objectFactory, new ArrayList<>(visited));
    }
}
