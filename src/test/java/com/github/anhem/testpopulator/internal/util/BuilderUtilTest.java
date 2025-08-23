package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.model.custombuilder.CustomBuilder;
import com.github.anhem.testpopulator.model.java.setter.PojoExtendsPojoAbstract;
import com.github.anhem.testpopulator.model.lombok.LombokImmutable;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.config.PopulateConfig.DEFAULT_BUILDER_METHOD;
import static com.github.anhem.testpopulator.config.Strategy.BUILDER;
import static com.github.anhem.testpopulator.config.Strategy.CONSTRUCTOR;
import static com.github.anhem.testpopulator.internal.util.BuilderUtil.getMethodsForCustomBuilder;
import static com.github.anhem.testpopulator.internal.util.BuilderUtil.isMatchingBuilderStrategy;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class BuilderUtilTest {

    @Test
    void isMatchingBuilderStrategyReturnsTrue() {
        assertThat(isMatchingBuilderStrategy(BUILDER, LombokImmutable.class, BuilderPattern.LOMBOK, DEFAULT_BUILDER_METHOD)).isTrue();
    }

    @Test
    void isMatchingBuilderStrategyReturnsFalse() {
        assertThat(isMatchingBuilderStrategy(CONSTRUCTOR, LombokImmutable.class, BuilderPattern.LOMBOK, DEFAULT_BUILDER_METHOD)).isFalse();
        assertThat(isMatchingBuilderStrategy(BUILDER, PojoExtendsPojoAbstract.class, BuilderPattern.LOMBOK, DEFAULT_BUILDER_METHOD)).isFalse();
    }

    @Test
    void getMethodsForCustomBuilderReturnsMethods() {
        assertThat(getMethodsForCustomBuilder(CustomBuilder.CustomBuilderBuilder.class, emptyList())).hasSize(7);
    }
}