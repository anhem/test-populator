package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.kotlin.KotlinLikeClass;
import com.github.anhem.testpopulator.model.kotlin.KotlinLikeClass.InnerClass;
import com.github.anhem.testpopulator.model.kotlin.KotlinLikeClass.LargeKotlinLikeClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.testutil.AssertTestUtil.RECURSIVE_ASSERTION_CONFIGURATION;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryWithKotlinSupportTest {

    @Getter
    @AllArgsConstructor
    public static class JavaClassWithKotlinField {
        private final KotlinLikeClass kotlinLikeClass;
        private final InnerClass innerClass;
    }

    @Test
    void canPopulateKotlinLikeClass() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .kotlinSupport(true)
                .objectFactoryEnabled(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        KotlinLikeClass result = populateFactory.populate(KotlinLikeClass.class);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
        assertGeneratedCode(result, populateConfig);
    }

    @Test
    void canPopulateLargeKotlinLikeClass() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .kotlinSupport(true)
                .objectFactoryEnabled(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        LargeKotlinLikeClass result = populateFactory.populate(LargeKotlinLikeClass.class);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
        assertGeneratedCode(result, populateConfig);
    }

    @Test
    void canPopulateRegularJavaClassWithKotlinLikeClassField() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .kotlinSupport(true)
                .objectFactoryEnabled(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        JavaClassWithKotlinField result = populateFactory.populate(JavaClassWithKotlinField.class);

        assertThat(result).isNotNull();
        assertThat(result.getKotlinLikeClass()).isNotNull();
        assertThat(result.getKotlinLikeClass()).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
        assertGeneratedCode(result, populateConfig);
    }

    @Test
    void canPopulateKotlinLikeClassUsingConstructorStrategy() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .constructorStrategy()
                    .kotlinSupport(true)
                    .and()
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        KotlinLikeClass result = populateFactory.populate(KotlinLikeClass.class);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
    }
}
