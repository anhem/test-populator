package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        int parameterCount = constructor.getParameterCount();
        classCarrier.getObjectFactory().constructor(classCarrier.getClazz(), parameterCount);
        if (classCarrier.getPopulateConfig().isKotlinSupport() && isKotlinConstructor(constructor)) {
            int maskCount = (parameterCount - 2) / 32 + 1;
            int realParameterCount = parameterCount - maskCount - 1;
            Object[] arguments = IntStream.range(0, realParameterCount).mapToObj(i -> populateArgument(constructor, classCarrier, populator, i)).toArray();
            Object[] masks = IntStream.range(0, maskCount).mapToObj(i -> 0).toArray();
            Object[] allArguments = Stream.concat(Arrays.stream(arguments), Stream.concat(Arrays.stream(masks), Stream.of((Object) null))).toArray();

            Arrays.stream(masks).forEach(mask -> classCarrier.getObjectFactory().value((Integer) mask, int.class, null));
            classCarrier.getObjectFactory().nullValue(constructor.getParameterTypes()[parameterCount - 1]);
            return constructor.newInstance(allArguments);
        } else {
            Object[] arguments = IntStream.range(0, parameterCount).mapToObj(i -> populateArgument(constructor, classCarrier, populator, i)).toArray();
            return constructor.newInstance(arguments);
        }
    }

    private <T> Object populateArgument(Constructor<T> constructor, ClassCarrier<T> classCarrier, Populator populator, int i) {
        Parameter parameter = constructor.getParameters()[i];
        if (isCollectionLike(parameter.getType())) {
            return populator.populate(classCarrier.toCollectionCarrier(parameter));
        } else {
            return populator.populate(classCarrier.toClassCarrier(parameter));
        }
    }
}
