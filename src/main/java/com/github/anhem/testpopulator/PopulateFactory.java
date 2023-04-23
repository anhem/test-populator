package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.ImmutablesUtil.getImmutablesGeneratedClass;
import static com.github.anhem.testpopulator.ImmutablesUtil.getMethodsForImmutablesBuilder;
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

    static final String MISSING_BUILDER_PATTERN = "%s strategy configured, but no builderPattern specified. Should be one of %s";
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
        ObjectFactory objectFactory = new ObjectFactory();
        T t = populateWithOverrides(clazz, objectFactory);
        ObjectBuilder objectBuilder = objectFactory.getTopObjectBuilder();
        System.out.println(objectBuilder.build());
        return t;
    }

    private <T> T populateWithOverrides(Class<T> clazz, ObjectFactory objectFactory) {
        return populateWithOverrides(clazz, null, null, objectFactory);
    }

    private <T> T populateWithOverrides(Class<T> clazz, Parameter parameter, Type[] typeArguments, ObjectFactory objectFactory) {
        if (overridePopulates.containsKey(clazz)) {
            T overridePopulateValue = getOverridePopulateValue(clazz, overridePopulates);
            objectFactory.addOverridePopulate(clazz, overridePopulates.get(clazz));
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
            objectFactory.addValue(value);
            return value;
        }
        return continuePopulateWithStrategies(clazz, objectFactory);
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForArray(Class<T> clazz, ObjectFactory objectFactory) {
        Class<?> componentType = clazz.getComponentType();
        objectFactory.startArray(componentType);
        Object value = populateWithOverrides(componentType, objectFactory);
        objectFactory.endArray();
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
                    objectFactory.startMap(clazz, argumentTypes.get(0), argumentTypes.get(1));
                    objectFactory.startPutMap();
                    Object key = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    objectFactory.keyValueDividerForPutMap();
                    Object value = continuePopulateWithType(argumentTypes.get(1), objectFactory);
                    objectFactory.endPutMap();
                    objectFactory.endMap();
                    Map<Object, Object> map = (Map<Object, Object>) clazz.getConstructor().newInstance();
                    map.put(key, value);
                    return (T) map;
                } else {
                    objectFactory.startMapOf();
                    Object key = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    objectFactory.keyValueDividerForMapOf();
                    Object value = continuePopulateWithType(argumentTypes.get(1), objectFactory);
                    objectFactory.endMapOf();
                    return (T) Map.of(key, value);
                }
            }
            if (isSet(clazz)) {
                if (clazz.getConstructors().length > 0) {
                    objectFactory.startSet(clazz, argumentTypes.get(0));
                    Object value = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    objectFactory.endSet();
                    Set<Object> set = (Set<Object>) clazz.getConstructor().newInstance();
                    set.add(value);
                    return (T) set;
                } else {
                    objectFactory.startSetOf();
                    Object value = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    objectFactory.endSetOf();
                    return (T) Set.of(value);
                }
            }
            if (isCollection(clazz)) {
                if (clazz.getConstructors().length > 0) {
                    objectFactory.startList(clazz, argumentTypes.get(0));
                    Object value = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    objectFactory.endList();
                    List<Object> list = (List<Object>) clazz.getConstructor().newInstance();
                    list.add(value);
                    return (T) list;
                } else {
                    objectFactory.startListOf();
                    Object value = continuePopulateWithType(argumentTypes.get(0), objectFactory);
                    objectFactory.endListOf();
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
            objectFactory.startConstructor(clazz);
            Object[] arguments = IntStream.range(0, constructor.getParameterCount()).mapToObj(i -> {
                Parameter parameter = constructor.getParameters()[i];
                objectFactory.parameterDividerForConstructor(i);
                return populateWithOverrides(parameter.getType(), parameter, null, objectFactory);
            }).toArray();
            objectFactory.endConstructor();
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
            objectFactory.startSetter(clazz);
            T objectOfClass = constructor.newInstance();
            getDeclaredMethods(clazz, populateConfig.getBlacklistedMethods()).stream()
                    .filter(method -> isSetterMethod(method, populateConfig.getSetterPrefix()))
                    .forEach(method -> continuePopulateForMethod(objectOfClass, method, SETTER, objectFactory));
            objectFactory.endSetter();
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
            objectFactory.startBuilder(clazz);
            Object builderObject = clazz.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            Map<Integer, List<Method>> builderObjectMethodsGroupedByInvokeOrder = getMethodsForLombokBuilderGroupedByInvokeOrder(builderObject.getClass(), populateConfig.getBlacklistedMethods());
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(1)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, BUILDER, objectFactory)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(2)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, BUILDER, objectFactory)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(3)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, BUILDER, objectFactory)));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            setAccessible(buildMethod, builderObject);
            objectFactory.endBuilder();
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, LOMBOK)), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingImmutablesBuilder(Class<T> clazz, ObjectFactory objectFactory) {
        try {
            Class<?> immutablesGeneratedClass = getImmutablesGeneratedClass(clazz);
            objectFactory.startBuilder(clazz, immutablesGeneratedClass);
            Object builderObject = immutablesGeneratedClass.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            List<Method> builderObjectMethods = getMethodsForImmutablesBuilder(immutablesGeneratedClass, builderObject, populateConfig.getBlacklistedMethods());
            builderObjectMethods.forEach(method -> continuePopulateForMethod(builderObject, method, BUILDER, objectFactory));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            objectFactory.endBuilder();
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, IMMUTABLES)), e);
        }
    }

    private <T> void continuePopulateForMethod(T objectOfClass, Method method, Strategy strategy, ObjectFactory objectFactory) {
        try {
            method.invoke(objectOfClass, Stream.of(method.getParameters())
                    .map(parameter -> {
                        objectFactory.startMethod(strategy, method.getName());
                        Object object = populateWithOverrides(parameter.getType(), parameter, null, objectFactory);
                        objectFactory.endMethod(strategy);
                        return object;
                    })
                    .toArray());
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CALL_METHOD, method.getName(), objectOfClass.getClass().getName()), e);
        }
    }
}
