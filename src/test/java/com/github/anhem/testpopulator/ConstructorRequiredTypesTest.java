package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.constructor.ConstructorRequiredTypes;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.testutil.AssertTestUtil.RECURSIVE_ASSERTION_CONFIGURATION;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

class ConstructorRequiredTypesTest {

    @Test
    void canPopulateConstructorRequiredTypes() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactoryEnabled(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        ConstructorRequiredTypes result = populateFactory.populate(ConstructorRequiredTypes.class);

        assertThat(result).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
        assertGeneratedCode(ConstructorRequiredTypes.class, result, populateConfig);
    }
}
