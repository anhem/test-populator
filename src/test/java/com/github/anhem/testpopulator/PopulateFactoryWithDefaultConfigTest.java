package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.model.java.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.testutil.AssertUtil.assertRandomlyPopulatedValues;

class PopulateFactoryWithDefaultConfigTest {

    PopulateFactory populateFactory;

    @BeforeEach
    public void setUp() {
        populateFactory = new PopulateFactory();
    }

    @Test
    public void string() {
        String value_1 = populateFactory.populate(String.class);
        String value_2 = populateFactory.populate(String.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    public void integer() {
        Integer value_1 = populateFactory.populate(Integer.class);
        Integer value_2 = populateFactory.populate(Integer.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    public void pojo() {
        Pojo value_1 = populateFactory.populate(Pojo.class);
        Pojo value_2 = populateFactory.populate(Pojo.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    public void allArgsConstructor() {
        AllArgsConstructor value_1 = populateFactory.populate(AllArgsConstructor.class);
        AllArgsConstructor value_2 = populateFactory.populate(AllArgsConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    public void pojoExtendsPojoAbstract() {
        PojoExtendsPojoAbstract value_1 = populateFactory.populate(PojoExtendsPojoAbstract.class);
        PojoExtendsPojoAbstract value_2 = populateFactory.populate(PojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    public void PojoExtendsPojoExtendsPojoAbstract() {
        PojoExtendsPojoExtendsPojoAbstract value_1 = populateFactory.populate(PojoExtendsPojoExtendsPojoAbstract.class);
        PojoExtendsPojoExtendsPojoAbstract value_2 = populateFactory.populate(PojoExtendsPojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    public void allArgsConstructorExtendsAllArgsConstructorAbstract() {
        AllArgsConstructorExtendsAllArgsConstructorAbstract value_1 = populateFactory.populate(AllArgsConstructorExtendsAllArgsConstructorAbstract.class);
        AllArgsConstructorExtendsAllArgsConstructorAbstract value_2 = populateFactory.populate(AllArgsConstructorExtendsAllArgsConstructorAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    public void oddPojo() {
        OddPojo value_1 = populateFactory.populate(OddPojo.class);
        OddPojo value_2 = populateFactory.populate(OddPojo.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    public void oddAllArgsConstructor() {
        OddAllArgsConstructor value_1 = populateFactory.populate(OddAllArgsConstructor.class);
        OddAllArgsConstructor value_2 = populateFactory.populate(OddAllArgsConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }
}
