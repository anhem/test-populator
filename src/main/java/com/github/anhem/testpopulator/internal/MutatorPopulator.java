package com.github.anhem.testpopulator.internal;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static com.github.anhem.testpopulator.config.Strategy.MUTATOR;
import static com.github.anhem.testpopulator.internal.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.util.MutatorUtil.getConstructor;
import static com.github.anhem.testpopulator.internal.util.MutatorUtil.getMutatorMethods;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.setAccessible;
import static java.lang.String.format;

public class MutatorPopulator extends MethodPopulator implements PopulatingStrategy {

    private final ConstructorPopulator constructorPopulator;

    public MutatorPopulator(ConstructorPopulator constructorPopulator) {
        this.constructorPopulator = constructorPopulator;
    }

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        return continuePopulateUsingMutator(classCarrier, populator);
    }

    private <T> T continuePopulateUsingMutator(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Constructor<T> constructor = getConstructor(clazz, populateConfig.canAccessNonPublicConstructors(), populateConfig.getConstructorType());
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            if (constructor.getParameterCount() > 0) {
                T objectOfClass = constructorPopulator.continuePopulateUsingConstructor(constructor, classCarrier, populator);
                List<Method> methods = getMutatorMethods(clazz, populateConfig.getBlacklistedMethods());
                classCarrier.getObjectFactory().mutator(clazz, methods.size());
                methods.forEach(method -> continuePopulateForMethod(objectOfClass, method, classCarrier, populator));
                return objectOfClass;
            } else {
                T objectOfClass = constructor.newInstance();
                List<Method> methods = getMutatorMethods(clazz, populateConfig.getBlacklistedMethods());
                classCarrier.getObjectFactory().setter(clazz, methods.size());
                methods.forEach(method -> continuePopulateForMethod(objectOfClass, method, classCarrier, populator));
                return objectOfClass;
            }
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), MUTATOR), e);
        }
    }
}
