package com.github.anhem.testpopulator.config;

import com.github.anhem.testpopulator.model.java.override.MyUUID;
import com.github.anhem.testpopulator.model.java.override.MyUUIDOverride;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PopulateConfigTest {

    @Test
    void buildingPopulateConfigResultsInDefaultValues() {
        PopulateConfig populateConfig = PopulateConfig.builder().build();

        assertThat(populateConfig).isNotNull();
        assertThat(populateConfig.getStrategyOrder()).isNotEmpty();
        assertThat(populateConfig.getOverridePopulate()).isNotNull();
        assertThat(populateConfig.useRandomValues()).isTrue();
    }

    @Test
    void buildingPopulateConfigAddingValuesOneByOne() {
        MyUUIDOverride overridePopulate = new MyUUIDOverride();
        Class<? extends MyUUID> overriddenClass = overridePopulate.create().getClass();

        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(Strategy.CONSTRUCTOR)
                .strategyOrder(Strategy.SETTER)
                .overridePopulate(overridePopulate)
                .build();

        assertThat(populateConfig.getStrategyOrder()).hasSize(2);
        assertThat(populateConfig.getStrategyOrder()).contains(Strategy.CONSTRUCTOR, Strategy.SETTER);
        assertThat(populateConfig.getOverridePopulate()).hasSize(1);
        assertThat(populateConfig.getOverridePopulate().get(overriddenClass)).isEqualTo(overridePopulate);
    }
}
