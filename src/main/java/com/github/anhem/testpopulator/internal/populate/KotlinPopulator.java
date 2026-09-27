package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_POPULATE_KOTLIN_TYPE;
import static com.github.anhem.testpopulator.internal.util.KotlinUtil.*;
import static java.lang.String.format;

public class KotlinPopulator implements PopulatingStrategy {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            if (isKotlinSingleton(clazz)) {
                return populateSingleton(classCarrier);
            }
            if (hasKotlinCompanion(clazz)) {
                Object companionObject = getCompanionObject(clazz);
                if (companionObject != null) {
                    Method companionMethod = getCompanionMethod(companionObject.getClass(), clazz, populateConfig.getBlacklistedMethods(), populateConfig.getMethodType());
                    classCarrier.getObjectFactory().staticMethod(clazz, getCompanionMethodName(companionMethod), companionMethod.getParameters().length);
                    return (T) companionMethod.invoke(companionObject, Stream.of(companionMethod.getParameters())
                            .map(parameter -> {
                                return populator.populate(classCarrier.createChild(parameter));
                            }).toArray());
                }
            }
            if (isKotlinValueClass(clazz)) {
                Method valueClassConstructor = getKotlinValueClassConstructor(clazz);
                if (valueClassConstructor != null) {
                    classCarrier.getObjectFactory().staticMethod(clazz, valueClassConstructor.getName(), valueClassConstructor.getParameters().length);
                    return (T) valueClassConstructor.invoke(null, Stream.of(valueClassConstructor.getParameters())
                            .map(parameter -> {
                                return populator.populate(classCarrier.createChild(parameter));
                            }).toArray());
                }
            }
            if (isKotlinSealedClass(clazz)) {
                List<Class<?>> sealedSubclasses = getKotlinSealedSubclasses(clazz);
                if (!sealedSubclasses.isEmpty()) {
                    Class<?> subclass = sealedSubclasses.get(0);
                    return (T) populator.populate(classCarrier.createChild(subclass));
                }
            }
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_POPULATE_KOTLIN_TYPE, clazz.getName()), e);
        }
        throw new PopulateException(format(FAILED_TO_POPULATE_KOTLIN_TYPE, clazz.getName()));
    }

    @SuppressWarnings("unchecked")
    private <T> T populateSingleton(ClassCarrier<T> classCarrier) {
        try {
            Class<T> clazz = classCarrier.getClazz();
            T value = (T) clazz.getField("INSTANCE").get(null);
            classCarrier.getObjectFactory().value(value, clazz, classCarrier.getName());
            return value;
        } catch (Exception e) {
            throw new PopulateException(format("Failed to get INSTANCE for %s", classCarrier.getClazz().getName()), e);
        }
    }
}
