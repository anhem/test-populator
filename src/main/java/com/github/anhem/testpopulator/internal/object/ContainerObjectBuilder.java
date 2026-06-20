package com.github.anhem.testpopulator.internal.object;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.util.ObjectBuilderUtil.collectionHasNullValues;

public class ContainerObjectBuilder extends ObjectBuilder {

    private final CodeTemplate codeTemplate;
    private final String referencedClassName;

    private ContainerObjectBuilder(Builder builder) {
        super(builder);
        this.codeTemplate = builder.codeTemplate;
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
        if (codeTemplate == null) {
            return Stream.empty();
        }
        return Stream.of(codeTemplate.render(
                getLanguage(),
                getLanguage().getModifier(),
                getClassName(),
                formatTypes(),
                getName(),
                referencedClassName
        ));
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

    public static class Builder extends BaseBuilder<Builder> {
        private CodeTemplate codeTemplate;
        private String referencedClassName;

        public Builder codeTemplate(CodeTemplate codeTemplate) {
            this.codeTemplate = codeTemplate;
            return this;
        }

        public Builder referencedClassName(String referencedClassName) {
            this.referencedClassName = referencedClassName;
            return this;
        }

        @Override
        public ContainerObjectBuilder build() {
            return new ContainerObjectBuilder(this);
        }
    }
}
