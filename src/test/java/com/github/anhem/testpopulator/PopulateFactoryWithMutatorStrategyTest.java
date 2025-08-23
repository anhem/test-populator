package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.model.circular.A;
import com.github.anhem.testpopulator.model.immutables.ImmutableImmutablesAbstract;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.mutator.Mutator;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithConstructor;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithMultipleConstructors;
import com.github.anhem.testpopulator.model.java.setter.*;
import com.github.anhem.testpopulator.model.lombok.LombokImmutable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.config.BuilderPattern.LOMBOK;
import static com.github.anhem.testpopulator.config.ConstructorType.*;
import static com.github.anhem.testpopulator.config.Strategy.*;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.FAILED_TO_CREATE_OBJECT;
import static com.github.anhem.testpopulator.internal.populate.PopulatorExceptionMessages.NO_MATCHING_STRATEGY;
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
        String value1 = populateAndAssertWithGeneratedCode(String.class);
        String value2 = populateAndAssertWithGeneratedCode(String.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void pojo() {
        Pojo value1 = populateAndAssertWithGeneratedCode(Pojo.class);
        Pojo value2 = populateAndAssertWithGeneratedCode(Pojo.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void pojoExtendsPojoAbstract() {
        PojoExtendsPojoAbstract value1 = populateAndAssertWithGeneratedCode(PojoExtendsPojoAbstract.class);
        PojoExtendsPojoAbstract value2 = populateAndAssertWithGeneratedCode(PojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void PojoExtendsPojoExtendsPojoAbstract() {
        PojoExtendsPojoExtendsPojoAbstract value1 = populateAndAssertWithGeneratedCode(PojoExtendsPojoExtendsPojoAbstract.class);
        PojoExtendsPojoExtendsPojoAbstract value2 = populateAndAssertWithGeneratedCode(PojoExtendsPojoExtendsPojoAbstract.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void oddPojo() {
        OddPojo value1 = populateAndAssertWithGeneratedCode(OddPojo.class);
        OddPojo value2 = populateAndAssertWithGeneratedCode(OddPojo.class);
        assertRandomlyPopulatedValues(value1, value2);
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
        PojoWithCustomSetters value1 = populateAndAssertWithGeneratedCode(PojoWithCustomSetters.class);
        PojoWithCustomSetters value2 = populateAndAssertWithGeneratedCode(PojoWithCustomSetters.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void PojoWithBlankSetters() {
        populateFactory = new PopulateFactory(populateConfig);
        PojoWithCustomSetters value1 = populateAndAssertWithGeneratedCode(PojoWithCustomSetters.class);
        PojoWithCustomSetters value2 = populateAndAssertWithGeneratedCode(PojoWithCustomSetters.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void PojoWithMultipleCustomSetters() {
        populateFactory = new PopulateFactory(populateConfig);
        PojoWithMultipleCustomSetters value1 = populateAndAssertWithGeneratedCode(PojoWithMultipleCustomSetters.class);
        PojoWithMultipleCustomSetters value2 = populateAndAssertWithGeneratedCode(PojoWithMultipleCustomSetters.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void PojoWithMultipleCustomSettersUsingBlankSetter() {
        populateConfig = populateConfig.toBuilder()
                .setterPrefix("")
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        PojoWithMultipleCustomSetters value1 = populateAndAssertWithGeneratedCode(PojoWithMultipleCustomSetters.class);
        PojoWithMultipleCustomSetters value2 = populateAndAssertWithGeneratedCode(PojoWithMultipleCustomSetters.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void PojoPrivateConstructor() {
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(true)
                .objectFactoryEnabled(false)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        PojoPrivateConstructor value1 = populateAndAssert(PojoPrivateConstructor.class);
        PojoPrivateConstructor value2 = populateAndAssert(PojoPrivateConstructor.class);
        assertRandomlyPopulatedValues(value1, value2);
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
        Pojo value1 = populateAndAssertWithGeneratedCode(Pojo.class);
        Pojo value2 = populateAndAssertWithGeneratedCode(Pojo.class);

        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void circularDependencyCreatesObjectWhenNullOnCircularDependencyIsTrue() {
        populateConfig = populateConfig.toBuilder()
                .nullOnCircularDependency(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        A value1 = populateAndAssertWithGeneratedCode(A.class);
        A value2 = populateAndAssertWithGeneratedCode(A.class);

        assertCircularDependency(value1, value2);
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
        Pojo value1 = populateFactory.populate(clazz);
        Pojo value2 = populateFactory.populate(clazz);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void Mutator() {
        Mutator value1 = populateAndAssertWithGeneratedCode(Mutator.class);
        Mutator value2 = populateAndAssertWithGeneratedCode(Mutator.class);
        assertRandomlyPopulatedValues(value1, value2);
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
        Mutator value1 = populateFactory.populate(clazz);
        Mutator value2 = populateFactory.populate(clazz);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void MutatorWithConstructorUsingLargestConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(LARGEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithConstructor value1 = populateAndAssertWithGeneratedCode(MutatorWithConstructor.class);
        MutatorWithConstructor value2 = populateAndAssertWithGeneratedCode(MutatorWithConstructor.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void MutatorWithConstructorUsingSmallestConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(SMALLEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithConstructor value1 = populateAndAssertWithGeneratedCode(MutatorWithConstructor.class);
        MutatorWithConstructor value2 = populateAndAssertWithGeneratedCode(MutatorWithConstructor.class);
        assertRandomlyPopulatedValues(value1, value2);
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

        MutatorWithMultipleConstructors value1 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        MutatorWithMultipleConstructors value2 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void MutatorWithMultipleConstructorsUsingSmallestConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(SMALLEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithMultipleConstructors value1 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        MutatorWithMultipleConstructors value2 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        assertRandomlyPopulatedValues(value1, value2, "localDate");
    }

    @Test
    void MutatorWithMultipleConstructorsUsingNoArgsConstructorType() {
        populateConfig = populateConfig.toBuilder()
                .constructorType(NO_ARGS)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithMultipleConstructors value1 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        MutatorWithMultipleConstructors value2 = populateAndAssertWithGeneratedCode(MutatorWithMultipleConstructors.class);
        assertRandomlyPopulatedValues(value1, value2, "arbitraryEnum", "localDate");
    }

    @Test
    void LombokImmutableBuilder() {
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(true)
                .objectFactoryEnabled(false)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        LombokImmutable value1 = populateAndAssert(LombokImmutable.LombokImmutableBuilder.class).build();
        LombokImmutable value2 = populateAndAssert(LombokImmutable.LombokImmutableBuilder.class).build();
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void ImmutableImmutablesAbstract() {
        populateConfig = populateConfig.toBuilder()
                .accessNonPublicConstructors(true)
                .objectFactoryEnabled(false)
                .blacklistedMethods(List.of("from"))
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        ImmutableImmutablesAbstract value1 = populateAndAssert(ImmutableImmutablesAbstract.Builder.class).build();
        ImmutableImmutablesAbstract value2 = populateAndAssert(ImmutableImmutablesAbstract.Builder.class).build();
        assertRandomlyPopulatedValues(value1, value2);
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
