package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.ConstructorType;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.mutator.Mutator;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithBothSettersAndFields;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithMultipleConstructors;
import com.github.anhem.testpopulator.model.java.mutator.Vector;
import com.github.anhem.testpopulator.model.java.setter.PojoPrivateConstructor;
import com.github.anhem.testpopulator.model.kotlin.TargetPositionComponent;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static com.github.anhem.testpopulator.config.ConstructorType.*;
import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.config.Strategy.MUTATOR;
import static com.github.anhem.testpopulator.internal.util.MutatorUtil.*;
import static com.github.anhem.testpopulator.internal.util.PopulateUtil.NO_CONSTRUCTOR_FOUND;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MutatorUtilTest {

    @Test
    void isMatchingMutatorStrategyReturnsTrue() {
        assertThat(isMatchingMutatorStrategy(MUTATOR, Mutator.class, false, NO_ARGS)).isTrue();
        assertThat(isMatchingMutatorStrategy(MUTATOR, Vector.class, false, NO_ARGS)).isTrue();
        assertThat(isMatchingMutatorStrategy(MUTATOR, TargetPositionComponent.class, false, NO_ARGS)).isTrue();
    }

    @Test
    void getMutatorGettersReturnsGettersForClass() {
        List<Method> getters = getMutatorGetters(TargetPositionComponent.class, emptyList(), emptySet());
        assertThat(getters).extracting(Method::getName).containsExactly("getTarget");
    }

    @Test
    void getMutatorGettersForObjectReturnsMutatorGetters() {
        TargetPositionComponent component = new TargetPositionComponent();
        List<MutatorGetter> getters = getMutatorGetters(component, TargetPositionComponent.class, emptyList(), PopulateConfig.builder().build());
        assertThat(getters).hasSize(1);
        assertThat(getters.get(0).getGetter().getName()).isEqualTo("getTarget");
        assertThat(getters.get(0).getExpectedChildren()).isEqualTo(2);
    }

    @Test
    void isMatchingMutatorStrategyReturnsFalse() {
        assertThat(isMatchingMutatorStrategy(CONSTRUCTOR, AllArgsConstructor.class, false, ConstructorType.LARGEST)).isFalse();
        assertThat(isMatchingMutatorStrategy(CONSTRUCTOR, AllArgsConstructor.class, false, ConstructorType.SMALLEST)).isFalse();
        assertThat(isMatchingMutatorStrategy(CONSTRUCTOR, AllArgsConstructor.class, false, NO_ARGS)).isFalse();
        assertThat(isMatchingMutatorStrategy(MUTATOR, AllArgsConstructor.class, false, NO_ARGS)).isFalse();
    }

    @Test
    void getMutatorMethodsReturnsMethods() {
        assertThat(getMutatorMethods(Mutator.class, emptySet())).hasSize(8);
    }

    @Test
    void getPublicFieldsReturnsMutableFields() {
        List<Field> fields = getPublicFields(Vector.class, emptyList(), emptySet());
        assertThat(fields).extracting(Field::getName).containsExactly("x", "y");
    }

    @Test
    void getPublicFieldsIgnoresStaticFinalAndBlacklisted() {
        List<Field> fields = getPublicFields(Vector.class, emptyList(), Set.of("x"));
        assertThat(fields).extracting(Field::getName).containsExactly("y");
    }

    @Test
    void getPublicFieldsFiltersPropertiesMatchingMutatorMethods() {
        List<Field> fields = getPublicFields(MutatorWithBothSettersAndFields.class, getMutatorMethods(MutatorWithBothSettersAndFields.class, emptySet()), emptySet());
        assertThat(fields).extracting(Field::getName).containsExactly("y");
    }

    @Test
    void MutatorWithMultipleConstructorsReturnsNoArgsConstructor() {
        assertThat(getConstructor(MutatorWithMultipleConstructors.class, false, NO_ARGS).getParameterCount()).isZero();
        assertThat(getConstructor(Mutator.class, false, NO_ARGS).getParameterCount()).isZero();
        assertThat(getConstructor(PojoPrivateConstructor.class, true, NO_ARGS).getParameterCount()).isZero();
        assertThatThrownBy(() -> getConstructor(PojoPrivateConstructor.class, false, NO_ARGS))
                .isInstanceOf(PopulateException.class)
                .hasMessage(String.format(NO_CONSTRUCTOR_FOUND, PojoPrivateConstructor.class.getName()));
    }

    @Test
    void MutatorWithMultipleConstructorsReturnsLargestConstructor() {
        assertThat(getConstructor(MutatorWithMultipleConstructors.class, false, LARGEST).getParameterCount()).isEqualTo(11);
        assertThat(getConstructor(Mutator.class, false, LARGEST).getParameterCount()).isZero();
        assertThat(getConstructor(PojoPrivateConstructor.class, true, LARGEST).getParameterCount()).isZero();
        assertThatThrownBy(() -> getConstructor(PojoPrivateConstructor.class, false, LARGEST))
                .isInstanceOf(PopulateException.class)
                .hasMessage(String.format(NO_CONSTRUCTOR_FOUND, PojoPrivateConstructor.class.getName()));
    }

    @Test
    void MutatorWithMultipleConstructorsReturnsSmallestConstructor() {
        assertThat(getConstructor(MutatorWithMultipleConstructors.class, false, SMALLEST).getParameterCount()).isEqualTo(1);
        assertThat(getConstructor(Mutator.class, false, SMALLEST).getParameterCount()).isZero();
        assertThat(getConstructor(PojoPrivateConstructor.class, true, SMALLEST).getParameterCount()).isZero();
        assertThatThrownBy(() -> getConstructor(PojoPrivateConstructor.class, false, SMALLEST))
                .isInstanceOf(PopulateException.class)
                .hasMessage(String.format(NO_CONSTRUCTOR_FOUND, PojoPrivateConstructor.class.getName()));
    }

}