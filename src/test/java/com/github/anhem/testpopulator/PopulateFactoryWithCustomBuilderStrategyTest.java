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
        String value1 = populateAndAssertWithGeneratedCode(String.class);
        String value2 = populateAndAssertWithGeneratedCode(String.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void CustomBuilder() {
        CustomBuilder value1 = populateAndAssertWithGeneratedCode(CustomBuilder.class);
        CustomBuilder value2 = populateAndAssertWithGeneratedCode(CustomBuilder.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void CustomBuilderCustomName() {
        this.populateConfig = populateConfig.toBuilder()
                .builderMethod("newBuilder")
                .buildMethod("done")
                .build();
        populateFactory = new PopulateFactory(populateConfig);
        CustomBuilderCustomName value1 = populateAndAssertWithGeneratedCode(CustomBuilderCustomName.class);
        CustomBuilderCustomName value2 = populateAndAssertWithGeneratedCode(CustomBuilderCustomName.class);
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void LombokImmutable() {
        LombokImmutable value1 = populateAndAssertWithGeneratedCode(LombokImmutable.class);
        LombokImmutable value2 = populateAndAssertWithGeneratedCode(LombokImmutable.class);
        assertRandomlyPopulatedValues(value1, value2);
        assertThat(List.of(value1, value2)).allSatisfy(value -> {
            assertThat(value.getListOfStrings()).hasSize(1);
            assertThat(value.getMapOfStringsToIntegers()).hasSize(1);
            assertThat(value.getMapOfStringsToIntegers().values()).hasSize(1);
        });
    }

    @Test
    void LombokImmutableWithSingular() {
        LombokImmutableWithSingular value1 = populateAndAssertWithGeneratedCode(LombokImmutableWithSingular.class);
        LombokImmutableWithSingular value2 = populateAndAssertWithGeneratedCode(LombokImmutableWithSingular.class);
        assertRandomlyPopulatedValues(value1, value2);
        assertThat(List.of(value1, value2))
                .allSatisfy(value -> {
                    assertThat(value.getListOfStrings()).hasSize(2);
                    assertThat(value.getMapOfStringsToIntegers()).hasSize(2);
                    assertThat(value.getMapOfStringsToIntegers().values()).hasSize(2);
                    assertThat(value.getSetOfIntegers()).hasSize(2);
                });
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
