package com.github.anhem.testpopulator;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class Carrier<T> {

    private final ObjectFactory objectFactory;
    private final Class<T> clazz;
    private final Parameter parameter;
    private final Type[] typeArguments;
    private final Type type;

    public static <T> Carrier<T> initialize(Class<T> clazz, ObjectFactory objectFactory) {
        return new Carrier<>(clazz, objectFactory);
    }

    private Carrier(Class<T> clazz, ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        this.clazz = clazz;
        this.parameter = null;
        this.typeArguments = null;
        this.type = null;
    }

    private Carrier(Type type, ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        this.clazz = null;
        this.parameter = null;
        this.typeArguments = null;
        this.type = type;
    }

    private Carrier(Class<T> clazz, Type[] typeArguments, ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        this.clazz = clazz;
        this.parameter = null;
        this.typeArguments = typeArguments;
        this.type = null;
    }

    private Carrier(Class<T> clazz, Parameter parameter, ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        this.clazz = clazz;
        this.parameter = parameter;
        this.typeArguments = null;
        this.type = null;
    }

    public <V> Carrier<V> toClassCarrier(Class<V> clazz) {
        return new Carrier<>(clazz, objectFactory);
    }

    public <V> Carrier<V> toTypeCarrier(Type type) {
        return new Carrier<>(type, objectFactory);
    }

    public Carrier<?> toClassTypeArgumentsCarrier(Type type, Type[] typeArguments) {
        return new Carrier<>((Class<?>) type, typeArguments, objectFactory);
    }

    public Carrier<?> toClassTypeCarrier(Parameter parameter) {
        return new Carrier<>(parameter.getType(), parameter, objectFactory);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public Type[] getTypeArguments() {
        return typeArguments;
    }

    public Type getType() {
        return type;
    }
}
