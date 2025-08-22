package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.ConstructorType;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.mutator.Mutator;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithMultipleConstructors;
import com.github.anhem.testpopulator.model.java.setter.PojoPrivateConstructor;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.config.ConstructorType.*;
import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.config.Strategy.MUTATOR;
import static com.github.anhem.testpopulator.internal.util.MutatorUtil.*;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MutatorUtilTest {

    @Test
    void isMatchingMutatorStrategyReturnsTrue() {
        assertThat(isMatchingMutatorStrategy(MUTATOR, Mutator.class, false, NO_ARGS)).isTrue();
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
        assertThat(getMutatorMethods(Mutator.class, emptyList())).hasSize(8);
    }

    @Test
    void MutatorWithMultipleConstructorsReturnsNoArgsConstructor() {
        assertThat(getConstructor(MutatorWithMultipleConstructors.class, false, NO_ARGS).getParameterCount()).isEqualTo(0);
        assertThat(getConstructor(Mutator.class, false, NO_ARGS).getParameterCount()).isEqualTo(0);
        assertThat(getConstructor(PojoPrivateConstructor.class, true, NO_ARGS).getParameterCount()).isEqualTo(0);
        assertThatThrownBy(() -> getConstructor(PojoPrivateConstructor.class, false, NO_ARGS).getParameterCount());
    }

    @Test
    void MutatorWithMultipleConstructorsReturnsLargestConstructor() {
        assertThat(getConstructor(MutatorWithMultipleConstructors.class, false, LARGEST).getParameterCount()).isEqualTo(11);
        assertThat(getConstructor(Mutator.class, false, LARGEST).getParameterCount()).isEqualTo(0);
        assertThat(getConstructor(PojoPrivateConstructor.class, true, LARGEST).getParameterCount()).isEqualTo(0);
        assertThatThrownBy(() -> getConstructor(PojoPrivateConstructor.class, false, LARGEST).getParameterCount());
    }

    @Test
    void MutatorWithMultipleConstructorsReturnsSmallestConstructor() {
        assertThat(getConstructor(MutatorWithMultipleConstructors.class, false, SMALLEST).getParameterCount()).isEqualTo(1);
        assertThat(getConstructor(Mutator.class, false, SMALLEST).getParameterCount()).isEqualTo(0);
        assertThat(getConstructor(PojoPrivateConstructor.class, true, SMALLEST).getParameterCount()).isEqualTo(0);
        assertThatThrownBy(() -> getConstructor(PojoPrivateConstructor.class, false, SMALLEST).getParameterCount());
    }

}