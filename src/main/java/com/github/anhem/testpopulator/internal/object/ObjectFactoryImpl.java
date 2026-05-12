package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.OverrideTarget;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.ObjectException;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.*;

import static com.github.anhem.testpopulator.internal.object.BuildType.*;
import static com.github.anhem.testpopulator.internal.object.ObjectBuilder.NULL;
import static com.github.anhem.testpopulator.internal.util.FileWriterUtil.*;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.useFullyQualifiedName;

public class ObjectFactoryImpl implements ObjectFactory {

    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";
    private static final String NEW_PREFIX = "new ";

    private final PopulateConfig populateConfig;
    private final Map<String, Integer> classNameCounters;
    private final Map<String, Class<?>> classNames;
    private ObjectBuilder currentObjectBuilder;

    public ObjectFactoryImpl(PopulateConfig populateConfig) {
        this.populateConfig = populateConfig;
        this.classNameCounters = new HashMap<>();
        this.classNames = new HashMap<>();
    }

    @Override
    public <T> void constructor(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(templateBuilder(clazz, CONSTRUCTOR, expectedChildren)
                .codeTemplate(CodeTemplate.CONSTRUCTOR)
                .build());
    }

    @Override
    public <T> void setter(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(containerBuilder(clazz, SETTER, expectedChildren)
                .template(CodeTemplate.SETTER.getFormat())
                .build());
    }

    @Override
    public <T> void mutator(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(containerBuilder(clazz, MUTATOR, expectedChildren)
                .build());
    }

    @Override
    public <T> void builder(Class<T> clazz, int expectedChildren, String builderMethodName, String buildMethodName) {
        setNextObjectBuilder(templateBuilder(clazz, BUILDER, expectedChildren)
                .codeTemplate(CodeTemplate.BUILDER)
                .methodName(builderMethodName)
                .buildMethodName(buildMethodName)
                .build());
    }

    @Override
    public void method(String methodName, int expectedChildren) {
        setNextObjectBuilder(new MethodBuilder(methodName, expectedChildren));
    }

    @Override
    public <T> void staticMethod(Class<T> clazz, String methodName, int expectedChildren) {
        setNextObjectBuilder(templateBuilder(clazz, STATIC_METHOD, expectedChildren)
                .codeTemplate(CodeTemplate.STATIC_METHOD)
                .factoryClassName(clazz.getSimpleName())
                .methodName(methodName)
                .build());
    }

    @Override
    public <T> void set(Class<T> clazz) {
        setNextObjectBuilder(containerBuilder(clazz, SET, 1)
                .template(CodeTemplate.TYPED_COLLECTION.getFormat())
                .parameterized(true)
                .build());
        method("add", 1);
    }

    @Override
    public void setOf() {
        setNextObjectBuilder(templateBuilder(Set.class, SET, 1)
                .codeTemplate(CodeTemplate.IMMUTABLE)
                .parameterized(true)
                .factoryClassName(Set.class.getSimpleName())
                .methodName("of")
                .clearArgsIfNullChild(true)
                .build());
    }

    @Override
    public <T> void enumSet(Class<T> clazz, Class<?> enumClazz) {
        setNextObjectBuilder(containerBuilder(clazz, ENUM_SET, 1)
                .template(CodeTemplate.ENUM_SET.getFormat())
                .parameterized(true)
                .referencedClassName(enumClazz.getSimpleName())
                .referencedClasses(enumClazz)
                .build());
        method("add", 1);
    }

    @Override
    public <T> void list(Class<T> clazz) {
        setNextObjectBuilder(containerBuilder(clazz, LIST, 1)
                .template(CodeTemplate.TYPED_COLLECTION.getFormat())
                .parameterized(true)
                .build());
        method("add", 1);
    }

    @Override
    public void listOf() {
        setNextObjectBuilder(templateBuilder(List.class, LIST, 1)
                .codeTemplate(CodeTemplate.IMMUTABLE)
                .parameterized(true)
                .factoryClassName(List.class.getSimpleName())
                .methodName("of")
                .clearArgsIfNullChild(true)
                .build());
    }

    @Override
    public <T> void map(Class<T> clazz) {
        boolean parameterized = !clazz.equals(Properties.class);
        CodeTemplate codeTemplate = parameterized ? CodeTemplate.TYPED_COLLECTION : CodeTemplate.COLLECTION;
        setNextObjectBuilder(containerBuilder(clazz, MAP, 1)
                .template(codeTemplate.getFormat())
                .parameterized(parameterized)
                .build());
        method("put", 2);
    }

    @Override
    public void mapOf() {
        setNextObjectBuilder(templateBuilder(Map.class, MAP, 2)
                .codeTemplate(CodeTemplate.IMMUTABLE)
                .parameterized(true)
                .factoryClassName(Map.class.getSimpleName())
                .methodName("of")
                .clearArgsIfNullChild(true)
                .build());
    }

    @Override
    public <T> void enumMap(Class<T> clazz, Class<?> enumClazz) {
        boolean parameterized = !clazz.equals(Properties.class);
        CodeTemplate codeTemplate = parameterized ? CodeTemplate.ENUM_MAP : CodeTemplate.COLLECTION;
        setNextObjectBuilder(containerBuilder(clazz, ENUM_MAP, 1)
                .template(codeTemplate.getFormat())
                .parameterized(parameterized)
                .referencedClassName(enumClazz.getSimpleName())
                .referencedClasses(enumClazz)
                .build());
        method("put", 2);
    }

    @Override
    public <T> void mapEntry(Class<T> clazz) {
        setNextObjectBuilder(templateBuilder(clazz, STATIC_METHOD, 2)
                .codeTemplate(CodeTemplate.MAP_ENTRY)
                .parameterized(true)
                .factoryClassName(AbstractMap.class.getSimpleName())
                .referencedClasses(AbstractMap.class)
                .build());
    }

    @Override
    public void optional() {
        setNextObjectBuilder(templateBuilder(Optional.class, STATIC_METHOD, 1)
                .codeTemplate(CodeTemplate.OPTIONAL)
                .parameterized(true)
                .factoryClassName(Optional.class.getSimpleName())
                .methodName("ofNullable")
                .referencedClasses(Optional.class)
                .build());
    }

    @Override
    public <T> void array(Class<T> clazz) {
        setNextObjectBuilder(templateBuilder(clazz, ARRAY, 1)
                .codeTemplate(CodeTemplate.ARRAY)
                .build());
    }

    @Override
    public <T> void stream(Class<T> clazz) {
        TemplateObjectBuilder.Builder streamBuilder = templateBuilder(clazz, STATIC_METHOD, 1)
                .methodName("of");

        if (clazz.equals(IntStream.class)) {
            streamBuilder.codeTemplate(CodeTemplate.INT_STREAM).factoryClassName(IntStream.class.getSimpleName()).referencedClasses(IntStream.class);
        } else if (clazz.equals(LongStream.class)) {
            streamBuilder.codeTemplate(CodeTemplate.LONG_STREAM).factoryClassName(LongStream.class.getSimpleName()).referencedClasses(LongStream.class);
        } else if (clazz.equals(DoubleStream.class)) {
            streamBuilder.codeTemplate(CodeTemplate.DOUBLE_STREAM).factoryClassName(DoubleStream.class.getSimpleName()).referencedClasses(DoubleStream.class);
        } else {
            streamBuilder.codeTemplate(CodeTemplate.STREAM).parameterized(true).factoryClassName(Stream.class.getSimpleName()).referencedClasses(Stream.class);
        }
        setNextObjectBuilder(streamBuilder.build());
    }

    @Override
    public <T> void iterator(Class<T> clazz) {
        setNextObjectBuilder(templateBuilder(clazz, STATIC_METHOD, 1)
                .codeTemplate(CodeTemplate.ITERATOR)
                .parameterized(true)
                .factoryClassName(Stream.class.getSimpleName())
                .methodName("of")
                .referencedClasses(Stream.class, Objects.class)
                .build());
    }

    @Override
    public <T> void iterable(Class<T> clazz) {
        setNextObjectBuilder(templateBuilder(clazz, STATIC_METHOD, 1)
                .codeTemplate(CodeTemplate.ITERABLE)
                .parameterized(true)
                .factoryClassName(Stream.class.getSimpleName())
                .methodName("of")
                .referencedClasses(Stream.class, Objects.class, Collectors.class)
                .build());
    }

    @Override
    public <T> void scanner(Class<T> clazz) {
        setNextObjectBuilder(templateBuilder(clazz, STATIC_METHOD, 1)
                .codeTemplate(CodeTemplate.SCANNER)
                .factoryClassName(Scanner.class.getSimpleName())
                .referencedClasses(Scanner.class)
                .build());
    }

    @Override
    public <T> void future(Class<T> clazz) {
        setNextObjectBuilder(templateBuilder(clazz, STATIC_METHOD, 1)
                .codeTemplate(CodeTemplate.FUTURE)
                .parameterized(true)
                .factoryClassName(CompletableFuture.class.getSimpleName())
                .methodName("completedFuture")
                .referencedClasses(CompletableFuture.class)
                .build());
    }

    @Override
    public <T> void value(T value, Class<T> clazz, String name) {
        TemplateObjectBuilder objectBuilder = templateBuilder(clazz, VALUE, 0)
                .codeTemplate(CodeTemplate.VALUE)
                .build();
        String stringValue = toStringValue(value, clazz, name, objectBuilder);
        if (objectBuilder.isUseFullyQualifiedName()) {
            if (stringValue.startsWith(NEW_PREFIX)) {
                stringValue = String.format("%s%s.%s", NEW_PREFIX, clazz.getPackageName(), stringValue.replace(NEW_PREFIX, ""));
            } else {
                stringValue = String.format("%s.%s", clazz.getPackageName(), stringValue);
            }
        }
        objectBuilder.setValue(stringValue);
        setNextObjectBuilder(objectBuilder);
    }

    @Override
    public <T> void nullValue(Class<T> clazz) {
        TemplateObjectBuilder objectBuilder = templateBuilder(clazz, VALUE, 0)
                .codeTemplate(CodeTemplate.VALUE)
                .build();
        objectBuilder.setValue(NULL);
        setNextObjectBuilder(objectBuilder);
    }

    @Override
    public ObjectResult build() {
        ObjectBuilder topObjectBuilder = toTop();
        return topObjectBuilder != null ? topObjectBuilder.buildAll() : ObjectResult.EMPTY_OBJECT_RESULT;
    }

    @Override
    public void writeToFile() {
        ObjectResult objectResult = build();
        if (objectResult.isValid()) {
            Path path = getPath(objectResult, populateConfig);
            createOrOverwriteFile(path);
            writePackage(objectResult, path);
            writeImports(objectResult, path);
            writeStaticImports(objectResult, path);
            writeStartClass(objectResult, path, populateConfig);
            writeObjects(objectResult, path);
            writeMethods(objectResult, path);
            writeEndClass(path);
        }
    }

    private ObjectBuilder toTop() {
        return Stream.iterate(currentObjectBuilder, Objects::nonNull, ObjectBuilder::getParent)
                .reduce((child, parent) -> parent)
                .orElse(null);
    }

    private String toStringValue(Object object, Class<?> clazz, String name, ObjectBuilder objectBuilder) {
        if (object.getClass().isEnum()) {
            return object.toString();
        }

        if (name != null) {
            OverrideTarget overrideTarget = OverrideTarget.of(name, clazz);
            OverridePopulate<?> nameOverride = populateConfig.getNameOverrides().get(overrideTarget);
            if (nameOverride != null && isCreateCodeOverridden(nameOverride)) {
                return applyOverride(nameOverride, objectBuilder);
            }
        }

        OverridePopulate<?> classOverride = populateConfig.getClassOverrides().get(clazz);
        if (classOverride != null && isCreateCodeOverridden(classOverride)) {
            return applyOverride(classOverride, objectBuilder);
        }

        String formattedValue = ValueFormatter.format(object, clazz);
        if (formattedValue != null) {
            return formattedValue;
        }

        if (name != null) {
            OverrideTarget overrideTarget = OverrideTarget.of(name, clazz);
            if (populateConfig.getNameOverrides().containsKey(overrideTarget)) {
                return populateConfig.getNameOverrides().get(overrideTarget).createCode();
            }
        }

        if (classOverride != null) {
            return classOverride.createCode();
        }

        throw new ObjectException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
    }

    private String applyOverride(OverridePopulate<?> overridePopulate, ObjectBuilder objectBuilder) {
        objectBuilder.addMethods(overridePopulate.createMethods());
        objectBuilder.addImports(overridePopulate.createImports());
        objectBuilder.addStaticImports(overridePopulate.createStaticImports());
        return overridePopulate.createCode();
    }

    private boolean isCreateCodeOverridden(OverridePopulate<?> overridePopulate) {
        try {
            return !overridePopulate.getClass().getMethod("createCode").getDeclaringClass().equals(OverridePopulate.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private TemplateObjectBuilder.Builder templateBuilder(Class<?> clazz, BuildType buildType, int expectedChildren) {
        return TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(buildType)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(expectedChildren);
    }

    private ContainerObjectBuilder.Builder containerBuilder(Class<?> clazz, BuildType buildType, int expectedChildren) {
        return ContainerObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(buildType)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(expectedChildren);
    }

    private void setNextObjectBuilder(ObjectBuilder objectBuilder) {
        if (currentObjectBuilder != null) {
            currentObjectBuilder.addChild(objectBuilder);
            objectBuilder.setParent(currentObjectBuilder);
        }
        currentObjectBuilder = objectBuilder;
        if (currentObjectBuilder.hasAllChildren()) {
            setPreviousObjectBuilder();
        }
    }

    private void setPreviousObjectBuilder() {
        while (currentObjectBuilder.getParent() != null && currentObjectBuilder.hasAllChildren()) {
            currentObjectBuilder = currentObjectBuilder.getParent();
        }
    }

    private String getName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        String key = clazz.getSimpleName().toLowerCase();
        int classCounter = classNameCounters.computeIfAbsent(key, k -> 0);
        String name = String.format("%s_%d", Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1), classCounter);
        classNameCounters.put(key, ++classCounter);
        return name;
    }
}
