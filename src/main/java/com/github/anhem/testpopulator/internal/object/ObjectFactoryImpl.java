package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.OverrideTarget;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.ObjectException;
import com.github.anhem.testpopulator.internal.object.builder.CodeTemplate;
import com.github.anhem.testpopulator.internal.object.builder.ContainerObjectBuilder;
import com.github.anhem.testpopulator.internal.object.builder.MethodBuilder;
import com.github.anhem.testpopulator.internal.object.builder.TemplateObjectBuilder;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.*;

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
        stringSuppliers.put(Month.class, object -> String.format("Month.%s", object));
        stringSuppliers.put(DayOfWeek.class, object -> String.format("DayOfWeek.%s", object));
        stringSuppliers.put(File.class, object -> String.format("new File(\"%s\")", ((File) object).getPath()));
        stringSuppliers.put(Path.class, object -> String.format("Path.of(\"%s\")", object.toString()));
        stringSuppliers.put(URL.class, object -> String.format("toUrl(\"%s\")", object));
        stringSuppliers.put(URI.class, object -> String.format("URI.create(\"%s\")", object));
        stringSuppliers.put(Charset.class, object -> String.format("Charset.forName(\"%s\")", ((Charset) object).name()));
        stringSuppliers.put(Calendar.class, object -> String.format("new Calendar.Builder().setInstant(%sL).build()", ((Calendar) object).getTimeInMillis()));
        stringSuppliers.put(BitSet.class, object -> String.format("BitSet.valueOf(new long[]{%sL})", ((BitSet) object).toLongArray()[0]));
        stringSuppliers.put(Throwable.class, object -> String.format("new Throwable(\"%s\")", ((Throwable) object).getMessage()));
        stringSuppliers.put(Exception.class, object -> String.format("new Exception(\"%s\")", ((Exception) object).getMessage()));
        stringSuppliers.put(RuntimeException.class, object -> String.format("new RuntimeException(\"%s\")", ((RuntimeException) object).getMessage()));
        stringSuppliers.put(Error.class, object -> String.format("new Error(\"%s\")", ((Error) object).getMessage()));
        stringSuppliers.put(ByteBuffer.class, object -> String.format("ByteBuffer.wrap(new byte[]{%s})", formatBytes(((ByteBuffer) object).array())));
        stringSuppliers.put(InetAddress.class, object -> String.format("toInetAddress(\"%s\")", ((InetAddress) object).getHostAddress()));
        stringSuppliers.put(Inet4Address.class, object -> String.format("(Inet4Address) toInetAddress(\"%s\")", ((Inet4Address) object).getHostAddress()));
        stringSuppliers.put(Inet6Address.class, object -> String.format("(Inet6Address) toInetAddress(\"%s\")", ((Inet6Address) object).getHostAddress()));
        stringSuppliers.put(InetSocketAddress.class, object -> String.format("new InetSocketAddress(toInetAddress(\"%s\"), %d)", ((InetSocketAddress) object).getAddress().getHostAddress(), ((InetSocketAddress) object).getPort()));
        stringSuppliers.put(CharSequence.class, object -> String.format("\"%s\"", object));
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
        TemplateObjectBuilder constructorBuilder = TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(CONSTRUCTOR)
                .codeTemplate(CodeTemplate.CONSTRUCTOR)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(expectedChildren)
                .build();
        setNextObjectBuilder(constructorBuilder);
    }

    @Override
    public <T> void setter(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(ContainerObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(SETTER)
                .template(CodeTemplate.SETTER.getFormat())
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(expectedChildren)
                .build());
    }

    @Override
    public <T> void mutator(Class<T> clazz, int expectedChildren) {
        setNextObjectBuilder(ContainerObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(MUTATOR)
                .expectedChildren(expectedChildren)
                .build());
    }

    @Override
    public <T> void builder(Class<T> clazz, int expectedChildren, String builderMethodName, String buildMethodName) {
        TemplateObjectBuilder builderObjectBuilder = TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(BUILDER)
                .codeTemplate(CodeTemplate.BUILDER)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(expectedChildren)
                .methodName(builderMethodName)
                .buildMethodName(buildMethodName)
                .build();
        setNextObjectBuilder(builderObjectBuilder);
    }

    @Override
    public void method(String methodName, int expectedChildren) {
        setNextObjectBuilder(new MethodBuilder(methodName, expectedChildren));
        if (expectedChildren == 0) {
            setPreviousObjectBuilder();
        }
    }

    @Override
    public <T> void staticMethod(Class<T> clazz, String methodName, int expectedChildren) {
        TemplateObjectBuilder staticMethodBuilder = TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(STATIC_METHOD)
                .codeTemplate(CodeTemplate.STATIC_METHOD)
                .expectedChildren(expectedChildren)
                .factoryClassName(clazz.getSimpleName())
                .methodName(methodName)
                .build();
        setNextObjectBuilder(staticMethodBuilder);
    }

    @Override
    public <T> void set(Class<T> clazz) {
        setNextObjectBuilder(ContainerObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(SET)
                .template(CodeTemplate.TYPED_COLLECTION.getFormat())
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .parameterized(true)
                .build());
        method("add", 1);
    }

    @Override
    public void setOf() {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(Set.class)
                .name(getName(Set.class))
                .buildType(SET)
                .codeTemplate(CodeTemplate.IMMUTABLE)
                .useFullyQualifiedName(useFullyQualifiedName(Set.class, classNames))
                .expectedChildren(1)
                .parameterized(true)
                .factoryClassName(Set.class.getSimpleName())
                .methodName("of")
                .clearArgsIfNullChild(true)
                .build());
    }

    @Override
    public <T> void enumSet(Class<T> clazz, Class<?> enumClazz) {
        setNextObjectBuilder(ContainerObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(ENUM_SET)
                .template(CodeTemplate.ENUM_SET.getFormat())
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .parameterized(true)
                .referencedClassName(enumClazz.getSimpleName())
                .referencedClasses(enumClazz)
                .build());
        method("add", 1);
    }

    @Override
    public <T> void list(Class<T> clazz) {
        setNextObjectBuilder(ContainerObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(LIST)
                .template(CodeTemplate.TYPED_COLLECTION.getFormat())
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .parameterized(true)
                .build());
        method("add", 1);
    }

    @Override
    public void listOf() {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(List.class)
                .name(getName(List.class))
                .buildType(LIST)
                .codeTemplate(CodeTemplate.IMMUTABLE)
                .useFullyQualifiedName(useFullyQualifiedName(List.class, classNames))
                .expectedChildren(1)
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
        setNextObjectBuilder(ContainerObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(MAP)
                .template(codeTemplate.getFormat())
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .parameterized(parameterized)
                .build());
        method("put", 2);
    }

    @Override
    public void mapOf() {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(Map.class)
                .name(getName(Map.class))
                .buildType(MAP)
                .codeTemplate(CodeTemplate.IMMUTABLE)
                .useFullyQualifiedName(useFullyQualifiedName(Map.class, classNames))
                .expectedChildren(2)
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
        setNextObjectBuilder(ContainerObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(ENUM_MAP)
                .template(codeTemplate.getFormat())
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .parameterized(parameterized)
                .referencedClassName(enumClazz.getSimpleName())
                .referencedClasses(enumClazz)
                .build());
        method("put", 2);
    }

    @Override
    public <T> void mapEntry(Class<T> clazz) {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(STATIC_METHOD)
                .codeTemplate(CodeTemplate.MAP_ENTRY)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(2)
                .parameterized(true)
                .factoryClassName(AbstractMap.class.getSimpleName())
                .referencedClasses(AbstractMap.class)
                .build());
    }

    @Override
    public void optional() {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(Optional.class)
                .name(getName(Optional.class))
                .buildType(STATIC_METHOD)
                .codeTemplate(CodeTemplate.OPTIONAL)
                .useFullyQualifiedName(useFullyQualifiedName(Optional.class, classNames))
                .expectedChildren(1)
                .parameterized(true)
                .factoryClassName(Optional.class.getSimpleName())
                .methodName("ofNullable")
                .referencedClasses(Optional.class)
                .build());
    }

    @Override
    public <T> void array(Class<T> clazz) {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(ARRAY)
                .codeTemplate(CodeTemplate.ARRAY)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .build());
    }

    @Override
    public <T> void stream(Class<T> clazz) {
        TemplateObjectBuilder.Builder streamBuilder = TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(STATIC_METHOD)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
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
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(STATIC_METHOD)
                .codeTemplate(CodeTemplate.ITERATOR)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .parameterized(true)
                .factoryClassName(Stream.class.getSimpleName())
                .methodName("of")
                .referencedClasses(Stream.class, Objects.class)
                .build());
    }

    @Override
    public <T> void iterable(Class<T> clazz) {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(STATIC_METHOD)
                .codeTemplate(CodeTemplate.ITERABLE)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .parameterized(true)
                .factoryClassName(Stream.class.getSimpleName())
                .methodName("of")
                .referencedClasses(Stream.class, Objects.class, Collectors.class)
                .build());
    }

    @Override
    public <T> void scanner(Class<T> clazz) {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(STATIC_METHOD)
                .codeTemplate(CodeTemplate.SCANNER)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .factoryClassName(Scanner.class.getSimpleName())
                .referencedClasses(Scanner.class)
                .build());
    }

    @Override
    public <T> void future(Class<T> clazz) {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(STATIC_METHOD)
                .codeTemplate(CodeTemplate.FUTURE)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(1)
                .parameterized(true)
                .factoryClassName(CompletableFuture.class.getSimpleName())
                .methodName("completedFuture")
                .referencedClasses(CompletableFuture.class)
                .build());
    }

    @Override
    public <T> void value(T value, Class<T> clazz, String name) {
        boolean useFullyQualifiedName = useFullyQualifiedName(clazz, classNames);
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(VALUE)
                .codeTemplate(CodeTemplate.VALUE)
                .useFullyQualifiedName(useFullyQualifiedName)
                .expectedChildren(0)
                .build());
        String stringValue = toStringValue(value, clazz, name);
        if (useFullyQualifiedName) {
            if (stringValue.startsWith(NEW_PREFIX)) {
                stringValue = String.format("%s%s.%s", NEW_PREFIX, clazz.getPackageName(), stringValue.replace(NEW_PREFIX, ""));
            } else {
                stringValue = String.format("%s.%s", clazz.getPackageName(), stringValue);
            }
        }
        currentObjectBuilder.setValue(stringValue);
        setPreviousObjectBuilder();
    }

    @Override
    public <T> void nullValue(Class<T> clazz) {
        setNextObjectBuilder(TemplateObjectBuilder.builder()
                .clazz(clazz)
                .name(getName(clazz))
                .buildType(VALUE)
                .codeTemplate(CodeTemplate.VALUE)
                .useFullyQualifiedName(useFullyQualifiedName(clazz, classNames))
                .expectedChildren(0)
                .build());
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

        if (name != null) {
            OverrideTarget overrideTarget = OverrideTarget.of(name, clazz);
            OverridePopulate<?> nameOverride = populateConfig.getNameOverrides().get(overrideTarget);
            if (nameOverride != null && isCreateCodeOverridden(nameOverride)) {
                return applyOverride(nameOverride);
            }
        }

        OverridePopulate<?> classOverride = populateConfig.getClassOverrides().get(clazz);
        if (classOverride != null && isCreateCodeOverridden(classOverride)) {
            return applyOverride(classOverride);
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
                return populateConfig.getNameOverrides().get(overrideTarget).createCode();
            }
        }

        if (classOverride != null) {
            return classOverride.createCode();
        }

        throw new ObjectException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
    }

    private String applyOverride(OverridePopulate<?> overridePopulate) {
        currentObjectBuilder.addMethods(overridePopulate.createMethods());
        currentObjectBuilder.addImports(overridePopulate.createImports());
        currentObjectBuilder.addStaticImports(overridePopulate.createStaticImports());
        return overridePopulate.createCode();
    }

    private boolean isCreateCodeOverridden(OverridePopulate<?> overridePopulate) {
        try {
            return !overridePopulate.getClass().getMethod("createCode").getDeclaringClass().equals(OverridePopulate.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private void setNextObjectBuilder(ObjectBuilder objectBuilder) {
        if (currentObjectBuilder == null) {
            currentObjectBuilder = objectBuilder;
        } else {
            currentObjectBuilder.addChild(objectBuilder);
            objectBuilder.setParent(currentObjectBuilder);
            currentObjectBuilder = objectBuilder;
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

    private static String formatBytes(byte[] bytes) {
        StringJoiner joiner = new StringJoiner(", ");
        for (byte b : bytes) {
            joiner.add(String.format("(byte) %d", b));
        }
        return joiner.toString();
    }
}
