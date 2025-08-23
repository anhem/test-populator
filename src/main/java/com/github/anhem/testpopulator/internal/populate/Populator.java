package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.value.StaticMethodPopulator;
import com.github.anhem.testpopulator.internal.value.ValueFactory;

import java.lang.reflect.Array;

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
    private final ConstructorPopulator constructorPopulator;
    private final FieldPopulator fieldPopulator;
    private final CollectionPopulator collectionPopulator;
    private final SetterPopulator setterPopulator;
    private final MutatorPopulator mutatorPopulator;
    private final BuilderPopulator builderPopulator;
    private final StaticMethodPopulator staticMethodPopulator;

    public Populator(ValueFactory valueFactory) {
        this.valueFactory = valueFactory;
        this.constructorPopulator = new ConstructorPopulator();
        this.fieldPopulator = new FieldPopulator();
        this.collectionPopulator = new CollectionPopulator();
        this.setterPopulator = new SetterPopulator();
        this.mutatorPopulator = new MutatorPopulator(this.constructorPopulator);
        this.builderPopulator = new BuilderPopulator();
        this.staticMethodPopulator = new StaticMethodPopulator();
    }

    public <T> T populate(ClassCarrier<T> classCarrier) {
        Class<T> clazz = classCarrier.getClazz();
        if (valueFactory.hasType(clazz)) {
            return createValue(classCarrier);
        }
        if (alreadyVisited(classCarrier, classCarrier.getPopulateConfig().isNullOnCircularDependency())) {
            return createNullValue(classCarrier);
        }
        if (isCollectionCarrier(classCarrier)) {
            return collectionPopulator.populate(classCarrier, this);
        }
        if (clazz.isArray()) {
            return populateForArray(classCarrier);
        }
        return populateWithStrategies(classCarrier);
    }

    private <T> T createValue(ClassCarrier<T> classCarrier) {
        T value = valueFactory.createValue(classCarrier.getClazz());
        classCarrier.getObjectFactory().value(value);
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
        Object value = populate(classCarrier.toClassCarrier(componentType));
        Object array = Array.newInstance(componentType, 1);
        Array.set(array, 0, value);
        return (T) array;
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
        if (isProtobufByteString(clazz, populateConfig)) {
            return staticMethodPopulator.populate(classCarrier, this);
        }
        throw new PopulateException(format(NO_MATCHING_STRATEGY, clazz.getName(), populateConfig.getStrategyOrder()));
    }
}
