package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.MISSING_BUILDER_PATTERN;
import static com.github.anhem.testpopulator.PopulateFactory.MISSING_STRATEGIES;
import static com.github.anhem.testpopulator.config.Strategy.BUILDER;
import static java.lang.String.format;
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
    void missingBuilderPatternWhenUsingBuilderStrategyThrowsException() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(Strategy.BUILDER))
                .build();

        assertThatThrownBy(() -> new PopulateFactory(populateConfig))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(format(MISSING_BUILDER_PATTERN, BUILDER, Arrays.toString(BuilderPattern.values())));
    }

    @Test
    void canCreatePopulateFactoryWithoutDefaultConfiguration() {
        PopulateFactory populateFactory = new PopulateFactory();

        assertThat(populateFactory).isNotNull();
    }
}
