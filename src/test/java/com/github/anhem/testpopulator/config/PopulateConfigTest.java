package com.github.anhem.testpopulator.config;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.anhem.testpopulator.config.BuilderPattern.*;
import static com.github.anhem.testpopulator.config.ConstructorType.SMALLEST;
import static com.github.anhem.testpopulator.config.PopulateConfig.*;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateConfigTest {

    private static final PopulateConfig DEFAULT_POPULATE_CONFIG = PopulateConfig.builder().build();

    @Test
    void buildingPopulateConfigResultsInDefaultValues() {
        assertThat(DEFAULT_POPULATE_CONFIG).isNotNull();
        assertThat(DEFAULT_POPULATE_CONFIG.getStrategyOrder()).containsExactly(CONSTRUCTOR, SETTER, STATIC_METHOD);
        assertThat(DEFAULT_POPULATE_CONFIG.getClassOverrides()).isEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.isRandomValues()).isTrue();
        assertThat(DEFAULT_POPULATE_CONFIG.isAccessNonPublicConstructors()).isFalse();
        assertThat(DEFAULT_POPULATE_CONFIG.getSetterPrefixes()).containsExactly("set");
        assertThat(DEFAULT_POPULATE_CONFIG.getBuilderPattern()).isEqualTo(CUSTOM);
        assertThat(DEFAULT_POPULATE_CONFIG.getBlacklistedMethods()).isNotEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.getBlacklistedFields()).isNotEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.isObjectFactoryEnabled()).isFalse();
        assertThat(DEFAULT_POPULATE_CONFIG.getObjectFactoryPath()).isNull();
        assertThat(DEFAULT_POPULATE_CONFIG.isNullOnCircularDependency()).isFalse();
        assertThat(DEFAULT_POPULATE_CONFIG.getMethodType()).isEqualTo(MethodType.LARGEST);
        assertEqual(DEFAULT_POPULATE_CONFIG.toBuilder().build(), DEFAULT_POPULATE_CONFIG);
    }

    @Test
    void buildingCustomPopulateConfig1() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderStrategy()
                    .pattern(LOMBOK)
                    .and()
                .setterStrategy()
                    .setPrefixes("with")
                    .and()
                .addOverride(Integer.class, () -> 1)
                .randomValues(false)
                .accessNonPublicConstructors(true)
                .nullOnCircularDependency(true)
                .methodType(MethodType.SIMPLEST)
                .build();

        assertThat(populateConfig.getStrategyOrder()).hasSize(2);
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER, SETTER);
        assertThat(populateConfig.getClassOverrides()).hasSize(1);
        assertThat(populateConfig.getClassOverrides()).containsKey(Integer.class);
        assertThat(populateConfig.isRandomValues()).isFalse();
        assertThat(populateConfig.isAccessNonPublicConstructors()).isTrue();
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(LOMBOK);
        assertThat(populateConfig.getSetterPrefixes()).containsExactly("with");
        assertThat(populateConfig.isNullOnCircularDependency()).isTrue();
        assertThat(populateConfig.getMethodType()).isEqualTo(MethodType.SIMPLEST);
    }

    @Test
    void buildingCustomPopulateConfig2() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderStrategy()
                    .pattern(IMMUTABLES)
                    .and()
                .mutatorStrategy()
                    .constructorType(SMALLEST)
                    .and()
                .addBlacklistedMethods("getSomething")
                .addBlacklistedFields("something")
                .build();

        assertThat(populateConfig.getStrategyOrder()).hasSize(2);
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER, MUTATOR);
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(IMMUTABLES);
        assertThat(populateConfig.getConstructorType()).isEqualTo(SMALLEST);
        assertThat(populateConfig.getBlacklistedMethods()).contains("getSomething");
        assertThat(populateConfig.getBlacklistedFields()).contains("something");
    }

    @Test
    void buildingCustomPopulateConfig3() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .staticMethodStrategy()
                    .methodType(MethodType.SMALLEST)
                    .and()
                .fieldStrategy()
                    .and()
                .build();

        assertThat(populateConfig.getStrategyOrder()).hasSize(2);
        assertThat(populateConfig.getStrategyOrder()).containsExactly(STATIC_METHOD, FIELD);
        assertThat(populateConfig.getMethodType()).isEqualTo(MethodType.SMALLEST);
    }

    @Test
    void buildingCustomPopulateConfig4() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderStrategy()
                    .pattern(CUSTOM)
                    .builderMethod("create")
                    .buildMethod("finish")
                    .and()
                .build();

        assertThat(populateConfig.getStrategyOrder()).hasSize(1);
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER);
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(CUSTOM);
        assertThat(populateConfig.getBuilderMethod()).isEqualTo("create");
        assertThat(populateConfig.getBuildMethod()).isEqualTo("finish");
    }

    @Test
    void reorderStrategiesOnFreshBuilderWorks() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .reorderStrategies(SETTER, CONSTRUCTOR)
                .build();

        assertThat(populateConfig.getStrategyOrder()).containsExactly(SETTER, CONSTRUCTOR);
    }

    @Test
    void reorderStrategiesWorks() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .constructorStrategy()
                    .and()
                .setterStrategy()
                    .and()
                .reorderStrategies(SETTER, CONSTRUCTOR)
                .build();

        assertThat(populateConfig.getStrategyOrder()).containsExactly(SETTER, CONSTRUCTOR);
    }

    @Test
    void setBlacklistedMethodsReplacesExisting() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .setBlacklistedMethods("m1", "m2")
                .build();

        assertThat(populateConfig.getBlacklistedMethods()).containsExactlyInAnyOrder("m1", "m2");
    }

    @Test
    void addBlacklistedMethodsAddsToExisting() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addBlacklistedMethods("m1")
                .addBlacklistedMethods("m2")
                .build();

        assertThat(populateConfig.getBlacklistedMethods()).contains("m1", "m2");
    }

    @Test
    void setBlacklistedFieldsReplacesExisting() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .setBlacklistedFields("f1", "f2")
                .build();

        assertThat(populateConfig.getBlacklistedFields()).containsExactlyInAnyOrder("f1", "f2");
    }

    @Test
    void addBlacklistedFieldsAddsToExisting() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addBlacklistedFields("f1")
                .addBlacklistedFields("f2")
                .build();

        assertThat(populateConfig.getBlacklistedFields()).contains("f1", "f2");
    }

    @Test
    void setClassOverridesReplacesExisting() {
        Map<Class<?>, OverridePopulate<?>> overrides = new HashMap<>();
        overrides.put(Integer.class, () -> 1);
        overrides.put(Double.class, () -> 2.0);

        PopulateConfig populateConfig = PopulateConfig.builder()
                .setClassOverrides(overrides)
                .build();

        assertThat(populateConfig.getClassOverrides()).hasSize(2);
        assertThat(populateConfig.getClassOverrides()).containsKey(Integer.class);
        assertThat(populateConfig.getClassOverrides()).containsKey(Double.class);
    }

    @Test
    void addClassOverridesAddsToExisting() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(Integer.class, () -> 1)
                .addClassOverrides(Map.of(Double.class, () -> 3.0))
                .build();

        assertThat(populateConfig.getClassOverrides()).hasSize(2);
        assertThat(populateConfig.getClassOverrides().get(Integer.class).create()).isEqualTo(1);
        assertThat(populateConfig.getClassOverrides().get(Double.class).create()).isEqualTo(3.0);
    }

    @Test
    void setNameOverridesReplacesExisting() {
        Map<OverrideTarget, OverridePopulate<?>> overrides = new HashMap<>();
        overrides.put(OverrideTarget.of("name1", String.class), () -> "val1");
        overrides.put(OverrideTarget.of("name2", String.class), () -> "val2");

        PopulateConfig populateConfig = PopulateConfig.builder()
                .setNameOverrides(overrides)
                .build();

        assertThat(populateConfig.getNameOverrides()).hasSize(2);
        assertThat(populateConfig.getNameOverrides()).containsKey(OverrideTarget.of("name1", String.class));
        assertThat(populateConfig.getNameOverrides()).containsKey(OverrideTarget.of("name2", String.class));
    }

    @Test
    void addNameOverridesAddsToExisting() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride("name1", String.class, () -> "val1")
                .addNameOverrides(Map.of(OverrideTarget.of("name2", String.class), () -> "val3"))
                .build();

        assertThat(populateConfig.getNameOverrides()).hasSize(2);
        assertThat(populateConfig.getNameOverrides().get(OverrideTarget.of("name1", String.class)).create()).isEqualTo("val1");
        assertThat(populateConfig.getNameOverrides().get(OverrideTarget.of("name2", String.class)).create()).isEqualTo("val3");
    }

    @Test
    void setterPrefixesMethods() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .setterStrategy()
                    .setPrefixes(List.of("with"))
                    .and()
                .build();
        assertThat(populateConfig.getSetterPrefixes()).containsExactly("with");

        populateConfig = PopulateConfig.builder()
                .setterStrategy()
                    .setPrefixes("with")
                    .and()
                .build();
        assertThat(populateConfig.getSetterPrefixes()).containsExactly("with");

        populateConfig = PopulateConfig.builder()
                .setterStrategy()
                    .addPrefixes(List.of("set"))
                    .and()
                .build();
        assertThat(populateConfig.getSetterPrefixes()).contains("set");

        populateConfig = PopulateConfig.builder()
                .setterStrategy()
                    .addPrefixes("set")
                    .and()
                .build();
        assertThat(populateConfig.getSetterPrefixes()).contains("set");
    }

    @Test
    void toBuilderCreatesCopy() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(Integer.class, () -> 1)
                .addOverride("name", String.class, () -> "val")
                .build();

        PopulateConfig copy = populateConfig.toBuilder().build();

        assertEqual(copy, populateConfig);
        assertThat(copy).isNotSameAs(populateConfig);
    }

    @Test
    void validateThrowsExceptionWhenAccessNonPublicConstructorsAndObjectFactoryEnabled() {
        assertThatThrownBy(() -> PopulateConfig.builder()
                .accessNonPublicConstructors(true)
                .objectFactoryEnabled(true)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_CONFIG_NON_PUBLIC_CONSTRUCTOR_AND_OBJECT_FACTORY);
    }

    @Test
    void validateThrowsExceptionWhenFieldStrategyAndObjectFactoryEnabled() {
        assertThatThrownBy(() -> PopulateConfig.builder()
                .fieldStrategy()
                .and()
                .objectFactoryEnabled(true)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INVALID_CONFIG_FIELD_STRATEGY_AND_OBJECT_FACTORY);
    }

    @Test
    void builderPatternSpecificMethodNames() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderPattern(PROTOBUF)
                .build();

        assertThat(populateConfig.getBuilderPattern()).isEqualTo(PROTOBUF);
        assertThat(populateConfig.getBuilderMethod()).isEqualTo(PROTOBUF_BUILDER_METHOD);
        assertThat(populateConfig.getBuildMethod()).isEqualTo(DEFAULT_BUILD_METHOD);

        populateConfig = PopulateConfig.builder()
                .builderPattern(LOMBOK)
                .build();

        assertThat(populateConfig.getBuilderPattern()).isEqualTo(LOMBOK);
        assertThat(populateConfig.getBuilderMethod()).isEqualTo(DEFAULT_BUILDER_METHOD);
        assertThat(populateConfig.getBuildMethod()).isEqualTo(DEFAULT_BUILD_METHOD);

        populateConfig = PopulateConfig.builder()
                .builderPattern(IMMUTABLES)
                .build();

        assertThat(populateConfig.getBuilderPattern()).isEqualTo(IMMUTABLES);
        assertThat(populateConfig.getBuilderMethod()).isEqualTo(DEFAULT_BUILDER_METHOD);
        assertThat(populateConfig.getBuildMethod()).isEqualTo(DEFAULT_BUILD_METHOD);

        populateConfig = PopulateConfig.builder()
                .builderPattern(CUSTOM)
                .build();

        assertThat(populateConfig.getBuilderPattern()).isEqualTo(CUSTOM);
        assertThat(populateConfig.getBuilderMethod()).isEqualTo(DEFAULT_BUILDER_METHOD);
        assertThat(populateConfig.getBuildMethod()).isEqualTo(DEFAULT_BUILD_METHOD);
    }

    @Test
    void toStringContainsAllFields() {
        String toString = DEFAULT_POPULATE_CONFIG.toString();
        assertThat(toString).contains("classOverrides");
        assertThat(toString).contains("nameOverrides");
    }

    @Test
    void clearStrategiesWorks() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .constructorStrategy()
                .and()
                .clearStrategies()
                .setterStrategy()
                .and()
                .build();

        assertThat(populateConfig.getStrategyOrder()).containsExactly(SETTER);
    }

    @Test
    void canSetEmptyBlacklistedMethods() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .setBlacklistedMethods(List.of())
                .build();

        assertThat(populateConfig.getBlacklistedMethods()).isEmpty();
    }

    @Test
    void canSetEmptyBlacklistedFields() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .setBlacklistedFields(List.of())
                .build();

        assertThat(populateConfig.getBlacklistedFields()).isEmpty();
    }

    @Test
    void canSetEmptySetterPrefixes() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .setSetterPrefixes(List.of())
                .build();

        assertThat(populateConfig.getSetterPrefixes()).isEmpty();
    }

    @Test
    void objectFactoryConfigWorks() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactory(true)
                .path("custom/path")
                .and()
                .build();

        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        assertThat(populateConfig.getObjectFactoryPath()).isEqualTo("custom/path");
    }

    @Test
    void objectFactoryPathIsNullWhenDisabled() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactory(false)
                .build();

        assertThat(populateConfig.isObjectFactoryEnabled()).isFalse();
        assertThat(populateConfig.getObjectFactoryPath()).isNull();
    }

    private static void assertEqual(PopulateConfig populateConfig, PopulateConfig expectedPopulateConfig) {
        assertThat(populateConfig)
                .usingRecursiveComparison()
                .isEqualTo(expectedPopulateConfig);
    }
}
