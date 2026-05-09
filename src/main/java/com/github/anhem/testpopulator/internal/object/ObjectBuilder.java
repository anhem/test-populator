package com.github.anhem.testpopulator.internal.object;

import java.util.*;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.*;
import static java.util.stream.Collectors.joining;

public abstract class ObjectBuilder {

    public static final String NULL = "null";
    public static final String PSF = "public static final";
    protected static final String ARGUMENT_DELIMITER = ", ";
    private final String name;
    private final BuildType buildType;
    private final List<ObjectBuilder> children = new ArrayList<>();
    private final Set<Class<?>> referencedClasses = new HashSet<>();
    private final Set<String> extraMethods = new HashSet<>();
    private final Set<String> extraImports = new HashSet<>();
    private final Set<String> extraStaticImports = new HashSet<>();
    private final int expectedChildren;
    private ObjectBuilder parent;
    private String value;

    public ObjectBuilder(String name, BuildType buildType, int expectedChildren) {
        this.name = name;
        this.buildType = buildType;
        this.expectedChildren = expectedChildren;
    }

    public abstract Class<?> getClazz();

    public abstract boolean isUseFullyQualifiedName();

    public abstract List<String> build();

    public void addChild(ObjectBuilder child) {
        children.add(child);
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
        referencedClasses.forEach(clazz -> addImport(clazz, null, isUseFullyQualifiedName(), imports, staticImports));
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

    protected String getClassName() {
        return isUseFullyQualifiedName() ? getClazz().getCanonicalName() : getClazz().getSimpleName();
    }

    protected Stream<String> createMethods() {
        return children.stream()
                .map(child -> buildType == BuildType.BUILDER ?
                        String.format(".%s(%s)", child.getName(), child.buildArguments()) :
                        String.format("%s.%s(%s);", name, child.getName(), child.buildArguments()));
    }

    protected String buildArguments() {
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
        return List.of(this.value);
    }

    protected String formatTypes() {
        return children.stream()
                .map(child -> {
                    if (child.getClazz() == null) {
                        return child.formatTypes();
                    }
                    if (child.getBuildType().isParameterizedType()) {
                        return String.format("%s<%s>", child.getClassName(), child.formatTypes());
                    } else {
                        return child.getClassName();
                    }
                }).collect(joining(ARGUMENT_DELIMITER));
    }
}
