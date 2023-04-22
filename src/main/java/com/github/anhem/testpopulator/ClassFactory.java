package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.Strategy;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.PopulateFactory.BUILDER_METHOD;
import static com.github.anhem.testpopulator.PopulateFactory.BUILD_METHOD;

public class ClassFactory {

    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";
    private final Map<Class<?>, Integer> classCounters;
    private ClassBuilder currentClassBuilder;

    public ClassFactory() {
        this.classCounters = new HashMap<>();
    }

    public void startConstructor(Class<?> clazz) {
        createAndSetNextClassBuilder(clazz);
        currentClassBuilder.getStringBuilder().append(String.format("new %s(", clazz.getSimpleName()));
    }

    public void parameterDividerForConstructor(int parameterPosition) {
        if (parameterPosition > 0) {
            currentClassBuilder.getStringBuilder().append(", ");
        }
    }

    public void endConstructor() {
        currentClassBuilder.getStringBuilder().append(");");
        finalizeAndSetPreviousClassBuilder();
    }

    public void startSetter(Class<?> clazz) {
        createAndSetNextClassBuilder(clazz);
        currentClassBuilder.getStringBuilder().append(String.format("new %s();", clazz.getSimpleName()));
    }

    public void endSetter() {
        finalizeAndSetPreviousClassBuilder();
    }

    public void startBuilder(Class<?> clazz) {
        createAndSetNextClassBuilder(clazz);
        currentClassBuilder.getStringBuilder().append(String.format("%s.%s()", clazz.getSimpleName(), BUILDER_METHOD));
    }

    public void startBuilder(Class<?> clazz, Class<?> generatedClass) {
        createAndSetNextClassBuilder(clazz);
        currentClassBuilder.getStringBuilder().append(String.format("%s.%s()", generatedClass.getSimpleName(), BUILDER_METHOD));
    }

    public void endBuilder() {
        currentClassBuilder.getStringBuilder()
                .append(System.lineSeparator())
                .append(String.format(".%s();", BUILD_METHOD));
        finalizeAndSetPreviousClassBuilder();
    }

    public void startMethod(Method method, Strategy strategy) {
        switch (strategy) {
            case SETTER:
                currentClassBuilder.getStringBuilder()
                        .append(System.lineSeparator())
                        .append(String.format("%s.%s(", currentClassBuilder.getName(), method.getName()));
                break;
            case BUILDER:
                currentClassBuilder.getStringBuilder()
                        .append(System.lineSeparator())
                        .append(String.format(".%s(", method.getName()));
                break;
            default:
                throw new PopulateException(String.format("Invalid strategy %s on startMethod", strategy));
        }
    }

    public void endMethod(Strategy strategy) {
        switch (strategy) {
            case SETTER:
                currentClassBuilder.getStringBuilder().append(");");
                break;
            case BUILDER:
                currentClassBuilder.getStringBuilder().append(")");
                break;
            default:
                throw new PopulateException(String.format("Invalid strategy %s on endMethod", strategy));
        }
    }

    public void startSet() {
        currentClassBuilder.getStringBuilder().append("Set.of(");
    }

    public void endSet() {
        currentClassBuilder.getStringBuilder().append(")");
    }

    public void startList() {
        currentClassBuilder.getStringBuilder().append("List.of(");
    }

    public void endList() {
        currentClassBuilder.getStringBuilder().append(")");
    }

    public void startMap() {
        currentClassBuilder.getStringBuilder().append("Map.of(");
    }

    public void keyValueDividerForMap() {
        currentClassBuilder.getStringBuilder().append(",");
    }

    public void endMap() {
        currentClassBuilder.getStringBuilder().append(")");
    }

    public void startMapEntry() {
        currentClassBuilder.getStringBuilder().append("new AbstractMap.SimpleEntry<>(");
    }

    public void startArray(Class<?> clazz) {
        currentClassBuilder.getStringBuilder().append(String.format("new %s[]{", clazz.getSimpleName()));
    }

    public void endArray() {
        currentClassBuilder.getStringBuilder().append("}");
    }

    public <T> void overridePopulateValue(Class<?> clazz, OverridePopulate<T> overridePopulateValue) {
        if (currentClassBuilder == null) {
            currentClassBuilder = new ClassBuilder(clazz, getName(clazz));
        }
        currentClassBuilder.getStringBuilder().append(overridePopulateValue.createString());
        finalizeAndSetPreviousClassBuilder();
    }

    public <T> void value(T value) {
        if (currentClassBuilder == null) {
            currentClassBuilder = new ClassBuilder(value.getClass(), getName(value.getClass()));
        }
        currentClassBuilder.getStringBuilder().append(getValue(value));
    }

    public ClassBuilder getTopClassBuilder() {
        return Stream.iterate(currentClassBuilder, Objects::nonNull, ClassBuilder::getParent)
                .reduce((child, parent) -> parent)
                .orElse(null);
    }

    private Object getValue(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isEnum()) {
            return object;
        }
        if (clazz.equals(Integer.class)) {
            return object;
        }
        if (clazz.equals(Long.class)) {
            return String.format("%sL", object);
        }
        if (clazz.equals(Double.class)) {
            return object;
        }
        if (clazz.equals(Boolean.class)) {
            return object;
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

        throw new PopulateException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
    }

    private void createAndSetNextClassBuilder(Class<?> clazz) {
        String name = getName(clazz);
        if (currentClassBuilder == null) {
            currentClassBuilder = new ClassBuilder(clazz, name);
        } else {
            ClassBuilder child = new ClassBuilder(clazz, name);
            currentClassBuilder.getChildren().add(child);
            child.setParent(currentClassBuilder);
            currentClassBuilder = child;
        }
    }

    private void finalizeAndSetPreviousClassBuilder() {
        if (currentClassBuilder.getParent() != null) {
            currentClassBuilder.getParent().getStringBuilder().append(currentClassBuilder.getName());
            currentClassBuilder = currentClassBuilder.getParent();
        }
    }

    private String getName(Class<?> clazz) {
        int classCounter = classCounters.computeIfAbsent(clazz, (k) -> 0);
        String name = String.format("%s%d", lowerCaseFirstChar(clazz.getSimpleName()), classCounter);
        classCounters.put(clazz, ++classCounter);
        return name;
    }

    private String lowerCaseFirstChar(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
