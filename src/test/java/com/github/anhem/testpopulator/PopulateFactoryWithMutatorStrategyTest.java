package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.model.circular.A;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.mutator.Mutator;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithConstructor;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithMultipleConstructors;
import com.github.anhem.testpopulator.model.java.setter.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.PopulateFactory.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.PopulateFactory.NO_MATCHING_STRATEGY;
import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.ConstructorType.*;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertCircularDependency;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithMutatorStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(MUTATOR))
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
    void pojo() {
        Pojo value_1 = populateAndAssertWithGeneratedCode(Pojo.class);
        Pojo value_2 = populateAndAssertWithGeneratedCode(Pojo.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void pojoExtendsPojoAbstract() {
        PojoExtendsPojoAbstract value_1 = populateAndAssertWithGeneratedCode(PojoExtendsPojoAbstract.class);
        PojoExtendsPojoAbstract value_2 = populateAndAssertWithGeneratedCode(PojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void PojoExtendsPojoExtendsPojoAbstract() {
        PojoExtendsPojoExtendsPojoAbstract value_1 = populateAndAssertWithGeneratedCode(PojoExtendsPojoExtendsPojoAbstract.class);
        PojoExtendsPojoExtendsPojoAbstract value_2 = populateAndAssertWithGeneratedCode(PojoExtendsPojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void oddPojo() {
        OddPojo value_1 = populateAndAssertWithGeneratedCode(OddPojo.class);
        OddPojo value_2 = populateAndAssertWithGeneratedCode(OddPojo.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void tryingToInstantiateAbstractClassThrowsException() {
        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(PojoAbstract.class))
                .isInstanceOf(PopulateException.class)
                .hasMessage(format(FAILED_TO_CREATE_OBJECT, PojoAbstract.class.getName(), MUTATOR));
    }

    @Test
    void allArgsConstructor() {
        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(AllArgsConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, AllArgsConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void PojoWithCustomSetters() {
        populateFactory = new PopulateFactory(populateConfig);
        PojoWithCustomSetters value_1 = populateAndAssertWithGeneratedCode(PojoWithCustomSetters.class);
        PojoWithCustomSetters value_2 = populateAndAssertWithGeneratedCode(PojoWithCustomSetters.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void PojoWithBlankSetters() {
        populateFactory = new PopulateFactory(populateConfig);
        PojoWithCustomSetters value_1 = populateAndAssertWithGeneratedCode(PojoWithCustomSetters.class);
        PojoWithCustomSetters value_2 = populateAndAssertWithGeneratedCode(PojoWithCustomSetters.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void PojoWithMultipleCustomSetters() {
        populateFactory = new PopulateFactory(populateConfig);
        PojoWithMultipleCustomSetters value_1 = populateAndAssertWithGeneratedCode(PojoWithMultipleCustomSetters.class);
        PojoWithMultipleCustomSetters value_2 = populateAndAssertWithGeneratedCode(PojoWithMultipleCustomSetters.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void PojoWithMultipleCustomSettersUsingBlankSetter() {
        populateConfig = populateConfig.toBuilder()
                .setterPrefix("")
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        PojoWithMultipleCustomSetters value_1 = populateAndAssertWithGeneratedCode(PojoWithMultipleCustomSetters.class);
        PojoWithMultipleCustomSetters value_2 = populateAndAssertWithGeneratedCode(PojoWithMultipleCustomSetters.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void PojoPrivateConstructor() {
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(true)
                .objectFactoryEnabled(false)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        PojoPrivateConstructor value_1 = populateAndAssert(PojoPrivateConstructor.class);
        PojoPrivateConstructor value_2 = populateAndAssert(PojoPrivateConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void tryingToAccessPrivateConstructorThrowsException() {
        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(PojoPrivateConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, PojoPrivateConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void createsObjectWhenNullOnCircularDependencyIsTrue() {
        populateConfig = populateConfig.toBuilder()
                .nullOnCircularDependency(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        Pojo value_1 = populateAndAssertWithGeneratedCode(Pojo.class);
        Pojo value_2 = populateAndAssertWithGeneratedCode(Pojo.class);

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
    void setterIsUsedWhenClassOnlySupportsSetterAndOtherStrategiesAreAvailable() {
        Class<Pojo> clazz = Pojo.class;
        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(BUILDER, CONSTRUCTOR))
                .builderPattern(LOMBOK)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        assertThatThrownBy(() -> populateFactory.populate(clazz)).isInstanceOf(PopulateException.class);

        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(BUILDER, CONSTRUCTOR, MUTATOR))
                .builderPattern(LOMBOK)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        Pojo value_1 = populateFactory.populate(clazz);
        Pojo value_2 = populateFactory.populate(clazz);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void Mutator() {
        Mutator value_1 = populateAndAssertWithGeneratedCode(Mutator.class);
        Mutator value_2 = populateAndAssertWithGeneratedCode(Mutator.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void mutatorIsUsedWhenClassOnlyMutatorAndOtherStrategiesAreAvailable() {
        Class<Mutator> clazz = Mutator.class;
        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(BUILDER, CONSTRUCTOR, SETTER))
                .builderPattern(LOMBOK)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        assertThatThrownBy(() -> populateFactory.populate(clazz)).isInstanceOf(PopulateException.class);

        populateConfig = populateConfig.toBuilder()
                .strategyOrder(List.of(BUILDER, CONSTRUCTOR, SETTER, MUTATOR))
                .builderPattern(LOMBOK)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        Mutator value_1 = populateFactory.populate(clazz);
        Mutator value_2 = populateFactory.populate(clazz);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void MutatorWithConstructorUsingLargestConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(LARGEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithConstructor value_1 = populateAndAssertWithGeneratedCode(MutatorWithConstructor.class);
        MutatorWithConstructor value_2 = populateAndAssertWithGeneratedCode(MutatorWithConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void MutatorWithConstructorUsingSmallestConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(SMALLEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithConstructor value_1 = populateAndAssertWithGeneratedCode(MutatorWithConstructor.class);
        MutatorWithConstructor value_2 = populateAndAssertWithGeneratedCode(MutatorWithConstructor.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void MutatorWithConstructorUsingDefaultConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(NO_ARGS)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        assertThatThrownBy(() -> populateAndAssertWithGeneratedCode(MutatorWithConstructor.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(NO_MATCHING_STRATEGY, MutatorWithConstructor.class.getName(), populateConfig.getStrategyOrder()));
    }

    @Test
    void MutatorWithMultipleConstructorsUsingLargestConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(LARGEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithMultipleConstructors value_1 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        MutatorWithMultipleConstructors value_2 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void MutatorWithMultipleConstructorsUsingSmallestConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(SMALLEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithMultipleConstructors value_1 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        MutatorWithMultipleConstructors value_2 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        assertRandomlyPopulatedValues(value_1, value_2, "localDate");
    }

    @Test
    void MutatorWithMultipleConstructorsUsingNoArgsConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(NO_ARGS)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithMultipleConstructors value_1 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        MutatorWithMultipleConstructors value_2 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        assertRandomlyPopulatedValues(value_1, value_2, "arbitraryEnum", "localDate");
    }

    private <T> T populateAndAssertWithGeneratedCode(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(MUTATOR);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);
        assertGeneratedCode(value, populateConfig);

        return value;
    }

    private <T> T populateAndAssert(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isFalse();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(MUTATOR);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);

        return value;
    }
}
