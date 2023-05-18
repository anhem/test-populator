package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.AllArgsConstructor;
import com.github.anhem.testpopulator.model.lombok.LombokImmutable;
import com.github.anhem.testpopulator.model.lombok.LombokImmutableExtendsLombokAbstractImmutable;
import com.github.anhem.testpopulator.model.lombok.LombokImmutableWithSingular;
import com.github.anhem.testpopulator.model.lombok.LombokOddImmutable;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.Strategy.BUILDER;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PopulateFactoryWithLombokBuilderStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(BUILDER))
                .builderPattern(BuilderPattern.LOMBOK)
                .objectFactoryEnabled(true)
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
    void LombokImmutable() {
        LombokImmutable value_1 = populateFactory.populate(LombokImmutable.class);
        LombokImmutable value_2 = populateFactory.populate(LombokImmutable.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
        assertObjectCanBeRebuilt(value_1);
        assertObjectCanBeRebuilt(value_2);
    }

    @Test
    void LombokImmutableWithSingular() {
        LombokImmutableWithSingular value_1 = populateFactory.populate(LombokImmutableWithSingular.class);
        LombokImmutableWithSingular value_2 = populateFactory.populate(LombokImmutableWithSingular.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers()).hasSize(1);
        assertThat(value_1.getMapOfStringsToIntegers().values()).hasSize(1);
        assertThat(value_1.getSetOfIntegers()).hasSize(1);
    }

    @Test
    void LombokImmutableExtendsLombokAbstractImmutable() {
        LombokImmutableExtendsLombokAbstractImmutable value_1 = populateFactory.populate(LombokImmutableExtendsLombokAbstractImmutable.class);
        LombokImmutableExtendsLombokAbstractImmutable value_2 = populateFactory.populate(LombokImmutableExtendsLombokAbstractImmutable.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
    }

    @Test
    void allArgsConstructor() {
        assertThatThrownBy(() -> populateFactory.populate(AllArgsConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void LombokImmutableBuilder() {
        assertThatThrownBy(() -> populateFactory.populate(LombokImmutable.LombokImmutableBuilder.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, LombokImmutable.LombokImmutableBuilder.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void LombokOddImmutable() {
        LombokOddImmutable value_1 = populateFactory.populate(LombokOddImmutable.class);
        LombokOddImmutable value_2 = populateFactory.populate(LombokOddImmutable.class);
        assertRandomlyPopulatedValues(value_1, value_2);

        assertObjectCanBeRebuilt(value_1);
        assertObjectCanBeRebuilt(value_2);
    }

    private ObjectAssert<LombokImmutable> assertObjectCanBeRebuilt(LombokImmutable lombokImmutable) {
        return assertThat(lombokImmutable.toBuilder().build()).isEqualTo(lombokImmutable);
    }

    private ObjectAssert<LombokOddImmutable> assertObjectCanBeRebuilt(LombokOddImmutable lombokOddImmutable) {
        return assertThat(lombokOddImmutable.toBuilder().build()).isEqualTo(lombokOddImmutable);
    }
}
