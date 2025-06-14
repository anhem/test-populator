package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.ObjectException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.BuildType.*;
import static com.github.anhem.testpopulator.internal.object.ObjectBuilder.NULL;
import static com.github.anhem.testpopulator.internal.util.FileWriterUtil.*;

public class ObjectFactoryImpl implements ObjectFactory {

    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";

    private static final Map<Class<?>, Function<Object, String>> stringSuppliers = new HashMap<>();

    static {
        stringSuppliers.put(Integer.class, Object::toString);
        stringSuppliers.put(Long.class, object -> object + "L");
        stringSuppliers.put(Double.class, Object::toString);
        stringSuppliers.put(Boolean.class, Object::toString);
        stringSuppliers.put(BigDecimal.class, object -> String.format("BigDecimal.valueOf(%d)", ((BigDecimal) object).intValue()));
        stringSuppliers.put(String.class, object -> "\"" + object + "\"");
        stringSuppliers.put(Character.class, object -> "'" + object + "'");
        stringSuppliers.put(LocalDate.class, object -> String.format("LocalDate.parse(\"%s\")", object));
        stringSuppliers.put(LocalDateTime.class, object -> String.format("LocalDateTime.parse(\"%s\")", object));
        stringSuppliers.put(ZonedDateTime.class, object -> String.format("ZonedDateTime.parse(\"%s\")", object));
        stringSuppliers.put(Instant.class, object -> String.format("Instant.parse(\"%s\")", object));
        stringSuppliers.put(Date.class, object -> String.format("new Date(%sL)", ((Date) object).getTime()));
        stringSuppliers.put(UUID.class, object -> String.format("UUID.fromString(\"%s\")", object));
        stringSuppliers.put(Byte.class, object -> String.format("Byte.parseByte(\"%s\")", object));
        stringSuppliers.put(Short.class, object -> String.format("Short.parseShort(\"%s\")", object));
        stringSuppliers.put(Float.class, object -> object + "f");
        stringSuppliers.put(LocalTime.class, object -> String.format("LocalTime.parse(\"%s\")", object));
        stringSuppliers.put(BigInteger.class, object -> String.format("BigInteger.valueOf(%d)", ((BigInteger) object).intValue()));
        stringSuppliers.put(OffsetDateTime.class, object -> String.format("OffsetDateTime.parse(\"%s\")", object));
        stringSuppliers.put(OffsetTime.class, object -> String.format("OffsetTime.parse(\"%s\")", object));
        stringSuppliers.put(Duration.class, object -> String.format("Duration.ofSeconds(%d)", ((Duration) object).getSeconds()));
        stringSuppliers.put(Period.class, object -> String.format("Period.ofDays(%d)", ((Period) object).getDays()));
        stringSuppliers.put(java.sql.Date.class, object -> String.format("java.sql.Date.valueOf(\"%s\")", object.toString()));
        stringSuppliers.put(Time.class, object -> String.format("Time.valueOf(\"%s\")", object.toString()));
        stringSuppliers.put(Timestamp.class, object -> String.format("Timestamp.valueOf(\"%s\")", object.toString()));
    }

    private final PopulateConfig populateConfig;
    private final Map<Class<?>, Integer> classCounters;
    private ObjectBuilder currentObjectBuilder;

    public ObjectFactoryImpl(PopulateConfig populateConfig) {
        this.populateConfig = populateConfig;
        this.classCounters = new HashMap<>();
    }

    @Override
    public <T> void constructor(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, CONSTRUCTOR, expectedChildren);
    }

    @Override
    public <T> void setter(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, SETTER, expectedChildren);
    }

    @Override
    public <T> void mutator(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, MUTATOR, expectedChildren);
    }

    @Override
    public <T> void builder(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, BUILDER, expectedChildren);
    }

    @Override
    public void method(String methodName, int expectedChildren) {
        setNextObjectBuilder(new ObjectBuilder(methodName, METHOD, expectedChildren));
        if (expectedChildren == 0) {
            setPreviousObjectBuilder();
        }
    }

    @Override
    public <T> void set(Class<T> clazz) {
        setNextObjectBuilder(clazz, SET, 1);
        method("add", 1);
    }

    @Override
    public void setOf() {
        setNextObjectBuilder(Set.class, SET_OF, 1);
    }

    @Override
    public <T> void list(Class<T> clazz) {
        setNextObjectBuilder(clazz, LIST, 1);
        method("add", 1);
    }

    @Override
    public void listOf() {
        setNextObjectBuilder(List.class, LIST_OF, 1);
    }

    @Override
    public <T> void map(Class<T> clazz) {
        setNextObjectBuilder(clazz, MAP, 1);
        method("put", 2);
    }

    @Override
    public void mapOf() {
        setNextObjectBuilder(Map.class, MAP_OF, 2);
    }

    @Override
    public <T> void mapEntry(Class<T> clazz) {
        setNextObjectBuilder(clazz, MAP_ENTRY, 2);
    }

    @Override
    public <T> void array(Class<T> clazz) {
        setNextObjectBuilder(clazz, ARRAY, 1);
    }

    @Override
    public <T> void value(T value) {
        setNextObjectBuilder(value.getClass(), VALUE, 0);
        currentObjectBuilder.setValue(toStringValue(value));
        setPreviousObjectBuilder();
    }

    @Override
    public <T> void nullValue(Class<T> clazz) {
        setNextObjectBuilder(clazz, VALUE, 0);
        currentObjectBuilder.setValue(NULL);
        setPreviousObjectBuilder();
    }

    @Override
    public ObjectResult build() {
        ObjectBuilder topObjectBuilder = toTop();
        return topObjectBuilder != null ? topObjectBuilder.build() : ObjectResult.EMPTY_OBJECT_RESULT;
    }

    @Override
    public void writeToFile() {
        ObjectResult objectResult = build();
        if (objectResult.isValid()) {
            Path path = getPath(objectResult, populateConfig);
            createOrOverwriteFile(path);
            writePackage(objectResult, path);
            writeImports(objectResult, path);
            writeStaticImports(objectResult, path);
            writeStartClass(objectResult, path, populateConfig);
            writeObjects(objectResult, path);
            writeEndClass(path);
        }
    }

    private ObjectBuilder toTop() {
        return Stream.iterate(currentObjectBuilder, Objects::nonNull, ObjectBuilder::getParent)
                .reduce((child, parent) -> parent)
                .orElse(null);
    }

    private String toStringValue(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isEnum()) {
            return object.toString();
        }

        return Optional.ofNullable(stringSuppliers.get(clazz))
                .map(supplier -> supplier.apply(object))
                .orElseGet(() -> populateConfig.getOverridePopulate().getOrDefault(object.getClass(), () -> {
                    throw new ObjectException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
                }).createString());
    }

    private void setNextObjectBuilder(Class<?> clazz, BuildType buildType, int expectedChildren) {
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new ObjectBuilder(clazz, getName(clazz), buildType, expectedChildren);
        } else if (buildType == MUTATOR) {
            setNextObjectBuilder(new ObjectBuilder(clazz, currentObjectBuilder.getName(), buildType, expectedChildren));
        } else {
            setNextObjectBuilder(new ObjectBuilder(clazz, getName(clazz), buildType, expectedChildren));
        }
    }

    private void setNextObjectBuilder(ObjectBuilder objectBuilder) {
        currentObjectBuilder.addChild(objectBuilder);
        objectBuilder.setParent(currentObjectBuilder);
        currentObjectBuilder = objectBuilder;
    }

    private void setPreviousObjectBuilder() {
        while (currentObjectBuilder.getParent() != null && currentObjectBuilder.hasAllChildren()) {
            currentObjectBuilder = currentObjectBuilder.getParent();
        }
    }

    private String getName(Class<?> clazz) {
        int classCounter = classCounters.computeIfAbsent(clazz, k -> 0);
        String simpleName = clazz.getSimpleName();
        String name = String.format("%s_%d", Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1), classCounter);
        classCounters.put(clazz, ++classCounter);
        return name;
    }
}
