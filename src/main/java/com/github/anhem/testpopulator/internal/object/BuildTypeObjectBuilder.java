package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.exception.ObjectException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.BuildType.MUTATOR;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.*;
import static java.util.Collections.emptyList;

public class BuildTypeObjectBuilder extends ObjectBuilder {
    private static final String NEW_OBJECT_WITH_ARGUMENTS = "%s %s %s = new %s(%s);";
    private static final String NEW_OBJECT = "%s %s %s = new %s();";
    private static final String NEW_TYPED_OBJECT = "%s %s<%s> %s = new %s<>();";
    private static final String SET_OF = "%s %s<%s> %s = Set.of(%s);";
    private static final String LIST_OF = "%s %s<%s> %s = List.of(%s);";
    private static final String MAP_OF = "%s %s<%s> %s = Map.of(%s);";
    private static final String MAP_ENTRY = "%s %s<%s> %s = new AbstractMap.SimpleEntry<>(%s);";
    private static final String NEW_ARRAY = "%s %s[] %s = new %s[]{%s};";
    private static final String NEW_VALUE = "%s %s %s = %s;";

    private final Class<?> clazz;
    private final boolean useFullyQualifiedName;

    public BuildTypeObjectBuilder(Class<?> clazz, String name, BuildType buildType, boolean useFullyQualifiedName, int expectedChildren) {
        super(name, buildType, expectedChildren);
        this.clazz = clazz;
        this.useFullyQualifiedName = useFullyQualifiedName;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean isUseFullyQualifiedName() {
        return useFullyQualifiedName;
    }

    @Override
    public List<String> build() {
        return buildByType();
    }

    private List<String> buildByType() {
        switch (getBuildType()) {
            case CONSTRUCTOR:
                return buildConstructor();
            case SETTER:
                return buildSetter();
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
            case MAP_ENTRY:
                return buildMapEntry();
            case ARRAY:
                return buildArray();
            case VALUE:
                return buildValue();
            case MUTATOR:
                return buildMutator();
            default:
                throw new ObjectException(String.format("Invalid buildType %s", getBuildType()));
        }
    }

    private List<String> buildConstructor() {
        if (getChildren().stream().anyMatch(child -> child.getBuildType() == MUTATOR)) {
            Map<Boolean, List<ObjectBuilder>> childrenByMutator = getChildren().stream()
                    .collect(Collectors.groupingBy(child -> child.getBuildType() == MUTATOR));
            List<ObjectBuilder> mutatorChildren = childrenByMutator.getOrDefault(true, emptyList());
            List<ObjectBuilder> otherChildren = childrenByMutator.getOrDefault(false, emptyList());
            return concatenate(
                    buildChildren(otherChildren),
                    Stream.of(String.format(NEW_OBJECT_WITH_ARGUMENTS, PSF, getClassName(), getName(), getClassName(), buildArguments(otherChildren))),
                    buildChildren(mutatorChildren))
                    .collect(Collectors.toList());
        } else {
            return concatenate(buildChildren(),
                    Stream.of(String.format(NEW_OBJECT_WITH_ARGUMENTS, PSF, getClassName(), getName(), getClassName(), buildArguments())))
                    .collect(Collectors.toList());
        }
    }

    private List<String> buildSetter() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(NEW_OBJECT, PSF, getClassName(), getName(), getClassName())),
                startStaticBlock(),
                createMethods(),
                endStaticBlock()
        ).collect(Collectors.toList());
    }

    private List<String> buildSet() {
        return buildCollection();
    }

    private List<String> buildSetOf() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(SET_OF, PSF, getClassName(), formatTypes(), getName(), getNullableArguments(buildArguments()))))
                .collect(Collectors.toList());
    }

    private List<String> buildList() {
        return buildCollection();
    }

    private List<String> buildListOf() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(LIST_OF, PSF, getClassName(), formatTypes(), getName(), getNullableArguments(buildArguments()))))
                .collect(Collectors.toList());
    }

    private List<String> buildMap() {
        return buildCollection();
    }

    private List<String> buildMapOf() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(MAP_OF, PSF, getClassName(), formatTypes(), getName(), getNullableArguments(buildArguments()))))
                .collect(Collectors.toList());
    }

    private List<String> buildMapEntry() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(MAP_ENTRY, PSF, getClassName(), formatTypes(), getName(), buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildArray() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(NEW_ARRAY, PSF, getClassName(), getName(), getClassName(), buildArguments())))
                .collect(Collectors.toList());
    }

    private List<String> buildCollection() {
        if (collectionHasNullValues(this)) {
            return concatenate(
                    buildChildren(),
                    Stream.of(String.format(NEW_TYPED_OBJECT, PSF, getClassName(), formatTypes(), getName(), getClassName())))
                    .collect(Collectors.toList());
        }
        return concatenate(
                buildChildren(),
                Stream.of(String.format(NEW_TYPED_OBJECT, PSF, getClassName(), formatTypes(), getName(), getClassName())),
                startStaticBlock(),
                createMethods(),
                endStaticBlock()
        ).collect(Collectors.toList());
    }


    private List<String> buildValue() {
        if (isNullValue()) {
            return List.of();
        }
        return List.of(String.format(NEW_VALUE, PSF, getClassName(), getName(), getValue()));
    }

    private List<String> buildMutator() {
        return concatenate(
                buildChildren(),
                startStaticBlock(),
                createMethods(),
                endStaticBlock()
        ).collect(Collectors.toList());
    }

    protected static String getNullableArguments(String buildArguments) {
        return Arrays.asList(buildArguments.split(ARGUMENT_DELIMITER)).contains(NULL) ? "" : buildArguments;
    }

}
