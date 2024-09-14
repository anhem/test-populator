package com.github.anhem.testpopulator;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class ClassCarrier<T> extends Carrier {

    private final Class<T> clazz;

    public ClassCarrier(Class<T> clazz, ObjectFactory objectFactory, List<String> visited) {
        super(objectFactory, visited);
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public <V> ClassCarrier<V> toClassCarrier(Class<V> clazz) {
        return new ClassCarrier<>(clazz, objectFactory, new ArrayList<>(visited));
    }

    @SuppressWarnings("unchecked")
    public <V> ClassCarrier<V> toClassCarrier(Parameter parameter) {
        return (ClassCarrier<V>) new ClassCarrier<>(parameter.getType(), objectFactory, new ArrayList<>(visited));
    }

    public TypeCarrier toTypeCarrier(Type type) {
        return new TypeCarrier(type, objectFactory, visited);
    }

    @SuppressWarnings("unchecked")
    public <V> CollectionCarrier<V> toCollectionCarrier(Parameter parameter) {
        return new CollectionCarrier<>((Class<V>) parameter.getType(), parameter, objectFactory, visited);
    }

    public boolean addVisited() {
        if (visited.contains(clazz.getName())) {
            return false;
        }
        visited.add(clazz.getName());
        return true;
    }
}


