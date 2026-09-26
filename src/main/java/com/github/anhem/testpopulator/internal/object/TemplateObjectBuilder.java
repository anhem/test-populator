package com.github.anhem.testpopulator.internal.object;

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
    private final List<String> kotlinParameterNames;
    private final List<Boolean> isVarargs;

    private TemplateObjectBuilder(Builder builder) {
        super(builder.clazz, builder.name, builder.buildType, builder.useFullyQualifiedName, builder.expectedChildren, builder.parameterized, builder.isKotlinSupport);
        this.codeTemplate = builder.codeTemplate;
        this.factoryClassName = builder.factoryClassName;
        this.methodName = builder.methodName;
        this.skipIfNull = builder.skipIfNull;
        this.clearArgsIfNullChild = builder.clearArgsIfNullChild;
        this.buildMethodName = builder.buildMethodName;
        this.kotlinParameterNames = builder.kotlinParameterNames;
        this.isVarargs = builder.isVarargs;
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
        if (isKotlinSupport() && getClazz() != null && getClazz().isMemberClass() && !java.lang.reflect.Modifier.isStatic(getClazz().getModifiers()) && !argumentChildren.isEmpty()) {
            String outerInstance = com.github.anhem.testpopulator.internal.object.util.ArgumentFormatterUtil.getChildArgument(argumentChildren.get(0));
            List<ObjectBuilder> remainingArgs = argumentChildren.subList(1, argumentChildren.size());
            String remainingArgsString = getArgs(remainingArgs);
            return Stream.of(String.format("%s %s: %s = %s.%s(%s)", PSF, getName(), formatTypes(), outerInstance, getClassName(), remainingArgsString));
        }
        String args = getArgs(argumentChildren);
        return Stream.of(codeTemplate.render(isKotlinSupport(), PSF, getClassName(), formatTypes(), getName(), factoryClassName, methodName, args));
    }

    @Override
    protected String buildArguments(List<ObjectBuilder> children) {
        if (clearArgsIfNullChild && children.stream().anyMatch(ObjectBuilder::isNullValue)) {
            return "";
        }
        boolean hasKotlinNames = kotlinParameterNames != null && !kotlinParameterNames.isEmpty();
        if (hasKotlinNames || (isVarargs != null && !isVarargs.isEmpty())) {
            return java.util.stream.IntStream.range(0, children.size())
                    .mapToObj(i -> {
                        String argument = com.github.anhem.testpopulator.internal.object.util.ArgumentFormatterUtil.getChildArgument(children.get(i));
                        if (isKotlinSupport() && isVarargs != null && i < isVarargs.size() && isVarargs.get(i) && !argument.equals(NULL)) {
                            argument = "*" + argument;
                        }
                        if (hasKotlinNames) {
                            String paramName = kotlinParameterNames.get(i);
                            if (!paramName.matches("arg\\d+")) {
                                return paramName + " = " + argument;
                            }
                        }
                        return argument;
                    })
                    .collect(Collectors.joining(", "));
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
                Stream.of(codeTemplate.render(isKotlinSupport(), PSF, getClassName(), formatTypes(), getName(), factoryClassName, methodName, buildArguments(getArgumentChildren()))),
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
        private List<String> kotlinParameterNames;
        private List<Boolean> isVarargs;

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

        public Builder kotlinParameterNames(List<String> kotlinParameterNames) {
            this.kotlinParameterNames = kotlinParameterNames;
            return this;
        }

        public Builder isVarargs(List<Boolean> isVarargs) {
            this.isVarargs = isVarargs;
            return this;
        }

        @Override
        public TemplateObjectBuilder build() {
            return new TemplateObjectBuilder(this);
        }
    }
}
