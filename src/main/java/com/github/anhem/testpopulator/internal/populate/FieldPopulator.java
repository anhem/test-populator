package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import static com.github.anhem.testpopulator.config.Strategy.FIELD;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_SET_FIELD;
import static com.github.anhem.testpopulator.internal.util.KotlinUtil.isKotlinDelegate;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static java.lang.String.format;

public class FieldPopulator implements PopulatingStrategy {

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            setAccessible(constructor, populateConfig.isAccessNonPublicConstructors());
            T objectOfClass = constructor.newInstance();
            getDeclaredFields(clazz, populateConfig.getBlacklistedFields()).stream()
                    .filter(field -> !Modifier.isFinal(field.getModifiers()))
                    .filter(field -> !isKotlinDelegate(field, populateConfig.isKotlinSupport()))
                    .forEach(field -> {
                        try {
                            setAccessible(field, objectOfClass);
                            if (isCollectionLike(field.getType())) {
                                CollectionCarrier<Object> collectionCarrier = classCarrier.toCollectionCarrier(
                                        field.getType(),
                                        field.getName(),
                                        toArgumentTypes(field.getGenericType(), field.getType()).toArray(new Type[0])
                                );
                                field.set(objectOfClass, populator.populate(collectionCarrier));
                            } else {
                                field.set(objectOfClass, populator.populate(classCarrier.toClassCarrier(field.getType(), field.getName())));
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
