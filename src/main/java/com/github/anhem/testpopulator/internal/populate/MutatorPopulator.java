package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.github.anhem.testpopulator.config.Strategy.MUTATOR;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_SET_FIELD;
import static com.github.anhem.testpopulator.internal.util.MutatorUtil.*;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.setAccessible;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

public class MutatorPopulator extends MethodPopulator implements PopulatingStrategy {

    private final ConstructorPopulator constructorPopulator;

    public MutatorPopulator(ConstructorPopulator constructorPopulator) {
        this.constructorPopulator = constructorPopulator;
    }

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Constructor<T> constructor = getConstructor(clazz, populateConfig.isAccessNonPublicConstructors(), populateConfig.getConstructorType());
            setAccessible(constructor, populateConfig.isAccessNonPublicConstructors());
            T objectOfClass;
            if (constructor.getParameterCount() > 0) {
                objectOfClass = constructorPopulator.populateUsingConstructor(constructor, classCarrier, populator);
            } else {
                objectOfClass = constructor.newInstance();
            }
            List<Method> methods = populateConfig.isMutatorMethods()
                    ? getMutatorMethods(clazz, populateConfig.getBlacklistedMethods())
                    : emptyList();
            List<Field> publicFields = populateConfig.isMutatorPublicFields()
                    ? getPublicFields(clazz, populateConfig.getBlacklistedFields())
                    : emptyList();
            int totalExpectedChildren = methods.size() + publicFields.size();
            if (constructor.getParameterCount() > 0) {
                classCarrier.getObjectFactory().mutator(clazz, totalExpectedChildren);
            } else {
                classCarrier.getObjectFactory().setter(clazz, totalExpectedChildren);
            }
            methods.forEach(method -> populateForMethod(objectOfClass, method, classCarrier, populator));
            publicFields.forEach(field -> populateForField(objectOfClass, field, classCarrier, populator));
            return objectOfClass;
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), MUTATOR), e);
        }
    }

    private <T, V> void populateForField(V objectOfClass, Field field, ClassCarrier<T> classCarrier, Populator populator) {
        String fieldName = field.getName();
        try {
            classCarrier.getObjectFactory().publicField(fieldName, 1);
            setAccessible(field, objectOfClass);
            field.set(objectOfClass, populator.populate(classCarrier.createChild(field.getGenericType(), fieldName)));
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_SET_FIELD, fieldName, objectOfClass.getClass().getName()), e);
        }
    }

}
