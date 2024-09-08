package com.github.anhem.testpopulator;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

import static com.github.anhem.testpopulator.PopulateUtil.toArgumentTypes;

public class CollectionCarrier<T> extends ClassCarrier<T> {

    private final Parameter parameter;
    private final Type[] typeArguments;

    public CollectionCarrier(Class<T> clazz, Type[] typeArguments, ObjectFactory objectFactory) {
        super(clazz, objectFactory);
        this.typeArguments = typeArguments;
        this.parameter = null;
    }

    public CollectionCarrier(Class<T> clazz, Parameter parameter, ObjectFactory objectFactory) {
        super(clazz, objectFactory);
        this.parameter = parameter;
        this.typeArguments = null;
    }

    public List<Type> getArgumentTypes() {
        return toArgumentTypes(parameter, typeArguments);
    }
}
