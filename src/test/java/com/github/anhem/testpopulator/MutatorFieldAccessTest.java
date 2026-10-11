package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.mutator.ExtendsVector;
import com.github.anhem.testpopulator.model.java.mutator.MutatorWithBothSettersAndFields;
import com.github.anhem.testpopulator.model.java.mutator.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.config.ConstructorType.LARGEST;
import static com.github.anhem.testpopulator.config.Strategy.MUTATOR;
import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCodeContains;
import static org.assertj.core.api.Assertions.assertThat;

class MutatorFieldAccessTest {

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
    void testPopulateClassWithDirectPublicFields() {
        Vector value1 = populateAndAssertWithGeneratedCode(Vector.class);
        Vector value2 = populateAndAssertWithGeneratedCode(Vector.class);

        assertThat(value1.x).isNotZero();
        assertThat(value1.y).isNotZero();
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void testStaticAndFinalFieldsAreIgnored() {
        populateAndAssertWithGeneratedCode(Vector.class);

        assertThat(Vector.ZERO.x).isZero();
        assertThat(Vector.ZERO.y).isZero();
    }

    @Test
    void testClassWithBothSettersAndFields() {
        MutatorWithBothSettersAndFields value1 = populateAndAssertWithGeneratedCode(MutatorWithBothSettersAndFields.class);
        MutatorWithBothSettersAndFields value2 = populateAndAssertWithGeneratedCode(MutatorWithBothSettersAndFields.class);

        assertThat(value1.x).isNotZero();
        assertThat(value1.y).isNotZero();
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void testClassWithBothSettersAndFieldsDisablingPublicFields() {
        populateConfig = populateConfig.toBuilder()
                .mutatorStrategy()
                .publicFields(false)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithBothSettersAndFields value = populateAndAssertWithGeneratedCode(MutatorWithBothSettersAndFields.class);

        assertThat(value.x).isGreaterThanOrEqualTo(1000f);
        assertThat(value.y).isZero();
    }

    @Test
    void testClassWithBothSettersAndFieldsDisablingMutatorMethods() {
        populateConfig = populateConfig.toBuilder()
                .mutatorStrategy()
                .mutatorMethods(false)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        MutatorWithBothSettersAndFields value = populateAndAssertWithGeneratedCode(MutatorWithBothSettersAndFields.class);

        assertThat(value.x).isNotZero();
        assertThat(value.x).isLessThan(1000f);
        assertThat(value.y).isNotZero();
    }

    @Test
    void testInheritedPublicFields() {
        ExtendsVector value1 = populateAndAssertWithGeneratedCode(ExtendsVector.class);
        ExtendsVector value2 = populateAndAssertWithGeneratedCode(ExtendsVector.class);

        assertThat(value1.x).isNotZero();
        assertThat(value1.y).isNotZero();
        assertThat(value1.z).isNotZero();
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void testBlacklistedPublicFieldsAreIgnored() {
        populateConfig = populateConfig.toBuilder()
                .setBlacklistedFields("x")
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        Vector value = populateFactory.populate(Vector.class);

        assertThat(value.x).isZero();
        assertThat(value.y).isNotZero();
    }

    @Test
    void testPopulateVectorWithLargestConstructor() {
        populateConfig = populateConfig.toBuilder()
                .mutatorStrategy()
                .constructorType(LARGEST)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        Vector value1 = populateAndAssertWithGeneratedCode(Vector.class);
        Vector value2 = populateAndAssertWithGeneratedCode(Vector.class);

        assertThat(value1.x).isNotZero();
        assertThat(value1.y).isNotZero();
        assertRandomlyPopulatedValues(value1, value2);
    }

    @Test
    void testKotlinSupportGeneratesAssignmentWithoutSemicolon() {
        populateConfig = populateConfig.toBuilder()
                .kotlinSupport(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        Vector value = populateFactory.populate(Vector.class);

        assertThat(value.x).isNotZero();
        assertThat(value.y).isNotZero();
        assertGeneratedCodeContains(value, populateConfig, "VECTOR_0.x = ", "VECTOR_0.y = ");
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
