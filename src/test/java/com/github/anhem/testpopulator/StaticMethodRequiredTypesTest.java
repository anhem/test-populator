package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.stc.StaticMethodRequiredTypes;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.testutil.AssertTestUtil.RECURSIVE_ASSERTION_CONFIGURATION;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

class StaticMethodRequiredTypesTest {

    @Test
    void canPopulateStaticMethodRequiredTypes() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactoryEnabled(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        StaticMethodRequiredTypes result = populateFactory.populate(StaticMethodRequiredTypes.class);

        assertThat(result).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
        assertGeneratedCode(StaticMethodRequiredTypes.class, result, populateConfig);
    }
}
