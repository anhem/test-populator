package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.immutables.*;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.Strategy.BUILDER;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithImmutablesBuilderStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(BUILDER))
                .builderPattern(IMMUTABLES)
                .objectFactoryEnabled(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void string() {
        String value_1 = populateAndAssertWithGeneratedCode(String.class);
        String value_2 = populateAndAssertWithGeneratedCode(String.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void immutablesInterfaceImplementation() {
        ImmutableImmutablesInterface value_1 = populateAndAssertWithGeneratedCode(ImmutableImmutablesInterface.class);
        ImmutableImmutablesInterface value_2 = populateAndAssertWithGeneratedCode(ImmutableImmutablesInterface.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesInterface() {
        ImmutablesInterface value_1 = populateAndAssertWithGeneratedCode(ImmutablesInterface.class);
        ImmutablesInterface value_2 = populateAndAssertWithGeneratedCode(ImmutablesInterface.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesAbstractImplementation() {
        ImmutableImmutablesAbstract value_1 = populateAndAssertWithGeneratedCode(ImmutableImmutablesAbstract.class);
        ImmutableImmutablesAbstract value_2 = populateAndAssertWithGeneratedCode(ImmutableImmutablesAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesAbstract() {
        ImmutablesAbstract value_1 = populateAndAssertWithGeneratedCode(ImmutablesAbstract.class);
        ImmutablesAbstract value_2 = populateAndAssertWithGeneratedCode(ImmutablesAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesOddInterface() {
        ImmutablesOddInterface value_1 = populateAndAssertWithGeneratedCode(ImmutablesOddInterface.class);
        ImmutablesOddInterface value_2 = populateAndAssertWithGeneratedCode(ImmutablesOddInterface.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void allArgsConstructor() {
        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(AllArgsConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void createsObjectWhenNullOnCircularDependencyIsTrue() {
        populateConfig = populateConfig.toBuilder()
                .nullOnCircularDependency(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        ImmutableImmutablesInterface value_1 = populateFactory.populate(ImmutableImmutablesInterface.class);
        ImmutableImmutablesInterface value_2 = populateFactory.populate(ImmutableImmutablesInterface.class);

        assertRandomlyPopulatedValues(value_1, value_2);
    }

    private <T> T populateAndAssertWithGeneratedCode(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER);
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(IMMUTABLES);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);
        assertGeneratedCode(value, populateConfig);

        return value;
    }

}
