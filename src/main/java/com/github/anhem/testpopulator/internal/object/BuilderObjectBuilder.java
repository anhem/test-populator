package com.github.anhem.testpopulator.internal.object;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.concatenate;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.endBuilder;

public class BuilderObjectBuilder extends ObjectBuilder {

    private static final String BUILDER = "%s %s %s = %s.%s()";

    private final Class<?> clazz;
    private final boolean useFullyQualifiedName;
    private final String builderMethodName;
    private final String buildMethodName;

    public BuilderObjectBuilder(Class<?> clazz, String name, boolean useFullyQualifiedName, int expectedChildren, String builderMethodName, String buildMethodName) {
        super(name, BuildType.BUILDER, expectedChildren);
        this.clazz = clazz;
        this.useFullyQualifiedName = useFullyQualifiedName;
        this.builderMethodName = builderMethodName;
        this.buildMethodName = buildMethodName;
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
        return buildBuilder();
    }

    private List<String> buildBuilder() {
        return concatenate(
                buildChildren(),
                Stream.of(String.format(BUILDER, PSF, getClassName(), getName(), getClassName(), builderMethodName)),
                createMethods(),
                endBuilder(buildMethodName)
        ).collect(Collectors.toList());
    }
}
