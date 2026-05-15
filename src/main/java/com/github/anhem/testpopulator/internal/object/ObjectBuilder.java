package com.github.anhem.testpopulator.internal.object;

import java.util.*;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.*;
import static java.util.stream.Collectors.joining;

public abstract class ObjectBuilder {

    public static final String NULL = "null";
    public static final String PSF = "public static final";
    protected static final String ARGUMENT_DELIMITER = ", ";
    private final Class<?> clazz;
    private final String name;
    private final BuildType buildType;
    private final boolean useFullyQualifiedName;
    private final List<ObjectBuilder> children = new ArrayList<>();
    private final List<ObjectBuilder> argumentChildren = new ArrayList<>();
    private final List<ObjectBuilder> methodChildren = new ArrayList<>();
    private final Set<Class<?>> referencedClasses = new HashSet<>();
    private final Set<String> extraMethods = new HashSet<>();
    private final Set<String> extraImports = new HashSet<>();
    private final Set<String> extraStaticImports = new HashSet<>();
    private final int expectedChildren;
    private final boolean parameterized;
    private boolean skipNullMethods;
    private ObjectBuilder parent;
    private String value;

    protected ObjectBuilder(Class<?> clazz, String name, BuildType buildType, boolean useFullyQualifiedName, int expectedChildren) {
        this(clazz, name, buildType, useFullyQualifiedName, expectedChildren, false);
    }

    protected ObjectBuilder(Class<?> clazz, String name, BuildType buildType, boolean useFullyQualifiedName, int expectedChildren, boolean parameterized) {
        this.clazz = clazz;
        this.name = name;
        this.buildType = buildType;
        this.useFullyQualifiedName = useFullyQualifiedName;
        this.expectedChildren = expectedChildren;
        this.parameterized = parameterized;
    }

    public void setSkipNullMethods(boolean skipNullMethods) {
        this.skipNullMethods = skipNullMethods;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean isUseFullyQualifiedName() {
        return useFullyQualifiedName;
    }

    public boolean isParameterized() {
        return parameterized;
    }

    public List<String> build() {
        Stream<String> dependencies = concatenate(
                buildChildren(argumentChildren),
                buildChildren(methodChildren)
        );

        Stream<String> methodBlock = parent != null && parent.methodChildren.contains(this) ? Stream.empty() : buildMethodBlock(methodChildren);

        return concatenate(
                dependencies,
                getInstantiationLine(argumentChildren),
                methodBlock
        ).collect(java.util.stream.Collectors.toList());
    }

    protected List<ObjectBuilder> getArgumentChildren() {
        return argumentChildren;
    }

    protected List<ObjectBuilder> getMethodChildren() {
        return methodChildren;
    }

    protected abstract Stream<String> getInstantiationLine(List<ObjectBuilder> argumentChildren);

    protected boolean shouldSkipMethods(List<ObjectBuilder> methodChildren) {
        return methodChildren.isEmpty();
    }

    protected Stream<String> buildMethodBlock(List<ObjectBuilder> methodChildren) {
        if (shouldSkipMethods(methodChildren)) {
            return Stream.empty();
        }
        return concatenate(startStaticBlock(), createMethods(methodChildren), endStaticBlock());
    }

    protected Stream<String> createMethods(List<ObjectBuilder> methodChildren) {
        return methodChildren.stream()
                .flatMap(child -> {
                    if (!child.methodChildren.isEmpty()) {
                        return child.createMethods(child.methodChildren);
                    }
                    return Stream.of(String.format("%s.%s(%s);", getMethodTargetName(), child.getName(), child.buildArguments()));
                });
    }

    protected String getMethodTargetName() {
        return getName();
    }

    public void addChild(ObjectBuilder child) {
        children.add(child);
        if (child.getBuildType() == BuildType.MUTATOR || child.getBuildType() == BuildType.METHOD) {
            methodChildren.add(child);
        } else {
            argumentChildren.add(child);
        }
    }

    public List<ObjectBuilder> getChildren() {
        return children;
    }

    public void addMethods(Collection<String> methods) {
        this.extraMethods.addAll(methods);
    }

    public void addImports(Collection<String> imports) {
        this.extraImports.addAll(imports);
    }

    public void addReferencedClass(Class<?> clazz) {
        this.referencedClasses.add(clazz);
    }

    public void addStaticImports(Collection<String> staticImports) {
        this.extraStaticImports.addAll(staticImports);
    }

    public boolean hasAllChildren() {
        return expectedChildren == children.size();
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

    public String getValue() {
        return value;
    }

    public ObjectResult buildAll() {
        String packageName = getPackageName(getClazz());
        String className = formatClassName(getClazz());
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();
        getImports(imports, staticImports);
        List<String> objects = build();
        Set<String> methods = getMethods();

        return new ObjectResult(packageName, className, imports, staticImports, objects, methods);
    }

    private void getImports(Set<String> imports, Set<String> staticImports) {
        addImport(getClazz(), value, isUseFullyQualifiedName(), imports, staticImports);
        referencedClasses.forEach(c -> addImport(c, null, isUseFullyQualifiedName(), imports, staticImports));
        imports.addAll(extraImports);
        staticImports.addAll(extraStaticImports);
        children.forEach(objectBuilder -> objectBuilder.getImports(imports, staticImports));
    }

    private Set<String> getMethods() {
        Set<String> methods = new HashSet<>(extraMethods);
        Optional.ofNullable(getHelperMethod(getClazz())).ifPresent(methods::add);
        children.forEach(child -> methods.addAll(child.getMethods()));
        return methods;
    }

    protected Stream<String> buildChildren() {
        return buildChildren(children);
    }

    protected Stream<String> buildChildren(List<ObjectBuilder> children) {
        return children.stream()
                .filter(child -> !isBasicValue(child))
                .map(ObjectBuilder::build)
                .flatMap(Collection::stream);
    }
    public boolean isNullValue() {
        return value != null && value.equals(NULL);
    }

    public boolean anyArgumentIsNull() {
        if (buildType == BuildType.VALUE) {
            return isNullValue();
        }
        return argumentChildren.stream().anyMatch(ObjectBuilder::anyArgumentIsNull);
    }

    protected String getClassName() {
        return isUseFullyQualifiedName() ? getClazz().getCanonicalName() : getClazz().getSimpleName();
    }

    protected Stream<String> createMethods() {
        return children.stream()
                .filter(child -> !skipNullMethods || !child.anyArgumentIsNull())
                .map(child -> {
                    if (buildType == BuildType.BUILDER) {
                        return String.format("    .%s(%s)", child.getName(), child.buildArguments());
                    }
                    return String.format("%s.%s(%s);", name, child.getName(), child.buildArguments());
                });
    }


    public String buildArguments() {
        if (children.isEmpty()) {
            if (getBuildType() == BuildType.VALUE) {
                return value == null ? NULL : value;
            }
            return "";
        }
        return buildArguments(children);
    }

    protected String buildArguments(List<ObjectBuilder> children) {
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
                .collect(joining(ARGUMENT_DELIMITER));
    }

    private List<String> buildInlineArgument() {
        return List.of(value == null ? NULL : value);
    }

    protected String formatTypes() {
        return children.stream()
                .map(child -> {
                    if (child.getClazz() == null) {
                        return child.formatTypes();
                    }
                    if (child.isParameterized()) {
                        return String.format("%s<%s>", child.getClassName(), child.formatTypes());
                    } else {
                        return child.getClassName();
                    }
                }).collect(joining(ARGUMENT_DELIMITER));
    }
}
