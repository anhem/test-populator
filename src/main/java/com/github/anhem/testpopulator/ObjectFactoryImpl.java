package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.BuildType.*;

public class ObjectFactoryImpl implements ObjectFactory {

    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";
    private final Map<Class<?>, Integer> classCounters;
    private ObjectBuilder currentObjectBuilder;

    public ObjectFactoryImpl() {
        this.classCounters = new HashMap<>();
    }

    @Override
    public void constructor(Class<?> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, CONSTRUCTOR, expectedChildren);
    }

    @Override
    public void setter(Class<?> clazz) {
        setNextObjectBuilder(clazz, SETTER, 1);
    }

    @Override
    public void builder(Class<?> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, BUILDER, expectedChildren);
    }

    @Override
    public void method(String methodName, int expectedChildren) {
        setNextObjectBuilder(new ObjectBuilder(methodName, METHOD, expectedChildren));
    }

    @Override
    public void set(Class<?> clazz) {
        setNextObjectBuilder(clazz, SET, 1);
    }

    @Override
    public void setOf() {
        setNextObjectBuilder(Set.class, SET_OF, 1);
    }

    @Override
    public void list(Class<?> clazz) {
        setNextObjectBuilder(clazz, LIST, 1);
    }

    @Override
    public void listOf() {
        setNextObjectBuilder(List.class, LIST_OF, 1);
    }

    @Override
    public void map(Class<?> clazz) {
        setNextObjectBuilder(clazz, MAP, 2);
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
    public <T> void overridePopulate(Class<?> clazz, OverridePopulate<T> overridePopulateValue) {
        setNextObjectBuilder(clazz, OVERRIDE_VALUE, 0);
        currentObjectBuilder.setValue(overridePopulateValue.createString());
        setPreviousObjectBuilder();
    }

    @Override
    public <T> void value(T value) {
        setNextObjectBuilder(value.getClass(), VALUE, 0);
        currentObjectBuilder.setValue(toStringValue(value));
        setPreviousObjectBuilder();
    }

    @Override
    public List<String> build() {
        return toTop().buildByBuildType();
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
        if (clazz.equals(Integer.class)) {
            return object.toString();
        }
        if (clazz.equals(Long.class)) {
            return String.format("%sL", object);
        }
        if (clazz.equals(Double.class)) {
            return object.toString();
        }
        if (clazz.equals(Boolean.class)) {
            return object.toString();
        }
        if (clazz.equals(BigDecimal.class)) {
            return String.format("BigDecimal.valueOf(%d)", ((BigDecimal) object).intValue());
        }
        if (clazz.equals(String.class)) {
            return String.format("\"%s\"", object);
        }
        if (clazz.equals(LocalDate.class)) {
            return String.format("LocalDate.parse(\"%s\")", object);
        }
        if (clazz.equals(LocalDateTime.class)) {
            return String.format("LocalDateTime.parse(\"%s\")", object);
        }
        if (clazz.equals(ZonedDateTime.class)) {
            return String.format("ZonedDateTime.parse(\"%s\")", object);
        }
        if (clazz.equals(Instant.class)) {
            return String.format("Instant.parse(\"%s\")", object);
        }
        if (clazz.equals(Character.class)) {
            return String.format("'%s'", object);
        }
        if (clazz.equals(UUID.class)) {
            return String.format("UUID.fromString(%s)", object);
        }

        throw new ObjectException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
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
        if (currentObjectBuilder.getParent() != null) {
            if (currentObjectBuilder.getParent().hasAllChildren()) {
                if (currentObjectBuilder.getParent().getParent() != null) {
                    currentObjectBuilder = currentObjectBuilder.getParent().getParent();
                } else {
                    currentObjectBuilder = currentObjectBuilder.getParent();
                }
            } else {
                currentObjectBuilder = currentObjectBuilder.getParent();
            }
        }
    }

    private String getName(Class<?> clazz) {
        int classCounter = classCounters.computeIfAbsent(clazz, (k) -> 0);
        String string = clazz.getSimpleName();
        String name = String.format("%s%d", Character.toLowerCase(string.charAt(0)) + string.substring(1), classCounter);
        classCounters.put(clazz, ++classCounter);
        return name;
    }

}
