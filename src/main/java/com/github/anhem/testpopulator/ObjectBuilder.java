package com.github.anhem.testpopulator;

import java.lang.reflect.ParameterizedType;
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

    public void addTypes(Type... types) {
        this.types.addAll(Arrays.stream(types).map(type -> {
            if (type instanceof ParameterizedType) {
                return ((ParameterizedType) type).getRawType();
            } else {
                return type;
            }
        }).collect(Collectors.toList()));
    }

    public List<Type> getTypes() {
        return types;
    }

    public ObjectBuilder getParent() {
        return parent;
    }

    public void setParent(ObjectBuilder parent) {
        this.parent = parent;
    }

    public String build() {
        return Stream.concat(children.stream().map(ObjectBuilder::build), Stream.of(buildClass())).collect(Collectors.joining(System.lineSeparator()));
    }

    private String buildClass() {
        if (types.isEmpty()) {
            return String.format("%s %s = %s", clazz.getSimpleName(), name, stringBuilder);
        } else {
            return String.format("%s<%s> %s = %s", clazz.getSimpleName(), formatTypes(), name, stringBuilder);
        }
    }

    private String formatTypes() {
        if (!types.isEmpty()) {
            return children.stream()
                    .map(child -> {
                        if (!child.getTypes().isEmpty()) {
                            return String.format("%s<%s>", child.getClazz().getSimpleName(), child.formatTypes());
                        } else {
                            return child.getClazz().getSimpleName();
                        }
                    }).collect(Collectors.joining(", "));
        }
        return "";
    }
}

