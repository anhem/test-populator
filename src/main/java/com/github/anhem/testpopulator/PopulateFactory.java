package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;
import com.github.anhem.testpopulator.internal.carrier.TypeCarrier;
import com.github.anhem.testpopulator.internal.object.ObjectFactory;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryImpl;
import com.github.anhem.testpopulator.internal.object.ObjectFactoryVoid;
import com.github.anhem.testpopulator.internal.value.ValueFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.internal.carrier.CollectionCarrier.initialize;
import static com.github.anhem.testpopulator.internal.util.ImmutablesUtil.getImmutablesGeneratedClass;
import static com.github.anhem.testpopulator.internal.util.ImmutablesUtil.getMethodsForImmutablesBuilder;
import static com.github.anhem.testpopulator.internal.util.LombokUtil.calculateExpectedChildren;
import static com.github.anhem.testpopulator.internal.util.LombokUtil.getMethodsForLombokBuilderGroupedByInvokeOrder;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static java.lang.String.format;

/**
 * Factory for creating populated objects from classes
 */
public class PopulateFactory {

    static final String MISSING_COLLECTION_TYPE = "Failed to find type for collection '%s'";
    static final String NO_MATCHING_STRATEGY = "Unable to populate '%s'. No matching strategy found. Tried with %s. Try another strategy or override population for this class";
    static final String FAILED_TO_SET_FIELD = "Failed to set field '%s' in object of class %s";
    static final String FAILED_TO_CALL_METHOD = "Failed to call method '%s' in object of class '%s'";
    static final String FAILED_TO_CREATE_OBJECT = "Failed to create object of '%s' using '%s' strategy";
    static final String FAILED_TO_CREATE_COLLECTION = "Failed to create and populate collection '%s'";

    public static final String BUILD_METHOD = "build";
    public static final String BUILDER_METHOD = "builder";

    private final PopulateConfig populateConfig;
    private final ValueFactory valueFactory;

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
        this.valueFactory = new ValueFactory(populateConfig.useRandomValues(), populateConfig.getOverridePopulate());
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
        if (valueFactory.hasType(clazz)) {
            return creatValue(classCarrier);
        }
        if (alreadyVisited(classCarrier, populateConfig.isNullOnCircularDependency())) {
            return createNullValue(classCarrier);
        }
        if (isCollectionCarrier(classCarrier)) {
            return continuePopulateForCollection((CollectionCarrier<T>) classCarrier);
        }
        if (clazz.isArray()) {
            return continuePopulateForArray(classCarrier);
        }
        return continuePopulateWithStrategies(classCarrier);
    }

    private <T> T creatValue(ClassCarrier<T> classCarrier) {
        T value = valueFactory.createValue(classCarrier.getClazz());
        classCarrier.getObjectFactory().value(value);
        return value;
    }

    private static <T> T createNullValue(ClassCarrier<T> classCarrier) {
        classCarrier.getObjectFactory().nullValue(classCarrier.getClazz());
        return null;
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

    private <T> T continuePopulateForCollection(CollectionCarrier<T> collectionCarrier) {
        try {
            Class<T> clazz = collectionCarrier.getClazz();
            if (isMap(clazz)) {
                return continuePopulateForMap(collectionCarrier);
            } else if (isSet(clazz)) {
                return continuePopulateForSet(collectionCarrier);
            } else if (isMapEntry(clazz)) {
                return continuePopulateForMapEntry(collectionCarrier);
            } else if (isCollection(clazz)) {
                return continuePopulateForList(collectionCarrier);
            }
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_COLLECTION, collectionCarrier.getClazz().getTypeName()), e);
        }
        throw new PopulateException(format(MISSING_COLLECTION_TYPE, collectionCarrier.getClazz().getTypeName()));
    }

    @SuppressWarnings("unchecked")
    private <T> T continuePopulateForMap(CollectionCarrier<T> classCarrier) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().map(classCarrier.getClazz());
            Map<Object, Object> map = (Map<Object, Object>) classCarrier.getClazz().getConstructor().newInstance();
            Optional<Object> key = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0))));
            Optional<Object> value = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(1))));
            key.ifPresent(k -> map.put(k, value.orElse(null)));
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
    private <T> T continuePopulateForMapEntry(CollectionCarrier<T> classCarrier) {
        classCarrier.getObjectFactory().mapEntry(classCarrier.getClazz());
        Object key = continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)));
        Object value = continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(1)));
        return (T) new AbstractMap.SimpleEntry<>(key, value);
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
            if (isMatchingSetterStrategy(strategy, clazz, populateConfig.getSetterPrefixes(), populateConfig.canAccessNonPublicConstructors())) {
                return continuePopulateUsingSetters(classCarrier);
            }
            if (isMatchingMutatorStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors(), populateConfig.getConstructorType())) {
                return continuePopulateUsingMutator(classCarrier);
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
            return continuePopulateUsingConstructor(constructor, classCarrier);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), CONSTRUCTOR), e);
        }
    }

    private <T> T continuePopulateUsingConstructor(Constructor<T> constructor, ClassCarrier<T> classCarrier) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        classCarrier.getObjectFactory().constructor(classCarrier.getClazz(), constructor.getParameterCount());
        Object[] arguments = IntStream.range(0, constructor.getParameterCount()).mapToObj(i -> {
            Parameter parameter = constructor.getParameters()[i];
            if (isCollection(parameter.getType())) {
                return populateWithOverrides(classCarrier.toCollectionCarrier(parameter));
            } else {
                return populateWithOverrides(classCarrier.toClassCarrier(parameter));
            }
        }).toArray();
        return constructor.newInstance(arguments);
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
                                field.set(objectOfClass, populateWithOverrides(classCarrier.toCollectionCarrier(field.getType(), ((ParameterizedType) field.getGenericType()).getActualTypeArguments())));
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
            List<Method> methods = getSetterMethods(clazz, populateConfig.getBlacklistedMethods(), populateConfig.getSetterPrefixes());
            classCarrier.getObjectFactory().setter(clazz, methods.size());
            methods.forEach(method -> continuePopulateForMethod(objectOfClass, method, classCarrier));
            return objectOfClass;
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), SETTER), e);
        }
    }

    private <T> T continuePopulateUsingMutator(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        try {
            Constructor<T> constructor = getConstructor(clazz, populateConfig.canAccessNonPublicConstructors(), populateConfig.getConstructorType());
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            if (constructor.getParameterCount() > 0) {
                T objectOfClass = continuePopulateUsingConstructor(constructor, classCarrier);
                List<Method> methods = getMutatorMethods(clazz, populateConfig.getBlacklistedMethods());
                classCarrier.getObjectFactory().mutator(clazz, methods.size());
                methods.forEach(method -> continuePopulateForMethod(objectOfClass, method, classCarrier));
                return objectOfClass;
            } else {
                T objectOfClass = constructor.newInstance();
                List<Method> methods = getMutatorMethods(clazz, populateConfig.getBlacklistedMethods());
                classCarrier.getObjectFactory().setter(clazz, methods.size());
                methods.forEach(method -> continuePopulateForMethod(objectOfClass, method, classCarrier));
                return objectOfClass;
            }
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), MUTATOR), e);
        }
    }

    private <T> T continuePopulateUsingBuilder(ClassCarrier<T> classCarrier) {
        switch (populateConfig.getBuilderPattern()) {
            case LOMBOK:
                return continuePopulateUsingLombokBuilder(classCarrier);
            case IMMUTABLES:
                return continuePopulateUsingImmutablesBuilder(classCarrier);
            default:
                throw new PopulateException("Unsupported builder pattern");
        }
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
