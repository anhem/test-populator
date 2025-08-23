package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.model.immutables.*;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.config.BuilderPattern.IMMUTABLES;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.internal.PopulatorExceptionMessages.NO_MATCHING_STRATEGY;
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
        String value1 = populateAndAssertWithGeneratedCode(String.class);
        String value2 = populateAndAssertWithGeneratedCode(String.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void immutablesInterfaceImplementation() {
        ImmutableImmutablesInterface value1 = populateAndAssertWithGeneratedCode(ImmutableImmutablesInterface.class);
        ImmutableImmutablesInterface value2 = populateAndAssertWithGeneratedCode(ImmutableImmutablesInterface.class);
        assertRandomlyPopulatedValues(value1, value2);
        assertThat(value1.getListOfStrings()).hasSize(1);
        assertThat(value1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesInterface() {
        ImmutablesInterface value1 = populateAndAssertWithGeneratedCode(ImmutablesInterface.class);
        ImmutablesInterface value2 = populateAndAssertWithGeneratedCode(ImmutablesInterface.class);
        assertRandomlyPopulatedValues(value1, value2);
        assertThat(value1.getListOfStrings()).hasSize(1);
        assertThat(value1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesAbstractImplementation() {
        ImmutableImmutablesAbstract value1 = populateAndAssertWithGeneratedCode(ImmutableImmutablesAbstract.class);
        ImmutableImmutablesAbstract value2 = populateAndAssertWithGeneratedCode(ImmutableImmutablesAbstract.class);
        assertRandomlyPopulatedValues(value1, value2);
        assertThat(value1.getListOfStrings()).hasSize(1);
        assertThat(value1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesAbstract() {
        ImmutablesAbstract value1 = populateAndAssertWithGeneratedCode(ImmutablesAbstract.class);
        ImmutablesAbstract value2 = populateAndAssertWithGeneratedCode(ImmutablesAbstract.class);
        assertRandomlyPopulatedValues(value1, value2);
        assertThat(value1.getListOfStrings()).hasSize(1);
        assertThat(value1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value1.getMapOfStringsToIntegers().values()).hasSize(1);
    }

    @Test
    void immutablesOddInterface() {
        ImmutablesOddInterface value1 = populateAndAssertWithGeneratedCode(ImmutablesOddInterface.class);
        ImmutablesOddInterface value2 = populateAndAssertWithGeneratedCode(ImmutablesOddInterface.class);
        assertRandomlyPopulatedValues(value1, value2);
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
        ImmutableImmutablesInterface value1 = populateFactory.populate(ImmutableImmutablesInterface.class);
        ImmutableImmutablesInterface value2 = populateFactory.populate(ImmutableImmutablesInterface.class);

        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void builderIsUsedWhenClassOnlySupportsBuilderAndOtherStrategiesAreAvailable() {
        Class<ImmutableImmutablesInterface> clazz = ImmutableImmutablesInterface.class;
        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(SETTER, CONSTRUCTOR))
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        assertThatThrownBy(() -> populateFactory.populate(clazz)).isInstanceOf(PopulateException.class);

        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(SETTER, CONSTRUCTOR, BUILDER))
                .builderPattern(IMMUTABLES)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        ImmutableImmutablesInterface value1 = populateFactory.populate(clazz);
        ImmutableImmutablesInterface value2 = populateFactory.populate(clazz);
        assertRandomlyPopulatedValues(value1, value2);
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
