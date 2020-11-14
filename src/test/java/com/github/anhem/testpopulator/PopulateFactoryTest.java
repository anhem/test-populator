package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.github.anhem.testpopulator.PopulateFactory.MISSING_STRATEGIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryTest {

    @Test
    void missingStrategiesThrowsException() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> new PopulateFactory(populateConfig))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(MISSING_STRATEGIES);
    }

    @Test
    void canCreatePopulateFactoryWithoutDefaultConfiguration() {
        PopulateFactory populateFactory = new PopulateFactory();

        assertThat(populateFactory).isNotNull();
    }
}
