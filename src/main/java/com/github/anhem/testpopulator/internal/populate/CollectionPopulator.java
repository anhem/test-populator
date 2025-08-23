package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;
import com.github.anhem.testpopulator.internal.carrier.TypeCarrier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_COLLECTION;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.MISSING_COLLECTION_TYPE;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.*;
import static java.lang.String.format;

public class CollectionPopulator implements PopulatingStrategy {

    @Override
    public <T> T populate(ClassCarrier<T> classCarrier, Populator populator) {
        return doPopulate((CollectionCarrier<T>) classCarrier, populator);
    }

    private <T> T doPopulate(CollectionCarrier<T> collectionCarrier, Populator populator) {
        try {
            Class<T> clazz = collectionCarrier.getClazz();
            if (isMap(clazz)) {
                return populateForMap(collectionCarrier, populator);
            } else if (isSet(clazz)) {
                return populateForSet(collectionCarrier, populator);
            } else if (isMapEntry(clazz)) {
                return populateForMapEntry(collectionCarrier, populator);
            } else if (isCollectionLike(clazz)) {
                return populateForList(collectionCarrier, populator);
            }
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_COLLECTION, collectionCarrier.getClazz().getTypeName()), e);
        }
        throw new PopulateException(format(MISSING_COLLECTION_TYPE, collectionCarrier.getClazz().getTypeName()));
    }


    @SuppressWarnings("unchecked")
    private <T> T populateForMap(CollectionCarrier<T> classCarrier, Populator populator) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().map(classCarrier.getClazz());
            Map<Object, Object> map = (Map<Object, Object>) classCarrier.getClazz().getConstructor().newInstance();
            Optional<Object> key = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator));
            Optional<Object> value = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(1)), populator));
            key.ifPresent(k -> map.put(k, value.orElse(null)));
            return (T) map;
        } else {
            classCarrier.getObjectFactory().mapOf();
            Optional<Object> key = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator));
            Optional<Object> value = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(1)), populator));
            if (key.isPresent() && value.isPresent()) {
                return (T) Map.of(key.get(), value.get());
            }
            return (T) Map.of();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForSet(CollectionCarrier<T> classCarrier, Populator populator) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().set(classCarrier.getClazz());
            Set<Object> set = (Set<Object>) classCarrier.getClazz().getConstructor().newInstance();
            Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator))
                    .ifPresent(set::add);
            return (T) set;
        } else {
            classCarrier.getObjectFactory().setOf();
            return Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator))
                    .map(value -> (T) Set.of(value))
                    .orElseGet(() -> (T) Set.of());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForMapEntry(CollectionCarrier<T> classCarrier, Populator populator) {
        classCarrier.getObjectFactory().mapEntry(classCarrier.getClazz());
        Object key = continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator);
        Object value = continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(1)), populator);
        return (T) new AbstractMap.SimpleEntry<>(key, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForList(CollectionCarrier<T> classCarrier, Populator populator) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().list(classCarrier.getClazz());
            List<Object> list = (List<Object>) classCarrier.getClazz().getConstructor().newInstance();
            Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator))
                    .ifPresent(list::add);
            return (T) list;
        } else {
            classCarrier.getObjectFactory().listOf();
            return Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator))
                    .map(value -> (T) List.of(value))
                    .orElseGet(() -> (T) List.of());
        }
    }

    private Object continuePopulateWithType(TypeCarrier typeCarrier, Populator populator) {
        Type type = typeCarrier.getType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return populator.populate(typeCarrier.toCollectionCarrier(parameterizedType.getRawType(), parameterizedType.getActualTypeArguments()));
        }
        return populator.populate(typeCarrier.toClassCarrier(type));
    }
}
