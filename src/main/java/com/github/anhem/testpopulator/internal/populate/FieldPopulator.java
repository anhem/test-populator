package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;

import static com.github.anhem.testpopulator.config.Strategy.FIELD;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_SET_FIELD;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static java.lang.String.format;

public class FieldPopulator implements PopulatingStrategy {

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            T objectOfClass = constructor.newInstance();
            getDeclaredFields(clazz, populateConfig.getBlacklistedFields()).stream()
                    .filter(field -> !Modifier.isFinal(field.getModifiers()))
                    .forEach(field -> {
                        try {
                            setAccessible(field, objectOfClass);
                            if (isCollectionLike(field.getType())) {
                                field.set(objectOfClass, populator.populateWithOverrides(classCarrier.toCollectionCarrier(field.getType(), ((ParameterizedType) field.getGenericType()).getActualTypeArguments())));
                            } else {
                                field.set(objectOfClass, populator.populateWithOverrides(classCarrier.toClassCarrier(field.getType())));
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
}
