package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static com.github.anhem.testpopulator.config.Strategy.SETTER;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.setAccessible;
import static com.github.anhem.testpopulator.internal.util.SetterUtil.getSetterMethods;
import static java.lang.String.format;

public class SetterPopulator extends MethodPopulator implements PopulatingStrategy {

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            setAccessible(constructor, populateConfig.canAccessNonPublicConstructors());
            T objectOfClass = constructor.newInstance();
            List<Method> methods = getSetterMethods(clazz, populateConfig.getBlacklistedMethods(), populateConfig.getSetterPrefixes());
            classCarrier.getObjectFactory().setter(clazz, methods.size());
            methods.forEach(method -> populateForMethod(objectOfClass, method, classCarrier, populator));
            return objectOfClass;
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_OBJECT, clazz.getName(), SETTER), e);
        }
    }

}
