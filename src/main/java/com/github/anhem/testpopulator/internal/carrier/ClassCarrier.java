package com.github.anhem.testpopulator.internal.carrier;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;

public class ClassCarrier<T> extends Carrier {

    private final Class<T> clazz;
    private final String name;
    private final Map<String, Type> typeVariables;

    public ClassCarrier(
            Class<T> clazz,
            ObjectFactory objectFactory,
            List<String> visited,
            PopulateConfig populateConfig
    ) {
        this(clazz, null, objectFactory, visited, populateConfig, Collections.emptyMap());
    }

    public ClassCarrier(
            Class<T> clazz,
            String name,
            ObjectFactory objectFactory,
            List<String> visited,
            PopulateConfig populateConfig
    ) {
        this(clazz, name, objectFactory, visited, populateConfig, Collections.emptyMap());
    }

    public ClassCarrier(
            Class<T> clazz,
            String name,
            ObjectFactory objectFactory,
            List<String> visited,
            PopulateConfig populateConfig,
            Map<String, Type> typeVariables
    ) {
        super(objectFactory, visited, populateConfig);
        this.clazz = clazz;
        this.name = name;
        this.typeVariables = typeVariables;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public Type resolveType(Type type) {
        return type instanceof TypeVariable ? typeVariables.getOrDefault(((TypeVariable<?>) type).getName(), type) : type;
    }

    public <V> ClassCarrier<V> toClassCarrier(Class<V> clazz) {
        return new ClassCarrier<>(clazz, name, objectFactory, new ArrayList<>(visited), populateConfig, typeVariables);
    }

    public <V> ClassCarrier<V> toClassCarrier(Class<V> clazz, String name) {
        return new ClassCarrier<>(clazz, name, objectFactory, new ArrayList<>(visited), populateConfig, typeVariables);
    }

    @SuppressWarnings("unchecked")
    public <V> ClassCarrier<V> toClassCarrier(Type type, String name) {
        Type resolvedType = type instanceof TypeVariable ? typeVariables.getOrDefault(((TypeVariable<?>) type).getName(), type) : type;
        Class<V> targetClass = (Class<V>) resolveClass(resolvedType, populateConfig.getWildcardFallbackType());
        Map<String, Type> newTypeVariables = buildTypeVariables(targetClass, resolvedType, typeVariables);
        return new ClassCarrier<>(targetClass, name, objectFactory, new ArrayList<>(visited), populateConfig, newTypeVariables);
    }

    @SuppressWarnings("unchecked")
    public <V> ClassCarrier<V> toClassCarrier(Parameter parameter) {
        return toClassCarrier(parameter.getParameterizedType(), parameter.getName());
    }

    @SuppressWarnings("unchecked")
    public <V> ClassCarrier<V> toClassCarrier(Parameter parameter, String name) {
        return toClassCarrier(parameter.getParameterizedType(), name);
    }

    public TypeCarrier toTypeCarrier(Type type) {
        return new TypeCarrier(type, name, objectFactory, visited, populateConfig);
    }

    @SuppressWarnings("unchecked")
    public <V> CollectionCarrier<V> toCollectionCarrier(Parameter parameter) {
        return toCollectionCarrier(parameter.getParameterizedType(), parameter.getName());
    }

    @SuppressWarnings("unchecked")
    public <V> CollectionCarrier<V> toCollectionCarrier(Parameter parameter, String name) {
        return toCollectionCarrier(parameter.getParameterizedType(), name);
    }

    @SuppressWarnings("unchecked")
    public <V> CollectionCarrier<V> toCollectionCarrier(Type type, String name) {
        Type resolvedType = type instanceof TypeVariable ? typeVariables.getOrDefault(((TypeVariable<?>) type).getName(), type) : type;
        Class<V> targetClass = (Class<V>) resolveClass(resolvedType, populateConfig.getWildcardFallbackType());
        Map<String, Type> newTypeVariables = buildTypeVariables(targetClass, resolvedType, typeVariables);

        List<Type> argTypes = toArgumentTypes(resolvedType, targetClass, populateConfig.getWildcardFallbackType());
        List<Type> resolvedArgTypes = new ArrayList<>();
        for (Type arg : argTypes) {
            resolvedArgTypes.add(arg instanceof TypeVariable ? newTypeVariables.getOrDefault(((TypeVariable<?>) arg).getName(), arg) : arg);
        }

        return new CollectionCarrier<>(
                targetClass,
                name,
                resolvedArgTypes.toArray(new Type[0]),
                objectFactory,
                visited,
                populateConfig,
                newTypeVariables
        );
    }

    public <V> CollectionCarrier<V> toCollectionCarrier(Class<V> clazz) {
        return new CollectionCarrier<>(
                clazz,
                name,
                toArgumentTypes(null, clazz, populateConfig.getWildcardFallbackType()).toArray(new Type[0]),
                objectFactory,
                visited,
                populateConfig,
                typeVariables
        );
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


