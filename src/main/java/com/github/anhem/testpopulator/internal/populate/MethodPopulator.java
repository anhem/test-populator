package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CALL_METHOD;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.isCollectionLike;
import static com.github.anhem.testpopulator.internal.util.ProtobufUtil.isProtobufByteString;
import static java.lang.String.format;

public abstract class MethodPopulator {

    public <T, V> void populateForMethod(V objectOfClass, Method method, ClassCarrier<T> classCarrier, Populator populator) {
        try {
            classCarrier.getObjectFactory().method(method.getName(), method.getParameters().length);
            method.invoke(objectOfClass, Stream.of(method.getParameters())
                    .map(parameter -> {
                        if (isProtobufByteString(parameter, classCarrier.getPopulateConfig())) {
                            return populator.populate(classCarrier.toClassCarrier(parameter));
                        }
                        if (isCollectionLike(parameter.getType())) {
                            return populator.populate(classCarrier.toCollectionCarrier(parameter));
                        } else {
                            return populator.populate(classCarrier.toClassCarrier(parameter));
                        }
                    }).toArray());
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CALL_METHOD, method.getName(), objectOfClass.getClass().getName()), e);
        }
    }
}
