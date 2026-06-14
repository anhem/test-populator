package com.github.anhem.testpopulator.internal.object;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.*;

public class TemplateObjectBuilder extends ObjectBuilder {

    private final CodeTemplate codeTemplate;
    private final String factoryClassName;
    private final String methodName;
    private final boolean skipIfNull;
    private final boolean clearArgsIfNullChild;
    private final String buildMethodName;

    private final boolean privateConstructor;

    private TemplateObjectBuilder(Builder builder) {
        super(builder.clazz, builder.name, builder.buildType, builder.useFullyQualifiedName, builder.expectedChildren, builder.parameterized);
        this.codeTemplate = builder.codeTemplate;
        this.factoryClassName = builder.factoryClassName;
        this.methodName = builder.methodName;
        this.skipIfNull = builder.skipIfNull;
        this.clearArgsIfNullChild = builder.clearArgsIfNullChild;
        this.buildMethodName = builder.buildMethodName;
        this.privateConstructor = builder.privateConstructor;
        for (Class<?> referencedClass : builder.referencedClasses) {
            addReferencedClass(referencedClass);
        }
    }

    public boolean isPrivateConstructor() {
        return privateConstructor;
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
    public Set<String> getMethods() {
        Set<String> methods = new HashSet<>(super.getMethods());
        if (isPrivateConstructor()) {
            methods.add(com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.getInstantiateHelperMethod());
        }
        return methods;
    }

    @Override
    protected void getImports(Set<String> imports, Set<String> staticImports) {
        super.getImports(imports, staticImports);
        if (isPrivateConstructor()) {
            getArgumentChildren().forEach(child -> {
                Class<?> c = child.getClazz();
                if (c != null) {
                    addImport(c, null, child.isUseFullyQualifiedName(), imports, staticImports);
                }
            });
        }
    }

    @Override
    protected Stream<String> getInstantiationLine(List<ObjectBuilder> argumentChildren) {
        if (codeTemplate == null || (skipIfNull && isNullValue())) {
            return Stream.empty();
        }
        if (isPrivateConstructor()) {
            String parameterTypes = formatParameterTypes(argumentChildren);
            String args = getArgs(argumentChildren);
            return Stream.of(String.format("%s %s %s = instantiate(%s.class, new Class<?>[]{%s}, new Object[]{%s});",
                    PSF, getClassName(), getName(), getClassName(), parameterTypes, args));
        }
        String args = getArgs(argumentChildren);
        return Stream.of(codeTemplate.render(PSF, getClassName(), formatTypes(), getName(), factoryClassName, methodName, args));
    }

    private String formatParameterTypes(List<ObjectBuilder> argumentChildren) {
        return argumentChildren.stream()
                .map(child -> {
                    Class<?> c = child.getClazz();
                    if (c == null) {
                        return "null";
                    }
                    String className = child.isUseFullyQualifiedName() ? c.getCanonicalName() : c.getSimpleName();
                    if (child.getBuildType() == BuildType.ARRAY) {
                        return className + "[].class";
                    }
                    return className + ".class";
                })
                .collect(Collectors.joining(", "));
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

    public static class Builder extends BaseBuilder<Builder> {
        private CodeTemplate codeTemplate;
        private String factoryClassName;
        private String methodName;
        private boolean skipIfNull;
        private boolean clearArgsIfNullChild;
        private String buildMethodName;
        private boolean privateConstructor;

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

        public Builder privateConstructor(boolean privateConstructor) {
            this.privateConstructor = privateConstructor;
            return this;
        }

        @Override
        public TemplateObjectBuilder build() {
            return new TemplateObjectBuilder(this);
        }
    }
}
