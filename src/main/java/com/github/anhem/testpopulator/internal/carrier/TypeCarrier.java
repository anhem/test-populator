package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeCarrier extends Carrier {

    private final Type type;
    private final String name;

    public TypeCarrier(Type type, ObjectFactory objectFactory, List<String> visited, PopulateConfig populateConfig) {
        this(type, null, objectFactory, visited, populateConfig);
    }

    public TypeCarrier(Type type, String name, ObjectFactory objectFactory, List<String> visited, PopulateConfig populateConfig) {
        super(objectFactory, visited, populateConfig);
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public <T> ClassCarrier<T> toClassCarrier(Type type) {
        return new ClassCarrier<>((Class<T>) type, name, objectFactory, new ArrayList<>(visited), populateConfig);
    }
}
