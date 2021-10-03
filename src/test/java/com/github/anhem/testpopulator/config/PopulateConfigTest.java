package com.github.anhem.testpopulator.config;

import com.github.anhem.testpopulator.model.java.override.MyUUID;
import com.github.anhem.testpopulator.model.java.override.MyUUIDOverride;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateConfigTest {

    @Test
    void buildingPopulateConfigResultsInDefaultValues() {
        PopulateConfig populateConfig = PopulateConfig.builder().build();

        assertThat(populateConfig).isNotNull();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(CONSTRUCTOR, SETTER, FIELD);
        assertThat(populateConfig.createOverridePopulates()).isNotNull();
        assertThat(populateConfig.createOverridePopulates()).isEmpty();
        assertThat(populateConfig.useRandomValues()).isTrue();
        assertThat(populateConfig.canAccessNonPublicConstructors()).isFalse();
        assertThat(populateConfig.getSetterPrefix()).isEqualTo("set");
        assertThat(populateConfig.getBuilderPattern()).isNull();
    }

    @Test
    void buildingCustomPopulateConfig() {
        MyUUIDOverride overridePopulate = new MyUUIDOverride();
        Class<? extends MyUUID> overriddenClass = overridePopulate.create().getClass();

        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(BUILDER)
                .builderPattern(LOMBOK)
                .strategyOrder(SETTER)
                .setterPrefix("with")
                .overridePopulate(overridePopulate)
                .randomValues(false)
                .accessNonPublicConstructors(true)
                .build();

        assertThat(populateConfig.getStrategyOrder()).hasSize(2);
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER, SETTER);
        assertThat(populateConfig.createOverridePopulates()).hasSize(1);
        assertThat(populateConfig.createOverridePopulates()).containsEntry(overriddenClass, overridePopulate);
        assertThat(populateConfig.useRandomValues()).isFalse();
        assertThat(populateConfig.canAccessNonPublicConstructors()).isTrue();
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(LOMBOK);
        assertThat(populateConfig.getSetterPrefix()).isEqualTo("with");
    }
}
