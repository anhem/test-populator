package com.github.anhem.testpopulator.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.PopulateConfig.*;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PopulateConfigTest {

    @Test
    void buildingPopulateConfigResultsInDefaultValues() {
        assertThat(DEFAULT_POPULATE_CONFIG).isNotNull();
        assertThat(DEFAULT_POPULATE_CONFIG.getStrategyOrder()).containsExactly(CONSTRUCTOR, SETTER);
        assertThat(DEFAULT_POPULATE_CONFIG.getOverridePopulate()).isNotNull();
        assertThat(DEFAULT_POPULATE_CONFIG.getOverridePopulate()).isEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.useRandomValues()).isTrue();
        assertThat(DEFAULT_POPULATE_CONFIG.canAccessNonPublicConstructors()).isFalse();
        assertThat(DEFAULT_POPULATE_CONFIG.getSetterPrefix()).isEqualTo("set");
        assertThat(DEFAULT_POPULATE_CONFIG.getBuilderPattern()).isNull();
        assertThat(DEFAULT_POPULATE_CONFIG.getBlacklistedMethods()).isNotEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.getBlacklistedFields()).isNotEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.isObjectFactoryEnabled()).isFalse();
        assertThat(DEFAULT_POPULATE_CONFIG.isNullOnCircularDependency()).isFalse();
        assertEqual(DEFAULT_POPULATE_CONFIG.toBuilder().build(), DEFAULT_POPULATE_CONFIG);
    }

    @Test
    void buildingCustomPopulateConfig() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(BUILDER)
                .builderPattern(LOMBOK)
                .strategyOrder(SETTER)
                .setterPrefix("with")
                .overridePopulate(Integer.class, () -> 1)
                .randomValues(false)
                .accessNonPublicConstructors(true)
                .nullOnCircularDependency(true)
                .build();

        assertThat(populateConfig.getStrategyOrder()).hasSize(2);
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER, SETTER);
        assertThat(populateConfig.getOverridePopulate()).hasSize(1);
        assertThat(populateConfig.getOverridePopulate()).containsKey(Integer.class);
        assertThat(populateConfig.useRandomValues()).isFalse();
        assertThat(populateConfig.canAccessNonPublicConstructors()).isTrue();
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(LOMBOK);
        assertThat(populateConfig.getSetterPrefix()).isEqualTo("with");
        assertThat(populateConfig.isNullOnCircularDependency()).isTrue();
        assertEqual(populateConfig.toBuilder().build(), populateConfig);
    }

    private static void assertEqual(PopulateConfig populateConfig, PopulateConfig expectedPopulateConfig) {
        assertThat(populateConfig)
                .usingRecursiveComparison()
                .isEqualTo(expectedPopulateConfig);
    }

    @Test
    void settingBuilderWithoutBuilderPatternThrowsException() {
        PopulateConfigBuilder populateConfigBuilder = DEFAULT_POPULATE_CONFIG.toBuilder()
                .strategyOrder(BUILDER);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, populateConfigBuilder::build);
        assertThat(illegalArgumentException.getMessage()).isEqualTo(format(INVALID_CONFIG_MISSING_BUILDER_PATTERN, BUILDER, Arrays.toString(BuilderPattern.values())));
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
                .strategyOrder(FIELD)
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
}
