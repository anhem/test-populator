package com.github.anhem.testpopulator.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KotlinSupportBugReproductionTest {

    @Test
    void canTurnOffKotlinSupportAfterTurningItOn() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .kotlinSupport(true)
                .and()
                .kotlinSupport(false)
                .build();

        assertThat(populateConfig.isKotlinSupport()).isFalse();
    }

    @Test
    void canTurnOffKotlinSupportViaToBuilder() {
        PopulateConfig config1 = PopulateConfig.builder()
                .kotlinSupport(true)
                .build();
        assertThat(config1.isKotlinSupport()).isTrue();

        PopulateConfig config2 = config1.toBuilder()
                .kotlinSupport(false)
                .build();

        assertThat(config2.isKotlinSupport()).isFalse();
    }

    @Test
    void canTurnOffKotlinDefaultValuesViaToBuilder() {
        PopulateConfig config1 = PopulateConfig.builder()
                .kotlinSupport(true)
                .defaultValues(true)
                .build();
        assertThat(config1.isUseKotlinDefaultValues()).isTrue();

        PopulateConfig config2 = config1.toBuilder()
                .kotlinSupport(true)
                .defaultValues(false)
                .build();

        assertThat(config2.isUseKotlinDefaultValues()).isFalse();
    }
}
