package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.circular.A;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.setter.*;
import com.github.anhem.testpopulator.model.lombok.LombokImmutable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.Strategy.FIELD;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertCircularDependency;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithFieldStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(FIELD))
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void string() {
        String value_1 = populateAndAssert(String.class);
        String value_2 = populateAndAssert(String.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void pojo() {
        Pojo value_1 = populateAndAssert(Pojo.class);
        Pojo value_2 = populateAndAssert(Pojo.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void pojoExtendsPojoAbstract() {
        PojoExtendsPojoAbstract value_1 = populateAndAssert(PojoExtendsPojoAbstract.class);
        PojoExtendsPojoAbstract value_2 = populateAndAssert(PojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void PojoExtendsPojoExtendsPojoAbstract() {
        PojoExtendsPojoExtendsPojoAbstract value_1 = populateAndAssert(PojoExtendsPojoExtendsPojoAbstract.class);
        PojoExtendsPojoExtendsPojoAbstract value_2 = populateAndAssert(PojoExtendsPojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void oddPojo() {
        OddPojo value_1 = populateAndAssert(OddPojo.class);
        OddPojo value_2 = populateAndAssert(OddPojo.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void tryingToInstantiateAbstractClassThrowsException() {
        assertThatThrownBy(() -> populateAndAssert(PojoAbstract.class))
                .isInstanceOf(PopulateException.class)
                .hasMessage(format(FAILED_TO_CREATE_OBJECT, PojoAbstract.class.getName(), FIELD));
    }

    @Test
    void populatingWithNonMatchingStrategyThrowsException() {
        assertThatThrownBy(() -> populateAndAssert(AllArgsConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void PojoPrivateConstructor() {
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        PojoPrivateConstructor value_1 = populateAndAssert(PojoPrivateConstructor.class);
        PojoPrivateConstructor value_2 = populateAndAssert(PojoPrivateConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void tryingToAccessPrivateConstructorThrowsException() {
        assertThatThrownBy(() -> populateAndAssert(PojoPrivateConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, PojoPrivateConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void lombokBuilderClass() {
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        LombokImmutable value_1 = populateAndAssert(LombokImmutable.LombokImmutableBuilder.class).build();
        LombokImmutable value_2 = populateAndAssert(LombokImmutable.LombokImmutableBuilder.class).build();
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void createsObjectWhenNullOnCircularDependencyIsTrue() {
        populateConfig = populateConfig.toBuilder()
                .nullOnCircularDependency(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        Pojo value_1 = populateFactory.populate(Pojo.class);
        Pojo value_2 = populateFactory.populate(Pojo.class);

        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void circularDependencyCreatesObjectWhenNullOnCircularDependencyIsTrue() {
        populateConfig = populateConfig.toBuilder()
                .nullOnCircularDependency(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        A value_1 = getPopulate();
        A value_2 = getPopulate();

        assertCircularDependency(value_1, value_2);
    }

    @Test
    void circularDependencyThrowsExceptionWhenNullOnCircularDependencyIsFalse() {
        assertThatThrownBy(() -> populateFactory.populate(A.class)).isInstanceOfAny(PopulateException.class, StackOverflowError.class);
    }

    private A getPopulate() {
        return populateAndAssert(A.class);
    }

    private <T> T populateAndAssert(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isFalse();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(FIELD);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);

        return value;
    }
}
