package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.BuildType.*;
import static com.github.anhem.testpopulator.ObjectBuilder.PSF;
import static com.github.anhem.testpopulator.ObjectBuilderUtil.STATIC_BLOCK_END;
import static com.github.anhem.testpopulator.ObjectBuilderUtil.STATIC_BLOCK_START;

public class ObjectFactoryImpl implements ObjectFactory {

    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";
    private final Map<Class<?>, Integer> classCounters;
    private ObjectBuilder currentObjectBuilder;

    public ObjectFactoryImpl() {
        this.classCounters = new HashMap<>();
    }

    @Override
    public void constructor(Class<?> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, CONSTRUCTOR, expectedChildren);
    }

    @Override
    public void setter(Class<?> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, SETTER, expectedChildren);
    }

    @Override
    public void builder(Class<?> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, BUILDER, expectedChildren);
    }

    @Override
    public void method(String methodName, int expectedChildren) {
        setNextObjectBuilder(new ObjectBuilder(methodName, METHOD, expectedChildren));
        if (expectedChildren == 0) {
            setPreviousObjectBuilder();
        }
    }

    @Override
    public void set(Class<?> clazz) {
        setNextObjectBuilder(clazz, SET, 1);
        method("add", 1);
    }

    @Override
    public void setOf() {
        setNextObjectBuilder(Set.class, SET_OF, 1);
    }

    @Override
    public void list(Class<?> clazz) {
        setNextObjectBuilder(clazz, LIST, 1);
        method("add", 1);
    }

    @Override
    public void listOf() {
        setNextObjectBuilder(List.class, LIST_OF, 1);
    }

    @Override
    public void map(Class<?> clazz) {
        setNextObjectBuilder(clazz, MAP, 1);
        method("put", 2);
    }

    @Override
    public void mapOf() {
        setNextObjectBuilder(Map.class, MAP_OF, 2);
    }

    @Override
    public void array(Class<?> clazz) {
        setNextObjectBuilder(clazz, ARRAY, 1);
    }

    @Override
    public <T> void overridePopulate(Class<?> clazz, OverridePopulate<T> overridePopulateValue) {
        setNextObjectBuilder(clazz, OVERRIDE_VALUE, 0);
        currentObjectBuilder.setValue(overridePopulateValue.createString());
        setPreviousObjectBuilder();
    }

    @Override
    public <T> void value(T value) {
        setNextObjectBuilder(value.getClass(), VALUE, 0);
        currentObjectBuilder.setValue(toStringValue(value));
        setPreviousObjectBuilder();
    }

    @Override
    public ObjectResult build() {
        return toTop().build();
    }

    @Override
    public void writeToFile() {
        ObjectResult objectResult = build();
        if (objectResult.isValid()) {
            Path path = Paths.get(String.format("target/generated-test-sources/test-populator/%s/%s.java", objectResult.getPackageName(), objectResult.getClassName()));
            createOrOverwriteFile(path);
            writePackage(objectResult, path);
            writeImports(path, objectResult.getImports());
            writeStaticImports(path, objectResult.getStaticImports());
            writeStartClass(objectResult, path);
            writeObjects(path, objectResult.getObjects());
            writeEndClass(path);
        }
    }

    private static void createOrOverwriteFile(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new ObjectException(String.format("Could not create or overwrite %s", path.toAbsolutePath()), e);
        }
    }

    private void writePackage(ObjectResult objectResult, Path path) {
        writeLine(path, String.format("package %s;%s", objectResult.getPackageName(), System.lineSeparator()));
    }

    private void writeStartClass(ObjectResult objectResult, Path path) {
        writeLine(path, String.format("public class %s {%s", objectResult.getClassName(), System.lineSeparator()));
    }

    private void writeEndClass(Path path) {
        writeLine(path, "}");
    }

    private void writeObjects(Path path, List<String> objects) {
        objects.forEach(s -> {
            if (s.startsWith(STATIC_BLOCK_START)) {
                writeLine(path, String.format("%s\t%s", System.lineSeparator(), s));
            } else if (s.startsWith(STATIC_BLOCK_END)) {
                writeLine(path, String.format("\t%s%s", s, System.lineSeparator()));
            } else if (s.startsWith(PSF)) {
                writeLine(path, String.format("\t%s", s));
            } else {
                writeLine(path, String.format("\t\t%s", s));
            }
        });
    }

    private void writeImports(Path path, Set<String> imports) {
        imports.stream()
                .sorted()
                .forEach(s -> writeLine(path, String.format("import %s;", s)));
        writeLine(path, "");
    }

    private void writeStaticImports(Path path, Set<String> staticImports) {
        staticImports.stream()
                .sorted()
                .forEach(s -> writeLine(path, String.format("import static %s;", s)));
        writeLine(path, "");
    }

    private void writeLine(Path path, String line) {
        String formattedLine = String.format("%s%s", line, System.lineSeparator());
        try {
            Files.writeString(path, formattedLine, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new ObjectException(String.format("Write %s to %s failed", formattedLine, path.toAbsolutePath()), e);
        }
    }

    private ObjectBuilder toTop() {
        return Stream.iterate(currentObjectBuilder, Objects::nonNull, ObjectBuilder::getParent)
                .reduce((child, parent) -> parent)
                .orElse(null);
    }

    private String toStringValue(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isEnum()) {
            return object.toString();
        }
        if (clazz.equals(Integer.class)) {
            return object.toString();
        }
        if (clazz.equals(Long.class)) {
            return String.format("%sL", object);
        }
        if (clazz.equals(Double.class)) {
            return object.toString();
        }
        if (clazz.equals(Boolean.class)) {
            return object.toString();
        }
        if (clazz.equals(BigDecimal.class)) {
            return String.format("BigDecimal.valueOf(%d)", ((BigDecimal) object).intValue());
        }
        if (clazz.equals(String.class)) {
            return String.format("\"%s\"", object);
        }
        if (clazz.equals(LocalDate.class)) {
            return String.format("LocalDate.parse(\"%s\")", object);
        }
        if (clazz.equals(LocalDateTime.class)) {
            return String.format("LocalDateTime.parse(\"%s\")", object);
        }
        if (clazz.equals(ZonedDateTime.class)) {
            return String.format("ZonedDateTime.parse(\"%s\")", object);
        }
        if (clazz.equals(Instant.class)) {
            return String.format("Instant.parse(\"%s\")", object);
        }
        if (clazz.equals(Character.class)) {
            return String.format("'%s'", object);
        }
        if (clazz.equals(UUID.class)) {
            return String.format("UUID.fromString(\"%s\")", object);
        }

        throw new ObjectException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
    }

    private void setNextObjectBuilder(Class<?> clazz, BuildType buildType, int expectedChildren) {
        String name = getName(clazz);
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new ObjectBuilder(clazz, name, buildType, expectedChildren);
        } else {
            ObjectBuilder child = new ObjectBuilder(clazz, name, buildType, expectedChildren);
            setNextObjectBuilder(child);
        }
    }

    private void setNextObjectBuilder(ObjectBuilder objectBuilder) {
        currentObjectBuilder.addChild(objectBuilder);
        objectBuilder.setParent(currentObjectBuilder);
        currentObjectBuilder = objectBuilder;
    }

    private void setPreviousObjectBuilder() {
        while (currentObjectBuilder.getParent() != null && currentObjectBuilder.hasAllChildren()) {
            currentObjectBuilder = currentObjectBuilder.getParent();
        }
    }

    private String getName(Class<?> clazz) {
        int classCounter = classCounters.computeIfAbsent(clazz, (k) -> 0);
        String string = clazz.getSimpleName();
        String name = String.format("%s%d", Character.toLowerCase(string.charAt(0)) + string.substring(1), classCounter);
        classCounters.put(clazz, ++classCounter);
        return name;
    }

}
