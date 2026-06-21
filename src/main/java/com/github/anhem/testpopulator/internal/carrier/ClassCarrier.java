package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class ClassCarrier<T> extends Carrier {

    private final Class<T> clazz;
    private final String name;
    private final Map<String, Type> typeVariables;
    private final List<Type> argumentTypes;

    public ClassCarrier(
            Class<T> clazz,
            ObjectFactory objectFactory,
            List<String> visited,
            PopulateConfig populateConfig
    ) {
        this(clazz, null, objectFactory, visited, populateConfig, emptyMap());
    }

    public ClassCarrier(
            Class<T> clazz,
            String name,
            ObjectFactory objectFactory,
            List<String> visited,
            PopulateConfig populateConfig
    ) {
        this(clazz, name, objectFactory, visited, populateConfig, emptyMap());
    }

    public ClassCarrier(
            Class<T> clazz,
            String name,
            ObjectFactory objectFactory,
            List<String> visited,
            PopulateConfig populateConfig,
            Map<String, Type> typeVariables
    ) {
        this(clazz, name, objectFactory, visited, populateConfig, typeVariables, emptyList());
    }

    public ClassCarrier(
            Class<T> clazz,
            String name,
            ObjectFactory objectFactory,
            List<String> visited,
            PopulateConfig populateConfig,
            Map<String, Type> typeVariables,
            List<Type> argumentTypes
    ) {
        super(objectFactory, visited, populateConfig);
        this.clazz = clazz;
        this.name = name;
        this.typeVariables = typeVariables;
        this.argumentTypes = argumentTypes;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public List<Type> getArgumentTypes() {
        return argumentTypes;
    }

    public Type resolveType(Type type) {
        return type instanceof TypeVariable ? typeVariables.getOrDefault(((TypeVariable<?>) type).getName(), type) : type;
    }

    public <V> ClassCarrier<V> createChild(Class<V> clazz) {
        return new ClassCarrier<>(clazz, name, objectFactory, new ArrayList<>(visited), populateConfig, typeVariables);
    }

    public <V> ClassCarrier<V> createChild(Class<V> clazz, String name) {
        return new ClassCarrier<>(clazz, name, objectFactory, new ArrayList<>(visited), populateConfig, typeVariables);
    }

    @SuppressWarnings("unchecked")
    public <V> ClassCarrier<V> createChild(Type type, String name) {
        Type resolvedType = type instanceof TypeVariable ? typeVariables.getOrDefault(((TypeVariable<?>) type).getName(), type) : type;
        Class<V> targetClass = (Class<V>) resolveClass(resolvedType, populateConfig.getWildcardFallbackType());
        Map<String, Type> newTypeVariables = buildTypeVariables(targetClass, resolvedType, typeVariables);

        List<Type> argTypes = toArgumentTypes(resolvedType, targetClass, populateConfig.getWildcardFallbackType());
        List<Type> resolvedArgTypes = new ArrayList<>();
        for (Type arg : argTypes) {
            resolvedArgTypes.add(arg instanceof TypeVariable ? newTypeVariables.getOrDefault(((TypeVariable<?>) arg).getName(), arg) : arg);
        }

        return new ClassCarrier<>(targetClass, name, objectFactory, new ArrayList<>(visited), populateConfig, newTypeVariables, resolvedArgTypes);
    }

    public <V> ClassCarrier<V> createChild(Parameter parameter) {
        return createChild(parameter.getParameterizedType(), parameter.getName());
    }

    public <V> ClassCarrier<V> createChild(Parameter parameter, String name) {
        return createChild(parameter.getParameterizedType(), name);
    }

    public TypeCarrier toTypeCarrier(Type type) {
        return new TypeCarrier(type, name, objectFactory, visited, populateConfig);
    }

    public boolean alreadyVisited() {
        return populateConfig.isNullOnCircularDependency() && !isJavaBaseClass(clazz) && !addVisited();
    }

    public boolean hasConstructors() {
        return clazz.getConstructors().length > 0;
    }

    public boolean addVisited() {
        if (visited.contains(clazz.getName())) {
            return false;
        }
        visited.add(clazz.getName());
        return true;
    }
}


