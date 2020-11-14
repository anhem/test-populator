package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.Strategy.SETTER;
import static com.github.anhem.testpopulator.testutil.AssertUtil.assertRandomlyPopulatedValues;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithSetterStrategyTest {

    PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(SETTER))
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
    void integer() {
        Integer value_1 = populateFactory.populate(Integer.class);
        Integer value_2 = populateFactory.populate(Integer.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void pojo() {
        Pojo value_1 = populateFactory.populate(Pojo.class);
        Pojo value_2 = populateFactory.populate(Pojo.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void pojoExtendsPojoAbstract() {
        PojoExtendsPojoAbstract value_1 = populateFactory.populate(PojoExtendsPojoAbstract.class);
        PojoExtendsPojoAbstract value_2 = populateFactory.populate(PojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void PojoExtendsPojoExtendsPojoAbstract() {
        PojoExtendsPojoExtendsPojoAbstract value_1 = populateFactory.populate(PojoExtendsPojoExtendsPojoAbstract.class);
        PojoExtendsPojoExtendsPojoAbstract value_2 = populateFactory.populate(PojoExtendsPojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void oddPojo() {
        OddPojo value_1 = populateFactory.populate(OddPojo.class);
        OddPojo value_2 = populateFactory.populate(OddPojo.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void allArgsConstructor() {
        assertThatThrownBy(() -> populateFactory.populate(AllArgsConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructor.class.getName()));
    }

}
