package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.immutables.ImmutableImmutablesAbstract;
import com.github.anhem.testpopulator.model.immutables.ImmutableImmutablesInterface;
import com.github.anhem.testpopulator.model.immutables.ImmutablesAbstract;
import com.github.anhem.testpopulator.model.immutables.ImmutablesInterface;
import com.github.anhem.testpopulator.model.java.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.Strategy.BUILDER;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithImmutablesBuilderStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(BUILDER))
                .builderPattern(BuilderPattern.IMMUTABLES)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void string() {
        String value_1 = populateFactory.populate(String.class);
        String value_2 = populateFactory.populate(String.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void immutablesInterfaceImplementation() {
        ImmutableImmutablesInterface value_1 = populateFactory.populate(ImmutableImmutablesInterface.class);
        ImmutableImmutablesInterface value_2 = populateFactory.populate(ImmutableImmutablesInterface.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesInterface() {
        ImmutablesInterface value_1 = populateFactory.populate(ImmutablesInterface.class);
        ImmutablesInterface value_2 = populateFactory.populate(ImmutablesInterface.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesAbstractImplementation() {
        ImmutableImmutablesAbstract value_1 = populateFactory.populate(ImmutableImmutablesAbstract.class);
        ImmutableImmutablesAbstract value_2 = populateFactory.populate(ImmutableImmutablesAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesAbstract() {
        ImmutablesAbstract value_1 = populateFactory.populate(ImmutablesAbstract.class);
        ImmutablesAbstract value_2 = populateFactory.populate(ImmutablesAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void allArgsConstructor() {
        assertThatThrownBy(() -> populateFactory.populate(AllArgsConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }
}
