package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.config.OverrideTarget;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.ObjectException;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.BuildType.*;
import static com.github.anhem.testpopulator.internal.object.ObjectBuilder.NULL;
import static com.github.anhem.testpopulator.internal.util.FileWriterUtil.*;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.useFullyQualifiedName;

public class ObjectFactoryImpl implements ObjectFactory {

    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";

    private static final String NEW_PREFIX = "new ";
    private static final Map<Class<?>, Function<Object, String>> stringSuppliers = new HashMap<>();

    static {
        stringSuppliers.put(Integer.class, Object::toString);
        stringSuppliers.put(int.class, Object::toString);
        stringSuppliers.put(Long.class, object -> object + "L");
        stringSuppliers.put(long.class, object -> object + "L");
        stringSuppliers.put(Double.class, Object::toString);
        stringSuppliers.put(double.class, Object::toString);
        stringSuppliers.put(Boolean.class, Object::toString);
        stringSuppliers.put(boolean.class, Object::toString);
        stringSuppliers.put(BigDecimal.class, object -> String.format("BigDecimal.valueOf(%d)", ((BigDecimal) object).intValue()));
        stringSuppliers.put(String.class, object -> "\"" + object + "\"");
        stringSuppliers.put(Character.class, object -> "'" + object + "'");
        stringSuppliers.put(char.class, object -> "'" + object + "'");
        stringSuppliers.put(LocalDate.class, object -> String.format("LocalDate.parse(\"%s\")", object));
        stringSuppliers.put(LocalDateTime.class, object -> String.format("LocalDateTime.parse(\"%s\")", object));
        stringSuppliers.put(ZonedDateTime.class, object -> String.format("ZonedDateTime.parse(\"%s\")", object));
        stringSuppliers.put(Instant.class, object -> String.format("Instant.parse(\"%s\")", object));
        stringSuppliers.put(Date.class, object -> String.format("new Date(%sL)", ((Date) object).getTime()));
        stringSuppliers.put(UUID.class, object -> String.format("UUID.fromString(\"%s\")", object));
        stringSuppliers.put(Byte.class, object -> String.format("Byte.parseByte(\"%s\")", object));
        stringSuppliers.put(byte.class, object -> String.format("Byte.parseByte(\"%s\")", object));
        stringSuppliers.put(Short.class, object -> String.format("Short.parseShort(\"%s\")", object));
        stringSuppliers.put(short.class, object -> String.format("Short.parseShort(\"%s\")", object));
        stringSuppliers.put(Float.class, object -> object + "f");
        stringSuppliers.put(float.class, object -> object + "f");
        stringSuppliers.put(LocalTime.class, object -> String.format("LocalTime.parse(\"%s\")", object));
        stringSuppliers.put(BigInteger.class, object -> String.format("BigInteger.valueOf(%d)", ((BigInteger) object).intValue()));
        stringSuppliers.put(OffsetDateTime.class, object -> String.format("OffsetDateTime.parse(\"%s\")", object));
        stringSuppliers.put(OffsetTime.class, object -> String.format("OffsetTime.parse(\"%s\")", object));
        stringSuppliers.put(Duration.class, object -> String.format("Duration.ofSeconds(%d)", ((Duration) object).getSeconds()));
        stringSuppliers.put(Period.class, object -> String.format("Period.ofDays(%d)", ((Period) object).getDays()));
        stringSuppliers.put(java.sql.Date.class, object -> String.format("Date.valueOf(\"%s\")", object.toString()));
        stringSuppliers.put(Time.class, object -> String.format("Time.valueOf(\"%s\")", object.toString()));
        stringSuppliers.put(Timestamp.class, object -> String.format("Timestamp.valueOf(\"%s\")", object.toString()));
        stringSuppliers.put(Currency.class, object -> String.format("Currency.getInstance(\"%s\")", object));
        stringSuppliers.put(Locale.class, object -> String.format("Locale.forLanguageTag(\"%s\")", ((Locale) object).toLanguageTag()));
        stringSuppliers.put(TimeZone.class, object -> String.format("TimeZone.getTimeZone(\"%s\")", ((TimeZone) object).getID()));
        stringSuppliers.put(ZoneId.class, object -> String.format("ZoneId.of(\"%s\")", object));
        stringSuppliers.put(ZoneOffset.class, object -> String.format("ZoneOffset.of(\"%s\")", object));
        stringSuppliers.put(Year.class, object -> String.format("Year.of(%d)", ((Year) object).getValue()));
        stringSuppliers.put(YearMonth.class, object -> String.format("YearMonth.of(%d, %d)", ((YearMonth) object).getYear(), ((YearMonth) object).getMonthValue()));
        stringSuppliers.put(MonthDay.class, object -> String.format("MonthDay.of(%d, %d)", ((MonthDay) object).getMonthValue(), ((MonthDay) object).getDayOfMonth()));
        stringSuppliers.put(File.class, object -> String.format("new File(\"%s\")", ((File) object).getPath()));
        stringSuppliers.put(Path.class, object -> String.format("Path.of(\"%s\")", object.toString()));
        stringSuppliers.put(URL.class, object -> String.format("toUrl(\"%s\")", object));
        stringSuppliers.put(URI.class, object -> String.format("URI.create(\"%s\")", object));
    }

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
        setNextObjectBuilder(clazz, CONSTRUCTOR, expectedChildren);
    }

    @Override
    public <T> void setter(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, SETTER, expectedChildren);
    }

    @Override
    public <T> void mutator(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(clazz, MUTATOR, expectedChildren);
    }

    @Override
    public <T> void builder(Class<T> clazz, int expectedChildren, String builderMethodName, String buildMethodName) {
        boolean useFullyQualifiedName = useFullyQualifiedName(clazz, classNames);
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new BuilderObjectBuilder(clazz, getName(clazz), useFullyQualifiedName, expectedChildren, builderMethodName, buildMethodName);
        } else {
            setNextObjectBuilder(new BuilderObjectBuilder(clazz, getName(clazz), useFullyQualifiedName, expectedChildren, builderMethodName, buildMethodName));
        }
    }

    @Override
    public void method(String methodName, int expectedChildren) {
        setNextObjectBuilder(new MethodObjectBuilder(methodName, expectedChildren));
        if (expectedChildren == 0) {
            setPreviousObjectBuilder();
        }
    }

    @Override
    public <T> void staticMethod(Class<T> clazz, String methodName, int expectedChildren) {
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new StaticMethodObjectBuilder(clazz, getName(clazz), methodName, expectedChildren);
        } else {
            setNextObjectBuilder(new StaticMethodObjectBuilder(clazz, getName(clazz), methodName, expectedChildren));
        }
    }

    @Override
    public <T> void set(Class<T> clazz) {
        setNextObjectBuilder(clazz, SET, 1);
        method("add", 1);
    }

    @Override
    public void setOf() {
        setNextObjectBuilder(Set.class, SET_OF, 1);
    }

    @Override
    public <T> void list(Class<T> clazz) {
        setNextObjectBuilder(clazz, LIST, 1);
        method("add", 1);
    }

    @Override
    public void listOf() {
        setNextObjectBuilder(List.class, LIST_OF, 1);
    }

    @Override
    public <T> void map(Class<T> clazz) {
        setNextObjectBuilder(clazz, MAP, 1);
        method("put", 2);
    }

    @Override
    public void mapOf() {
        setNextObjectBuilder(Map.class, MAP_OF, 2);
    }

    @Override
    public <T> void mapEntry(Class<T> clazz) {
        setNextObjectBuilder(clazz, MAP_ENTRY, 2);
    }

    @Override
    public <T> void array(Class<T> clazz) {
        setNextObjectBuilder(clazz, ARRAY, 1);
    }

    @Override
    public <T> void value(T value, Class<T> clazz, String name) {
        setNextObjectBuilder(clazz, VALUE, 0);
        String stringValue = toStringValue(value, clazz, name);
        if (currentObjectBuilder.isUseFullyQualifiedName()) {
            if (stringValue.startsWith(NEW_PREFIX)) {
                currentObjectBuilder.setValue(String.format("%s%s.%s", NEW_PREFIX, currentObjectBuilder.getClazz().getPackageName(), stringValue.replace(NEW_PREFIX, "")));
            } else {
                currentObjectBuilder.setValue(String.format("%s.%s", currentObjectBuilder.getClazz().getPackageName(), stringValue));
            }
        } else {
            currentObjectBuilder.setValue(stringValue);
        }
        setPreviousObjectBuilder();
    }

    @Override
    public <T> void nullValue(Class<T> clazz) {
        setNextObjectBuilder(clazz, VALUE, 0);
        currentObjectBuilder.setValue(NULL);
        setPreviousObjectBuilder();
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

    private String toStringValue(Object object, Class<?> clazz, String name) {
        if (object.getClass().isEnum()) {
            return object.toString();
        }

        Function<Object, String> stringSupplier = stringSuppliers.get(clazz);
        if (stringSupplier == null) {
            stringSupplier = stringSuppliers.get(object.getClass());
        }

        if (stringSupplier != null) {
            return stringSupplier.apply(object);
        }

        if (name != null) {
            OverrideTarget overrideTarget = OverrideTarget.of(name, clazz);
            if (populateConfig.getNameOverrides().containsKey(overrideTarget)) {
                return populateConfig.getNameOverrides().get(overrideTarget).createString();
            }
        }

        return populateConfig.getClassOverrides().getOrDefault(clazz, () -> {
            throw new ObjectException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
        }).createString();
    }

    private void setNextObjectBuilder(Class<?> clazz, BuildType buildType, int expectedChildren) {
        boolean useFullyQualifiedName = useFullyQualifiedName(clazz, classNames);
        if (currentObjectBuilder == null) {
            currentObjectBuilder = new BuildTypeObjectBuilder(clazz, getName(clazz), buildType, useFullyQualifiedName, expectedChildren);
        } else if (buildType == MUTATOR) {
            setNextObjectBuilder(new BuildTypeObjectBuilder(clazz, currentObjectBuilder.getName(), buildType, useFullyQualifiedName, expectedChildren));
        } else {
            setNextObjectBuilder(new BuildTypeObjectBuilder(clazz, getName(clazz), buildType, useFullyQualifiedName, expectedChildren));
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
        int classCounter = classNameCounters.computeIfAbsent(clazz.getSimpleName(), k -> 0);
        String simpleName = clazz.getSimpleName();
        String name = String.format("%s_%d", Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1), classCounter);
        classNameCounters.put(clazz.getSimpleName(), ++classCounter);
        return name;
    }
}
