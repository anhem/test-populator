package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeCarrier extends Carrier {

    private final Type type;

    public TypeCarrier(Type type, ObjectFactory objectFactory, List<String> visited, PopulateConfig populateConfig) {
        super(objectFactory, visited, populateConfig);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> ClassCarrier<T> toClassCarrier(Type type) {
        return new ClassCarrier<>((Class<T>) type, objectFactory, new ArrayList<>(visited), populateConfig);
    }
}
