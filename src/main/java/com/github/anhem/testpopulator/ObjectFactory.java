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

public class ObjectFactory {

    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";
    private final Map<Class<?>, Integer> classCounters;
    private ObjectBuilder currentObjectBuilder;

    public ObjectFactory() {
        this.classCounters = new HashMap<>();
    }

    public void constructor(Class<?> clazz) {
        setNextObjectBuilder(clazz, CONSTRUCTOR);
    }

    public void setter(Class<?> clazz) {
        setNextObjectBuilder(clazz, SETTER);
    }

    public void builder(Class<?> clazz) {
        setNextObjectBuilder(clazz, BUILDER);
    }

    public void method(String methodName) {
        setNextObjectBuilder(new ObjectBuilder(methodName, METHOD));

    }

    public void set(Class<?> clazz) {
        setNextObjectBuilder(new ObjectBuilder(clazz, getName(clazz), SET));
    }

    public void setOf() {
        setNextObjectBuilder(new ObjectBuilder(Set.class, getName(Set.class), SET_OF));
    }

    public void list(Class<?> clazz) {
        setNextObjectBuilder(new ObjectBuilder(clazz, getName(clazz), LIST));
    }

    public void listOf() {
        setNextObjectBuilder(new ObjectBuilder(List.class, getName(List.class), LIST_OF));
    }

    public void map(Class<?> clazz) {
        setNextObjectBuilder(new ObjectBuilder(clazz, getName(clazz), MAP));
    }

    public void mapOf() {
        setNextObjectBuilder(new ObjectBuilder(Map.class, getName(Map.class), MAP_OF));
    }

    public void array(Class<?> clazz) {
        setNextObjectBuilder(new ObjectBuilder(clazz, getName(clazz), ARRAY));
    }

    public <T> void overridePopulate(Class<?> clazz, OverridePopulate<T> overridePopulateValue) {
        setNextObjectBuilder(clazz, overridePopulateValue.createString(), OVERRIDE_VALUE);
        setPreviousObjectBuilder();
    }

    public <T> void value(T value) {
        setNextObjectBuilder(value.getClass(), toStringValue(value), VALUE);
        setPreviousObjectBuilder();
    }

    public ObjectBuilder toTop() {
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

    private void setNextObjectBuilder(Class<?> clazz, BuildType buildType) {
        String name = getName(clazz);
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new ObjectBuilder(clazz, name, buildType);
        } else {
            ObjectBuilder child = new ObjectBuilder(clazz, name, buildType);
            setNextObjectBuilder(child);
        }
    }

    private void setNextObjectBuilder(Class<?> clazz, String value, BuildType buildType) {
        String name = getName(clazz);
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new ObjectBuilder(clazz, name, buildType);
        } else {
            ObjectBuilder child = new ObjectBuilder(clazz, name, buildType);
            setNextObjectBuilder(child);
        }
        currentObjectBuilder.setValue(value);
    }


    private void setNextObjectBuilder(ObjectBuilder objectBuilder) {
        currentObjectBuilder.addChild(objectBuilder);
        objectBuilder.setParent(currentObjectBuilder);
        currentObjectBuilder = objectBuilder;
    }

    private void setPreviousObjectBuilder() {
        if (currentObjectBuilder.getParent() != null) {
            BuildType buildType = currentObjectBuilder.getParent().getBuildType();
            if (buildType.isExpectingOneArgument()) {
                if (currentObjectBuilder.getParent().getChildren().size() == 1) {
                    currentObjectBuilder = currentObjectBuilder.getParent().getParent();
                }
            } else if (buildType.isExpectingTwoArguments()) {
                if (currentObjectBuilder.getParent().getChildren().size() == 2) {
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
