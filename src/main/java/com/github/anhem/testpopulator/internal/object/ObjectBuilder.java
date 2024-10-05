package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.exception.ObjectException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.PopulateFactory.BUILDER_METHOD;
import static com.github.anhem.testpopulator.internal.object.BuildType.MUTATOR;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.*;
import static java.util.Collections.emptyList;

public class ObjectBuilder {

    public static final String NULL = "null";
    public static final String PSF = "public static final";
    private static final String NEW_OBJECT_WITH_ARGUMENTS = "%s %s %s = new %s(%s);";
    private static final String NEW_OBJECT = "%s %s %s = new %s();";
    private static final String BUILDER = "%s %s %s = %s.%s()";
    private static final String NEW_TYPED_OBJECT = "%s %s<%s> %s = new %s<>();";
    private static final String SET_OF = "%s %s<%s> %s = Set.of(%s);";
    private static final String LIST_OF = "%s %s<%s> %s = List.of(%s);";
    private static final String MAP_OF = "%s %s<%s> %s = Map.of(%s);";
    private static final String NEW_ARRAY = "%s %s[] %s = new %s[]{%s};";
    private static final String NEW_VALUE = "%s %s %s = %s;";
    private static final String ARGUMENT_DELIMITER = ", ";
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

    public List<ObjectBuilder> getChildren() {
        return children;
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

    public ObjectResult build() {
        String packageName = getPackageName(clazz);
        String className = formatClassName(clazz);
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();
        getImports(imports, staticImports);
        List<String> objects = buildByBuildType();

        return new ObjectResult(packageName, className, imports, staticImports, objects);
    }

    private void getImports(Set<String> imports, Set<String> staticImports) {
        addImport(clazz, value, imports, staticImports);
        children.forEach(objectBuilder -> objectBuilder.getImports(imports, staticImports));
    }

    private List<String> buildByBuildType() {
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
                return buildValue();
            case MUTATOR:
                return buildMutator();
            default:
                throw new ObjectException(String.format("Invalid buildType %s", buildType));
        }
    }

    private Stream<String> buildChildren() {
        return buildChildren(children);
    }

    private Stream<String> buildChildren(List<ObjectBuilder> children) {
        return children.stream()
                .filter(child -> !isBasicValue(child))
                .map(ObjectBuilder::buildByBuildType)
                .flatMap(Collection::stream);
    }

    private List<String> buildConstructor() {
        if (children.stream().anyMatch(child -> child.buildType == MUTATOR)) {
            Map<Boolean, List<ObjectBuilder>> childrenByMutator = children.stream()
                    .collect(Collectors.groupingBy(child -> child.getBuildType() == MUTATOR));
            List<ObjectBuilder> mutatorChildren = childrenByMutator.getOrDefault(true, emptyList());
            List<ObjectBuilder> otherChildren = childrenByMutator.getOrDefault(false, emptyList());
            return concatenate(
                    buildChildren(otherChildren),
                    Stream.of(String.format(NEW_OBJECT_WITH_ARGUMENTS, PSF, clazz.getSimpleName(), name, clazz.getSimpleName(), buildArguments(otherChildren))),
                    buildChildren(mutatorChildren))
                    .collect(Collectors.toList());
        } else {
            return concatenate(buildChildren(),
                    Stream.of(String.format(NEW_OBJECT_WITH_ARGUMENTS, PSF, clazz.getSimpleName(), name, clazz.getSimpleName(), buildArguments())))
                    .collect(Collectors.toList());
        }
    }

    private List<String> buildSetter() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(NEW_OBJECT, PSF, clazz.getSimpleName(), name, clazz.getSimpleName())),
                startStaticBlock(),
                createMethods(),
                endStaticBlock()
        ).collect(Collectors.toList());
    }

    private List<String> buildMutator() {
        return concatenate(
                buildChildren(),
                startStaticBlock(),
                createMethods(),
                endStaticBlock()
        ).collect(Collectors.toList());
    }

    private List<String> buildBuilder() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(BUILDER, PSF, clazz.getSimpleName(), name, clazz.getSimpleName(), BUILDER_METHOD)),
                createMethods(),
                endBuilder()
        ).collect(Collectors.toList());
    }

    private List<String> buildMethod() {
        return buildChildren().collect(Collectors.toList());
    }

    private List<String> buildSet() {
        return buildCollection();
    }

    private List<String> buildSetOf() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(SET_OF, PSF, clazz.getSimpleName(), formatTypes(), name, getNullableArguments(buildArguments()))))
                .collect(Collectors.toList());
    }

    private List<String> buildList() {
        return buildCollection();
    }

    private List<String> buildListOf() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(LIST_OF, PSF, clazz.getSimpleName(), formatTypes(), name, getNullableArguments(buildArguments()))))
                .collect(Collectors.toList());
    }

    private List<String> buildMap() {
        return buildCollection();
    }

    private List<String> buildMapOf() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(MAP_OF, PSF, clazz.getSimpleName(), formatTypes(), name, getNullableArguments(buildArguments()))))
                .collect(Collectors.toList());
    }

    private List<String> buildArray() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(NEW_ARRAY, PSF, clazz.getSimpleName(), name, clazz.getSimpleName(), buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildCollection() {
        if (collectionHasNullValues(this)) {
            return concatenate(
                    buildChildren(),
                    Stream.of(String.format(NEW_TYPED_OBJECT, PSF, clazz.getSimpleName(), formatTypes(), name, clazz.getSimpleName())))
                    .collect(Collectors.toList());
        }
        return concatenate(
                buildChildren(),
                Stream.of(String.format(NEW_TYPED_OBJECT, PSF, clazz.getSimpleName(), formatTypes(), name, clazz.getSimpleName())),
                startStaticBlock(),
                createMethods(),
                endStaticBlock()
        ).collect(Collectors.toList());
    }

    private List<String> buildValue() {
        if (isNullValue()) {
            return List.of();
        }
        return List.of(String.format(NEW_VALUE, PSF, clazz.getSimpleName(), name, value));
    }

    public boolean isNullValue() {
        return value != null && value.equals(NULL);
    }

    private Stream<String> createMethods() {
        return children.stream()
                .map(child -> buildType == BuildType.BUILDER ?
                        String.format(".%s(%s)", child.getName(), child.buildArguments()) :
                        String.format("%s.%s(%s);", name, child.getName(), child.buildArguments()));
    }

    private static String getNullableArguments(String buildArguments) {
        return Arrays.asList(buildArguments.split(ARGUMENT_DELIMITER)).contains(NULL) ? "" : buildArguments;
    }

    private String buildArguments() {
        return buildArguments(children);
    }

    private String buildArguments(List<ObjectBuilder> children) {
        return children.stream()
                .map(child -> {
                    if (child.isNullValue()) {
                        return List.of(NULL);
                    }
                    if (isBasicValue(child)) {
                        return child.buildInlineArgument();
                    }
                    return List.of(child.getName());
                }).flatMap(Collection::stream)
                .collect(Collectors.joining(ARGUMENT_DELIMITER));
    }

    private List<String> buildInlineArgument() {
        return List.of(this.value);
    }

    private String formatTypes() {
        return children.stream()
                .map(child -> {
                    if (child.getClazz() == null) {
                        return child.formatTypes();
                    }
                    if (child.getBuildType().isParameterizedType()) {
                        return String.format("%s<%s>", child.getClazz().getSimpleName(), child.formatTypes());
                    } else {
                        return child.getClazz().getSimpleName();
                    }
                }).collect(Collectors.joining(ARGUMENT_DELIMITER));

    }
}
