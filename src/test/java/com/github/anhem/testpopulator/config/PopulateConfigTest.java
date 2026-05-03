package com.github.anhem.testpopulator.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.anhem.testpopulator.config.BuilderPattern.CUSTOM;
import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.PopulateConfig.*;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PopulateConfigTest {

    @Test
    void buildingPopulateConfigResultsInDefaultValues() {
        assertThat(DEFAULT_POPULATE_CONFIG).isNotNull();
        assertThat(DEFAULT_POPULATE_CONFIG.getStrategyOrder()).containsExactly(CONSTRUCTOR, SETTER, STATIC_METHOD);
        assertThat(DEFAULT_POPULATE_CONFIG.getOverridePopulate()).isEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.useRandomValues()).isTrue();
        assertThat(DEFAULT_POPULATE_CONFIG.canAccessNonPublicConstructors()).isFalse();
        assertThat(DEFAULT_POPULATE_CONFIG.getSetterPrefixes()).containsExactly("set");
        assertThat(DEFAULT_POPULATE_CONFIG.getBuilderPattern()).isEqualTo(CUSTOM);
        assertThat(DEFAULT_POPULATE_CONFIG.getBlacklistedMethods()).isNotEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.getBlacklistedFields()).isNotEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.isObjectFactoryEnabled()).isFalse();
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
                .prefixes("with")
                .and()
                .overridePopulate(Integer.class, () -> 1)
                .randomValues(false)
                .accessNonPublicConstructors(true)
                .nullOnCircularDependency(true)
                .methodType(MethodType.SIMPLEST)
                .build();

        assertThat(populateConfig.getStrategyOrder()).hasSize(2);
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER, SETTER);
        assertThat(populateConfig.getOverridePopulate()).hasSize(1);
        assertThat(populateConfig.getOverridePopulate()).containsKey(Integer.class);
        assertThat(populateConfig.useRandomValues()).isFalse();
        assertThat(populateConfig.canAccessNonPublicConstructors()).isTrue();
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(LOMBOK);
        assertThat(populateConfig.getSetterPrefixes()).hasSize(1);
        assertThat(populateConfig.getSetterPrefixes()).containsExactly("with");
        assertThat(populateConfig.isNullOnCircularDependency()).isTrue();
        assertThat(populateConfig.getMethodType()).isEqualTo(MethodType.SIMPLEST);
        assertEqual(populateConfig.toBuilder().build(), populateConfig);
    }

    @Test
    void buildingCustomPopulateConfigWithImplicitStrategyOrder() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderStrategy()
                .pattern(LOMBOK)
                .and()
                .constructorStrategy()
                .and()
                .setterStrategy()
                .prefixes("set")
                .and()
                .staticMethodStrategy()
                .methodType(MethodType.SIMPLEST)
                .and()
                .fieldStrategy()
                .and()
                .mutatorStrategy()
                .constructorType(ConstructorType.SMALLEST)
                .and()
                .build();

        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER, CONSTRUCTOR, SETTER, STATIC_METHOD, FIELD, MUTATOR);
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(LOMBOK);
        assertThat(populateConfig.getSetterPrefixes()).containsExactly("set");
        assertThat(populateConfig.getMethodType()).isEqualTo(MethodType.SIMPLEST);
        assertThat(populateConfig.getConstructorType()).isEqualTo(ConstructorType.SMALLEST);
    }

    @Test
    void canBuildDirectlyFromStrategyBuilder() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .constructorStrategy()
                .build();

        assertThat(populateConfig.getStrategyOrder()).containsExactly(CONSTRUCTOR);
    }

    @Test
    void reorderingUnconfiguredStrategiesThrowsException() {
        PopulateConfigBuilder populateConfigBuilder = PopulateConfig.builder()
                .constructorStrategy()
                .and();

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> populateConfigBuilder.reorderStrategies(SETTER));
        assertThat(illegalArgumentException.getMessage()).contains("Cannot reorder strategies that have not been configured: [SETTER]");
    }

    @Test
    void toBuilderCanReorderStrategies() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .constructorStrategy()
                .and()
                .setterStrategy()
                .and()
                .build();

        assertThat(populateConfig.getStrategyOrder()).containsExactly(CONSTRUCTOR, SETTER);

        PopulateConfig modifiedConfig = populateConfig.toBuilder()
                .reorderStrategies(SETTER, CONSTRUCTOR)
                .build();

        assertThat(modifiedConfig.getStrategyOrder()).containsExactly(SETTER, CONSTRUCTOR);
    }

    @Test
    void toBuilderCanClearStrategiesAndSetNewOrder() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .constructorStrategy()
                .and()
                .build();

        assertThat(populateConfig.getStrategyOrder()).containsExactly(CONSTRUCTOR);

        PopulateConfig modifiedConfig = populateConfig.toBuilder()
                .clearStrategies()
                .setterStrategy()
                .and()
                .build();

        assertThat(modifiedConfig.getStrategyOrder()).containsExactly(SETTER);
    }

    @Test
    void buildingCustomPopulateConfig2() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderStrategy()
                .pattern(LOMBOK)
                .and()
                .setterStrategy()
                .prefixes("also", "with", "as")
                .and()
                .overridePopulate(Map.of(Integer.class, () -> 2, Double.class, () -> 3.0))
                .randomValues(true)
                .accessNonPublicConstructors(false)
                .nullOnCircularDependency(false)
                .addBlacklistedMethod("blacklistedMethod")
                .addBlacklistedField("blacklistedField")
                .build();

        assertThat(populateConfig.getStrategyOrder()).hasSize(2);
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER, SETTER);
        assertThat(populateConfig.getOverridePopulate()).hasSize(2);
        assertThat(populateConfig.getOverridePopulate()).containsKey(Integer.class);
        assertThat(populateConfig.getOverridePopulate()).containsKey(Double.class);
        assertThat(populateConfig.useRandomValues()).isTrue();
        assertThat(populateConfig.canAccessNonPublicConstructors()).isFalse();
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(LOMBOK);
        assertThat(populateConfig.getSetterPrefixes()).containsExactly("also", "with", "as");
        assertThat(populateConfig.isNullOnCircularDependency()).isFalse();
        assertThat(DEFAULT_POPULATE_CONFIG.getMethodType()).isEqualTo(MethodType.LARGEST);
        assertThat(populateConfig.getBlacklistedMethods()).containsExactly("blacklistedMethod");
        assertThat(populateConfig.getBlacklistedFields()).containsExactly("blacklistedField");
        assertEqual(populateConfig.toBuilder().build(), populateConfig);
    }

    @Test
    void buildingCustomPopulateConfig3() {
        String blacklistedField = "blacklistedField";
        String blacklistedMethod = "blacklistedMethod";
        PopulateConfig populateConfig = builder()
                .build()
                .toBuilder()
                .addSetterPrefix("with")
                .addBlacklistedField(blacklistedField)
                .addBlacklistedMethod(blacklistedMethod)
                .overridePopulate(Map.of(Integer.class, () -> 2, Double.class, () -> 3.0))
                .build()
                .toBuilder()
                .overridePopulate(Integer.class, () -> 1)
                .build();

        assertThat(populateConfig.getSetterPrefixes()).containsExactly("set", "with");
        assertThat(populateConfig.getBlacklistedFields()).containsExactlyElementsOf(Stream.concat(DEFAULT_BLACKLISTED_FIELDS.stream(), Stream.of(blacklistedField)).collect(Collectors.toList()));
        assertThat(populateConfig.getBlacklistedMethods()).containsExactlyElementsOf(Stream.concat(DEFAULT_BLACKLISTED_METHODS.stream(), Stream.of(blacklistedMethod)).collect(Collectors.toList()));
        assertThat(populateConfig.getOverridePopulate()).hasSize(2);
        assertThat(populateConfig.getOverridePopulate().get(Integer.class).create()).isEqualTo(1);
        assertThat(populateConfig.getOverridePopulate().get(Double.class).create()).isEqualTo(3.0);
    }

    @Test
    void combiningAccessNonPublicConstructorsAndObjectFactoryEnabledThrowsException() {
        PopulateConfigBuilder populateConfigBuilder = DEFAULT_POPULATE_CONFIG.toBuilder()
                .accessNonPublicConstructors(true)
                .objectFactoryEnabled(true);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, populateConfigBuilder::build);
        assertThat(illegalArgumentException.getMessage()).isEqualTo(INVALID_CONFIG_NON_PUBLIC_CONSTRUCTOR_AND_OBJECT_FACTORY);
    }

    @Test
    void combiningFieldStrategyAndObjectFactoryEnabledThrowsException() {
        PopulateConfigBuilder populateConfigBuilder = DEFAULT_POPULATE_CONFIG.toBuilder()
                .fieldStrategy()
                .and()
                .objectFactoryEnabled(true);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, populateConfigBuilder::build);
        assertThat(illegalArgumentException.getMessage()).isEqualTo(INVALID_CONFIG_FIELD_STRATEGY_AND_OBJECT_FACTORY);
    }

    @Test
    void toStringReturnsAllConfiguredFields() {
        List<String> fieldNames = Arrays.stream(PopulateConfig.class.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(Field::getName)
                .map(s -> String.format("%s=", s))
                .collect(Collectors.toList());

        assertThat(DEFAULT_POPULATE_CONFIG.toString()).contains(fieldNames);
    }

    @Test
    void buildingCustomPopulateConfigWithProtobufBuilderPattern() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderStrategy()
                .pattern(BuilderPattern.PROTOBUF)
                .and()
                .build();

        assertThat(populateConfig.getBuilderPattern()).isEqualTo(BuilderPattern.PROTOBUF);
        assertThat(populateConfig.getBuilderMethod()).isEqualTo(PROTOBUF_BUILDER_METHOD);
        assertThat(populateConfig.getBuildMethod()).isEqualTo(DEFAULT_BUILD_METHOD);
    }

    @Test
    void buildingCustomPopulateConfigWithCustomBuilderPattern() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderStrategy()
                .pattern(CUSTOM)
                .method("customBuilder")
                .buildMethod("customBuild")
                .and()
                .build();

        assertThat(populateConfig.getBuilderPattern()).isEqualTo(CUSTOM);
        assertThat(populateConfig.getBuilderMethod()).isEqualTo("customBuilder");
        assertThat(populateConfig.getBuildMethod()).isEqualTo("customBuild");
    }

    @Test
    void buildingCustomPopulateConfigWithLists() {
        List<String> blacklistedMethods = List.of("method1", "method2");
        List<String> blacklistedFields = List.of("field1", "field2");
        List<String> setterPrefixes = List.of("set", "with");

        PopulateConfig populateConfig = PopulateConfig.builder()
                .blacklistedMethods(blacklistedMethods)
                .blacklistedFields(blacklistedFields)
                .setterStrategy()
                .prefixes(setterPrefixes)
                .and()
                .build();

        assertThat(populateConfig.getBlacklistedMethods()).isEqualTo(blacklistedMethods);
        assertThat(populateConfig.getBlacklistedFields()).isEqualTo(blacklistedFields);
        assertThat(populateConfig.getSetterPrefixes()).isEqualTo(setterPrefixes);
    }

    @Test
    void buildingCustomPopulateConfigWithReorderStrategies() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .constructorStrategy().and()
                .setterStrategy().and()
                .reorderStrategies(CONSTRUCTOR, SETTER)
                .build();

        assertThat(populateConfig.getStrategyOrder()).containsExactly(CONSTRUCTOR, SETTER);
    }

    @Test
    void buildingPopulateConfigWithBuilderMethods() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderMethod("builder")
                .buildMethod("build")
                .constructorType(ConstructorType.SMALLEST)
                .build();

        assertThat(populateConfig.getBuilderMethod()).isEqualTo("builder");
        assertThat(populateConfig.getBuildMethod()).isEqualTo("build");
        assertThat(populateConfig.getConstructorType()).isEqualTo(ConstructorType.SMALLEST);
    }

    @Test
    void buildingCustomPopulateConfigWithLombokBuilderPatternUsesDefaultMethods() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .builderStrategy()
                .pattern(LOMBOK)
                .and()
                .build();

        assertThat(populateConfig.getBuilderPattern()).isEqualTo(LOMBOK);
        assertThat(populateConfig.getBuilderMethod()).isEqualTo(DEFAULT_BUILDER_METHOD);
        assertThat(populateConfig.getBuildMethod()).isEqualTo(DEFAULT_BUILD_METHOD);
    }

    private static void assertEqual(PopulateConfig populateConfig, PopulateConfig expectedPopulateConfig) {
        assertThat(populateConfig)
                .usingRecursiveComparison()
                .isEqualTo(expectedPopulateConfig);
    }
}
