package com.github.anhem.testpopulator.internal.object.util;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.ContainerObjectBuilder;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;
import com.github.anhem.testpopulator.internal.object.TemplateObjectBuilder;
import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.constructor.NestedCollections;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.net.*;
import java.util.*;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.BuildType.*;
import static com.github.anhem.testpopulator.internal.object.ObjectBuilder.NULL;
import static com.github.anhem.testpopulator.internal.object.util.ObjectBuilderUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class ObjectBuilderUtilTest {

    @Test
    void getPackageNameReturnsPackageName() {
        assertThat(getPackageName(Pojo.class)).isEqualTo("com.github.anhem.testpopulator.model.java.setter");
    }

    @Test
    void getPackageNameReturnsPackageNameOfObjectBuilderWhenClassNameStartsWithJava() {
        assertThat(getPackageName(String.class)).isEqualTo("com.github.anhem.testpopulator.internal.object");
    }

    @Test
    void formatClassNameReturnsClassNameWithSuffix() {
        assertThat(formatClassName(Pojo.class)).isEqualTo("Pojo_TestData");
    }

    @Test
    void addImportAddsClassToImports() {
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        addImport(Pojo.class, null, false, imports, staticImports);

        assertThat(imports).isEqualTo(Set.of("com.github.anhem.testpopulator.model.java.setter.Pojo"));
        assertThat(staticImports).isEqualTo(Set.of());
    }

    @Test
    void addImportAddsInnerClassToStaticImports() {
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        addImport(NestedCollections.SimpleClass.class, null, false, imports, staticImports);

        assertThat(imports).isEqualTo(Set.of());
        assertThat(staticImports).isEqualTo(Set.of("com.github.anhem.testpopulator.model.java.constructor.NestedCollections.SimpleClass"));
    }

    @Test
    void addImportAddsEnumToStaticImports() {
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        addImport(ArbitraryEnum.class, ArbitraryEnum.A, false, imports, staticImports);

        assertThat(imports).isEqualTo(Set.of());
        assertThat(staticImports).isEqualTo(Set.of("com.github.anhem.testpopulator.model.java.ArbitraryEnum.A"));
    }

    @Test
    void isBasicValueReturnsFalse() {
        assertThat(isBasicValue(TemplateObjectBuilder.builder()
                .clazz(Pojo.class)
                .name("pojo_0")
                .buildType(BuildType.CONSTRUCTOR)
                .expectedChildren(0)
                .skipIfNull(true)
                .build())).isFalse();
    }

    @Test
    void isBasicValueReturnsTrueWhenBuildTypeIsValueAndClassIsJavaBaseClass() {
        ObjectBuilder string = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(VALUE)
                .expectedChildren(0)
                .skipIfNull(true)
                .build();
        string.setValue("\"string_0\"");
        assertThat(isBasicValue(string)).isTrue();
    }

    @Test
    void isBasicValueReturnsTrueWhenBuildTypeIsValueAndClassIsEnum() {
        ObjectBuilder arbitraryEnum = TemplateObjectBuilder.builder()
                .clazz(ArbitraryEnum.class)
                .name("arbitraryEnum_0")
                .buildType(VALUE)
                .expectedChildren(0)
                .skipIfNull(true)
                .build();
        arbitraryEnum.setValue("A");
        assertThat(isBasicValue(arbitraryEnum)).isTrue();
    }

    @Test
    void endBuilderReturnsStreamOfString() {
        assertThat(endBuilder("build")).hasSize(1).contains("\t.build();");
    }

    @Test
    void startStaticBlockReturnsStreamOfString() {
        assertThat(startStaticBlock()).hasSize(1).contains(STATIC_BLOCK_START);
    }

    @Test
    void endStaticBlockBlockReturnsStreamOfString() {
        assertThat(endStaticBlock()).hasSize(1).contains(STATIC_BLOCK_END);
    }

    @Test
    void concatenateMergesMultipleStreams() {
        assertThat(concatenate(
                Stream.of("a"),
                Stream.of("b"),
                Stream.of("c"),
                Stream.of("d"))
        ).hasSize(4).contains("a", "b", "c", "d");
    }

    @Test
    void collectionHasNullValuesReturnsTrueWhenObjectBuilderIsListAndWithoutChildren() {
        ObjectBuilder objectBuilder = ContainerObjectBuilder.builder()
                .clazz(ArrayList.class)
                .name("arrayList_0")
                .buildType(LIST)
                .expectedChildren(1)
                .parameterized(true)
                .build();
        ObjectBuilder addMethod = TemplateObjectBuilder.builder()
                .name("add")
                .buildType(METHOD)
                .expectedChildren(1)
                .build();
        ObjectBuilder string = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(VALUE)
                .expectedChildren(0)
                .skipIfNull(true)
                .build();
        string.setValue(NULL);
        addMethod.addChild(string);
        objectBuilder.addChild(addMethod);

        assertThat(collectionHasNullValues(objectBuilder)).isTrue();
    }

    @Test
    void collectionHasNullValuesReturnsFalseWhenObjectBuilderIsListAndWithChildren() {
        ObjectBuilder objectBuilder = ContainerObjectBuilder.builder()
                .clazz(ArrayList.class)
                .name("arrayList_0")
                .buildType(LIST)
                .expectedChildren(1)
                .parameterized(true)
                .build();
        ObjectBuilder addMethod = TemplateObjectBuilder.builder()
                .name("add")
                .buildType(METHOD)
                .expectedChildren(1)
                .build();
        ObjectBuilder string = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(VALUE)
                .expectedChildren(0)
                .skipIfNull(true)
                .build();
        string.setValue("abc123");
        addMethod.addChild(string);
        objectBuilder.addChild(addMethod);

        assertThat(collectionHasNullValues(objectBuilder)).isFalse();
    }

    @Test
    void collectionHasNullValuesReturnsFalseWhenObjectBuilderIsNotCollection() {
        ObjectBuilder string = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(VALUE)
                .expectedChildren(0)
                .skipIfNull(true)
                .build();
        string.setValue("\"string_0\"");

        assertThat(collectionHasNullValues(string)).isFalse();
    }

    @Test
    void useFullyQualifiedNameReturnsFalseAndDoesNotKeepTrackOfClassesThatDoesNotRequireImport() {
        HashMap<String, Class<?>> classNames = new HashMap<>();

        assertThat(useFullyQualifiedName(Integer.class, classNames)).isFalse();
        assertThat(useFullyQualifiedName(int.class, classNames)).isFalse();
        assertThat(useFullyQualifiedName(Boolean.class, classNames)).isFalse();
        assertThat(useFullyQualifiedName(boolean.class, classNames)).isFalse();
        assertThat(classNames).isEmpty();
    }

    @Test
    void useFullyQualifiedNameReturnsTrueWhenClassWithTheSameSimpleNameAlreadyExists() {
        HashMap<String, Class<?>> classNames = new HashMap<>();

        assertThat(useFullyQualifiedName(Date.class, classNames)).isFalse();
        assertThat(useFullyQualifiedName(java.sql.Date.class, classNames)).isTrue();
        assertThat(classNames).hasSize(1);

        classNames = new HashMap<>();

        assertThat(useFullyQualifiedName(java.sql.Date.class, classNames)).isFalse();
        assertThat(useFullyQualifiedName(Date.class, classNames)).isTrue();
        assertThat(classNames).hasSize(1);
    }

    @Test
    void getHelperMethodReturnsUrlHelperForUrlClass() {
        String result = getHelperMethod(URL.class);

        assertThat(result).isEqualTo(String.join(System.lineSeparator(),
                "\tprivate static java.net.URL toUrl(String url) {",
                "\t\ttry {",
                "\t\t\treturn new java.net.URL(url);",
                "\t\t} catch (java.net.MalformedURLException e) {",
                "\t\t\tthrow new RuntimeException(e);",
                "\t\t}",
                "\t}"));
    }

    @Test
    void getHelperMethodReturnsInetAddressHelperForInetAddressClass() {
        String expected = String.join(System.lineSeparator(),
                "\tprivate static java.net.InetAddress toInetAddress(String host) {",
                "\t\ttry {",
                "\t\t\treturn java.net.InetAddress.getByName(host);",
                "\t\t} catch (java.net.UnknownHostException e) {",
                "\t\t\tthrow new RuntimeException(e);",
                "\t\t}",
                "\t}");

        assertThat(getHelperMethod(InetAddress.class)).isEqualTo(expected);
        assertThat(getHelperMethod(Inet4Address.class)).isEqualTo(expected);
        assertThat(getHelperMethod(Inet6Address.class)).isEqualTo(expected);
        assertThat(getHelperMethod(InetSocketAddress.class)).isEqualTo(expected);
    }

    @Test
    void getHelperMethodReturnsNullForUnrelatedClass() {
        assertThat(getHelperMethod(Pojo.class)).isNull();
    }

    @Test
    void getHelperMethodReturnsNullForNullInput() {
        assertThat(getHelperMethod(null)).isNull();
    }

    @Test
    void getPrivateConstructorHelperMethodGeneratesReflectionHelperWithMixedParameterTypes() {
        Map<String, Class<?>> classNames = new HashMap<>();
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        String result = getPrivateConstructorHelperMethod(
                Pojo.class,
                "createPojo_0",
                new Class<?>[]{String.class, int.class},
                classNames, imports, staticImports);

        assertThat(result).isEqualTo(String.join(System.lineSeparator(),
                "\tprivate static Pojo createPojo_0(",
                "\t\t\tString p0,",
                "\t\t\tint p1",
                "\t) {",
                "\t\ttry {",
                "\t\t\tConstructor<Pojo> constructor = Pojo.class.getDeclaredConstructor(",
                "\t\t\t\t\tString.class,",
                "\t\t\t\t\tint.class",
                "\t\t\t\t);",
                "\t\t\tconstructor.setAccessible(true);",
                "\t\t\treturn constructor.newInstance(",
                "\t\t\t\tp0,",
                "\t\t\t\tp1",
                "\t\t\t);",
                "\t\t} catch (Exception e) {",
                "\t\t\tthrow new RuntimeException(e);",
                "\t\t}",
                "\t}"));
        assertThat(imports).containsExactlyInAnyOrder(
                "com.github.anhem.testpopulator.model.java.setter.Pojo",
                "java.lang.reflect.Constructor"
        );
    }

    @Test
    void getPrivateConstructorHelperMethodGeneratesReflectionHelperWithNoParameters() {
        Map<String, Class<?>> classNames = new HashMap<>();
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        String result = getPrivateConstructorHelperMethod(
                Pojo.class,
                "createPojo_0",
                new Class<?>[0],
                classNames, imports, staticImports);

        assertThat(result).isEqualTo(String.join(System.lineSeparator(),
                "\tprivate static Pojo createPojo_0() {",
                "\t\ttry {",
                "\t\t\tConstructor<Pojo> constructor = Pojo.class.getDeclaredConstructor();",
                "\t\t\tconstructor.setAccessible(true);",
                "\t\t\treturn constructor.newInstance();",
                "\t\t} catch (Exception e) {",
                "\t\t\tthrow new RuntimeException(e);",
                "\t\t}",
                "\t}"));
    }

    @Test
    void getPrivateConstructorHelperMethodGeneratesReflectionHelperWithObjectOnlyParameters() {
        Map<String, Class<?>> classNames = new HashMap<>();
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        String result = getPrivateConstructorHelperMethod(
                Pojo.class,
                "createPojo_0",
                new Class<?>[]{String.class, Integer.class},
                classNames, imports, staticImports);

        assertThat(result).isEqualTo(String.join(System.lineSeparator(),
                "\tprivate static Pojo createPojo_0(",
                "\t\t\tString p0,",
                "\t\t\tInteger p1",
                "\t) {",
                "\t\ttry {",
                "\t\t\tConstructor<Pojo> constructor = Pojo.class.getDeclaredConstructor(",
                "\t\t\t\t\tString.class,",
                "\t\t\t\t\tInteger.class",
                "\t\t\t\t);",
                "\t\t\tconstructor.setAccessible(true);",
                "\t\t\treturn constructor.newInstance(",
                "\t\t\t\tp0,",
                "\t\t\t\tp1",
                "\t\t\t);",
                "\t\t} catch (Exception e) {",
                "\t\t\tthrow new RuntimeException(e);",
                "\t\t}",
                "\t}"));
    }

    @Test
    void getFieldHelperMethodGeneratesReflectionHelper() throws NoSuchFieldException {
        java.lang.reflect.Field stringValueField = Pojo.class.getDeclaredField("stringValue");
        java.lang.reflect.Field integerValueField = Pojo.class.getDeclaredField("integerValue");

        Map<String, Class<?>> classNames = new HashMap<>();
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        String result = getFieldHelperMethod(
                Pojo.class,
                "createPojo_0",
                List.of(stringValueField, integerValueField),
                classNames, imports, staticImports);

        assertThat(result).isEqualTo(String.join(System.lineSeparator(),
                "\tprivate static Pojo createPojo_0(",
                "\t\t\tString p0,",
                "\t\t\tInteger p1",
                "\t) {",
                "\t\ttry {",
                "\t\t\tConstructor<Pojo> constructor = Pojo.class.getDeclaredConstructor();",
                "\t\t\tconstructor.setAccessible(true);",
                "\t\t\tPojo obj = constructor.newInstance();",
                "\t\t\tsetField(obj, Pojo.class, \"stringValue\", p0);",
                "\t\t\tsetField(obj, Pojo.class, \"integerValue\", p1);",
                "\t\t\treturn obj;",
                "\t\t} catch (Exception e) {",
                "\t\t\tthrow new RuntimeException(e);",
                "\t\t}",
                "\t}"));
        assertThat(imports).containsExactlyInAnyOrder(
                "com.github.anhem.testpopulator.model.java.setter.Pojo",
                "java.lang.reflect.Constructor"
        );
    }

    @Test
    void getSetFieldMethodGeneratesSetFieldMethod() {
        Map<String, Class<?>> classNames = new HashMap<>();
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        String result = getSetFieldMethod(classNames, imports, staticImports);

        assertThat(result).isEqualTo(String.join(System.lineSeparator(),
                "\tprivate static void setField(Object obj, Class<?> clazz, String fieldName, Object value) {",
                "\t\ttry {",
                "\t\t\tField field = clazz.getDeclaredField(fieldName);",
                "\t\t\tfield.setAccessible(true);",
                "\t\t\tfield.set(obj, value);",
                "\t\t} catch (Exception e) {",
                "\t\t\tthrow new RuntimeException(e);",
                "\t\t}",
                "\t}"));
        assertThat(imports).containsExactlyInAnyOrder("java.lang.reflect.Field");
    }
}
