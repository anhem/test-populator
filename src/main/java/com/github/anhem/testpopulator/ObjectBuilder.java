package com.github.anhem.testpopulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.PopulateFactory.BUILDER_METHOD;
import static com.github.anhem.testpopulator.PopulateFactory.BUILD_METHOD;
import static com.github.anhem.testpopulator.PopulateUtil.isJavaBaseClass;

public class ObjectBuilder {

    private Class<?> clazz;
    private final String name;
    private final BuildType buildType;
    private final List<ObjectBuilder> children = new ArrayList<>();
    private final int expectedChildren;
    private ObjectBuilder parent;
    private String value;

    public ObjectBuilder(Class<?> clazz, String name, BuildType buildType, int expectedChildren) {
        this.clazz = clazz;
        this.name = name;
        this.buildType = buildType;
        this.expectedChildren = expectedChildren;
    }

    public ObjectBuilder(String name, BuildType buildType, int expectedChildren) {
        this.name = name;
        this.buildType = buildType;
        this.expectedChildren = expectedChildren;
    }

    public void addChild(ObjectBuilder child) {
        children.add(child);
    }

    public boolean hasAllChildren() {
        return expectedChildren == children.size();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public BuildType getBuildType() {
        return buildType;
    }

    public ObjectBuilder getParent() {
        return parent;
    }

    public void setParent(ObjectBuilder parent) {
        this.parent = parent;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String buildString() {
        return buildByBuildType().stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    List<String> buildByBuildType() {
        switch (buildType) {
            case CONSTRUCTOR:
                return buildConstructor();
            case SETTER:
                return buildSetter();
            case BUILDER:
                return buildBuilder();
            case METHOD:
                return buildMethod();
            case SET:
                return buildSet();
            case SET_OF:
                return buildSetOf();
            case LIST:
                return buildList();
            case LIST_OF:
                return buildListOf();
            case MAP:
                return buildMap();
            case MAP_OF:
                return buildMapOf();
            case ARRAY:
                return buildArray();
            case VALUE:
            case OVERRIDE_VALUE:
                return buildValue();
            default:
                throw new ObjectException(String.format("Invalid buildType %s", buildType));
        }
    }

    private Stream<String> buildChildren() {
        return children.stream()
                .filter(child -> !isBasicValue(child))
                .map(ObjectBuilder::buildByBuildType)
                .flatMap(Collection::stream);
    }


    private List<String> buildConstructor() {
        return concatenate(buildChildren(),
                Stream.of(String.format("public static final %s %s = new %s(%s);", clazz.getSimpleName(), name, clazz.getSimpleName(), buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildSetter() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format("public static final %s %s = new %s();", clazz.getSimpleName(), name, clazz.getSimpleName())),
                createMethods()
        ).collect(Collectors.toList());
    }

    private List<String> buildBuilder() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format("public static final %s %s = %s.%s()", clazz.getSimpleName(), name, clazz.getSimpleName(), BUILDER_METHOD)),
                createMethods(),
                Stream.of(String.format(".%s();", BUILD_METHOD))
        ).collect(Collectors.toList());
    }

    private List<String> buildMethod() {
        return buildChildren().collect(Collectors.toList());
    }

    private Stream<String> createMethods() {
        return children.stream()
                .map(child -> buildType == BuildType.BUILDER ?
                        String.format(".%s(%s)", child.getName(), child.buildArguments()) :
                        String.format("%s.%s(%s);", name, child.getName(), child.buildArguments()));
    }

    private List<String> buildSet() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format("public static final %s<%s> %s = new %s<>();", clazz.getSimpleName(), formatTypes(), name, clazz.getSimpleName())),
                children.stream().map(child -> String.format("%s.add(%s);", name, buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildSetOf() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format("public static final %s<%s> %s = Set.of(%s);", clazz.getSimpleName(), formatTypes(), name, buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildList() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format("public static final %s<%s> %s = new %s<>();", clazz.getSimpleName(), formatTypes(), name, clazz.getSimpleName())),
                children.stream().map(child -> String.format("%s.add(%s);", name, buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildListOf() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format("public static final %s<%s> %s = List.of(%s);", clazz.getSimpleName(), formatTypes(), name, buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildMap() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format("public static final %s<%s> %s = new %s<>();", clazz.getSimpleName(), formatTypes(), name, clazz.getSimpleName())),
                Stream.of(String.format("%s.put(%s);", name, buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildMapOf() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format("public static final %s<%s> %s = Map.of(%s);", clazz.getSimpleName(), formatTypes(), name, buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildArray() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format("public static final %s[] %s = new %s[]{%s};", clazz.getSimpleName(), name, clazz.getSimpleName(), buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildValue() {
        return List.of(String.format("public static final %s %s = %s;", clazz.getSimpleName(), name, this.value));
    }

    private List<String> buildInlineArgument() {
        return List.of(String.format("%s", this.value));
    }

    private String buildArguments() {
        return children.stream()
                .map(child -> {
                    if (isBasicValue(child)) {
                        return child.buildInlineArgument();
                    }
                    return List.of(child.getName());
                }).flatMap(Collection::stream)
                .collect(Collectors.joining(", "));
    }

    private String formatTypes() {
        if (buildType.isParameterizedType()) {
            return children.stream()
                    .map(child -> {
                        if (child.getBuildType() == BuildType.METHOD) {
                            return child.formatTypes();
                        }
                        if (child.getBuildType().isParameterizedType()) {
                            return String.format("%s<%s>", child.getClazz().getSimpleName(), child.formatTypes());
                        } else {
                            return child.getClazz().getSimpleName();
                        }
                    }).collect(Collectors.joining(", "));
        }
        return "";
    }

    private static boolean isBasicValue(ObjectBuilder objectBuilder) {
        return Objects.requireNonNull(objectBuilder.buildType) == BuildType.VALUE && (isJavaBaseClass(objectBuilder.getClazz()) || objectBuilder.getClazz().isEnum());
    }


    @SafeVarargs
    private <T> Stream<T> concatenate(Stream<T>... streams) {
        return Stream.of(streams).flatMap(s -> s);
    }

}

