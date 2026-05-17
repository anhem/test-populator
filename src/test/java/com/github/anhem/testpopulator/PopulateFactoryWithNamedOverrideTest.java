package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryWithNamedOverrideTest {

    @Test
    void nameOverrideWithTypeMismatchIsIgnoredAndFallsBackToDefault() {
        PopulateConfig config = PopulateConfig.builder()
                .addOverride("setStringValue", Integer.class, () -> 123)
                .build();
        PopulateFactory factory = new PopulateFactory(config);

        Pojo pojo = factory.populate(Pojo.class);

        assertThat(pojo.getStringValue()).isNotNull().isNotEqualTo("123");
    }

    @Test
    void nameOverrideWithTypeMismatchFallsBackToClassOverride() {
        String classOverrideValue = "classOverride";
        PopulateConfig config = PopulateConfig.builder()
                .addOverride("setStringValue", Integer.class, () -> 123)
                .addOverride(String.class, () -> classOverrideValue)
                .build();
        PopulateFactory factory = new PopulateFactory(config);

        Pojo pojo = factory.populate(Pojo.class);

        assertThat(pojo.getStringValue()).isEqualTo(classOverrideValue);
    }

    @Test
    void nameOverrideWithKotlinSupportEnabled() {
        PopulateConfig config = PopulateConfig.builder()
                .kotlinSupport(true)
                .and()
                .addOverride("setStringValue", String.class, () -> "overridden")
                .build();
        PopulateFactory factory = new PopulateFactory(config);

        Pojo pojo = factory.populate(Pojo.class);

        assertThat(pojo.getStringValue()).isEqualTo("overridden");
    }
}
