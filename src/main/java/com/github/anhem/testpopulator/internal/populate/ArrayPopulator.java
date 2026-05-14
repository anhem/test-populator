package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;
import com.github.anhem.testpopulator.internal.carrier.TypeCarrier;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ArrayPopulator implements PopulatingStrategy {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        Class<?> componentType = classCarrier.getClazz().getComponentType();
        classCarrier.getObjectFactory().array(componentType);
        Object value;
        if (classCarrier instanceof CollectionCarrier) {
            CollectionCarrier<T> collectionCarrier = (CollectionCarrier<T>) classCarrier;
            value = continuePopulateWithType(collectionCarrier.toTypeCarrier(collectionCarrier.getArgumentTypes().get(0)), populator);
        } else {
            value = populator.populate(classCarrier.toClassCarrier(componentType));
        }
        Object array = Array.newInstance(componentType, 1);
        Array.set(array, 0, value);
        return (T) array;
    }

    private Object continuePopulateWithType(TypeCarrier typeCarrier, Populator populator) {
        Type type = typeCarrier.getType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return populator.populate(typeCarrier.toCollectionCarrier(parameterizedType.getRawType(), parameterizedType.getActualTypeArguments()));
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            return populator.populate(typeCarrier.toCollectionCarrier(type, new Type[]{genericArrayType.getGenericComponentType()}));
        }
        return populator.populate(typeCarrier.toClassCarrier(type));
    }
}
