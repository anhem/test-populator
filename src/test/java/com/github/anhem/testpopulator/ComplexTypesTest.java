package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.ComplexTypes;
import org.junit.jupiter.api.Test;

import static com.github.anhem.testpopulator.testutil.AssertTestUtil.RECURSIVE_ASSERTION_CONFIGURATION;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;

public class ComplexTypesTest {

    @Test
    void canPopulateComplexTypes() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactoryEnabled(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        ComplexTypes complexTypes = populateFactory.populate(ComplexTypes.class);

        assertThat(complexTypes).hasNoNullFieldsOrProperties();
        assertThat(complexTypes).usingRecursiveAssertion(RECURSIVE_ASSERTION_CONFIGURATION).hasNoNullFields();
        assertGeneratedCode(ComplexTypes.class, complexTypes, populateConfig);
    }
}
