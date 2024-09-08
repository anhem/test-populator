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

import static com.github.anhem.testpopulator.CollectionCarrier.initialize;
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

    private <T> T populateWithOverrides(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        ObjectFactory objectFactory = classCarrier.getObjectFactory();
        if (overridePopulates.containsKey(clazz)) {
            T overridePopulateValue = getOverridePopulateValue(clazz, overridePopulates);
            objectFactory.overridePopulate(clazz, overridePopulates.get(clazz));
            return overridePopulateValue;
        }
        if (isCollectionCarrier(classCarrier)) {
            return continuePopulateForCollection((CollectionCarrier<T>) classCarrier);
        }
        if (clazz.isArray()) {
            return continuePopulateForArray(classCarrier);
        }
        if (isValue(clazz)) {
            T value = valueFactory.createValue(clazz);
            objectFactory.value(value);
            return value;
        }
        return continuePopulateWithStrategies(classCarrier);
    }


    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForArray(ClassCarrier<T> classCarrier) {
        Class<?> componentType = classCarrier.getClazz().getComponentType();
        classCarrier.getObjectFactory().array(componentType);
        Object value = populateWithOverrides(classCarrier.toClassCarrier(componentType));
        Object array = Array.newInstance(componentType, 1);
        Array.set(array, 0, value);
        return (T) array;
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForCollection(CollectionCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        ObjectFactory objectFactory = classCarrier.getObjectFactory();
        List<Type> argumentTypes = classCarrier.getArgumentTypes();
        try {
            TypeCarrier typeCarrier = classCarrier.toTypeCarrier(argumentTypes.get(0));
            if (isMap(clazz)) {
                if (clazz.getConstructors().length > 0) {
                    objectFactory.map(clazz);
                    Object key = continuePopulateWithType(typeCarrier);
                    Object value = continuePopulateWithType(classCarrier.toTypeCarrier(argumentTypes.get(1)));
                    Map<Object, Object> map = (Map<Object, Object>) clazz.getConstructor().newInstance();
                    map.put(key, value);
                    return (T) map;
                } else {
                    objectFactory.mapOf();
                    Object key = continuePopulateWithType(typeCarrier);
                    Object value = continuePopulateWithType(classCarrier.toTypeCarrier(argumentTypes.get(1)));
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

    private Object continuePopulateWithType(TypeCarrier typeCarrier) {
        Type type = typeCarrier.getType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return populateWithOverrides(typeCarrier.toCollectionCarrier(parameterizedType.getRawType(), parameterizedType.getActualTypeArguments()));
        }
        return populateWithOverrides(typeCarrier.toClassCarrier(type));
    }

    private <T> T continuePopulateWithStrategies(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        for (Strategy strategy : populateConfig.getStrategyOrder()) {
            if (isMatchingConstructorStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingConstructor(classCarrier);
            }
            if (isMatchingSetterStrategy(strategy, clazz, populateConfig.getSetterPrefix(), populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingSetters(classCarrier);
            }
            if (isMatchingFieldStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingFields(classCarrier);
            }
            if (isMatchingBuilderStrategy(strategy, clazz, populateConfig.getBuilderPattern())) {
                return continuePopulateUsingBuilder(classCarrier);
            }
        }
        throw new PopulateException(format(NO_MATCHING_STRATEGY, clazz.getName(), populateConfig.getStrategyOrder()));
    }

    private <T> T continuePopulateUsingConstructor(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        try {
            Constructor<T> constructor = getLargestConstructor(clazz, populateConfig.canAccessNonPublicConstructors());
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            classCarrier.getObjectFactory().constructor(clazz, constructor.getParameterCount());
            Object[] arguments = IntStream.range(0, constructor.getParameterCount()).mapToObj(i -> {
                Parameter parameter = constructor.getParameters()[i];
                if (isCollection(parameter.getType())) {
                    return populateWithOverrides(classCarrier.toCollectionCarrier(parameter));
                } else {
                    return populateWithOverrides(classCarrier.toClassCarrier(parameter));
                }
            }).toArray();
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), CONSTRUCTOR), e);
        }
    }

    private <T> T continuePopulateUsingFields(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
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
                                field.set(objectOfClass, populateWithOverrides(classCarrier.toCollectionCarrier(field.getType(), typeArguments)));
                            } else {
                                field.set(objectOfClass, populateWithOverrides(classCarrier.toClassCarrier(field.getType())));
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

    private <T> T continuePopulateUsingSetters(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            T objectOfClass = constructor.newInstance();
            List<Method> methods = getDeclaredMethods(clazz, populateConfig.getBlacklistedMethods()).stream()
                    .filter(method -> isSetterMethod(method, populateConfig.getSetterPrefix()))
                    .collect(Collectors.toList());
            classCarrier.getObjectFactory().setter(clazz, methods.size());
            methods.forEach(method -> continuePopulateForMethod(objectOfClass, method, classCarrier));
            return objectOfClass;
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), SETTER), e);
        }
    }

    private <T> T continuePopulateUsingBuilder(ClassCarrier<T> classCarrier) {
        if (populateConfig.getBuilderPattern().equals(LOMBOK)) {
            return continuePopulateUsingLombokBuilder(classCarrier);
        }
        if (populateConfig.getBuilderPattern().equals(IMMUTABLES)) {
            return continuePopulateUsingImmutablesBuilder(classCarrier);
        }
        throw new PopulateException("");
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingLombokBuilder(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        try {
            Object builderObject = clazz.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            Map<Integer, List<Method>> builderObjectMethodsGroupedByInvokeOrder = getMethodsForLombokBuilderGroupedByInvokeOrder(builderObject.getClass(), populateConfig.getBlacklistedMethods());
            classCarrier.getObjectFactory().builder(clazz, calculateExpectedChildren(builderObjectMethodsGroupedByInvokeOrder));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(1)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(2)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier)));
            Optional.ofNullable(builderObjectMethodsGroupedByInvokeOrder.get(3)).ifPresent(methods ->
                    methods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier)));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            setAccessible(buildMethod, builderObject);
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), format("%s (%s)", BUILDER, LOMBOK)), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateUsingImmutablesBuilder(ClassCarrier<T> classCarrier) {
        try {
            Class<?> immutablesGeneratedClass = getImmutablesGeneratedClass(classCarrier.getClazz());
            Object builderObject = immutablesGeneratedClass.getDeclaredMethod(BUILDER_METHOD).invoke(null);
            List<Method> builderObjectMethods = getMethodsForImmutablesBuilder(immutablesGeneratedClass, builderObject, populateConfig.getBlacklistedMethods());
            classCarrier.getObjectFactory().builder(immutablesGeneratedClass, builderObjectMethods.size());
            builderObjectMethods.forEach(method -> continuePopulateForMethod(builderObject, method, classCarrier));
            Method buildMethod = builderObject.getClass().getDeclaredMethod(BUILD_METHOD);
            return (T) buildMethod.invoke(builderObject);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, classCarrier.getClazz().getName(), format("%s (%s)", BUILDER, IMMUTABLES)), e);
        }
    }

    private <T, V> void continuePopulateForMethod(V objectOfClass, Method method, ClassCarrier<T> classCarrier) {
        try {
            classCarrier.getObjectFactory().method(method.getName(), method.getParameters().length);
            method.invoke(objectOfClass, Stream.of(method.getParameters())
                    .map(parameter -> {
                        if (isCollection(parameter.getType())) {
                            return populateWithOverrides(classCarrier.toCollectionCarrier(parameter));
                        } else {
                            return populateWithOverrides(classCarrier.toClassCarrier(parameter));
                        }
                    })
                    .toArray());
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CALL_METHOD, method.getName(), objectOfClass.getClass().getName()), e);
        }
    }
}
