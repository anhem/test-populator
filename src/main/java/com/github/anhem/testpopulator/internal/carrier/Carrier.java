package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class Carrier {

    protected final ObjectFactory objectFactory;
    protected final List<String> visited;
    protected final PopulateConfig populateConfig;

    public static <T> ClassCarrier<T> initialize(Class<T> clazz, ObjectFactory objectFactory, PopulateConfig populateConfig) {
        return new ClassCarrier<>(clazz, objectFactory, new ArrayList<>(), populateConfig);
    }

    protected Carrier(ObjectFactory objectFactory, List<String> visited, PopulateConfig populateConfig) {
        this.objectFactory = objectFactory;
        this.visited = visited;
        this.populateConfig = populateConfig;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public PopulateConfig getPopulateConfig() {
        return populateConfig;
    }

    @SuppressWarnings("unchecked")
    public <T> ClassCarrier<T> createChild(Type type, Type[] typeArguments) {
        return new ClassCarrier<>((Class<T>) type, null, objectFactory, visited, populateConfig, java.util.Collections.emptyMap(), java.util.Arrays.asList(typeArguments));
    }

    @SuppressWarnings("unchecked")
    public <T> ClassCarrier<T> createChild(Type type, String name, Type[] typeArguments) {
        return new ClassCarrier<>((Class<T>) type, name, objectFactory, visited, populateConfig, java.util.Collections.emptyMap(), java.util.Arrays.asList(typeArguments));
    }
}
