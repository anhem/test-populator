package com.github.anhem.testpopulator.internal.populate;

import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.carrier.ClassCarrier;
import com.github.anhem.testpopulator.internal.carrier.CollectionCarrier;
import com.github.anhem.testpopulator.internal.carrier.TypeCarrier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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
            } else if (isCollection(clazz)) {
                return populateForCollection(collectionCarrier, populator);
            } else if (isOptional(clazz)) {
                return populateForOptional(collectionCarrier, populator);
            } else if (isStream(clazz)) {
                return populateForStream(collectionCarrier, populator);
            } else if (isScanner(clazz)) {
                return populateForScanner(collectionCarrier, populator);
            } else if (isFuture(clazz)) {
                return populateForFuture(collectionCarrier, populator);
            } else if (isIterator(clazz)) {
                return populateForIterator(collectionCarrier, populator);
            } else if (isIterable(clazz)) {
                return populateForIterable(collectionCarrier, populator);
            }
        } catch (Exception e) {
            throw new PopulateException(format(FAILED_TO_CREATE_COLLECTION, collectionCarrier.getClazz().getTypeName()), e);
        }
        throw new PopulateException(format(MISSING_COLLECTION_TYPE, collectionCarrier.getClazz().getTypeName()));
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForMap(CollectionCarrier<T> classCarrier, Populator populator) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<T> clazz = classCarrier.getClazz();
        if (isEnumMap(clazz)) {
            Class<?> enumClass = (Class<?>) classCarrier.getArgumentTypes().get(0);
            if (enumClass.isEnum()) {
                classCarrier.getObjectFactory().enumMap(clazz, enumClass);
                return (T) populateMap(classCarrier, populator, createEnumMap(enumClass));
            }
        }
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().map(clazz);
            Map<Object, Object> map = (Map<Object, Object>) clazz.getConstructor().newInstance();
            return (T) populateMap(classCarrier, populator, map);
        } else if (isConcurrentMap(clazz) || isConcurrentNavigableMap(clazz)) {
            classCarrier.getObjectFactory().map(ConcurrentSkipListMap.class);
            return (T) populateMap(classCarrier, populator, new ConcurrentSkipListMap<>());
        } else if (isSortedMap(clazz) || isNavigableMap(clazz)) {
            classCarrier.getObjectFactory().map(TreeMap.class);
            return (T) populateMap(classCarrier, populator, new TreeMap<>());
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
        Class<T> clazz = classCarrier.getClazz();
        if (isEnumSet(clazz)) {
            Class<?> enumClass = (Class<?>) classCarrier.getArgumentTypes().get(0);
            if (enumClass.isEnum()) {
                classCarrier.getObjectFactory().enumSet(clazz, enumClass);
                return (T) populateCollection(classCarrier, populator, createEnumSet(enumClass));
            }
        }
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().set(clazz);
            Set<Object> set = (Set<Object>) clazz.getConstructor().newInstance();
            return (T) populateCollection(classCarrier, populator, set);
        } else if (isSortedSet(clazz) || isNavigableSet(clazz)) {
            classCarrier.getObjectFactory().set(TreeSet.class);
            return (T) populateCollection(classCarrier, populator, new TreeSet<>());
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
    private <T> T populateForCollection(CollectionCarrier<T> classCarrier, Populator populator) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<T> clazz = classCarrier.getClazz();
        if (hasConstructors(classCarrier)) {
            classCarrier.getObjectFactory().list(clazz);
            Collection<Object> collection = (Collection<Object>) clazz.getConstructor().newInstance();
            return (T) populateCollection(classCarrier, populator, collection);
        } else if (isDeque(clazz) || isQueue(clazz)) {
            classCarrier.getObjectFactory().list(LinkedList.class);
            return (T) populateCollection(classCarrier, populator, new LinkedList<>());
        } else {
            classCarrier.getObjectFactory().listOf();
            return Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator))
                    .map(value -> (T) List.of(value))
                    .orElseGet(() -> (T) List.of());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForStream(CollectionCarrier<T> classCarrier, Populator populator) {
        Class<T> clazz = classCarrier.getClazz();
        classCarrier.getObjectFactory().stream(clazz);
        if (clazz.equals(IntStream.class)) {
            int val = populator.populate(classCarrier.toClassCarrier(int.class));
            return (T) IntStream.of(val);
        } else if (clazz.equals(LongStream.class)) {
            long val = populator.populate(classCarrier.toClassCarrier(long.class));
            return (T) LongStream.of(val);
        } else if (clazz.equals(DoubleStream.class)) {
            double val = populator.populate(classCarrier.toClassCarrier(double.class));
            return (T) DoubleStream.of(val);
        } else {
            Object value = continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator);
            return (T) Stream.of(value);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForIterator(CollectionCarrier<T> classCarrier, Populator populator) {
        classCarrier.getObjectFactory().iterator(classCarrier.getClazz());
        Object value = continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator);
        return (T) List.of(value).iterator();
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForScanner(CollectionCarrier<T> classCarrier, Populator populator) {
        classCarrier.getObjectFactory().scanner(classCarrier.getClazz());
        String value = (String) continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator);
        return (T) new Scanner(value);
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForFuture(CollectionCarrier<T> classCarrier, Populator populator) {
        classCarrier.getObjectFactory().future(classCarrier.getClazz());
        Object value = continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator);
        return (T) CompletableFuture.completedFuture(value);
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForIterable(CollectionCarrier<T> classCarrier, Populator populator) {
        classCarrier.getObjectFactory().iterable(classCarrier.getClazz());
        Object value = continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator);
        return (T) List.of(value);
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Object> populateMap(CollectionCarrier<?> classCarrier, Populator populator, Map map) {
        Optional<Object> key = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator));
        Optional<Object> value = Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(1)), populator));
        key.ifPresent(k -> map.put(k, value.orElse(null)));
        return map;
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E>> EnumSet<E> createEnumSet(Class<?> enumClass) {
        return EnumSet.noneOf((Class<E>) enumClass);
    }

    @SuppressWarnings("unchecked")
    private <K extends Enum<K>, V> EnumMap<K, V> createEnumMap(Class<?> enumClass) {
        return new EnumMap<>((Class<K>) enumClass);
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> populateCollection(CollectionCarrier<?> classCarrier, Populator populator, Collection collection) {
        Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator))
                .ifPresent(collection::add);
        return collection;
    }

    @SuppressWarnings("unchecked")
    private <T> T populateForOptional(CollectionCarrier<T> classCarrier, Populator populator) {
        classCarrier.getObjectFactory().optional();
        return (T) Optional.ofNullable(continuePopulateWithType(classCarrier.toTypeCarrier(classCarrier.getArgumentTypes().get(0)), populator));
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
