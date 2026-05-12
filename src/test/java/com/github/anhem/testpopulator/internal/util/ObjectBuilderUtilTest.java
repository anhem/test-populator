package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.internal.object.*;
import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.constructor.NestedCollections;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.internal.object.BuildType.LIST;
import static com.github.anhem.testpopulator.internal.object.BuildType.VALUE;
import static com.github.anhem.testpopulator.internal.object.ObjectBuilder.NULL;
import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.*;
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
        assertThat(endBuilder("build")).hasSize(1).contains("    .build();");
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
        ObjectBuilder addMethod = new MethodBuilder("add", 1);
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
        ObjectBuilder addMethod = new MethodBuilder("add", 1);
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
}
