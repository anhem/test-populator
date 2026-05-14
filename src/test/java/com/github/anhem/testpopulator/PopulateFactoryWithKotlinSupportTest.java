package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.kotlin.JavaClassWithKotlinField;
import com.github.anhem.testpopulator.model.kotlin.KotlinLikeClass;
import com.github.anhem.testpopulator.model.kotlin.LargeKotlinLikeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.testutil.AssertTestUtil.RECURSIVE_ASSERTION_CONFIGURATION;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryWithKotlinSupportTest {

    private PopulateConfig populateConfig;
    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .kotlinSupport(true)
                .objectFactoryEnabled(true)
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
        populateConfig = PopulateConfig.builder()
                .kotlinSupport(true)
                .constructorStrategy()
                .and()
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        KotlinLikeClass result = populateFactory.populate(KotlinLikeClass.class);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
    }

    @Test
    void usesKotlinDefaultValuesWhenConfigured() {
        populateConfig = PopulateConfig.builder()
                .kotlinSupport(true, true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);

        KotlinLikeClass result = populateFactory.populate(KotlinLikeClass.class);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo("default_value");
        assertThat(result.getId()).isNotZero();
        assertThat(result.getInnerClass()).isNotNull();
    }

    private <T> void populateAndAssertWithGeneratedCode(Class<T> clazz) {
        T result = populateFactory.populate(clazz);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
        assertGeneratedCode(result, populateConfig);
    }
}
