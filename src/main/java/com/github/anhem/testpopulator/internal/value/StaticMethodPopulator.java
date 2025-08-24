package com.github.anhem.testpopulator.internal.value;

import com.github.anhem.testpopulator.config.MethodType;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.populate.PopulatingStrategy;
import com.github.anhem.testpopulator.internal.populate.Populator;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CALL_STATIC_METHOD;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.isCollectionLike;
import static com.github.anhem.testpopulator.internal.util.StaticMethodUtil.getStaticMethod;
import static java.lang.String.format;

public class StaticMethodPopulator implements PopulatingStrategy {

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        return populate(classCarrier, populator, classCarrier.getPopulateConfig().getMethodType());
    }

    @SuppressWarnings("unchecked")
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator, MethodType methodType) {
        Class<T> clazz = classCarrier.getClazz();
        PopulateConfig populateConfig = classCarrier.getPopulateConfig();
        Method staticMethod = getStaticMethod(clazz, populateConfig.getBlacklistedMethods(), methodType);
        try {
            classCarrier.getObjectFactory().staticMethod(clazz, staticMethod.getName(), staticMethod.getParameters().length);
            return (T) staticMethod.invoke(null, Stream.of(staticMethod.getParameters())
                    .map(parameter -> {
                        if (isCollectionLike(parameter.getType())) {
                            return populator.populate(classCarrier.toCollectionCarrier(parameter));
                        } else {
                            return populator.populate(classCarrier.toClassCarrier(parameter));
                        }
                    }).toArray());
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CALL_STATIC_METHOD, staticMethod.getName(), clazz.getName()), e);
        }
    }
}
