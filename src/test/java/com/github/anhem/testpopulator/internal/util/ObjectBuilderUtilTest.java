package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;
import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.constructor.NestedCollections;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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

        addImport(Pojo.class, null, imports, staticImports);

        assertThat(imports).isEqualTo(Set.of("com.github.anhem.testpopulator.model.java.setter.Pojo"));
        assertThat(staticImports).isEqualTo(Set.of());
    }

    @Test
    void addImportAddsInnerClassToStaticImports() {
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        addImport(NestedCollections.SimpleClass.class, null, imports, staticImports);

        assertThat(imports).isEqualTo(Set.of());
        assertThat(staticImports).isEqualTo(Set.of("com.github.anhem.testpopulator.model.java.constructor.NestedCollections.SimpleClass"));
    }

    @Test
    void addImportAddsEnumToStaticImports() {
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        addImport(ArbitraryEnum.class, ArbitraryEnum.A, imports, staticImports);

        assertThat(imports).isEqualTo(Set.of());
        assertThat(staticImports).isEqualTo(Set.of("com.github.anhem.testpopulator.model.java.ArbitraryEnum.A"));
    }

    @Test
    void isBasicValueReturnsFalse() {
        assertThat(isBasicValue(new ObjectBuilder(Pojo.class, "pojo_0", BuildType.VALUE, 0))).isFalse();
    }

    @Test
    void isBasicValueReturnsTrueWhenBuildTypeIsValueAndClassIsJavaBaseClass() {
        assertThat(isBasicValue(new ObjectBuilder(String.class, "string_0", BuildType.VALUE, 0))).isTrue();
    }

    @Test
    void isBasicValueReturnsTrueWhenBuildTypeIsValueAndClassIsEnum() {
        assertThat(isBasicValue(new ObjectBuilder(ArbitraryEnum.class, "arbitraryEnum_0", BuildType.VALUE, 0))).isTrue();
    }

    @Test
    void endBuilderReturnsStreamOfString() {
        assertThat(endBuilder()).hasSize(1).contains(".build();");
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
        ObjectBuilder objectBuilder = new ObjectBuilder(ArrayList.class, "arrayList_0", BuildType.LIST, 1);
        ObjectBuilder addMethod = new ObjectBuilder(String.class, "add", BuildType.METHOD, 1);
        ObjectBuilder string = new ObjectBuilder(String.class, "string_0", BuildType.VALUE, 0);
        string.setValue(NULL);
        addMethod.addChild(string);
        objectBuilder.addChild(addMethod);

        assertThat(collectionHasNullValues(objectBuilder)).isTrue();
    }

    @Test
    void collectionHasNullValuesReturnsFalseWhenObjectBuilderIsListAndWithChildren() {
        ObjectBuilder objectBuilder = new ObjectBuilder(ArrayList.class, "arrayList_0", BuildType.LIST, 1);
        ObjectBuilder addMethod = new ObjectBuilder(String.class, "add", BuildType.METHOD, 1);
        ObjectBuilder string = new ObjectBuilder(String.class, "string_0", BuildType.VALUE, 0);
        string.setValue("abc123");
        addMethod.addChild(string);
        objectBuilder.addChild(addMethod);

        assertThat(collectionHasNullValues(objectBuilder)).isFalse();
    }

    @Test
    void collectionHasNullValuesReturnsFalseWhenObjectBuilderIsNotCollection() {
        ObjectBuilder string = new ObjectBuilder(String.class, "string_0", BuildType.VALUE, 0);

        assertThat(collectionHasNullValues(string)).isFalse();
    }
}