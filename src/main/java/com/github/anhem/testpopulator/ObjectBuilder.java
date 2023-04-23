package com.github.anhem.testpopulator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectBuilder {

    private final Class<?> clazz;
    private final String name;
    private final List<Type> types;
    private ObjectBuilder parent;
    private final List<ObjectBuilder> children;
    private final StringBuilder stringBuilder;

    public ObjectBuilder(Class<?> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
        this.types = new ArrayList<>();
        this.children = new ArrayList<>();
        this.stringBuilder = new StringBuilder();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public List<ObjectBuilder> getChildren() {
        return children;
    }

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public String getName() {
        return name;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void addTypes(Type... types) {
        this.types.addAll(Arrays.asList(types));
    }

    public ObjectBuilder getParent() {
        return parent;
    }

    public void setParent(ObjectBuilder parent) {
        this.parent = parent;
    }

    public String build() {
        return build(this);
    }

    private String build(ObjectBuilder objectBuilder) {
        return Stream.concat(
                objectBuilder.children.stream().map(this::build),
                Stream.of(buildClass(objectBuilder))
        ).collect(Collectors.joining(System.lineSeparator()));
    }

    private static String buildClass(ObjectBuilder objectBuilder) {
        if (objectBuilder.getTypes().isEmpty()) {
            return String.format("public static final %s %s = %s", objectBuilder.getClazz().getSimpleName(), objectBuilder.getName(), objectBuilder.getStringBuilder());
        } else {
            return String.format("public static final %s<%s> %s = %s", objectBuilder.getClazz().getSimpleName(), toTypesString(objectBuilder), objectBuilder.getName(), objectBuilder.getStringBuilder());
        }
    }

    private static String toTypesString(ObjectBuilder objectBuilder) {
        return objectBuilder.getTypes().stream()
                .map(type -> ((Class<?>) type).getSimpleName())
                .collect(Collectors.joining(", "));
    }
}
