package com.github.anhem.testpopulator.readme;

import com.github.anhem.testpopulator.PopulateFactory;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.config.Strategy;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.readme.model.MyNestedConstructorClass;
import com.github.anhem.testpopulator.readme.model.MyNestedStaticMethodClass;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.http.HttpClient;

import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.NO_MATCHING_STRATEGY;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class StrategyFailureTest {

    @Test
    void populateFailsForClassWithNestedFileWhenConstructorStrategyIsDisabled() {
        // MyNestedConstructorClass can be instantiated with SETTER strategy.
        // However, java.io.File requires the CONSTRUCTOR strategy (it has no no-arg constructor).
        // If only the SETTER strategy is used, File will fail to populate,
        // causing the overall MyNestedConstructorClass population to fail.

        PopulateConfig populateConfig = PopulateConfig.builder()
                .reorderStrategies(Strategy.SETTER) // Only try SETTER strategy
                .build();

        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        assertThatExceptionOfType(PopulateException.class)
                .isThrownBy(() -> populateFactory.populate(MyNestedConstructorClass.class))
                .withMessage(format(FAILED_TO_CREATE_OBJECT, MyNestedConstructorClass.class.getName(), Strategy.SETTER))
                .havingCause()
                .withStackTraceContaining(format(NO_MATCHING_STRATEGY, File.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void populateFailsForClassWithNestedHttpClientWhenStaticMethodStrategyIsDisabled() {
        // MyNestedStaticMethodClass can be instantiated with SETTER strategy.
        // However, java.net.http.HttpClient requires the STATIC_METHOD strategy (it has no public constructor).
        // If only the SETTER strategy is used, HttpClient will fail to populate,
        // causing the overall MyNestedStaticMethodClass population to fail.

        PopulateConfig populateConfig = PopulateConfig.builder()
                .reorderStrategies(Strategy.SETTER) // Only try SETTER strategy
                .build();

        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        assertThatExceptionOfType(PopulateException.class)
                .isThrownBy(() -> populateFactory.populate(MyNestedStaticMethodClass.class))
                .withMessage(format(FAILED_TO_CREATE_OBJECT, MyNestedStaticMethodClass.class.getName(), Strategy.SETTER))
                .havingCause()
                .withStackTraceContaining(format(NO_MATCHING_STRATEGY, HttpClient.class.getName(), populateConfig.getStrategyOrder()));
    }
}
