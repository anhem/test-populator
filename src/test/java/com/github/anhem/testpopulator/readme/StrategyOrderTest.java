package com.github.anhem.testpopulator.readme;

import com.github.anhem.testpopulator.PopulateFactory;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.readme.model.OrderSensitiveClass;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.NO_MATCHING_STRATEGY;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class StrategyOrderTest {

    @Test
    void populationFailsWhenConstructorStrategyIsFirstAndOneArgumentCannotBePopulated() {
        // By default, CONSTRUCTOR strategy comes before SETTER.
        // OrderSensitiveClass has a constructor that takes 'Unpopulatable', which has no concrete implementation.
        // The populator will choose the CONSTRUCTOR strategy, try to populate its arguments, and fail.

        PopulateConfig populateConfig = PopulateConfig.builder()
                .reorderStrategies(Strategy.CONSTRUCTOR, Strategy.SETTER)
                .build();

        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        assertThatExceptionOfType(PopulateException.class)
                .isThrownBy(() -> populateFactory.populate(OrderSensitiveClass.class))
                .withMessage(format(FAILED_TO_CREATE_OBJECT, OrderSensitiveClass.class.getName(), Strategy.CONSTRUCTOR))
                .havingCause()
                .withStackTraceContaining(format(NO_MATCHING_STRATEGY, OrderSensitiveClass.Unpopulatable.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void populationSucceedsWhenSetterStrategyIsFirst() {
        // By reordering strategies to put SETTER first, the populator will use the no-arg constructor
        // and setters. Since the no-arg constructor does not require 'Unpopulatable',
        // the population succeeds.

        PopulateConfig populateConfig = PopulateConfig.builder()
                .reorderStrategies(Strategy.SETTER, Strategy.CONSTRUCTOR)
                .build();

        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        OrderSensitiveClass result = populateFactory.populate(OrderSensitiveClass.class);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isNotBlank();
    }
}
