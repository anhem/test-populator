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

public class ObjectFactory {

    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";
    private final Map<Class<?>, Integer> classCounters;
    private ObjectBuilder currentObjectBuilder;

    public ObjectFactory() {
        this.classCounters = new HashMap<>();
    }

    public void startConstructor(Class<?> clazz) {
        createAndSetNextClassBuilder(clazz);
        currentObjectBuilder.getStringBuilder().append(String.format("new %s(", clazz.getSimpleName()));
    }

    public void parameterDividerForConstructor(int parameterPosition) {
        if (parameterPosition > 0) {
            currentObjectBuilder.getStringBuilder().append(", ");
        }
    }

    public void endConstructor() {
        currentObjectBuilder.getStringBuilder().append(");");
        finalizeAndSetPreviousClassBuilder();
    }

    public void startSetter(Class<?> clazz) {
        createAndSetNextClassBuilder(clazz);
        currentObjectBuilder.getStringBuilder().append(String.format("new %s();", clazz.getSimpleName()));
    }

    public void endSetter() {
        finalizeAndSetPreviousClassBuilder();
    }

    public void startBuilder(Class<?> clazz) {
        createAndSetNextClassBuilder(clazz);
        currentObjectBuilder.getStringBuilder().append(String.format("%s.%s()", clazz.getSimpleName(), BUILDER_METHOD));
    }

    public void startBuilder(Class<?> clazz, Class<?> generatedClass) {
        createAndSetNextClassBuilder(clazz);
        currentObjectBuilder.getStringBuilder().append(String.format("%s.%s()", generatedClass.getSimpleName(), BUILDER_METHOD));
    }

    public void endBuilder() {
        currentObjectBuilder.getStringBuilder()
                .append(System.lineSeparator())
                .append(String.format(".%s();", BUILD_METHOD));
        finalizeAndSetPreviousClassBuilder();
    }

    public void startMethod(Method method, Strategy strategy) {
        switch (strategy) {
            case SETTER:
                currentObjectBuilder.getStringBuilder()
                        .append(System.lineSeparator())
                        .append(String.format("%s.%s(", currentObjectBuilder.getName(), method.getName()));
                break;
            case BUILDER:
                currentObjectBuilder.getStringBuilder()
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
                currentObjectBuilder.getStringBuilder().append(");");
                break;
            case BUILDER:
                currentObjectBuilder.getStringBuilder().append(")");
                break;
            default:
                throw new PopulateException(String.format("Invalid strategy %s on endMethod", strategy));
        }
    }

    public void startSet() {
        currentObjectBuilder.getStringBuilder().append("Set.of(");
    }

    public void endSet() {
        currentObjectBuilder.getStringBuilder().append(")");
    }

    public void startList() {
        currentObjectBuilder.getStringBuilder().append("List.of(");
    }

    public void endList() {
        currentObjectBuilder.getStringBuilder().append(")");
    }

    public void startMap() {
        currentObjectBuilder.getStringBuilder().append("Map.of(");
    }

    public void keyValueDividerForMap() {
        currentObjectBuilder.getStringBuilder().append(", ");
    }

    public void endMap() {
        currentObjectBuilder.getStringBuilder().append(")");
    }

    public void startMapEntry() {
        currentObjectBuilder.getStringBuilder().append("new AbstractMap.SimpleEntry<>(");
    }

    public void startArray(Class<?> clazz) {
        currentObjectBuilder.getStringBuilder().append(String.format("new %s[]{", clazz.getSimpleName()));
    }

    public void endArray() {
        currentObjectBuilder.getStringBuilder().append("}");
    }

    public <T> void addOverridePopulate(Class<?> clazz, OverridePopulate<T> overridePopulateValue) {
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new ObjectBuilder(clazz, getName(clazz));
        }
        currentObjectBuilder.getStringBuilder().append(overridePopulateValue.createString());
        finalizeAndSetPreviousClassBuilder();
    }

    public <T> void addValue(T value) {
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new ObjectBuilder(value.getClass(), getName(value.getClass()));
        }
        currentObjectBuilder.getStringBuilder().append(getValue(value));
    }

    public ObjectBuilder getTopClassBuilder() {
        return Stream.iterate(currentObjectBuilder, Objects::nonNull, ObjectBuilder::getParent)
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
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new ObjectBuilder(clazz, name);
        } else {
            ObjectBuilder child = new ObjectBuilder(clazz, name);
            currentObjectBuilder.getChildren().add(child);
            child.setParent(currentObjectBuilder);
            currentObjectBuilder = child;
        }
    }

    private void finalizeAndSetPreviousClassBuilder() {
        if (currentObjectBuilder.getParent() != null) {
            currentObjectBuilder.getParent().getStringBuilder().append(currentObjectBuilder.getName());
            currentObjectBuilder = currentObjectBuilder.getParent();
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
