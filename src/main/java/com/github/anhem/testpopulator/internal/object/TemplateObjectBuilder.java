package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.config.Language;
import com.github.anhem.testpopulator.internal.util.KotlinUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.util.ObjectBuilderUtil.concatenate;
import static com.github.anhem.testpopulator.internal.object.util.ObjectBuilderUtil.endBuilder;

public class TemplateObjectBuilder extends ObjectBuilder {

    private final CodeTemplate codeTemplate;
    private final String factoryClassName;
    private final String methodName;
    private final boolean skipIfNull;
    private final boolean clearArgsIfNullChild;
    private final String buildMethodName;
    private final Class<?>[] parameterTypes;

    private TemplateObjectBuilder(Builder builder) {
        super(builder);
        this.codeTemplate = builder.codeTemplate;
        this.factoryClassName = builder.factoryClassName;
        this.methodName = builder.methodName;
        this.skipIfNull = builder.skipIfNull;
        this.clearArgsIfNullChild = builder.clearArgsIfNullChild;
        this.buildMethodName = builder.buildMethodName;
        this.parameterTypes = builder.parameterTypes;
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
        return Stream.of(codeTemplate.render(
                getLanguage(),
                getLanguage().getModifier(),
                getClassName(),
                formatTypes(),
                getName(),
                factoryClassName,
                methodName,
                args)
        );
    }

    @Override
    protected String buildArguments(List<ObjectBuilder> children) {
        if (clearArgsIfNullChild && children.stream().anyMatch(ObjectBuilder::isNullValue)) {
            return "";
        }
        return super.buildArguments(getValidKotlinChildren(children));
    }

    @Override
    protected Stream<String> buildChildren(List<ObjectBuilder> children) {
        return super.buildChildren(getValidKotlinChildren(children));
    }

    private List<ObjectBuilder> getValidKotlinChildren(List<ObjectBuilder> children) {
        if (parameterTypes != null &&
                Language.fromClass(getClazz()) == Language.KOTLIN &&
                KotlinUtil.isKotlinConstructor(parameterTypes, true)) {
            int maskCount = (parameterTypes.length - 2) / 32 + 1;
            int realParameterCount = parameterTypes.length - maskCount - 1;
            if (children.size() >= realParameterCount) {
                return children.subList(0, realParameterCount);
            }
        }
        return children;
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
                Stream.of(codeTemplate.render(
                        getLanguage(),
                        getLanguage().getModifier(),
                        getClassName(),
                        formatTypes(),
                        getName(),
                        factoryClassName,
                        methodName,
                        buildArguments(getArgumentChildren())
                )),
                renderBuilderMethodCalls(),
                endBuilder(buildMethodName)
        ).collect(Collectors.toList());
    }

    public static class Builder extends BaseBuilder<Builder> {
        private CodeTemplate codeTemplate;
        private String factoryClassName;
        private String methodName;
        private boolean skipIfNull;
        private boolean clearArgsIfNullChild;
        private String buildMethodName;
        private Class<?>[] parameterTypes;

        public Builder codeTemplate(CodeTemplate codeTemplate) {
            this.codeTemplate = codeTemplate;
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

        public Builder parameterTypes(Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
            return this;
        }

        @Override
        public TemplateObjectBuilder build() {
            return new TemplateObjectBuilder(this);
        }
    }
}
