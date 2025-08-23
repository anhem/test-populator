package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.model.circular.A;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.lombok.LombokImmutable;
import com.github.anhem.testpopulator.model.lombok.LombokImmutableExtendsLombokAbstractImmutable;
import com.github.anhem.testpopulator.model.lombok.LombokImmutableWithSingular;
import com.github.anhem.testpopulator.model.lombok.LombokOddImmutable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.internal.PopulatorExceptionMessages.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertCircularDependency;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithLombokBuilderStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(BUILDER))
                .builderPattern(LOMBOK)
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
    void LombokImmutable() {
        LombokImmutable value1 = populateAndAssertWithGeneratedCode(LombokImmutable.class);
        LombokImmutable value2 = populateAndAssertWithGeneratedCode(LombokImmutable.class);
        assertRandomlyPopulatedValues(value1, value2);
        assertThat(List.of(value1, value2)).allSatisfy(value -> {
            assertThat(value.getListOfStrings()).hasSize(1);
            assertThat(value.getMapOfStringsToIntegers()).hasSize(1);
            assertThat(value.getMapOfStringsToIntegers().values()).hasSize(1);
        });
        assertObjectCanBeRebuilt(value1);
        assertObjectCanBeRebuilt(value2);
    }

    @Test
    void LombokImmutableWithSingular() {
        LombokImmutableWithSingular value1 = populateAndAssertWithGeneratedCode(LombokImmutableWithSingular.class);
        LombokImmutableWithSingular value2 = populateAndAssertWithGeneratedCode(LombokImmutableWithSingular.class);
        assertRandomlyPopulatedValues(value1, value2);
        assertThat(List.of(value1, value2))
                .allSatisfy(value -> {
                    assertThat(value.getListOfStrings()).hasSize(1);
                    assertThat(value.getMapOfStringsToIntegers()).hasSize(1);
                    assertThat(value.getMapOfStringsToIntegers().values()).hasSize(1);
                    assertThat(value.getSetOfIntegers()).hasSize(1);
                });
    }

    @Test
    void LombokImmutableExtendsLombokAbstractImmutable() {
        LombokImmutableExtendsLombokAbstractImmutable value1 = populateAndAssertWithGeneratedCode(LombokImmutableExtendsLombokAbstractImmutable.class);
        LombokImmutableExtendsLombokAbstractImmutable value2 = populateAndAssertWithGeneratedCode(LombokImmutableExtendsLombokAbstractImmutable.class);
        assertRandomlyPopulatedValues(value1, value2);
        assertThat(value1.getListOfStrings()).hasSize(1);
    }

    @Test
    void allArgsConstructor() {
        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(AllArgsConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void LombokImmutableBuilder() {
        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(LombokImmutable.LombokImmutableBuilder.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, LombokImmutable.LombokImmutableBuilder.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void LombokOddImmutable() {
        LombokOddImmutable value1 = populateAndAssertWithGeneratedCode(LombokOddImmutable.class);
        LombokOddImmutable value2 = populateAndAssertWithGeneratedCode(LombokOddImmutable.class);
        assertRandomlyPopulatedValues(value1, value2);

        assertObjectCanBeRebuilt(value1);
        assertObjectCanBeRebuilt(value2);
    }

    @Test
    void createsObjectWhenNullOnCircularDependencyIsTrue() {
        populateConfig = populateConfig.toBuilder()
                .nullOnCircularDependency(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        LombokImmutable value1 = populateAndAssertWithGeneratedCode(LombokImmutable.class);
        LombokImmutable value2 = populateAndAssertWithGeneratedCode(LombokImmutable.class);

        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void circularDependencyCreatesObjectWhenNullOnCircularDependencyIsTrue() {
        populateConfig = populateConfig.toBuilder()
                .nullOnCircularDependency(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        A value1 = populateAndAssertWithGeneratedCode(A.class);
        A value2 = populateAndAssertWithGeneratedCode(A.class);

        assertCircularDependency(value1, value2);
    }

    @Test
    void circularDependencyThrowsExceptionWhenNullOnCircularDependencyIsFalse() {
        populateConfig = populateConfig.toBuilder()
                .objectFactoryEnabled(false)
                .nullOnCircularDependency(false)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        assertThatThrownBy(() -> populateFactory.populate(A.class)).isInstanceOfAny(PopulateException.class, StackOverflowError.class);
    }

    @Test
    void builderIsUsedWhenClassOnlySupportsBuilderAndOtherStrategiesAreAvailable() {
        Class<LombokImmutable> clazz = LombokImmutable.class;
        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(SETTER, CONSTRUCTOR))
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        assertThatThrownBy(() -> populateFactory.populate(clazz)).isInstanceOf(PopulateException.class);

        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(SETTER, CONSTRUCTOR, BUILDER))
                .builderPattern(LOMBOK)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        LombokImmutable value1 = populateFactory.populate(clazz);
        LombokImmutable value2 = populateFactory.populate(clazz);
        assertRandomlyPopulatedValues(value1, value2);
    }

    private void assertObjectCanBeRebuilt(LombokImmutable lombokImmutable) {
        assertThat(lombokImmutable.toBuilder().build()).isEqualTo(lombokImmutable);
    }

    private void assertObjectCanBeRebuilt(LombokOddImmutable lombokOddImmutable) {
        assertThat(lombokOddImmutable.toBuilder().build()).isEqualTo(lombokOddImmutable);
    }

    private <T> T populateAndAssertWithGeneratedCode(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER);
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(LOMBOK);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);
        assertGeneratedCode(value, populateConfig);

        return value;
    }
}
