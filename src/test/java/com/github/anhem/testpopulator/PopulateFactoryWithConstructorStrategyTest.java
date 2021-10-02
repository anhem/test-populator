package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithConstructorStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(CONSTRUCTOR))
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
    void allArgsConstructor() {
        AllArgsConstructor value_1 = populateFactory.populate(AllArgsConstructor.class);
        AllArgsConstructor value_2 = populateFactory.populate(AllArgsConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void allArgsConstructorExtendsAllArgsConstructorAbstract() {
        AllArgsConstructorExtendsAllArgsConstructorAbstract value_1 = populateFactory.populate(AllArgsConstructorExtendsAllArgsConstructorAbstract.class);
        AllArgsConstructorExtendsAllArgsConstructorAbstract value_2 = populateFactory.populate(AllArgsConstructorExtendsAllArgsConstructorAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void oddAllArgsConstructor() {
        OddAllArgsConstructor value_1 = populateFactory.populate(OddAllArgsConstructor.class);
        OddAllArgsConstructor value_2 = populateFactory.populate(OddAllArgsConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void populatedWithPublicConstructor() {
        DifferentConstructorModifiers value_1 = populateFactory.populate(DifferentConstructorModifiers.class);
        DifferentConstructorModifiers value_2 = populateFactory.populate(DifferentConstructorModifiers.class);
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1).hasNoNullFieldsOrPropertiesExcept("privateConstructorField");
        assertThat(value_2).hasNoNullFieldsOrPropertiesExcept("privateConstructorField");
        assertThat(value_1).isNotEqualTo(value_2);
    }

    @Test
    void tryingToInstantiateAbstractClassThrowsException() {
        assertThatThrownBy(() -> populateFactory.populate(AllArgsConstructorAbstract.class))
                .isInstanceOf(PopulateException.class)
                .hasMessage(format(FAILED_TO_CREATE_OBJECT, AllArgsConstructorAbstract.class.getName(), CONSTRUCTOR));
    }

    @Test
    void pojo() {
        assertThatThrownBy(() -> populateFactory.populate(Pojo.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, Pojo.class.getName(), populateConfig.getStrategyOrder()));
    }
}
