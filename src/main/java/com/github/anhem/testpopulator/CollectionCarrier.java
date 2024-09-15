package com.github.anhem.testpopulator;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static com.github.anhem.testpopulator.util.PopulateUtil.toArgumentTypes;

public class CollectionCarrier<T> extends ClassCarrier<T> {

    private final List<Type> argumentTypes;

    public CollectionCarrier(Class<T> clazz, Type[] typeArguments, ObjectFactory objectFactory, List<String> visited) {
        super(clazz, objectFactory, visited);
        this.argumentTypes = Arrays.asList(typeArguments);
    }

    public CollectionCarrier(Class<T> clazz, Parameter parameter, ObjectFactory objectFactory, List<String> visited) {
        super(clazz, objectFactory, visited);
        this.argumentTypes = toArgumentTypes(parameter);
    }

    public List<Type> getArgumentTypes() {
        return argumentTypes;
    }
}
