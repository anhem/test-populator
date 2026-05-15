package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_POPULATE_KOTLIN_TYPE;
import static com.github.anhem.testpopulator.internal.util.KotlinUtil.*;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.isCollectionLike;
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
                                if (isCollectionLike(parameter.getType())) {
                                    return populator.populate(classCarrier.toCollectionCarrier(parameter));
                                } else {
                                    return populator.populate(classCarrier.toClassCarrier(parameter));
                                }
                            }).toArray());
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
