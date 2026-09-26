package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.util.KotlinUtil.isKotlinConstructor;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.getLargestConstructor;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.setAccessible;
import static java.lang.String.format;

public class ConstructorPopulator implements PopulatingStrategy {

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Constructor<T> constructor = getLargestConstructor(clazz, populateConfig.isAccessNonPublicConstructors());
            setAccessible(constructor, populateConfig.isAccessNonPublicConstructors());
            return populateUsingConstructor(constructor, classCarrier, populator);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), CONSTRUCTOR), e);
        }
    }

    protected <T> T populateUsingConstructor(Constructor<T> constructor, ClassCarrier<T> classCarrier, Populator populator) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        int parameterCount = constructor.getParameterCount();
        boolean isKotlinConstructor = isKotlinConstructor(constructor, classCarrier.getPopulateConfig().isKotlinSupport());

        if (isKotlinConstructor) {
            int maskCount = (parameterCount - 2) / 32 + 1;
            int realParameterCount = parameterCount - maskCount - 1;
            Constructor<T> primaryConstructor = findPrimaryConstructor(constructor, realParameterCount);

            if (classCarrier.getPopulateConfig().isUseKotlinDefaultValues()) {
                return populateWithKotlinDefaults(constructor, primaryConstructor, classCarrier, populator, realParameterCount, maskCount);
            }

            classCarrier.getObjectFactory().constructor(
                    classCarrier.getClazz(),
                    realParameterCount,
                    !Modifier.isPublic(constructor.getModifiers()),
                    primaryConstructor.getParameters()
            );
            Object[] arguments = populateKotlinArguments(primaryConstructor, classCarrier, populator, realParameterCount, maskCount);
            return constructor.newInstance(arguments);
        }

        classCarrier.getObjectFactory().constructor(
                classCarrier.getClazz(),
                parameterCount,
                !Modifier.isPublic(constructor.getModifiers()),
                constructor.getParameters()
        );
        Object[] arguments = populateArguments(constructor, classCarrier, populator, parameterCount);
        return constructor.newInstance(arguments);
    }

    private <T> T populateWithKotlinDefaults(Constructor<T> syntheticCtor, Constructor<T> primaryCtor, ClassCarrier<T> classCarrier, Populator populator, int realParamCount, int maskCount) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        java.util.Set<Integer> defaultedIndices = com.github.anhem.testpopulator.internal.util.KotlinUtil.detectDefaultedParameters(syntheticCtor, primaryCtor, classCarrier, populator, realParamCount, maskCount);

        int nonDefaultCount = realParamCount - defaultedIndices.size();
        java.util.List<String> parameterNames = new java.util.ArrayList<>();
        java.util.List<Boolean> isVarargs = new java.util.ArrayList<>();
        for (int i = 0; i < realParamCount; i++) {
            if (!defaultedIndices.contains(i)) {
                parameterNames.add(primaryCtor.getParameters()[i].getName());
                isVarargs.add(primaryCtor.getParameters()[i].isVarArgs());
            }
        }

        classCarrier.getObjectFactory().kotlinDefaultConstructor(
                classCarrier.getClazz(),
                nonDefaultCount,
                parameterNames,
                isVarargs
        );

        Object[] realArgs = new Object[realParamCount];
        for (int i = 0; i < realParamCount; i++) {
            if (defaultedIndices.contains(i)) {
                realArgs[i] = com.github.anhem.testpopulator.internal.util.KotlinUtil.getDefaultValueForType(primaryCtor.getParameterTypes()[i]);
            } else {
                realArgs[i] = populateArgument(primaryCtor, classCarrier, populator, i);
            }
        }

        Object[] masks = IntStream.range(0, maskCount).mapToObj(i -> -1).toArray();
        Object[] arguments = Stream.concat(Arrays.stream(realArgs), Stream.concat(Arrays.stream(masks), Stream.of((Object) null))).toArray();
        return syntheticCtor.newInstance(arguments);
    }

    private <T> Object[] populateArguments(Constructor<T> constructor, ClassCarrier<T> classCarrier, Populator populator, int parameterCount) {
        return IntStream.range(0, parameterCount)
                .mapToObj(i -> populateArgument(constructor, classCarrier, populator, i))
                .toArray();
    }

    private <T> Object populateArgument(Constructor<T> constructor, ClassCarrier<T> classCarrier, Populator populator, int i) {
        Parameter parameter = constructor.getParameters()[i];
        return populator.populate(classCarrier.createChild(parameter));
    }

    private <T> Object[] populateKotlinArguments(Constructor<T> primaryConstructor, ClassCarrier<T> classCarrier, Populator populator, int realParameterCount, int maskCount) {
        Object[] arguments = IntStream.range(0, realParameterCount)
                .mapToObj(i -> populateArgument(primaryConstructor, classCarrier, populator, i))
                .toArray();
        Object[] masks = IntStream.range(0, maskCount)
                .mapToObj(i -> 0)
                .toArray();
        return Stream.concat(Arrays.stream(arguments), Stream.concat(Arrays.stream(masks), Stream.of((Object) null)))
                .toArray();
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findPrimaryConstructor(Constructor<T> syntheticConstructor, int realParameterCount) {
        return (Constructor<T>) Arrays.stream(syntheticConstructor.getDeclaringClass().getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == realParameterCount)
                .findFirst()
                .orElse(syntheticConstructor);
    }
}
