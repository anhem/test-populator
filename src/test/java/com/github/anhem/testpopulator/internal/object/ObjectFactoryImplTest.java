package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.ObjectException;
import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static com.github.anhem.testpopulator.config.PopulateConfig.DEFAULT_BUILDER_METHOD;
import static com.github.anhem.testpopulator.config.PopulateConfig.DEFAULT_BUILD_METHOD;
import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

class ObjectFactoryImplTest {

    private static final String PACKAGE = "com.github.anhem.testpopulator.internal.object";
    private ObjectFactoryImpl objectFactoryImpl;

    @BeforeEach
    void setUp() {
        objectFactoryImpl = new ObjectFactoryImpl(DEFAULT_POPULATE_CONFIG);
    }

    @Test
    void createObjectUsingConstructor() {
        objectFactoryImpl.constructor(MyClass.class, 2);
        objectFactoryImpl.value("myString", String.class, null);
        objectFactoryImpl.value(1, Integer.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEmpty();
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of("public static final MyClass myClass_0 = new MyClass(\"myString\", 1);"));
    }

    @Test
    void createObjectUsingSetter() {
        objectFactoryImpl.setter(MyClass.class, 2);
        objectFactoryImpl.method("setString", 1);
        objectFactoryImpl.value("myString", String.class, null);
        objectFactoryImpl.method("setInteger", 1);
        objectFactoryImpl.value(1, Integer.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEmpty();
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final MyClass myClass_0 = new MyClass();",
                "static {",
                "myClass_0.setString(\"myString\");",
                "myClass_0.setInteger(1);",
                "}"
        ));
    }

    @Test
    void createObjectUsingBuilder() {
        objectFactoryImpl.builder(MyClass.class, 2, DEFAULT_BUILDER_METHOD, DEFAULT_BUILD_METHOD);
        objectFactoryImpl.method("string", 1);
        objectFactoryImpl.value("myString", String.class, null);
        objectFactoryImpl.method("integer", 1);
        objectFactoryImpl.value(1, Integer.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEmpty();
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final MyClass myClass_0 = MyClass.builder()",
                ".string(\"myString\")",
                ".integer(1)",
                ".build();"
        ));
    }

    @Test
    void createSetOf() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.setOf();
        objectFactoryImpl.value("myString", String.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEqualTo(Set.of(
                "java.util.Set"
        ));
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final Set<String> set_0 = Set.of(\"myString\");",
                "public static final MyClass myClass_0 = new MyClass(set_0);"
        ));
    }

    @Test
    void createSet() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.set(HashSet.class);
        objectFactoryImpl.value("myString", String.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEqualTo(Set.of(
                "java.util.HashSet"
        ));
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final HashSet<String> hashSet_0 = new HashSet<>();",
                "static {",
                "hashSet_0.add(\"myString\");",
                "}",
                "public static final MyClass myClass_0 = new MyClass(hashSet_0);"

        ));
    }

    @Test
    void createMapOf() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.mapOf();
        objectFactoryImpl.value("myKey", String.class, null);
        objectFactoryImpl.value("myValue", String.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEqualTo(Set.of(
                "java.util.Map"
        ));
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final Map<String, String> map_0 = Map.of(\"myKey\", \"myValue\");",
                "public static final MyClass myClass_0 = new MyClass(map_0);"
        ));
    }

    @Test
    void createMap() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.map(HashMap.class);
        objectFactoryImpl.value("myKey", String.class, null);
        objectFactoryImpl.value("myValue", String.class, null);
        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEqualTo(Set.of(
                "java.util.HashMap"
        ));
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final HashMap<String, String> hashMap_0 = new HashMap<>();",
                "static {",
                "hashMap_0.put(\"myKey\", \"myValue\");",
                "}",
                "public static final MyClass myClass_0 = new MyClass(hashMap_0);"
        ));
    }

    @Test
    void createListOf() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.listOf();
        objectFactoryImpl.value("myString", String.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEqualTo(Set.of(
                "java.util.List"
        ));
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final List<String> list_0 = List.of(\"myString\");",
                "public static final MyClass myClass_0 = new MyClass(list_0);"
        ));
    }

    @Test
    void createList() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.list(ArrayList.class);
        objectFactoryImpl.value("myString", String.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEqualTo(Set.of(
                "java.util.ArrayList"
        ));
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final ArrayList<String> arrayList_0 = new ArrayList<>();",
                "static {",
                "arrayList_0.add(\"myString\");",
                "}",
                "public static final MyClass myClass_0 = new MyClass(arrayList_0);"
        ));
    }

    @Test
    void createArray() {
        objectFactoryImpl.constructor(MyClass.class, 1);
        objectFactoryImpl.array(Boolean.class);
        objectFactoryImpl.value(true, Boolean.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEmpty();
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(getExpectedMyClassStaticImport()));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final Boolean[] boolean_0 = new Boolean[]{true};",
                "public static final MyClass myClass_0 = new MyClass(boolean_0);"
        ));
    }

    @Test
    void value() {
        objectFactoryImpl.value("myString", String.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("String_TestData");
        assertThat(objectResult.getImports()).isEmpty();
        assertThat(objectResult.getStaticImports()).isEmpty();
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final String string_0 = \"myString\";"
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void valueWithNameOverride() {
        String overrideName = "myCustomName";
        List<String> overrideValue = List.of("myOverriddenValue");
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(overrideName, List.class, new OverridePopulate<>() {
                    @Override
                    public Object create() {
                        return overrideValue;
                    }

                    @Override
                    public String createCode() {
                        return "CUSTOM_STRING";
                    }
                }).build();
        objectFactoryImpl = new ObjectFactoryImpl(populateConfig);

        objectFactoryImpl.value(overrideValue, (Class<List<String>>) (Class<?>) List.class, overrideName);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final List list_0 = CUSTOM_STRING;"
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    void valueWithClassOverride() {
        Class<List<String>> overrideClass = (Class<List<String>>) (Class<?>) List.class;
        List<String> overrideValue = List.of("myOverriddenValue");
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(overrideClass, new OverridePopulate<>() {
                    @Override
                    public List<String> create() {
                        return overrideValue;
                    }

                    @Override
                    public String createCode() {
                        return "CUSTOM_STRING_FOR_CLASS";
                    }
                }).build();
        objectFactoryImpl = new ObjectFactoryImpl(populateConfig);

        objectFactoryImpl.value(overrideValue, overrideClass, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final List list_0 = CUSTOM_STRING_FOR_CLASS;"
        ));
    }

    @Test
    void valueWithClassOverrideAndMethods() {
        Class<MyClass> overrideClass = MyClass.class;
        MyClass overrideValue = new MyClass();
        String methodDefinition = "private static MyClass myHelper() { return new MyClass(); }";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(overrideClass, new OverridePopulate<>() {
                    @Override
                    public MyClass create() {
                        return overrideValue;
                    }

                    @Override
                    public String createCode() {
                        return "myHelper()";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of(methodDefinition);
                    }
                }).build();
        objectFactoryImpl = new ObjectFactoryImpl(populateConfig);

        objectFactoryImpl.value(overrideValue, overrideClass, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final MyClass myClass_0 = myHelper();"
        ));
        assertThat(objectResult.getMethods()).contains(methodDefinition);
    }

    @Test
    void valueWithNameOverrideAndMethods() {
        String overrideName = "myCustomName";
        MyClass overrideValue = new MyClass();
        String methodDefinition = "private static MyClass myHelper() { return new MyClass(); }";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(overrideName, MyClass.class, new OverridePopulate<>() {
                    @Override
                    public Object create() {
                        return overrideValue;
                    }

                    @Override
                    public String createCode() {
                        return "myHelper()";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of(methodDefinition);
                    }
                }).build();
        objectFactoryImpl = new ObjectFactoryImpl(populateConfig);

        objectFactoryImpl.value(overrideValue, MyClass.class, overrideName);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final MyClass myClass_0 = myHelper();"
        ));
        assertThat(objectResult.getMethods()).contains(methodDefinition);
    }

    @Test
    void valueWithClassOverrideAndMethodsAndImports() {
        Class<MyClass> overrideClass = MyClass.class;
        MyClass overrideValue = new MyClass();
        String methodDefinition = "private static MyClass myHelper() { return new MyClass(); }";
        String importDefinition = "java.util.Collections";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(overrideClass, new OverridePopulate<>() {
                    @Override
                    public MyClass create() {
                        return overrideValue;
                    }

                    @Override
                    public String createCode() {
                        return "myHelper()";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of(methodDefinition);
                    }

                    @Override
                    public Set<String> createImports() {
                        return Set.of(importDefinition);
                    }
                }).build();
        objectFactoryImpl = new ObjectFactoryImpl(populateConfig);

        objectFactoryImpl.value(overrideValue, overrideClass, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final MyClass myClass_0 = myHelper();"
        ));
        assertThat(objectResult.getMethods()).contains(methodDefinition);
        assertThat(objectResult.getImports()).contains(importDefinition);
    }

    @Test
    void valueWithNameOverrideAndMethodsAndImports() {
        String overrideName = "myCustomName";
        MyClass overrideValue = new MyClass();
        String methodDefinition = "private static MyClass myHelper() { return new MyClass(); }";
        String importDefinition = "java.util.Collections";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(overrideName, MyClass.class, new OverridePopulate<>() {
                    @Override
                    public Object create() {
                        return overrideValue;
                    }

                    @Override
                    public String createCode() {
                        return "myHelper()";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of(methodDefinition);
                    }

                    @Override
                    public Set<String> createImports() {
                        return Set.of(importDefinition);
                    }
                }).build();
        objectFactoryImpl = new ObjectFactoryImpl(populateConfig);

        objectFactoryImpl.value(overrideValue, MyClass.class, overrideName);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final MyClass myClass_0 = myHelper();"
        ));
        assertThat(objectResult.getMethods()).contains(methodDefinition);
        assertThat(objectResult.getImports()).contains(importDefinition);
    }

    @Test
    void valueWithClassOverrideAndMethodsAndImportsAndStaticImports() {
        Class<MyClass> overrideClass = MyClass.class;
        MyClass overrideValue = new MyClass();
        String methodDefinition = "private static MyClass myHelper() { return new MyClass(); }";
        String importDefinition = "java.util.Collections";
        String staticImportDefinition = "java.util.Collections.singletonList";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(overrideClass, new OverridePopulate<>() {
                    @Override
                    public MyClass create() {
                        return overrideValue;
                    }

                    @Override
                    public String createCode() {
                        return "myHelper()";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of(methodDefinition);
                    }

                    @Override
                    public Set<String> createImports() {
                        return Set.of(importDefinition);
                    }

                    @Override
                    public Set<String> createStaticImports() {
                        return Set.of(staticImportDefinition);
                    }
                }).build();
        objectFactoryImpl = new ObjectFactoryImpl(populateConfig);

        objectFactoryImpl.value(overrideValue, overrideClass, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final MyClass myClass_0 = myHelper();"
        ));
        assertThat(objectResult.getMethods()).contains(methodDefinition);
        assertThat(objectResult.getImports()).contains(importDefinition);
        assertThat(objectResult.getStaticImports()).contains(staticImportDefinition);
    }

    @Test
    void valueWithNameOverrideAndMethodsAndImportsAndStaticImports() {
        String overrideName = "myCustomName";
        MyClass overrideValue = new MyClass();
        String methodDefinition = "private static MyClass myHelper() { return new MyClass(); }";
        String importDefinition = "java.util.Collections";
        String staticImportDefinition = "java.util.Collections.singletonList";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(overrideName, MyClass.class, new OverridePopulate<>() {
                    @Override
                    public Object create() {
                        return overrideValue;
                    }

                    @Override
                    public String createCode() {
                        return "myHelper()";
                    }

                    @Override
                    public Set<String> createMethods() {
                        return Set.of(methodDefinition);
                    }

                    @Override
                    public Set<String> createImports() {
                        return Set.of(importDefinition);
                    }

                    @Override
                    public Set<String> createStaticImports() {
                        return Set.of(staticImportDefinition);
                    }
                }).build();
        objectFactoryImpl = new ObjectFactoryImpl(populateConfig);

        objectFactoryImpl.value(overrideValue, MyClass.class, overrideName);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final MyClass myClass_0 = myHelper();"
        ));
        assertThat(objectResult.getMethods()).contains(methodDefinition);
        assertThat(objectResult.getImports()).contains(importDefinition);
        assertThat(objectResult.getStaticImports()).contains(staticImportDefinition);
    }

    @Test
    void allValues() {
        objectFactoryImpl.constructor(MyClass.class, 13);
        objectFactoryImpl.value(ArbitraryEnum.A, ArbitraryEnum.class, null);
        objectFactoryImpl.value(1, Integer.class, null);
        objectFactoryImpl.value(2L, Long.class, null);
        objectFactoryImpl.value(3D, Double.class, null);
        objectFactoryImpl.value(true, Boolean.class, null);
        objectFactoryImpl.value(BigDecimal.ONE, BigDecimal.class, null);
        objectFactoryImpl.value("myString", String.class, null);
        objectFactoryImpl.value(LocalDate.EPOCH, LocalDate.class, null);
        objectFactoryImpl.value(LocalDate.EPOCH.atTime(0, 0, 0), LocalDateTime.class, null);
        objectFactoryImpl.value(LocalDate.EPOCH.atTime(0, 0, 0).atZone(ZoneId.of("UTC")), ZonedDateTime.class, null);
        objectFactoryImpl.value(LocalDate.EPOCH.atTime(0, 0, 0).atZone(ZoneId.of("UTC")).toInstant(), Instant.class, null);
        objectFactoryImpl.value('c', Character.class, null);
        objectFactoryImpl.value(UUID.fromString("82e8962f-885d-4845-914b-c206a42d7c91"), UUID.class, null);

        ObjectResult objectResult = objectFactoryImpl.build();
        assertThat(objectResult.getPackageName()).isEqualTo(PACKAGE);
        assertThat(objectResult.getClassName()).isEqualTo("MyClass_TestData");
        assertThat(objectResult.getImports()).isEqualTo(Set.of(
                "java.math.BigDecimal",
                "java.time.LocalDate",
                "java.time.LocalDateTime",
                "java.time.ZonedDateTime",
                "java.time.Instant",
                "java.util.UUID"
        ));
        assertThat(objectResult.getStaticImports()).isEqualTo(Set.of(
                getExpectedMyClassStaticImport(),
                "com.github.anhem.testpopulator.model.java.ArbitraryEnum.A"
        ));
        assertThat(objectResult.getObjects()).isEqualTo(List.of(
                "public static final MyClass myClass_0 = new MyClass(" +
                        "A, " +
                        "1, " +
                        "2L, " +
                        "3.0, " +
                        "true, " +
                        "BigDecimal.valueOf(1), " +
                        "\"myString\", " +
                        "LocalDate.parse(\"1970-01-01\"), " +
                        "LocalDateTime.parse(\"1970-01-01T00:00\"), " +
                        "ZonedDateTime.parse(\"1970-01-01T00:00Z[UTC]\"), " +
                        "Instant.parse(\"1970-01-01T00:00:00Z\"), " +
                        "'c', " +
                        "UUID.fromString(\"82e8962f-885d-4845-914b-c206a42d7c91\")" +
                        ");"));
    }

    @Test
    void valueThrowsException() {
        Assertions.assertThrows(ObjectException.class, () -> objectFactoryImpl.value(Pojo.class, Class.class, null));
    }

    private String getExpectedMyClassStaticImport() {
        return String.format("%s.%s.%s", PACKAGE, this.getClass().getSimpleName(), MyClass.class.getSimpleName());
    }

    public static class MyClass {
    }

}
