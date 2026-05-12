package com.github.anhem.testpopulator.internal.object.builder;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.concatenate;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.endBuilder;

public class TemplateObjectBuilder extends ObjectBuilder {

    private final CodeTemplate codeTemplate;
    private final String factoryClassName;
    private final String methodName;
    private final boolean skipIfNull;
    private final boolean clearArgsIfNullChild;
    private final String buildMethodName;

    private TemplateObjectBuilder(Builder builder) {
        super(builder.clazz, builder.name, builder.buildType, builder.useFullyQualifiedName, builder.expectedChildren, builder.parameterized);
        this.codeTemplate = builder.codeTemplate;
        this.factoryClassName = builder.factoryClassName;
        this.methodName = builder.methodName;
        this.skipIfNull = builder.skipIfNull;
        this.clearArgsIfNullChild = builder.clearArgsIfNullChild;
        this.buildMethodName = builder.buildMethodName;
        for (Class<?> referencedClass : builder.referencedClasses) {
            addReferencedClass(referencedClass);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<String> build() {
        if (getBuildType() == BuildType.BUILDER) {
            return buildFluentBuilder();
        }
        return super.build();
    }

    @Override
    protected Stream<String> getInstantiationLine(List<ObjectBuilder> argumentChildren) {
        if (codeTemplate == null || (skipIfNull && isNullValue())) {
            return Stream.empty();
        }
        String args = getArgs(argumentChildren);
        return Stream.of(codeTemplate.render(PSF, getClassName(), formatTypes(), getName(), factoryClassName, methodName, args));
    }

    @Override
    protected String buildArguments(List<ObjectBuilder> children) {
        if (clearArgsIfNullChild && children.stream().anyMatch(ObjectBuilder::isNullValue)) {
            return "";
        }
        return super.buildArguments(children);
    }

    private String getArgs(List<ObjectBuilder> argumentChildren) {
        if (getBuildType() == BuildType.VALUE && argumentChildren.isEmpty()) {
            return getValue() == null ? NULL : getValue();
        } else {
            return buildArguments(argumentChildren);
        }
    }

    private List<String> buildFluentBuilder() {
        return concatenate(
                buildChildren(),
                Stream.of(codeTemplate.render(PSF, getClassName(), formatTypes(), getName(), factoryClassName, methodName, buildArguments(getArgumentChildren()))),
                createMethods(),
                endBuilder(buildMethodName)
        ).collect(Collectors.toList());
    }

    public static class Builder {
        private Class<?> clazz;
        private String name;
        private BuildType buildType;
        private CodeTemplate codeTemplate;
        private boolean useFullyQualifiedName;
        private int expectedChildren;
        private boolean parameterized;
        private String factoryClassName;
        private String methodName;
        private boolean skipIfNull;
        private boolean clearArgsIfNullChild;
        private String buildMethodName;
        private Class<?>[] referencedClasses = new Class<?>[0];

        public Builder clazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder buildType(BuildType buildType) {
            this.buildType = buildType;
            return this;
        }

        public Builder codeTemplate(CodeTemplate codeTemplate) {
            this.codeTemplate = codeTemplate;
            return this;
        }

        public Builder useFullyQualifiedName(boolean useFullyQualifiedName) {
            this.useFullyQualifiedName = useFullyQualifiedName;
            return this;
        }

        public Builder expectedChildren(int expectedChildren) {
            this.expectedChildren = expectedChildren;
            return this;
        }

        public Builder parameterized(boolean parameterized) {
            this.parameterized = parameterized;
            return this;
        }

        public Builder factoryClassName(String factoryClassName) {
            this.factoryClassName = factoryClassName;
            return this;
        }

        public Builder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Builder skipIfNull(boolean skipIfNull) {
            this.skipIfNull = skipIfNull;
            return this;
        }

        public Builder clearArgsIfNullChild(boolean clearArgsIfNullChild) {
            this.clearArgsIfNullChild = clearArgsIfNullChild;
            return this;
        }

        public Builder buildMethodName(String buildMethodName) {
            this.buildMethodName = buildMethodName;
            return this;
        }

        public Builder referencedClasses(Class<?>... referencedClasses) {
            this.referencedClasses = referencedClasses;
            return this;
        }

        public TemplateObjectBuilder build() {
            return new TemplateObjectBuilder(this);
        }
    }
}
