package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.object.ObjectFactory;
import com.github.anhem.testpopulator.object.ObjectFactoryImpl;
import com.github.anhem.testpopulator.object.ObjectFactoryVoid;
import com.github.anhem.testpopulator.value.ValueFactory;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.util.ImmutablesUtil.getImmutablesGeneratedClass;
import static com.github.anhem.testpopulator.util.ImmutablesUtil.getMethodsForImmutablesBuilder;
import static com.github.anhem.testpopulator.util.LombokUtil.calculateExpectedChildren;
import static com.github.anhem.testpopulator.util.LombokUtil.getMethodsForLombokBuilderGroupedByInvokeOrder;
import static com.github.anhem.testpopulator.util.PopulateUtil.*;
import static java.lang.String.format;

/**
 * Factory for creating populated objects from classes
 */
public class PopulateFactory {

    public static final String MISSING_COLLECTION_TYPE = "Failed to find type for collection %s";
    public static final String NO_MATCHING_STRATEGY = "Unable to populate %s. No matching strategy found. Tried with %s. Try another strategy or override population for this class";
    public static final String FAILED_TO_SET_FIELD = "Failed to set field %s in object of class %s";
    public static final String FAILED_TO_CALL_METHOD = "Failed to call method %s in object of class %s";
    public static final String FAILED_TO_CREATE_OBJECT = "Failed to create object of %s using %s strategy";
    public static final String FAILED_TO_CREATE_COLLECTION = "Failed to create and populate collection %s";

    public static final String BUILD_METHOD = "build";
    public static final String BUILDER_METHOD = "builder";

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
        T t = populateWithOverrides(clazz, objectFactory);
        objectFactory.writeToFile();
        return t;
    }

    private <T> T populateWithOverrides(Class<T> clazz, ObjectFactory objectFactory) {
        return populateWithOverrides(clazz, null, null, objectFactory);
    }

    private <T> T populateWithOverrides(Class<T> clazz, Parameter parameter, Type[] typeArguments, ObjectFactory objectFactory) {
        if (overridePopulates.containsKey(clazz)) {
            T overridePopulateValue = getOverridePopulateValue(clazz, overridePopulates);
            objectFactory.overridePopulate(clazz, overridePopulates.get(clazz));
            return overridePopulateValue;
        }
        if (clazz.isArray()) {
            return continuePopulateForArray(clazz, objectFactory);
        }
        if (isCollection(clazz)) {
            return continuePopulateForCollection(clazz, parameter, typeArguments, objectFactory);
        }
        if (isValue(clazz)) {
            T value = valueFactory.createValue(clazz);
            objectFactory.value(value);
            return value;
        }
        return continuePopulateWithStrategies(clazz, objectFactory);
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForArray(Class<T> clazz, ObjectFactory objectFactory) {
        Class<?> componentType = clazz.getComponentType();
        objectFactory.array(componentType);
        Object value = populateWithOverrides(componentType, objectFactory);
        Object array = Array.newInstance(componentType, 1);
        Array.set(array, 0, value);
        return (T) array;
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForCollection(Class<T> clazz, Parameter parameter, Type[] typeArguments, ObjectFactory objectFactory) {
        List<Type> argumentTypes = toArgumentTypes(parameter, typeArguments);
        try {
            if (isMap(clazz)) {
                if (clazz.getConstructors().length > 0) {
                    objectFactory.map(clazz);
                    Object key = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    Object value = continuePopulateWithType(argumentTypes.get(1), objectFactory);
                    Map<Object, Object> map = (Map<Object, Object>) clazz.getConstructor().newInstance();
                    map.put(key, value);
                    return (T) map;
                } else {
                    objectFactory.mapOf();
                    Object key = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    Object value = continuePopulateWithType(argumentTypes.get(1), objectFactory);
                    return (T) Map.of(key, value);
                }
            }
            if (isSet(clazz)) {
                if (clazz.getConstructors().length > 0) {
                    objectFactory.set(clazz);
                    Object value = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    Set<Object> set = (Set<Object>) clazz.getConstructor().newInstance();
                    set.add(value);
                    return (T) set;
                } else {
                    objectFactory.setOf();
                    Object value = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    return (T) Set.of(value);
                }
            }
            if (isCollection(clazz)) {
                if (clazz.getConstructors().length > 0) {
                    objectFactory.list(clazz);
                    Object value = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    List<Object> list = (List<Object>) clazz.getConstructor().newInstance();
                    list.add(value);
                    return (T) list;
                } else {
                    objectFactory.listOf();
                    Object value = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    return (T) List.of(value);
                }
            }
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_COLLECTION, clazz.getTypeName()), e);
        }
        throw new PopulateException(format(MISSING_COLLECTION_TYPE, clazz.getTypeName()));
    }

    private Object continuePopulateWithType(Type type, ObjectFactory objectFactory) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return populateWithOverrides((Class<?>) parameterizedType.getRawType(), null, parameterizedType.getActualTypeArguments(), objectFactory);
        }
        return populateWithOverrides((Class<?>) type, objectFactory);
    }

    private <T> T continuePopulateWithStrategies(Class<T> clazz, ObjectFactory objectFactory) {
        for (Strategy strategy : populateConfig.getStrategyOrder()) {
            if (isMatchingConstructorStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingConstructor(clazz, objectFactory);
            }
            if (isMatchingSetterStrategy(strategy, clazz, populateConfig.getSetterPrefix(), populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingSetters(clazz, objectFactory);
            }
            if (isMatchingFieldStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingFields(clazz, objectFactory);
            }
            if (isMatchingBuilderStrategy(strategy, clazz, populateConfig.getBuilderPattern())) {
                return continuePopulateUsingBuilder(clazz, objectFactory);
            }
        }
        throw new PopulateException(format(NO_MATCHING_STRATEGY, clazz.getName(), populateConfig.getStrategyOrder()));
    }

    private <T> T continuePopulateUsingConstructor(Class<T> clazz, ObjectFactory objectFactory) {
        try {
            Constructor<T> constructor = getLargestConstructor(clazz, populateConfig.canAccessNonPublicConstructors());
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            objectFactory.constructor(clazz, constructor.getParameterCount());
            Object[] arguments = IntStream.range(0, constructor.getParameterCount()).mapToObj(i -> {
                Parameter parameter = constructor.getParameters()[i];
                return populateWithOverrides(parameter.getType(), parameter, null, objectFactory);
            }).toArray();
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), CONSTRUCTOR), e);
        }
    }

    private <T> T continuePopulateUsingFields(Class<T> clazz, ObjectFactory objectFactory) {
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
                                field.set(objectOfClass, populateWithOverrides(field.getType(), null, typeArguments, objectFactory));
                            } else {
                                field.set(objectOfClass, populateWithOverrides(field.getType(), objectFactory));
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

    private <T> T continuePopulateUsingSetters(Class<T> clazz, ObjectFactory objectFactory) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            T objectOfClass = constructor.newInstance();
            List<Method> methods = getDeclaredMethods(clazz, populateConfig.getBlacklistedMethods()).stream()
                    .filter(method -> isSetterMethod(method, populateConfig.getSetterPrefix()))
                    .collect(Collectors.toList());
            objectFactory.setter(clazz, methods.size());
            methods.forEach(method -> continuePopulateForMethod(objectOfClass, method, objectFactory));
            return objectOfClass;
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), SETTER), e);
        }
    }

    private <T> T continuePopulateUsingBuilder(Class<T> clazz, ObjectFactory objectFactory) {
        if (populateConfig.getBuilderPattern().equals(LOMBOK)) {
            return continuePopulateUsingLombokBuilder(clazz, objectFactory);
        }
        if (populateConfig.getBuilderPattern().equals(IMMUTABLES)) {
            return continuePopulateUsingImmutablesBuilder(clazz, objectFactory);
        }
        throw new PopulateException("");
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingLombokBuilder(Class<T> clazz, ObjectFactory objectFactory) {
        try {
            Object builderObject = clazz.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            Map<Integer, List<Method>> builderObjectMethodsGroupedByInvokeOrder = getMethodsForLombokBuilderGroupedByInvokeOrder(builderObject.getClass(), populateConfig.getBlacklistedMethods());
            objectFactory.builder(clazz, calculateExpectedChildren(builderObjectMethodsGroupedByInvokeOrder));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(1)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, objectFactory)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(2)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, objectFactory)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(3)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, objectFactory)));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            setAccessible(buildMethod, builderObject);
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, LOMBOK)), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingImmutablesBuilder(Class<T> clazz, ObjectFactory objectFactory) {
        try {
            Class<?> immutablesGeneratedClass = getImmutablesGeneratedClass(clazz);
            Object builderObject = immutablesGeneratedClass.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            List<Method> builderObjectMethods = getMethodsForImmutablesBuilder(immutablesGeneratedClass, builderObject, populateConfig.getBlacklistedMethods());
            objectFactory.builder(immutablesGeneratedClass, builderObjectMethods.size());
            builderObjectMethods.forEach(method -> continuePopulateForMethod(builderObject, method, objectFactory));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, IMMUTABLES)), e);
        }
    }

    private <T> void continuePopulateForMethod(T objectOfClass, Method method, ObjectFactory objectFactory) {
        try {
            objectFactory.method(method.getName(), method.getParameters().length);
            method.invoke(objectOfClass, Stream.of(method.getParameters())
                    .map(parameter -> populateWithOverrides(parameter.getType(), parameter, null, objectFactory))
                    .toArray());
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CALL_METHOD, method.getName(), objectOfClass.getClass().getName()), e);
        }
    }
}
