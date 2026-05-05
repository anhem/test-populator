package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
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
                .builderStrategy()
                .and()
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
                .clearStrategies()
                .builderStrategy()
                .method("newBuilder")
                .buildMethod("done")
                .and()
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

    @Test
    void canOverrideCollectionByName() {
        List<String> list = List.of("foo", "bar");
        populateConfig = populateConfig.toBuilder()
                .addOverride("strings", List.class, new OverridePopulate<>() {
                    @Override
                    public List<String> create() {
                        return list;
                    }

                    @Override
                    public String createString() {
                        return "List.of(\"foo\", \"bar\")";
                    }
                })
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        CustomBuilder customBuilder = populateAndAssertWithGeneratedCode(CustomBuilder.class);

        assertThat(customBuilder.getStrings()).isEqualTo(list);
    }

    @Test
    void canPopulateBasedOnCustomName() {
        populateConfig = populateConfig.toBuilder()
                .addOverride("available", boolean.class, () -> true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        CustomBuilder customBuilder = populateAndAssertWithGeneratedCode(CustomBuilder.class);

        assertThat(customBuilder.isBooleanValue()).isTrue();
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
