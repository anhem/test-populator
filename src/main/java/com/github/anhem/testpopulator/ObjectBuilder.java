package com.github.anhem.testpopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ObjectBuilder {

    private final Class<?> clazz;
    private final String name;
    private ObjectBuilder parent;
    private final List<ObjectBuilder> children;
    private final StringBuilder stringBuilder;

    public ObjectBuilder(Class<?> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
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
        return String.format("public static final %s %s = %s", objectBuilder.getClazz().getSimpleName(), objectBuilder.getName(), objectBuilder.getStringBuilder());
    }
}
