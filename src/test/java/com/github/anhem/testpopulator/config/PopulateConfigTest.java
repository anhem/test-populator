package com.github.anhem.testpopulator.config;

import com.github.anhem.testpopulator.model.java.override.MyUUID;
import com.github.anhem.testpopulator.model.java.override.MyUUIDOverride;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.testutil.PopulateConfigTestUtil.DEFAULT_POPULATE_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateConfigTest {

    @Test
    void buildingPopulateConfigResultsInDefaultValues() {
        assertThat(DEFAULT_POPULATE_CONFIG).isNotNull();
        assertThat(DEFAULT_POPULATE_CONFIG.getStrategyOrder()).containsExactly(CONSTRUCTOR, SETTER, FIELD);
        assertThat(DEFAULT_POPULATE_CONFIG.createOverridePopulates()).isNotNull();
        assertThat(DEFAULT_POPULATE_CONFIG.createOverridePopulates()).isEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.useRandomValues()).isTrue();
        assertThat(DEFAULT_POPULATE_CONFIG.canAccessNonPublicConstructors()).isFalse();
        assertThat(DEFAULT_POPULATE_CONFIG.getSetterPrefix()).isEqualTo("set");
        assertThat(DEFAULT_POPULATE_CONFIG.getBuilderPattern()).isNull();
        assertThat(DEFAULT_POPULATE_CONFIG.getBlacklistedMethods()).isNotEmpty();
        assertThat(DEFAULT_POPULATE_CONFIG.getBlacklistedFields()).isNotEmpty();
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
