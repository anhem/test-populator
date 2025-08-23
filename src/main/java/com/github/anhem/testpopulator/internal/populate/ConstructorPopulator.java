package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.stream.IntStream;

import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static java.lang.String.format;

public class ConstructorPopulator implements PopulatingStrategy {

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Constructor<T> constructor = getLargestConstructor(clazz, populateConfig.canAccessNonPublicConstructors());
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            return populateUsingConstructor(constructor, classCarrier, populator);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), CONSTRUCTOR), e);
        }
    }

    protected <T> T populateUsingConstructor(Constructor<T> constructor, ClassCarrier<T> classCarrier, Populator populator) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        classCarrier.getObjectFactory().constructor(classCarrier.getClazz(), constructor.getParameterCount());
        Object[] arguments = IntStream.range(0, constructor.getParameterCount()).mapToObj(i -> {
            Parameter parameter = constructor.getParameters()[i];
            if (isCollectionLike(parameter.getType())) {
                return populator.populate(classCarrier.toCollectionCarrier(parameter));
            } else {
                return populator.populate(classCarrier.toClassCarrier(parameter));
            }
        }).toArray();
        return constructor.newInstance(arguments);
    }
}
