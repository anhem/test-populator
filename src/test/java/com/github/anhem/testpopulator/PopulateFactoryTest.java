package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.AllArgsConstructorAbstract;
import com.github.anhem.testpopulator.model.java.PojoAbstract;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.github.anhem.testpopulator.PopulateFactory.FAILED_TO_CREATE_INSTANCE;
import static com.github.anhem.testpopulator.PopulateFactory.MISSING_STRATEGIES;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopulateFactoryTest {

    @Test
    void missingStrategiesThrowsException() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .strategyOrder(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> new PopulateFactory(populateConfig))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(MISSING_STRATEGIES);
    }

    @Test
    public void tryingToInstantiateAbstractClassThrowsException() {
        PopulateFactory populateFactory = new PopulateFactory();
        assertThatThrownBy(() -> populateFactory.populate(AllArgsConstructorAbstract.class))
                .isInstanceOf(PopulateException.class)
                .hasMessageContaining(format(FAILED_TO_CREATE_INSTANCE, ""));

        assertThatThrownBy(() -> populateFactory.populate(PojoAbstract.class))
                .isInstanceOf(PopulateException.class)
                .hasMessageContaining(format(FAILED_TO_CREATE_INSTANCE, ""));
    }

}
