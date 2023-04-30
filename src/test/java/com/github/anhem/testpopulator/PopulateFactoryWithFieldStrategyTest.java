package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.*;
import com.github.anhem.testpopulator.model.lombok.LombokImmutable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.Strategy.FIELD;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithFieldStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(FIELD))
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
    void tryingToInstantiateAbstractClassThrowsException() {
        assertThatThrownBy(() -> populateFactory.populate(PojoAbstract.class))
                .isInstanceOf(PopulateException.class)
                .hasMessage(format(FAILED_TO_CREATE_OBJECT, PojoAbstract.class.getName(), FIELD));
    }

    @Test
    void allArgsConstructor() {
        assertThatThrownBy(() -> populateFactory.populate(AllArgsConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void PojoPrivateConstructor() {
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        PojoPrivateConstructor value_1 = populateFactory.populate(PojoPrivateConstructor.class);
        PojoPrivateConstructor value_2 = populateFactory.populate(PojoPrivateConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void tryingToAccessPrivateConstructorThrowsException() {
        assertThatThrownBy(() -> populateFactory.populate(PojoPrivateConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, PojoPrivateConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void lombokBuilderClass() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(FIELD))
                .accessNonPublicConstructors(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        LombokImmutable value_1 = populateFactory.populate(LombokImmutable.LombokImmutableBuilder.class).build();
        LombokImmutable value_2 = populateFactory.populate(LombokImmutable.LombokImmutableBuilder.class).build();
        assertRandomlyPopulatedValues(value_1, value_2);
    }
}
