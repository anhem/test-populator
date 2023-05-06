package com.github.anhem.testpopulator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryTest {

    @Test
    void canCreatePopulateFactoryWithoutDefaultConfiguration() {
        PopulateFactory populateFactory = new PopulateFactory();

        assertThat(populateFactory).isNotNull();
    }
}
