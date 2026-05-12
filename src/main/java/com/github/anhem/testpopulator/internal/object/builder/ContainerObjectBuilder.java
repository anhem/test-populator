package com.github.anhem.testpopulator.internal.object.builder;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.collectionHasNullValues;

public class ContainerObjectBuilder extends ObjectBuilder {

    private final String template;
    private final String referencedClassName;

    private ContainerObjectBuilder(Builder builder) {
        super(builder.clazz, builder.name, builder.buildType, builder.useFullyQualifiedName, builder.expectedChildren, builder.parameterized);
        this.template = builder.template;
        this.referencedClassName = builder.referencedClassName;
        for (Class<?> referencedClass : builder.referencedClasses) {
            addReferencedClass(referencedClass);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected Stream<String> getInstantiationLine(List<ObjectBuilder> argumentChildren) {
        if (template == null) {
            return Stream.empty();
        }
        return Stream.of(String.format(template, PSF, getClassName(), formatTypes(), getName(), referencedClassName));
    }

    @Override
    protected List<ObjectBuilder> getArgumentChildren() {
        return Collections.emptyList();
    }

    @Override
    protected List<ObjectBuilder> getMethodChildren() {
        return getChildren();
    }

    @Override
    protected boolean shouldSkipMethods(List<ObjectBuilder> methodChildren) {
        return super.shouldSkipMethods(methodChildren) || collectionHasNullValues(this);
    }

    @Override
    protected String getMethodTargetName() {
        return getBuildType() == BuildType.MUTATOR && getParent() != null ? getParent().getName() : getName();
    }

    public static class Builder {
        private Class<?> clazz;
        private String name;
        private BuildType buildType;
        private String template;
        private boolean useFullyQualifiedName;
        private int expectedChildren;
        private boolean parameterized;
        private String referencedClassName;
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

        public Builder template(String template) {
            this.template = template;
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

        public Builder referencedClassName(String referencedClassName) {
            this.referencedClassName = referencedClassName;
            return this;
        }

        public Builder referencedClasses(Class<?>... referencedClasses) {
            this.referencedClasses = referencedClasses;
            return this;
        }

        public ContainerObjectBuilder build() {
            return new ContainerObjectBuilder(this);
        }
    }
}
