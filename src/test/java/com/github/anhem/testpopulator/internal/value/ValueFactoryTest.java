package com.github.anhem.testpopulator.internal.value;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static com.github.anhem.testpopulator.internal.value.ValueFactory.UNSUPPORTED_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueFactoryTest {

    private ValueFactory valueFactory;

    @Test
    void randomValuesAreCreated() {
        valueFactory = new ValueFactory(true, Map.of());
        createAndAssertRandomValues(ArbitraryEnum.class);
        createAndAssertRandomValues(Integer.class);
        createAndAssertRandomValues(Long.class);
        createAndAssertRandomValues(Double.class);
        createAndAssertRandomValues(Boolean.class);
        createAndAssertRandomValues(BigDecimal.class);
        createAndAssertRandomValues(String.class);
        createAndAssertRandomValues(LocalDate.class);
        createAndAssertRandomValues(LocalDateTime.class);
        createAndAssertRandomValues(ZonedDateTime.class);
        createAndAssertRandomValues(Instant.class);
        createAndAssertRandomValues(Date.class);
        createAndAssertRandomValues(Character.class);
        createAndAssertRandomValues(UUID.class);
        createAndAssertRandomIntValues();
        createAndAssertRandomLongValues();
        createAndAssertRandomDoubleValues();
        createAndAssertRandomBooleanValues();
        createAndAssertRandomCharValues();
    }

    @Test
    void fixedValuesAreCreated() {
        valueFactory = new ValueFactory(false, Map.of());
        createAndAssertFixedValues(ArbitraryEnum.class);
        createAndAssertFixedValues(Integer.class);
        createAndAssertFixedValues(Long.class);
        createAndAssertFixedValues(Double.class);
        createAndAssertFixedValues(Boolean.class);
        createAndAssertFixedValues(BigDecimal.class);
        createAndAssertFixedValues(String.class);
        createAndAssertFixedValues(LocalDate.class);
        createAndAssertFixedValues(LocalDateTime.class);
        createAndAssertFixedValues(ZonedDateTime.class);
        createAndAssertFixedValues(Instant.class);
        createAndAssertFixedValues(Date.class);
        createAndAssertFixedValues(Character.class);
        createAndAssertFixedValues(UUID.class);
        createAndAssertFixedIntValues();
        createAndAssertFixedLongValues();
        createAndAssertFixedDoubleValues();
        createAndAssertFixedBooleanValues();
        createAndAssertFixedCharValues();
    }

    @Test
    void attemptingToCreateValueOfUnsupportedTypeThrowsException() {
        valueFactory = new ValueFactory(true, Map.of());
        assertThatThrownBy(() -> valueFactory.createValue(Pojo.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(UNSUPPORTED_TYPE, Pojo.class.getTypeName()));
    }

    private void createAndAssertRandomValues(Class<?> clazz) {
        Object value_1 = valueFactory.createValue(clazz);
        Object value_2 = createSecondNonMatchingValue(value_1);
        assertThat(value_1).isNotNull();
        assertThat(value_2).isNotNull();
        assertThat(value_1.getClass()).isEqualTo(clazz);
        assertThat(value_2.getClass()).isEqualTo(clazz);
        assertThat(value_1).isNotEqualTo(value_2);
    }

    private void createAndAssertRandomIntValues() {
        int value_1 = valueFactory.createValue(int.class);
        int value_2 = (int) createSecondNonMatchingValue(value_1);
        assertThat(value_1).isNotEqualTo(value_2);
    }

    private void createAndAssertRandomLongValues() {
        long value_1 = valueFactory.createValue(long.class);
        long value_2 = (long) createSecondNonMatchingValue(value_1);
        assertThat(value_1).isNotEqualTo(value_2);
    }

    private void createAndAssertRandomDoubleValues() {
        double value_1 = valueFactory.createValue(double.class);
        double value_2 = (double) createSecondNonMatchingValue(value_1);
        assertThat(value_1).isNotEqualTo(value_2);
    }

    private void createAndAssertRandomBooleanValues() {
        boolean value_1 = valueFactory.createValue(boolean.class);
        boolean value_2 = (boolean) createSecondNonMatchingValue(value_1);
        assertThat(value_1).isNotEqualTo(value_2);
    }

    private void createAndAssertRandomCharValues() {
        char value_1 = valueFactory.createValue(char.class);
        char value_2 = (char) createSecondNonMatchingValue(value_1);
        assertThat(value_1).isNotEqualTo(value_2);
    }

    private Object createSecondNonMatchingValue(Object value) {
        Object secondValue = valueFactory.createValue(value.getClass());
        int retry = 0;
        while (value.equals(secondValue) && retry < 10) {
            secondValue = valueFactory.createValue(value.getClass());
            retry++;
        }
        return secondValue;
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

    private void createAndAssertFixedIntValues() {
        int value_1 = valueFactory.createValue(int.class);
        int value_2 = valueFactory.createValue(int.class);
        assertThat(value_1).isEqualTo(value_2);
    }

    private void createAndAssertFixedLongValues() {
        long value_1 = valueFactory.createValue(long.class);
        long value_2 = valueFactory.createValue(long.class);
        assertThat(value_1).isEqualTo(value_2);
    }

    private void createAndAssertFixedDoubleValues() {
        double value_1 = valueFactory.createValue(double.class);
        double value_2 = valueFactory.createValue(double.class);
        assertThat(value_1).isEqualTo(value_2);
    }

    private void createAndAssertFixedBooleanValues() {
        boolean value_1 = valueFactory.createValue(boolean.class);
        boolean value_2 = valueFactory.createValue(boolean.class);
        assertThat(value_1).isEqualTo(value_2);
    }

    private void createAndAssertFixedCharValues() {
        char value_1 = valueFactory.createValue(char.class);
        char value_2 = valueFactory.createValue(char.class);
        assertThat(value_1).isEqualTo(value_2);
    }
}
