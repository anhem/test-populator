package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.anhem.testpopulator.PopulateUtil.*;
import static java.lang.String.format;
import static java.util.Arrays.stream;

public class PopulateFactory {

    static final String MISSING_STRATEGIES = "No strategy order defined";
    static final String MISSING_COLLECTION_TYPE = "Failed to find type for collection %s";
    static final String NO_MATCHING_STRATEGY = "Unable to populate %s. No matching strategy. Try another strategy or override population for this class";
    static final String FAILED_TO_POPULATE_CLASS = "Failed while populating %s for class %s";
    static final String FAILED_TO_SET_FIELD = "Failed to set field %s in object of class %s ";
    static final String FAILED_TO_CREATE_INSTANCE = "Failed to create new instance of %s";

    private final PopulateConfig populateConfig;
    private final ValueFactory valueFactory;
    private final Map<? extends Class<?>, OverridePopulate<?>> overridePopulate;

    public PopulateFactory() {
        this(PopulateConfig.builder().build());
    }

    public PopulateFactory(PopulateConfig populateConfig) {
        this.populateConfig = populateConfig;
        valueFactory = new ValueFactory(populateConfig.useRandomValues());
        overridePopulate = populateConfig.getOverridePopulate();
        if (populateConfig.getStrategyOrder().isEmpty()) {
            throw new IllegalArgumentException(MISSING_STRATEGIES);
        }
    }

    public <T> T populate(Class<T> clazz) {
        return populateWithOverrides(clazz);
    }

    private <T> T populateWithOverrides(Class<T> clazz) {
        return populateWithOverrides(clazz, null, null);
    }

    private <T> T populateWithOverrides(Class<T> clazz, Parameter parameter, Type[] typeArguments) {
        if (overridePopulate.containsKey(clazz)) {
            return getOverridePopulateValue(clazz, overridePopulate);
        }
        if (clazz.isArray()) {
            return continuePopulateForArray(clazz);
        }
        if (isCollection(clazz)) {
            return continuePopulateForCollection(clazz, parameter, typeArguments);
        }
        if (isValue(clazz)) {
            return valueFactory.createValue(clazz);
        }
        return continuePopulateWithStrategies(clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForArray(Class<T> clazz) {
        Class<?> componentType = clazz.getComponentType();
        Object value = populateWithOverrides(componentType);
        Object array = Array.newInstance(componentType, 1);
        Array.set(array, 0, value);
        return (T) array;
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForCollection(Class<T> clazz, Parameter parameter, Type[] typeArguments) {
        List<Type> argumentTypes = toArgumentTypes(parameter, typeArguments);
        if (isMap(clazz)) {
            Object key = populateWithOverrides((Class<?>) argumentTypes.get(0));
            Object value = populateWithOverrides((Class<?>) argumentTypes.get(1));
            return (T) Map.of(key, value);
        }
        if (isSet(clazz)) {
            Object value = populateWithOverrides((Class<?>) argumentTypes.get(0));
            return (T) Set.of(value);
        }
        if (isCollection(clazz)) {
            Object value = populateWithOverrides((Class<?>) argumentTypes.get(0));
            return (T) List.of(value);
        }
        throw new PopulateException(format(MISSING_COLLECTION_TYPE, clazz.getTypeName()));
    }

    private <T> T continuePopulateWithStrategies(Class<?> clazz) {
        for (Strategy strategy : populateConfig.getStrategyOrder()) {
            if (isMatchingFieldStrategy(strategy, clazz)) {
                return continuePopulateUsingFields(clazz);
            }
            if (isMatchingConstructorStrategy(strategy, clazz)) {
                return continuePopulateUsingConstructor(clazz);
            }
        }
        throw new PopulateException(format(NO_MATCHING_STRATEGY, clazz.getName()));
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingConstructor(Class<?> clazz) {
        Constructor<?> constructor = getLargestPublicConstructor(clazz);
        Object[] arguments = stream(constructor.getParameters())
                .map(parameter -> {
                    try {
                        return populateWithOverrides(parameter.getType(), parameter, null);
                    } catch (Exception e) {
                        throw new PopulateException(format(FAILED_TO_POPULATE_CLASS, parameter.getName(), clazz.getName()), e);
                    }
                }).toArray();
        try {
            return (T) constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_INSTANCE, constructor.getName()), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingFields(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T objectOfClass = (T) constructor.newInstance();
            getDeclaredFields(clazz).stream()
                    .filter(field -> !Modifier.isFinal(field.getModifiers()))
                    .forEach(field -> {
                        try {
                            field.setAccessible(true);
                            if (isCollection(field.getType())) {
                                Type[] typeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                                field.set(objectOfClass, populateWithOverrides(field.getType(), null, typeArguments));
                            } else {
                                field.set(objectOfClass, populateWithOverrides(field.getType()));
                            }
                        } catch (Exception e) {
                            throw new PopulateException(format(FAILED_TO_SET_FIELD, field.getName(), objectOfClass.getClass().getName()), e);
                        }
                    });
            return objectOfClass;
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_INSTANCE, clazz.getName()), e);
        }
    }
}
