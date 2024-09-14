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
        if (overridePopulates.containsKey(clazz)) {
            T overridePopulateValue = getOverridePopulateValue(clazz, overridePopulates);
            classCarrier.getObjectFactory().overridePopulate(clazz, overridePopulates.get(clazz));
            return overridePopulateValue;
        }
        if (populateConfig.isNullOnCircularDependency() && !isJavaBaseClass(clazz) && !classCarrier.addVisited()) {
            classCarrier.getObjectFactory().nullValue(clazz);
            return null;
        }
        if (isCollectionCarrier(classCarrier)) {
            return continuePopulateForCollection((CollectionCarrier<T>) classCarrier);
        }
        if (clazz.isArray()) {
            return continuePopulateForArray(classCarrier);
        }
        if (isValue(clazz)) {
            T value = valueFactory.createValue(clazz);
            classCarrier.getObjectFactory().value(value);
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

    private <T> T continuePopulateForCollection(CollectionCarrier<T> classCarrier) {
        try {
            if (isMap(classCarrier.getClazz())) {
                return continuePopulateForMap(classCarrier);
            }
            if (isSet(classCarrier.getClazz())) {
                return continuePopulateForSet(classCarrier);
            }
            if (isCollection(classCarrier.getClazz())) {
                return continuePopulateForList(classCarrier);
            }
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_COLLECTION, classCarrier.getClazz().getTypeName()), e);
        }
        throw new PopulateException(format(MISSING_COLLECTION_TYPE, classCarrier.getClazz().getTypeName()));
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForMap(CollectionCarrier<T> classCarrier) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().map(classCarrier.getClazz());
            Map<Object, Object> map = (Map<Object, Object>) classCarrier.getClazz().getConstructor().newInstance();
            Optional<Object> key = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0))));
            Optional<Object> value = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(1))));
            if (key.isPresent() && value.isPresent()) {
                map.put(key.get(), value.get());
            }
            return (T) map;
        } else {
            classCarrier.getObjectFactory().mapOf();
            Optional<Object> key = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0))));
            Optional<Object> value = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(1))));
            if (key.isPresent() && value.isPresent()) {
                return (T) Map.of(key.get(), value.get());
            }
            return (T) Map.of();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForSet(CollectionCarrier<T> classCarrier) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().set(classCarrier.getClazz());
            Set<Object> set = (Set<Object>) classCarrier.getClazz().getConstructor().newInstance();
            Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0))))
                    .ifPresent(set::add);
            return (T) set;
        } else {
            classCarrier.getObjectFactory().setOf();
            return Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0))))
                    .map(value -> (T) Set.of(value))
                    .orElseGet(() -> (T) Set.of());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForList(CollectionCarrier<T> classCarrier) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().list(classCarrier.getClazz());
            List<Object> list = (List<Object>) classCarrier.getClazz().getConstructor().newInstance();
            Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0))))
                    .ifPresent(list::add);
            return (T) list;
        } else {
            classCarrier.getObjectFactory().listOf();
            return Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0))))
                    .map(value -> (T) List.of(value))
                    .orElseGet(() -> (T) List.of());
        }
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
                    }).toArray());
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CALL_METHOD, method.getName(), objectOfClass.getClass().getName()), e);
        }
    }
}
