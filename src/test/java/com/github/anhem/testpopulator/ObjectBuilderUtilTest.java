package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.NestedCollections;
import com.github.anhem.testpopulator.model.java.Pojo;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.ObjectBuilderUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class ObjectBuilderUtilTest {

    @Test
    void getPackageNameReturnsPackageName() {
        assertThat(getPackageName(Pojo.class)).isEqualTo("com.github.anhem.testpopulator.model.java");
    }

    @Test
    void getPackageNameReturnsPackageNameOfObjectBuilderWhenClassNameStartsWithJava() {
        assertThat(getPackageName(String.class)).isEqualTo("com.github.anhem.testpopulator");
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

        assertThat(imports).isEqualTo(Set.of("com.github.anhem.testpopulator.model.java.Pojo"));
        assertThat(staticImports).isEqualTo(Set.of());
    }

    @Test
    void addImportAddsInnerClassToStaticImports() {
        Set<String> imports = new HashSet<>();
        Set<String> staticImports = new HashSet<>();

        addImport(NestedCollections.SimpleClass.class, null, imports, staticImports);

        assertThat(imports).isEqualTo(Set.of());
        assertThat(staticImports).isEqualTo(Set.of("com.github.anhem.testpopulator.model.java.NestedCollections.SimpleClass"));
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
        assertThat(isBasicValue(new ObjectBuilder(Pojo.class, "pojo0", BuildType.VALUE, 0))).isFalse();
    }

    @Test
    void isBasicValueReturnsTrueWhenBuildTypeIsValueAndClassIsJavaBaseClass() {
        assertThat(isBasicValue(new ObjectBuilder(String.class, "string0", BuildType.VALUE, 0))).isTrue();
    }

    @Test
    void isBasicValueReturnsTrueWhenBuildTypeIsValueAndClassIsEnum() {
        assertThat(isBasicValue(new ObjectBuilder(ArbitraryEnum.class, "arbitraryEnum0", BuildType.VALUE, 0))).isTrue();
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
}