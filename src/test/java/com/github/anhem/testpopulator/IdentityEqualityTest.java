package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.IdentityEqualityPojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentityEqualityTest {

    private PopulateFactory populateFactory;

    @BeforeEach
    void setUp() {
        PopulateConfig config = PopulateConfig.builder().build();
        populateFactory = new PopulateFactory(config);
    }

    @Test
    void canPopulateIdentityEqualityPojo() {
        IdentityEqualityPojo identityEqualityPojo = populateFactory.populate(IdentityEqualityPojo.class);

        assertThat(identityEqualityPojo).isNotNull();
        assertThat(identityEqualityPojo).hasNoNullFieldsOrProperties();
        assertThat(identityEqualityPojo.getThrowable()).isNotNull();
        assertThat(identityEqualityPojo.getException()).isNotNull();
        assertThat(identityEqualityPojo.getRuntimeException()).isNotNull();
        assertThat(identityEqualityPojo.getError()).isNotNull();
    }
}
