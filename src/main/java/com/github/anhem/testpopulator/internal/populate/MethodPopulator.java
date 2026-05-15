package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CALL_METHOD;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.isCollectionLike;
import static com.github.anhem.testpopulator.internal.util.ProtobufUtil.isProtobufAndHasNullArgument;
import static com.github.anhem.testpopulator.internal.util.ProtobufUtil.isProtobufByteString;
import static java.lang.String.format;

public abstract class MethodPopulator {

    public <T, V> void populateForMethod(V objectOfClass, Method method, ClassCarrier<T> classCarrier, Populator populator) {
        String methodName = method.getName();
        try {
            classCarrier.getObjectFactory().method(methodName, method.getParameters().length);
            Object[] args = Stream.of(method.getParameters())
                    .map(parameter -> {
                        if (isProtobufByteString(parameter, classCarrier.getPopulateConfig())) {
                            return populator.populate(classCarrier.toClassCarrier(parameter, methodName));
                        }
                        if (isCollectionLike(parameter.getType())) {
                            return populator.populate(classCarrier.toCollectionCarrier(parameter, methodName));
                        } else {
                            return populator.populate(classCarrier.toClassCarrier(parameter, methodName));
                        }
                    }).toArray();
            if (isProtobufAndHasNullArgument(classCarrier.getPopulateConfig(), args)) {
                return;
            }
            method.invoke(objectOfClass, args);
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CALL_METHOD, methodName, objectOfClass.getClass().getName()), e);
        }
    }
}
