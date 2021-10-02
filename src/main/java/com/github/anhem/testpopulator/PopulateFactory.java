package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.*;
import java.util.*;

import static com.github.anhem.testpopulator.ImmutablesUtil.getMethodsForImmutablesBuilder;
import static com.github.anhem.testpopulator.LombokUtil.getMethodsForLombokBuilderGroupedByInvokeOrder;
import static com.github.anhem.testpopulator.PopulateUtil.*;
import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static java.lang.String.format;
import static java.util.Arrays.stream;

/**
 * Factory for creating populated objects from classes
 */
public class PopulateFactory {

    static final String MISSING_BUILDER_PATTERN = "%s strategy configured, but no builderPattern specified. Should be one of %s";
    static final String MISSING_COLLECTION_TYPE = "Failed to find type for collection %s";
    static final String NO_MATCHING_STRATEGY = "Unable to populate %s. No matching strategy found. Tried with %s. Try another strategy or override population for this class";
    static final String FAILED_TO_SET_FIELD = "Failed to set field %s in object of class %s";
    static final String FAILED_TO_CALL_METHOD = "Failed to call method %s in object of class %s";
    static final String FAILED_TO_CREATE_OBJECT = "Failed to create object of %s using %s strategy";

    private static final String BUILD_METHOD = "build";
    static final String BUILDER_METHOD = "builder";

    private final PopulateConfig populateConfig;
    private final ValueFactory valueFactory;
    private final Map<? extends Class<?>, OverridePopulate<?>> overridePopulate;

    /**
     * Create new instance of PopulateFactory with default configuration
     */
    public PopulateFactory() {
        this(PopulateConfig.builder().build());
    }

    /**
     * Create new instance of PopulateFactory
     *
     * @param populateConfig configuration properties for PopulateFactory
     */
    public PopulateFactory(PopulateConfig populateConfig) {
        this.populateConfig = populateConfig;
        valueFactory = new ValueFactory(populateConfig.useRandomValues());
        overridePopulate = populateConfig.getOverridePopulate();
        if (populateConfig.getStrategyOrder().contains(BUILDER) && populateConfig.getBuilderPattern() == null) {
            throw new IllegalArgumentException(format(MISSING_BUILDER_PATTERN, BUILDER, Arrays.toString(BuilderPattern.values())));
        }
    }

    /**
     * Call to create a fully populated object from a class
     *
     * @param clazz Class that should be populated
     * @return object of clazz
     */
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
        if (isMapEntry(clazz)) {
            return continuePopulateForMapEntry(parameter, typeArguments);
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
            Object key = continuePopulateWithType(argumentTypes.get(0));
            Object value = continuePopulateWithType(argumentTypes.get(1));
            return (T) Map.of(key, value);
        }
        if (isSet(clazz)) {
            Object value = continuePopulateWithType(argumentTypes.get(0));
            return (T) Set.of(value);
        }
        if (isCollection(clazz)) {
            Object value = continuePopulateWithType(argumentTypes.get(0));
            return (T) List.of(value);
        }
        throw new PopulateException(format(MISSING_COLLECTION_TYPE, clazz.getTypeName()));
    }

    private Object continuePopulateWithType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return populateWithOverrides((Class<?>) parameterizedType.getRawType(), null, parameterizedType.getActualTypeArguments());
        }
        return populateWithOverrides((Class<?>) type);
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForMapEntry(Parameter parameter, Type[] typeArguments) {
        List<Type> argumentTypes = toArgumentTypes(parameter, typeArguments);
        Object key = populateWithOverrides((Class<?>) argumentTypes.get(0));
        Object value = populateWithOverrides((Class<?>) argumentTypes.get(1));
        return (T) new AbstractMap.SimpleEntry<>(key, value);
    }

    private <T> T continuePopulateWithStrategies(Class<T> clazz) {
        for (Strategy strategy : populateConfig.getStrategyOrder()) {
            if (isMatchingConstructorStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructor())) {
                return continuePopulateUsingConstructor(clazz);
            }
            if (isMatchingSetterStrategy(strategy, clazz, populateConfig.getSetterPrefix(), populateConfig.canAccessNonPublicConstructor())) {
                return continuePopulateUsingSetters(clazz);
            }
            if (isMatchingFieldStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructor())) {
                return continuePopulateUsingFields(clazz);
            }
            if (isMatchingBuilderStrategy(strategy, clazz)) {
                return continuePopulateUsingBuilder(clazz);
            }
        }
        throw new PopulateException(format(NO_MATCHING_STRATEGY, clazz.getName(), populateConfig.getStrategyOrder()));
    }

    private <T> T continuePopulateUsingConstructor(Class<T> clazz) {
        try {
            Constructor<T> constructor = getLargestConstructor(clazz, populateConfig.canAccessNonPublicConstructor());
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructor());
            Object[] arguments = stream(constructor.getParameters())
                    .map(parameter -> populateWithOverrides(parameter.getType(), parameter, null))
                    .toArray();
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), CONSTRUCTOR), e);
        }
    }

    private <T> T continuePopulateUsingFields(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructor());
            T objectOfClass = constructor.newInstance();
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
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), FIELD), e);
        }
    }

    private <T> T continuePopulateUsingSetters(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructor());
            T objectOfClass = constructor.newInstance();
            getDeclaredMethods(clazz).stream()
                    .filter(method -> isSetterMethod(method, populateConfig.getSetterPrefix()))
                    .forEach(method -> continuePopulateForMethod(objectOfClass, method));
            return objectOfClass;
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), SETTER), e);
        }
    }

    private <T> T continuePopulateUsingBuilder(Class<T> clazz) {
        if (populateConfig.getBuilderPattern().equals(LOMBOK)) {
            return continuePopulateUsingLombokBuilder(clazz);
        }
        if (populateConfig.getBuilderPattern().equals(IMMUTABLES)) {
            return continuePopulateUsingImmutablesBuilder(clazz);
        }
        throw new PopulateException("");
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingLombokBuilder(Class<T> clazz) {
        try {
            Object builderObject = clazz.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            Map<Integer, List<Method>> builderObjectMethodsGroupedByInvokeOrder = getMethodsForLombokBuilderGroupedByInvokeOrder(builderObject.getClass());
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(1)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(2)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(3)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method)));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            buildMethod.setAccessible(true);
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, LOMBOK)), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingImmutablesBuilder(Class<T> clazz) {
        try {
            Object builderObject = clazz.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            List<Method> builderObjectMethods = getMethodsForImmutablesBuilder(clazz, builderObject);
            builderObjectMethods.forEach(method -> continuePopulateForMethod(builderObject, method));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, IMMUTABLES)), e);
        }
    }

    private <T> void continuePopulateForMethod(T objectOfClass, Method method) {
        try {
            method.invoke(objectOfClass, List.of(method.getParameters()).stream()
                    .map(parameter -> populateWithOverrides(parameter.getType(), parameter, null))
                    .toArray());
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CALL_METHOD, method.getName(), objectOfClass.getClass().getName()), e);
        }
    }
}
