package com.github.anhem.testpopulator.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
                .addStrategyOrder(BUILDER)
                .builderPattern(LOMBOK)
                .addStrategyOrder(SETTER)
                .addSetterPrefix("with")
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
    void buildingCustomPopulateConfig2() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(BUILDER, SETTER))
                .builderPattern(LOMBOK)
                .setterPrefixes(new ArrayList<>(List.of("also", "with")))
                .addSetterPrefix("as")
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
                .addStrategyOrder(FIELD)
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

    private static void assertEqual(PopulateConfig populateConfig, PopulateConfig expectedPopulateConfig) {
        assertThat(populateConfig)
                .usingRecursiveComparison()
                .isEqualTo(expectedPopulateConfig);
    }
}
