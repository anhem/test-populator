package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.ObjectException;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.BuildType.*;
import static com.github.anhem.testpopulator.internal.object.ObjectBuilder.NULL;
import static com.github.anhem.testpopulator.internal.util.FileWriterUtil.*;

public class ObjectFactoryImpl implements ObjectFactory {

    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";

    private static final Map<Class<?>, Function<Object, String>> stringSuppliers = new HashMap<>() {{
        put(Integer.class, Object::toString);
        put(Long.class, object -> object + "L");
        put(Double.class, Object::toString);
        put(Boolean.class, Object::toString);
        put(BigDecimal.class, object -> String.format("BigDecimal.valueOf(%d)", ((BigDecimal) object).intValue()));
        put(String.class, object -> "\"" + object + "\"");
        put(Character.class, object -> "'" + object + "'");
        put(LocalDate.class, object -> String.format("LocalDate.parse(\"%s\")", object));
        put(LocalDateTime.class, object -> String.format("LocalDateTime.parse(\"%s\")", object));
        put(ZonedDateTime.class, object -> String.format("ZonedDateTime.parse(\"%s\")", object));
        put(Instant.class, object -> String.format("Instant.parse(\"%s\")", object));
        put(Date.class, object -> String.format("new Date(%sL)", ((Date) object).getTime()));
        put(UUID.class, object -> String.format("UUID.fromString(\"%s\")", object));
    }};

    private final PopulateConfig populateConfig;
    private final Map<Class<?>, Integer> classCounters;
    private ObjectBuilder currentObjectBuilder;

    public ObjectFactoryImpl(PopulateConfig populateConfig) {
        this.populateConfig = populateConfig;
        this.classCounters = new HashMap<>();
    }

    @Override
    public void constructor(Class<?> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, CONSTRUCTOR, expectedChildren);
    }

    @Override
    public void setter(Class<?> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, SETTER, expectedChildren);
    }

    @Override
    public void builder(Class<?> clazz, int expectedChildren) {
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
    public void set(Class<?> clazz) {
        setNextObjectBuilder(clazz, SET, 1);
        method("add", 1);
    }

    @Override
    public void setOf() {
        setNextObjectBuilder(Set.class, SET_OF, 1);
    }

    @Override
    public void list(Class<?> clazz) {
        setNextObjectBuilder(clazz, LIST, 1);
        method("add", 1);
    }

    @Override
    public void listOf() {
        setNextObjectBuilder(List.class, LIST_OF, 1);
    }

    @Override
    public void map(Class<?> clazz) {
        setNextObjectBuilder(clazz, MAP, 1);
        method("put", 2);
    }

    @Override
    public void mapOf() {
        setNextObjectBuilder(Map.class, MAP_OF, 2);
    }

    @Override
    public void array(Class<?> clazz) {
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
                .orElseGet(() -> populateConfig.getTypeSuppliers().getOrDefault(object.getClass(), () -> {
                    throw new ObjectException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
                }).createString());
    }

    private void setNextObjectBuilder(Class<?> clazz, BuildType buildType, int expectedChildren) {
        String name = getName(clazz);
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new ObjectBuilder(clazz, name, buildType, expectedChildren);
        } else {
            ObjectBuilder child = new ObjectBuilder(clazz, name, buildType, expectedChildren);
            setNextObjectBuilder(child);
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