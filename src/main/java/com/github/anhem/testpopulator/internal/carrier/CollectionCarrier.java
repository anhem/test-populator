package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static com.github.anhem.testpopulator.internal.util.PopulateUtil.toArgumentTypes;

public class CollectionCarrier<T> extends ClassCarrier<T> {

    private final List<Type> argumentTypes;

    public CollectionCarrier(Class<T> clazz, Type[] typeArguments, ObjectFactory objectFactory, List<String> visited, PopulateConfig populateConfig) {
        super(clazz, objectFactory, visited, populateConfig);
        this.argumentTypes = Arrays.asList(typeArguments);
    }

    public CollectionCarrier(Class<T> clazz, Parameter parameter, ObjectFactory objectFactory, List<String> visited, PopulateConfig populateConfig) {
        super(clazz, objectFactory, visited, populateConfig);
        this.argumentTypes = toArgumentTypes(parameter);
    }

    public List<Type> getArgumentTypes() {
        return argumentTypes;
    }
}
