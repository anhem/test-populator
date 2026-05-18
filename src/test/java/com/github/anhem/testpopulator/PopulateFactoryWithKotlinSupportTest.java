package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.model.kotlin.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.testutil.AssertTestUtil.RECURSIVE_ASSERTION_CONFIGURATION;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryWithKotlinSupportTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .kotlinSupport(true)
                .and()
                .constructorStrategy()
                .and()
                .objectFactory(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void canPopulateKotlinLikeClass() {
        populateAndAssertWithGeneratedCode(KotlinLikeClass.class);
    }

    @Test
    void canPopulateLargeKotlinLikeClass() {
        populateAndAssertWithGeneratedCode(LargeKotlinLikeClass.class);
    }

    @Test
    void canPopulateRegularJavaClassWithKotlinLikeClassField() {
        populateAndAssertWithGeneratedCode(JavaClassWithKotlinField.class);
    }

    @Test
    void canPopulateKotlinLikeClassUsingConstructorStrategy() {
        KotlinLikeClass result = populateFactory.populate(KotlinLikeClass.class);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
    }

    @Test
    void usesKotlinDefaultValuesWhenConfigured() {
        populateConfig = populateConfig.toBuilder()
                .kotlinSupport(true)
                .defaultValues(true)
                .and()
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        KotlinLikeClass result = populateFactory.populate(KotlinLikeClass.class);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo("default_value");
        assertThat(result.getId()).isNotZero();
        assertThat(result.getInnerClass()).isNotNull();
    }

    @Test
    void canPopulateInternalPropertyUsingSetterStrategy() {
        populateConfig = populateConfig.toBuilder()
                .clearStrategies()
                .setterStrategy()
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        KotlinLikeWithInternalProperty result = populateFactory.populate(KotlinLikeWithInternalProperty.class);

        assertThat(result).isNotNull();
        assertThat(result.getMyProp()).isNotEqualTo("initial");
    }

    @Test
    void canPopulateKotlinCompanion() {
        populateConfig = populateConfig.toBuilder()
                .clearStrategies()
                .staticMethodStrategy()
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        KotlinLikeWithCompanion result = populateFactory.populate(KotlinLikeWithCompanion.class);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isNotNull();
    }

    @Test
    void ignoresDelegateFieldWhenKotlinSupportIsEnabled() {
        populateConfig = populateConfig.toBuilder()
                .clearStrategies()
                .fieldStrategy()
                .and()
                .objectFactory(false)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        KotlinLikeWithComplexDelegate result = populateFactory.populate(KotlinLikeWithComplexDelegate.class);

        assertThat(result).isNotNull();
        // Since it's ignored, it should still have its initial value
        // (or at least the population didn't crash)
        assertThat(result.getMyProp$delegate()).isNotNull();
        assertThat(result.getMyProp$delegate().getValue()).isEqualTo("initial");
    }

    @Test
    void canPopulateKotlinSingleton() {
        KotlinLikeSingleton result = populateFactory.populate(KotlinLikeSingleton.class);

        assertThat(result).isSameAs(KotlinLikeSingleton.INSTANCE);
    }

    @Test
    void cannotPopulateKotlinSingletonWhenKotlinSupportIsDisabled() {
        populateConfig = PopulateConfig.builder().build();
        populateFactory = new PopulateFactory(populateConfig);

        assertThatThrownBy(() -> populateFactory.populate(KotlinLikeSingleton.class))
                .isInstanceOf(PopulateException.class)
                .hasMessageContaining("No matching strategy found");
    }

    @Test
    void canPopulateKotlinSingletonWithGeneratedCode() {
        KotlinLikeSingleton result = populateFactory.populate(KotlinLikeSingleton.class);

        assertThat(result).isSameAs(KotlinLikeSingleton.INSTANCE);
        assertGeneratedCode(result, populateConfig);
    }

    @Test
    void canPopulateKotlinLikeClassWithGenerics() {
        populateAndAssertWithGeneratedCode(KotlinLikeWithGenerics.class);
    }

    private <T> void populateAndAssertWithGeneratedCode(Class<T> clazz) {
        T result = populateFactory.populate(clazz);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
        assertGeneratedCode(result, populateConfig);
    }
}
