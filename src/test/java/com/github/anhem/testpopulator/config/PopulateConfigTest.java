package com.github.anhem.testpopulator.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PopulateConfigTest {

    @Test
    public void buildingPopulateConfigResultsInDefaultValues() {
        PopulateConfig populateConfig = PopulateConfig.builder().build();

        assertThat(populateConfig).isNotNull();
        assertThat(populateConfig.getStrategyOrder()).isNotEmpty();
        assertThat(populateConfig.getOverridePopulate()).isNotNull();
        assertThat(populateConfig.useRandomValues()).isTrue();
    }

}
