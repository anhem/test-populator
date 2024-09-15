package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.internal.object.ObjectFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class Carrier {

    protected final ObjectFactory objectFactory;
    protected final List<String> visited;

    public static <T> ClassCarrier<T> initialize(Class<T> clazz, ObjectFactory objectFactory) {
        return new ClassCarrier<>(clazz, objectFactory, new ArrayList<>());
    }

    protected Carrier(ObjectFactory objectFactory, List<String> visited) {
        this.objectFactory = objectFactory;
        this.visited = visited;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> CollectionCarrier<T> toCollectionCarrier(Type type, Type[] typeArguments) {
        return new CollectionCarrier<>((Class<T>) type, typeArguments, objectFactory, visited);
    }
}
