package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.model.java.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.PopulateFactory.FAILED_TO_CREATE_INSTANCE;
import static com.github.anhem.testpopulator.testutil.AssertUtil.assertRandomlyPopulatedValues;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithDefaultConfigTest {

    PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateFactory = new PopulateFactory();
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
    void allArgsConstructor() {
        AllArgsConstructor value_1 = populateFactory.populate(AllArgsConstructor.class);
        AllArgsConstructor value_2 = populateFactory.populate(AllArgsConstructor.class);
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
    void allArgsConstructorExtendsAllArgsConstructorAbstract() {
        AllArgsConstructorExtendsAllArgsConstructorAbstract value_1 = populateFactory.populate(AllArgsConstructorExtendsAllArgsConstructorAbstract.class);
        AllArgsConstructorExtendsAllArgsConstructorAbstract value_2 = populateFactory.populate(AllArgsConstructorExtendsAllArgsConstructorAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void oddPojo() {
        OddPojo value_1 = populateFactory.populate(OddPojo.class);
        OddPojo value_2 = populateFactory.populate(OddPojo.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void oddAllArgsConstructor() {
        OddAllArgsConstructor value_1 = populateFactory.populate(OddAllArgsConstructor.class);
        OddAllArgsConstructor value_2 = populateFactory.populate(OddAllArgsConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    public void tryingToInstantiateAbstractClassThrowsException() {
        assertThatThrownBy(() -> populateFactory.populate(AllArgsConstructorAbstract.class))
                .isInstanceOf(PopulateException.class)
                .hasMessageContaining(format(FAILED_TO_CREATE_INSTANCE, ""));

        assertThatThrownBy(() -> populateFactory.populate(PojoAbstract.class))
                .isInstanceOf(PopulateException.class)
                .hasMessageContaining(format(FAILED_TO_CREATE_INSTANCE, ""));
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
}
