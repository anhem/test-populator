package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.Pojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.testutil.AssertUtil.assertRandomlyPopulatedValues;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithConstructorStrategyTest {

    PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(CONSTRUCTOR))
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void allArgsConstructor() {
        AllArgsConstructor value_1 = populateFactory.populate(AllArgsConstructor.class);
        AllArgsConstructor value_2 = populateFactory.populate(AllArgsConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void pojo() {
        assertThatThrownBy(() -> populateFactory.populate(Pojo.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, Pojo.class.getName()));
    }
}
