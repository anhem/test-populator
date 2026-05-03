package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.github.anhem.testpopulator.internal.util.PopulateUtil.toArgumentTypes;

public class ClassCarrier<T> extends Carrier {

    private final Class<T> clazz;
    private final String name;

    public ClassCarrier(
            Class<T> clazz,
            ObjectFactory objectFactory,
            List<String> visited,
            PopulateConfig populateConfig
    ) {
        this(clazz, null, objectFactory, visited, populateConfig);
    }

    public ClassCarrier(
            Class<T> clazz,
            String name,
            ObjectFactory objectFactory,
            List<String> visited,
            PopulateConfig populateConfig
    ) {
        super(objectFactory, visited, populateConfig);
        this.clazz = clazz;
        this.name = name;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public <V> ClassCarrier<V> toClassCarrier(Class<V> clazz) {
        return new ClassCarrier<>(clazz, name, objectFactory, new ArrayList<>(visited), populateConfig);
    }

    public <V> ClassCarrier<V> toClassCarrier(Class<V> clazz, String name) {
        return new ClassCarrier<>(clazz, name, objectFactory, new ArrayList<>(visited), populateConfig);
    }

    @SuppressWarnings("unchecked")
    public <V> ClassCarrier<V> toClassCarrier(Parameter parameter) {
        return (ClassCarrier<V>) new ClassCarrier<>(
                parameter.getType(),
                parameter.getName(),
                objectFactory,
                new ArrayList<>(visited),
                populateConfig
        );
    }

    @SuppressWarnings("unchecked")
    public <V> ClassCarrier<V> toClassCarrier(Parameter parameter, String name) {
        return (ClassCarrier<V>) new ClassCarrier<>(parameter.getType(), name, objectFactory, new ArrayList<>(visited), populateConfig);
    }

    public TypeCarrier toTypeCarrier(Type type) {
        return new TypeCarrier(type, name, objectFactory, visited, populateConfig);
    }

    @SuppressWarnings("unchecked")
    public <V> CollectionCarrier<V> toCollectionCarrier(Parameter parameter) {
        return new CollectionCarrier<>((Class<V>) parameter.getType(), parameter, objectFactory, visited, populateConfig);
    }

    @SuppressWarnings("unchecked")
    public <V> CollectionCarrier<V> toCollectionCarrier(Parameter parameter, String name) {
        return new CollectionCarrier<>(
                (Class<V>) parameter.getType(),
                name,
                toArgumentTypes(parameter).toArray(new Type[0]),
                objectFactory,
                visited,
                populateConfig
        );
    }

    public boolean addVisited() {
        if (visited.contains(clazz.getName())) {
            return false;
        }
        visited.add(clazz.getName());
        return true;
    }
}


