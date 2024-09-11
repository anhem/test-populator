package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.anhem.testpopulator.ValueFactory.UNSUPPORTED_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueFactoryTest {

    private ValueFactory valueFactory;

    @Test
    void randomValuesAreCreated() {
        valueFactory = new ValueFactory(true);
        createAndAssertRandomValues(ArbitraryEnum.class);
        createAndAssertRandomValues(Integer.class);
        createAndAssertRandomValues(Long.class);
        createAndAssertRandomValues(Double.class);
        createAndAssertRandomValues(Boolean.class);
        createAndAssertRandomValues(BigDecimal.class);
        createAndAssertRandomValues(String.class);
        createAndAssertRandomValues(LocalDate.class);
        createAndAssertRandomValues(LocalDateTime.class);
        createAndAssertRandomValues(Character.class);
        createAndAssertRandomValues(UUID.class);
    }

    @Test
    void fixedValuesAreCreated() {
        valueFactory = new ValueFactory(false);
        createAndAssertFixedValues(ArbitraryEnum.class);
        createAndAssertFixedValues(Integer.class);
        createAndAssertFixedValues(Long.class);
        createAndAssertFixedValues(Double.class);
        createAndAssertFixedValues(Boolean.class);
        createAndAssertFixedValues(BigDecimal.class);
        createAndAssertFixedValues(String.class);
        createAndAssertFixedValues(LocalDate.class);
        createAndAssertFixedValues(LocalDateTime.class);
        createAndAssertFixedValues(Character.class);
        createAndAssertFixedValues(UUID.class);
    }

    @Test
    void attemptingToCreateValueOfUnsupportedTypeThrowsException() {
        valueFactory = new ValueFactory(true);
        assertThatThrownBy(() -> valueFactory.createValue(Pojo.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(UNSUPPORTED_TYPE, Pojo.class.getTypeName()));
    }

    private void createAndAssertRandomValues(Class<?> clazz) {
        Object value_1 = valueFactory.createValue(clazz);
        Object value_2 = valueFactory.createValue(clazz);
        int retry = 0;
        while (value_1.equals(value_2) && retry < 10) {
            value_2 = valueFactory.createValue(clazz);
            retry++;
        }
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1.getClass()).isEqualTo(clazz);
        assertThat(value_2.getClass()).isEqualTo(clazz);
        assertThat(value_1).isNotEqualTo(value_2);
    }

    private void createAndAssertFixedValues(Class<?> clazz) {
        Object value_1 = valueFactory.createValue(clazz);
        Object value_2 = valueFactory.createValue(clazz);
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1.getClass()).isEqualTo(clazz);
        assertThat(value_2.getClass()).isEqualTo(clazz);
        assertThat(value_1).isEqualTo(value_2);
    }

}
