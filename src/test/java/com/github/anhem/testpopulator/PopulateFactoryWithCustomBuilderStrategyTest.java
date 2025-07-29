package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.custombuilder.CustomBuilder;
import com.github.anhem.testpopulator.model.custombuilder.CustomBuilderCustomName;
import com.github.anhem.testpopulator.model.lombok.LombokImmutable;
import com.github.anhem.testpopulator.model.lombok.LombokImmutableWithSingular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.anhem.testpopulator.config.BuilderPattern.CUSTOM;
import static com.github.anhem.testpopulator.config.Strategy.BUILDER;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryWithCustomBuilderStrategyTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .strategyOrder(List.of(BUILDER))
                .builderPattern(CUSTOM)
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
    void CustomBuilder() {
        CustomBuilder value_1 = populateAndAssertWithGeneratedCode(CustomBuilder.class);
        CustomBuilder value_2 = populateAndAssertWithGeneratedCode(CustomBuilder.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void CustomBuilderCustomName() {
        this.populateConfig = populateConfig.toBuilder()
                .builderMethod("newBuilder")
                .buildMethod("done")
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        CustomBuilderCustomName value_1 = populateAndAssertWithGeneratedCode(CustomBuilderCustomName.class);
        CustomBuilderCustomName value_2 = populateAndAssertWithGeneratedCode(CustomBuilderCustomName.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void LombokImmutable() {
        LombokImmutable value_1 = populateAndAssertWithGeneratedCode(LombokImmutable.class);
        LombokImmutable value_2 = populateAndAssertWithGeneratedCode(LombokImmutable.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    @Test
    void LombokImmutableWithSingular() {
        LombokImmutableWithSingular value_1 = populateAndAssertWithGeneratedCode(LombokImmutableWithSingular.class);
        LombokImmutableWithSingular value_2 = populateAndAssertWithGeneratedCode(LombokImmutableWithSingular.class);
        assertRandomlyPopulatedValues(value_1, value_2);
    }

    private <T> T populateAndAssertWithGeneratedCode(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        assertThat(populateConfig.getStrategyOrder()).containsExactly(BUILDER);
        assertThat(populateConfig.getBuilderPattern()).isEqualTo(CUSTOM);
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);
        assertGeneratedCode(value, populateConfig);

        return value;
    }
}
