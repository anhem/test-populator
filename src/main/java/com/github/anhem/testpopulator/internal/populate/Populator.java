package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.MethodType;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;
import com.github.anhem.testpopulator.internal.carrier.TypeCarrier;
import com.github.anhem.testpopulator.internal.value.ValueFactory;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.internal.util.BuilderUtil.isMatchingBuilderStrategy;
import static com.github.anhem.testpopulator.internal.util.MutatorUtil.isMatchingMutatorStrategy;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static com.github.anhem.testpopulator.internal.util.ProtobufUtil.isProtobufByteString;
import static com.github.anhem.testpopulator.internal.util.SetterUtil.isMatchingSetterStrategy;
import static com.github.anhem.testpopulator.internal.util.StaticMethodUtil.isMatchingStaticMethodStrategy;
import static java.lang.String.format;

public class Populator {

    private final ValueFactory valueFactory;
    private final ConstructorPopulator constructorPopulator = new ConstructorPopulator();
    private final FieldPopulator fieldPopulator = new FieldPopulator();
    private final CollectionPopulator collectionPopulator = new CollectionPopulator();
    private final SetterPopulator setterPopulator = new SetterPopulator();
    private final MutatorPopulator mutatorPopulator = new MutatorPopulator(constructorPopulator);
    private final BuilderPopulator builderPopulator = new BuilderPopulator();
    private final StaticMethodPopulator staticMethodPopulator = new StaticMethodPopulator();

    public Populator(ValueFactory valueFactory) {
        this.valueFactory = valueFactory;
    }

    public <T> T populate(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        if (valueFactory.hasType(clazz, classCarrier.getName())) {
            return createValue(classCarrier);
        }
        if (classCarrier.alreadyVisited()) {
            return createNullValue(classCarrier);
        }
        if (clazz.isArray()) {
            return populateForArray(classCarrier);
        }
        if (classCarrier instanceof CollectionCarrier) {
            return collectionPopulator.populate(classCarrier, this);
        }

        if (isProtobufByteString(clazz, classCarrier.getPopulateConfig())) {
            return staticMethodPopulator.populate(classCarrier, this, MethodType.SIMPLEST);
        }
        if (isCollectionLike(clazz)) {
            return populate(classCarrier.toCollectionCarrier(clazz));
        }
        return populateWithStrategies(classCarrier);
    }

    private <T> T createValue(ClassCarrier<T> classCarrier) {
        T value = valueFactory.createValue(classCarrier.getClazz(), classCarrier.getName());
        classCarrier.getObjectFactory().value(value, classCarrier.getClazz(), classCarrier.getName());
        return value;
    }

    private static <T> T createNullValue(ClassCarrier<T> classCarrier) {
        classCarrier.getObjectFactory().nullValue(classCarrier.getClazz());
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForArray(ClassCarrier<T> classCarrier) {
        Class<?> componentType = classCarrier.getClazz().getComponentType();
        classCarrier.getObjectFactory().array(componentType);
        Object value;
        if (classCarrier instanceof CollectionCarrier) {
            CollectionCarrier<T> collectionCarrier = (CollectionCarrier<T>) classCarrier;
            value = continuePopulateWithType(collectionCarrier.toTypeCarrier(collectionCarrier.getArgumentTypes().get(0)));
        } else {
            value = populate(classCarrier.toClassCarrier(componentType));
        }
        Object array = Array.newInstance(componentType, 1);
        Array.set(array, 0, value);
        return (T) array;
    }

    public Object continuePopulateWithType(TypeCarrier typeCarrier) {
        Type type = typeCarrier.getType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return populate(typeCarrier.toCollectionCarrier(parameterizedType.getRawType(), parameterizedType.getActualTypeArguments()));
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            return populate(typeCarrier.toCollectionCarrier(type, new Type[]{genericArrayType.getGenericComponentType()}));
        }
        return populate(typeCarrier.toClassCarrier(type));
    }

    private <T> T populateWithStrategies(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        for (Strategy strategy : populateConfig.getStrategyOrder()) {
            if (isMatchingConstructorStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors())) {
                return constructorPopulator.populate(classCarrier, this);
            }
            if (isMatchingSetterStrategy(strategy, clazz, populateConfig.getSetterPrefixes(), populateConfig.canAccessNonPublicConstructors())) {
                return setterPopulator.populate(classCarrier, this);
            }
            if (isMatchingMutatorStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors(), populateConfig.getConstructorType())) {
                return mutatorPopulator.populate(classCarrier, this);
            }
            if (isMatchingFieldStrategy(strategy, clazz, populateConfig.canAccessNonPublicConstructors())) {
                return fieldPopulator.populate(classCarrier, this);
            }
            if (isMatchingBuilderStrategy(strategy, clazz, populateConfig.getBuilderPattern(), populateConfig.getBuilderMethod())) {
                return builderPopulator.populate(classCarrier, this);
            }
            if (isMatchingStaticMethodStrategy(strategy, clazz)) {
                return staticMethodPopulator.populate(classCarrier, this);
            }
        }
        throw new PopulateException(format(NO_MATCHING_STRATEGY, clazz.getName(), populateConfig.getStrategyOrder()));
    }
}
