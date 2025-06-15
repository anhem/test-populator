package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.model.circular.A;
import com.github.anhem.testpopulator.model.java.constructor.*;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertCircularDependency;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithConstructorTypeStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(CONSTRUCTOR))
                .objectFactoryEnabled(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void string() {
        String value_1 = populateAndAssertWithGeneratedCode(String.class);
        String value_2 = populateAndAssertWithGeneratedCode(String.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void allArgsConstructor() {
        AllArgsConstructor value_1 = populateAndAssertWithGeneratedCode(AllArgsConstructor.class);
        AllArgsConstructor value_2 = populateAndAssertWithGeneratedCode(AllArgsConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void allArgsConstructorExtendsAllArgsConstructorAbstract() {
        AllArgsConstructorExtendsAllArgsConstructorAbstract value_1 = populateAndAssertWithGeneratedCode(AllArgsConstructorExtendsAllArgsConstructorAbstract.class);
        AllArgsConstructorExtendsAllArgsConstructorAbstract value_2 = populateAndAssertWithGeneratedCode(AllArgsConstructorExtendsAllArgsConstructorAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void oddAllArgsConstructor() {
        OddAllArgsConstructor value_1 = populateAndAssertWithGeneratedCode(OddAllArgsConstructor.class);
        OddAllArgsConstructor value_2 = populateAndAssertWithGeneratedCode(OddAllArgsConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void populatedWithPublicConstructor() {
        DifferentConstructorModifiers value_1 = populateAndAssertWithGeneratedCode(DifferentConstructorModifiers.class);
        DifferentConstructorModifiers value_2 = populateAndAssertWithGeneratedCode(DifferentConstructorModifiers.class);
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1).hasNoNullFieldsOrPropertiesExcept("privateConstructorField");
        assertThat(value_2).hasNoNullFieldsOrPropertiesExcept("privateConstructorField");
        assertThat(value_1).isNotEqualTo(value_2);
    }

    @Test
    void tryingToInstantiateAbstractClassThrowsException() {
        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(AllArgsConstructorAbstract.class))
                .isInstanceOf(PopulateException.class)
                .hasMessage(format(FAILED_TO_CREATE_OBJECT, AllArgsConstructorAbstract.class.getName(), CONSTRUCTOR));
    }

    @Test
    void populatingWithNonMatchingStrategyThrowsException() {
        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(Pojo.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, Pojo.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void allArgsConstructorPrivate() {
        Class<AllArgsConstructorPrivate> clazz = AllArgsConstructorPrivate.class;
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(false)
                .objectFactoryEnabled(false)
                .build();
        assertThatThrownBy(() -> populateFactory.populate(clazz)).isInstanceOf(PopulateException.class);
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        AllArgsConstructorPrivate value_1 = populateAndAssert(clazz);
        AllArgsConstructorPrivate value_2 = populateAndAssert(clazz);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void allArgsConstructorProtected() {
        Class<AllArgsConstructorProtected> clazz = AllArgsConstructorProtected.class;
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(false)
                .objectFactoryEnabled(false)
                .build();
        assertThatThrownBy(() -> populateFactory.populate(clazz)).isInstanceOf(PopulateException.class);

        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        AllArgsConstructorProtected value_1 = populateAndAssert(clazz);
        AllArgsConstructorProtected value_2 = populateAndAssert(clazz);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void tryingToAccessPrivateConstructorThrowsException() {
        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(AllArgsConstructorPrivate.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructorPrivate.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void nestedCollections() {
        NestedCollections value_1 = populateAndAssertWithGeneratedCode(NestedCollections.class);
        NestedCollections value_2 = populateAndAssertWithGeneratedCode(NestedCollections.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void createsObjectWhenNullOnCircularDependencyIsTrue() {
        populateConfig = populateConfig.toBuilder()
                .nullOnCircularDependency(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        AllArgsConstructor value_1 = populateAndAssertWithGeneratedCode(AllArgsConstructor.class);
        AllArgsConstructor value_2 = populateAndAssertWithGeneratedCode(AllArgsConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void circularDependencyCreatesObjectWhenNullOnCircularDependencyIsTrue() {
        populateConfig = populateConfig.toBuilder()
                .nullOnCircularDependency(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        A value_1 = populateAndAssertWithGeneratedCode(A.class);
        A value_2 = populateAndAssertWithGeneratedCode(A.class);
        assertCircularDependency(value_1, value_2);
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
    void constructorIsUsedWhenClassOnlySupportsConstructorAndOtherStrategiesAreAvailable() {
        Class<AllArgsConstructor> clazz = AllArgsConstructor.class;
        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(BUILDER, SETTER))
                .builderPattern(LOMBOK)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        assertThatThrownBy(() -> populateFactory.populate(clazz)).isInstanceOf(PopulateException.class);

        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(BUILDER, SETTER, CONSTRUCTOR))
                .builderPattern(LOMBOK)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        AllArgsConstructor value_1 = populateFactory.populate(clazz);
        AllArgsConstructor value_2 = populateFactory.populate(clazz);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void allArgsConstructorDateAndTimeMix() {
        AllArgsConstructorDateAndTimeMix value_1 = populateAndAssertWithGeneratedCode(AllArgsConstructorDateAndTimeMix.class);
        AllArgsConstructorDateAndTimeMix value_2 = populateAndAssertWithGeneratedCode(AllArgsConstructorDateAndTimeMix.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    private <T> T populateAndAssertWithGeneratedCode(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(CONSTRUCTOR);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);
        assertGeneratedCode(value, populateConfig);

        return value;
    }

    private <T> T populateAndAssert(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isFalse();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(CONSTRUCTOR);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);

        return value;
    }
}
