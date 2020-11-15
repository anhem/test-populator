package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.AllArgsConstructor;
import com.github.anhem.testpopulator.model.lombok.Lombok;
import com.github.anhem.testpopulator.model.lombok.LombokWithSingular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.Strategy.LOMBOK_BUILDER;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithLombokBuilderStrategyTest {

    PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(LOMBOK_BUILDER))
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
    void LombokBuilder() {
        Lombok value_1 = populateFactory.populate(Lombok.class);
        Lombok value_2 = populateFactory.populate(Lombok.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
    }

    @Test
    void LombokBuilderWithSingular() {
        LombokWithSingular value_1 = populateFactory.populate(LombokWithSingular.class);
        LombokWithSingular value_2 = populateFactory.populate(LombokWithSingular.class);
        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getListOfStrings()).hasSize(1);
        assertThat(value_1.getSetOfIntegers()).hasSize(1);
    }

    @Test
    void allArgsConstructor() {
        assertThatThrownBy(() -> populateFactory.populate(AllArgsConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructor.class.getName()));
    }
}
