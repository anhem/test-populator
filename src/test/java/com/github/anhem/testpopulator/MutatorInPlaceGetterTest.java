package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.kotlin.InPlaceSetterComponent;
import com.github.anhem.testpopulator.model.kotlin.NamedTargetComponent;
import com.github.anhem.testpopulator.model.kotlin.NestedTargetPositionComponent;
import com.github.anhem.testpopulator.model.kotlin.TargetPositionComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.config.Strategy.MUTATOR;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCodeContains;
import static org.assertj.core.api.Assertions.assertThat;

class MutatorInPlaceGetterTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .mutatorStrategy()
                .and()
                .objectFactory(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void testPopulateClassWithInPlaceGetter() {
        TargetPositionComponent value1 = populateAndAssertWithGeneratedCode(TargetPositionComponent.class);
        TargetPositionComponent value2 = populateAndAssertWithGeneratedCode(TargetPositionComponent.class);

        assertThat(value1.getTarget().x).isNotZero();
        assertThat(value1.getTarget().y).isNotZero();
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void testKotlinSupportGeneratesPropertyAccess() {
        populateConfig = populateConfig.toBuilder()
                .kotlinSupport(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        TargetPositionComponent value = populateFactory.populate(TargetPositionComponent.class);

        assertThat(value.getTarget().x).isNotZero();
        assertThat(value.getTarget().y).isNotZero();
        assertGeneratedCodeContains(value, populateConfig,
                "TARGET_POSITION_COMPONENT_0.target.x = ",
                "TARGET_POSITION_COMPONENT_0.target.y = ");
    }

    @Test
    void testClassWithBothSettersAndInPlaceGetter() {
        NamedTargetComponent value1 = populateAndAssertWithGeneratedCode(NamedTargetComponent.class);
        NamedTargetComponent value2 = populateAndAssertWithGeneratedCode(NamedTargetComponent.class);

        assertThat(value1.getName()).isNotNull().isNotEmpty();
        assertThat(value1.getTarget().x).isNotZero();
        assertThat(value1.getTarget().y).isNotZero();
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void testChainedInPlaceGetters() {
        NestedTargetPositionComponent value1 = populateAndAssertWithGeneratedCode(NestedTargetPositionComponent.class);
        NestedTargetPositionComponent value2 = populateAndAssertWithGeneratedCode(NestedTargetPositionComponent.class);

        assertThat(value1.getPosition().getTarget().x).isNotZero();
        assertThat(value1.getPosition().getTarget().y).isNotZero();
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void testChainedInPlaceGettersKotlinSupport() {
        populateConfig = populateConfig.toBuilder()
                .kotlinSupport(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        NestedTargetPositionComponent value = populateFactory.populate(NestedTargetPositionComponent.class);

        assertThat(value.getPosition().getTarget().x).isNotZero();
        assertThat(value.getPosition().getTarget().y).isNotZero();
        assertGeneratedCodeContains(value, populateConfig,
                "NESTED_TARGET_POSITION_COMPONENT_0.position.target.x = ",
                "NESTED_TARGET_POSITION_COMPONENT_0.position.target.y = ");
    }

    @Test
    void testInPlaceGetterWithMutatorMethods() {
        InPlaceSetterComponent value1 = populateAndAssertWithGeneratedCode(InPlaceSetterComponent.class);
        InPlaceSetterComponent value2 = populateAndAssertWithGeneratedCode(InPlaceSetterComponent.class);

        assertThat(value1.getMutator().getStringValue()).isNotNull().isNotEmpty();
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void testBlacklistedGetterMethodIsIgnored() {
        populateConfig = populateConfig.toBuilder()
                .setBlacklistedMethods("getTarget")
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        TargetPositionComponent value = populateFactory.populate(TargetPositionComponent.class);

        assertThat(value.getTarget().x).isZero();
        assertThat(value.getTarget().y).isZero();
    }

    @Test
    void testBlacklistedFieldOnInPlaceTargetIsIgnored() {
        populateConfig = populateConfig.toBuilder()
                .setBlacklistedFields("x")
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        TargetPositionComponent value = populateFactory.populate(TargetPositionComponent.class);

        assertThat(value.getTarget().x).isZero();
        assertThat(value.getTarget().y).isNotZero();
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
}
