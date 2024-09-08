package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.Carrier.initialize;
import static com.github.anhem.testpopulator.ImmutablesUtil.getImmutablesGeneratedClass;
import static com.github.anhem.testpopulator.ImmutablesUtil.getMethodsForImmutablesBuilder;
import static com.github.anhem.testpopulator.LombokUtil.calculateExpectedChildren;
import static com.github.anhem.testpopulator.LombokUtil.getMethodsForLombokBuilderGroupedByInvokeOrder;
import static com.github.anhem.testpopulator.PopulateUtil.*;
import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static java.lang.String.format;

/**
 * Factory for creating populated objects from classes
 */
public class PopulateFactory {

    static final String MISSING_COLLECTION_TYPE = "Failed to find type for collection %s";
    static final String NO_MATCHING_STRATEGY = "Unable to populate %s. No matching strategy found. Tried with %s. Try another strategy or override population for this class";
    static final String FAILED_TO_SET_FIELD = "Failed to set field %s in object of class %s";
    static final String FAILED_TO_CALL_METHOD = "Failed to call method %s in object of class %s";
    static final String FAILED_TO_CREATE_OBJECT = "Failed to create object of %s using %s strategy";
    static final String FAILED_TO_CREATE_COLLECTION = "Failed to create and populate collection %s";

    static final String BUILD_METHOD = "build";
    static final String BUILDER_METHOD = "builder";

    private final PopulateConfig populateConfig;
    private final ValueFactory valueFactory;
    private final Map<Class<?>, OverridePopulate<?>> overridePopulates;

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
        overridePopulates = populateConfig.createOverridePopulates();
    }

    /**
     * Call to create a fully populated object from a class
     *
     * @param clazz Class that should be populated
     * @return object of clazz
     */
    public <T> T populate(Class<T> clazz) {
        ObjectFactory objectFactory = populateConfig.isObjectFactoryEnabled() ? new ObjectFactoryImpl(populateConfig) : new ObjectFactoryVoid();
        T t = populateWithOverrides(initialize(clazz, objectFactory));
        objectFactory.writeToFile();
        return t;
    }

    private <T> T populateWithOverrides(Carrier<T> carrier) {
        Class<T> clazz = carrier.getClazz();
        ObjectFactory objectFactory = carrier.getObjectFactory();
        if (overridePopulates.containsKey(clazz)) {
            T overridePopulateValue = getOverridePopulateValue(clazz, overridePopulates);
            objectFactory.overridePopulate(clazz, overridePopulates.get(clazz));
            return overridePopulateValue;
        }
        if (clazz.isArray()) {
            return continuePopulateForArray(carrier);
        }
        if (isCollection(clazz)) {
            return continuePopulateForCollection(carrier);
        }
        if (isValue(clazz)) {
            T value = valueFactory.createValue(clazz);
            objectFactory.value(value);
            return value;
        }
        return continuePopulateWithStrategies(carrier);
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForArray(Carrier<T> carrier) {
        Class<?> componentType = carrier.getClazz().getComponentType();
        carrier.getObjectFactory().array(componentType);
        Object value = populateWithOverrides(carrier.toClassCarrier(componentType));
        Object array = Array.newInstance(componentType, 1);
        Array.set(array, 0, value);
        return (T) array;
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForCollection(Carrier<T> carrier) {
        Class<T> clazz = carrier.getClazz();
        ObjectFactory objectFactory = carrier.getObjectFactory();
        List<Type> argumentTypes = toArgumentTypes(carrier.getParameter(), carrier.getTypeArguments());
        try {
            Carrier<?> typeCarrier = carrier.toTypeCarrier(argumentTypes.get(0));
            if (isMap(clazz)) {
                if (clazz.getConstructors().length > 0) {
                    objectFactory.map(clazz);
                    Object key = continuePopulateWithType(typeCarrier);
                    Object value = continuePopulateWithType(carrier.toTypeCarrier(argumentTypes.get(1)));
                    Map<Object, Object> map = (Map<Object, Object>) clazz.getConstructor().newInstance();
                    map.put(key, value);
                    return (T) map;
                } else {
                    objectFactory.mapOf();
                    Object key = continuePopulateWithType(typeCarrier);
                    Object value = continuePopulateWithType(carrier.toTypeCarrier(argumentTypes.get(1)));
                    return (T) Map.of(key, value);
                }
            }
            if (isSet(clazz)) {
                if (clazz.getConstructors().length > 0) {
                    objectFactory.set(clazz);
                    Object value = continuePopulateWithType(typeCarrier);
                    Set<Object> set = (Set<Object>) clazz.getConstructor().newInstance();
                    set.add(value);
                    return (T) set;
                } else {
                    objectFactory.setOf();
                    Object value = continuePopulateWithType(typeCarrier);
                    return (T) Set.of(value);
                }
            }
            if (isCollection(clazz)) {
                if (clazz.getConstructors().length > 0) {
                    objectFactory.list(clazz);
                    Object value = continuePopulateWithType(typeCarrier);
                    List<Object> list = (List<Object>) clazz.getConstructor().newInstance();
                    list.add(value);
                    return (T) list;
                } else {
                    objectFactory.listOf();
                    Object value = continuePopulateWithType(typeCarrier);
                    return (T) List.of(value);
                }
            }
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_COLLECTION, clazz.getTypeName()), e);
        }
        throw new PopulateException(format(MISSING_COLLECTION_TYPE, clazz.getTypeName()));
    }

    private Object continuePopulateWithType(Carrier<?> carrier) {
        Type type = carrier.getType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return populateWithOverrides(carrier.toClassTypeArgumentsCarrier(parameterizedType.getRawType(), parameterizedType.getActualTypeArguments()));
        }
        return populateWithOverrides(carrier.toClassCarrier((Class<?>) type));
    }

    private <T> T continuePopulateWithStrategies(Carrier<T> carrier) {
        Class<T> clazz = carrier.getClazz();
        for (Strategy strategy : populateConfig.getStrategyOrder()) {
            if (isMatchingConstructorStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingConstructor(carrier);
            }
            if (isMatchingSetterStrategy(strategy, clazz, populateConfig.getSetterPrefix(), populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingSetters(carrier);
            }
            if (isMatchingFieldStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingFields(carrier);
            }
            if (isMatchingBuilderStrategy(strategy, clazz, populateConfig.getBuilderPattern())) {
                return continuePopulateUsingBuilder(carrier);
            }
        }
        throw new PopulateException(format(NO_MATCHING_STRATEGY, clazz.getName(), populateConfig.getStrategyOrder()));
    }

    private <T> T continuePopulateUsingConstructor(Carrier<T> carrier) {
        Class<T> clazz = carrier.getClazz();
        try {
            Constructor<T> constructor = getLargestConstructor(clazz, populateConfig.canAccessNonPublicConstructors());
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            carrier.getObjectFactory().constructor(clazz, constructor.getParameterCount());
            Object[] arguments = IntStream.range(0, constructor.getParameterCount()).mapToObj(i -> {
                Parameter parameter = constructor.getParameters()[i];
                return populateWithOverrides(carrier.toClassTypeCarrier(parameter));
            }).toArray();
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), CONSTRUCTOR), e);
        }
    }

    private <T> T continuePopulateUsingFields(Carrier<T> carrier) {
        Class<T> clazz = carrier.getClazz();
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            T objectOfClass = constructor.newInstance();
            getDeclaredFields(clazz, populateConfig.getBlacklistedFields()).stream()
                    .filter(field -> !Modifier.isFinal(field.getModifiers()))
                    .forEach(field -> {
                        try {
                            setAccessible(field, objectOfClass);
                            if (isCollection(field.getType())) {
                                Type[] typeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                                field.set(objectOfClass, populateWithOverrides(carrier.toClassTypeArgumentsCarrier(field.getType(), typeArguments)));
                            } else {
                                field.set(objectOfClass, populateWithOverrides(carrier.toClassCarrier(field.getType())));
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

    private <T> T continuePopulateUsingSetters(Carrier<T> carrier) {
        Class<T> clazz = carrier.getClazz();
        ObjectFactory objectFactory = carrier.getObjectFactory();
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            T objectOfClass = constructor.newInstance();
            List<Method> methods = getDeclaredMethods(clazz, populateConfig.getBlacklistedMethods()).stream()
                    .filter(method -> isSetterMethod(method, populateConfig.getSetterPrefix()))
                    .collect(Collectors.toList());
            objectFactory.setter(clazz, methods.size());
            methods.forEach(method -> continuePopulateForMethod(objectOfClass, method, carrier));
            return objectOfClass;
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), SETTER), e);
        }
    }

    private <T> T continuePopulateUsingBuilder(Carrier<T> carrier) {
        if (populateConfig.getBuilderPattern().equals(LOMBOK)) {
            return continuePopulateUsingLombokBuilder(carrier);
        }
        if (populateConfig.getBuilderPattern().equals(IMMUTABLES)) {
            return continuePopulateUsingImmutablesBuilder(carrier);
        }
        throw new PopulateException("");
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingLombokBuilder(Carrier<T> carrier) {
        Class<T> clazz = carrier.getClazz();
        ObjectFactory objectFactory = carrier.getObjectFactory();
        try {
            Object builderObject = clazz.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            Map<Integer, List<Method>> builderObjectMethodsGroupedByInvokeOrder = getMethodsForLombokBuilderGroupedByInvokeOrder(builderObject.getClass(), populateConfig.getBlacklistedMethods());
            objectFactory.builder(clazz, calculateExpectedChildren(builderObjectMethodsGroupedByInvokeOrder));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(1)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, carrier)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(2)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, carrier)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(3)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, carrier)));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            setAccessible(buildMethod, builderObject);
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, LOMBOK)), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingImmutablesBuilder(Carrier<T> carrier) {
        Class<T> clazz = carrier.getClazz();
        ObjectFactory objectFactory = carrier.getObjectFactory();
        try {
            Class<?> immutablesGeneratedClass = getImmutablesGeneratedClass(clazz);
            Object builderObject = immutablesGeneratedClass.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            List<Method> builderObjectMethods = getMethodsForImmutablesBuilder(immutablesGeneratedClass, builderObject, populateConfig.getBlacklistedMethods());
            objectFactory.builder(immutablesGeneratedClass, builderObjectMethods.size());
            builderObjectMethods.forEach(method -> continuePopulateForMethod(builderObject, method, carrier));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, IMMUTABLES)), e);
        }
    }

    private <T, V> void continuePopulateForMethod(V objectOfClass, Method method, Carrier<T> carrier) {
        ObjectFactory objectFactory = carrier.getObjectFactory();
        try {
            objectFactory.method(method.getName(), method.getParameters().length);
            method.invoke(objectOfClass, Stream.of(method.getParameters())
                    .map(parameter -> {
                        return populateWithOverrides(carrier.toClassTypeCarrier(parameter));
                    })
                    .toArray());
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CALL_METHOD, method.getName(), objectOfClass.getClass().getName()), e);
        }
    }
}
