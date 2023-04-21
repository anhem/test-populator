package com.github.anhem.testpopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ClassBuilder {

    private final Class<?> clazz;
    private final String name;
    private ClassBuilder parent;
    private final List<ClassBuilder> children;
    private final StringBuilder stringBuilder;

    public ClassBuilder(Class<?> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
        this.children = new ArrayList<>();
        this.stringBuilder = new StringBuilder();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public List<ClassBuilder> getChildren() {
        return children;
    }

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public String getName() {
        return name;
    }

    public ClassBuilder getParent() {
        return parent;
    }

    public void setParent(ClassBuilder parent) {
        this.parent = parent;
    }

    public String build() {
        return build(this);
    }

    private String build(ClassBuilder classBuilder) {
        return Stream.concat(
                classBuilder.children.stream().map(this::build),
                Stream.of(buildClass(classBuilder))
        ).collect(Collectors.joining(System.lineSeparator()));
    }

    private static String buildClass(ClassBuilder classBuilder) {
        return String.format("public static final %s %s = %s", classBuilder.getClazz().getSimpleName(), classBuilder.getName(), classBuilder.getStringBuilder());
    }
}
