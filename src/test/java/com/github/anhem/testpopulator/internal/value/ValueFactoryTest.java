package com.github.anhem.testpopulator.internal.value;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.anhem.testpopulator.config.BuilderPattern.CUSTOM;
import static com.github.anhem.testpopulator.internal.value.ValueFactory.UNSUPPORTED_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueFactoryTest {

    private final List<Class<?>> CLASSES = List.of(
            ArbitraryEnum.class,
            Integer.class,
            Long.class,
            Double.class,
            Boolean.class,
            BigDecimal.class,
            String.class,
            LocalDate.class,
            LocalDateTime.class,
            ZonedDateTime.class,
            Instant.class,
            Date.class,
            Character.class,
            UUID.class,
            Byte.class,
            BigInteger.class,
            LocalTime.class,
            OffsetDateTime.class,
            OffsetTime.class,
            Duration.class,
            Period.class,
            java.sql.Date.class,
            Time.class,
            Timestamp.class
    );
    private ValueFactory valueFactory;

    @Test
    void randomValuesAreCreated() {
        valueFactory = new ValueFactory(true, Map.of(), CUSTOM);
        CLASSES.forEach(this::createAndAssertRandomValues);
        createAndAssertRandomIntValues();
        createAndAssertRandomLongValues();
        createAndAssertRandomDoubleValues();
        createAndAssertRandomShortValues();
        createAndAssertRandomFloatValues();
        createAndAssertRandomBooleanValues();
        createAndAssertRandomCharValues();
        createAndAssertRandomByteValues();
    }

    @Test
    void fixedValuesAreCreated() {
        valueFactory = new ValueFactory(false, Map.of(), CUSTOM);
        CLASSES.forEach(this::createAndAssertFixedValues);
        createAndAssertFixedIntValues();
        createAndAssertFixedLongValues();
        createAndAssertFixedDoubleValues();
        createAndAssertFixedShortValues();
        createAndAssertFixedFloatValues();
        createAndAssertFixedBooleanValues();
        createAndAssertFixedCharValues();
        createAndAssertFixedByteValues();
    }

    @Test
    void attemptingToCreateValueOfUnsupportedTypeThrowsException() {
        valueFactory = new ValueFactory(true, Map.of(), CUSTOM);
        assertThatThrownBy(() -> valueFactory.createValue(Pojo.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(UNSUPPORTED_TYPE, Pojo.class.getTypeName()));
    }

    private void createAndAssertRandomValues(Class<?> clazz) {
        Object value1 = valueFactory.createValue(clazz);
        Object value2 = createSecondNonMatchingValue(value1);
        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();
        assertThat(value1.getClass()).isEqualTo(clazz);
        assertThat(value2.getClass()).isEqualTo(clazz);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomIntValues() {
        int value1 = valueFactory.createValue(int.class);
        int value2 = (int) createSecondNonMatchingValue(value1);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomLongValues() {
        long value1 = valueFactory.createValue(long.class);
        long value2 = (long) createSecondNonMatchingValue(value1);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomDoubleValues() {
        double value1 = valueFactory.createValue(double.class);
        double value2 = (double) createSecondNonMatchingValue(value1);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomShortValues() {
        short value1 = valueFactory.createValue(short.class);
        short value2 = (short) createSecondNonMatchingValue(value1);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomFloatValues() {
        float value1 = valueFactory.createValue(float.class);
        float value2 = (float) createSecondNonMatchingValue(value1);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomBooleanValues() {
        boolean value1 = valueFactory.createValue(boolean.class);
        boolean value2 = (boolean) createSecondNonMatchingValue(value1);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomCharValues() {
        char value1 = valueFactory.createValue(char.class);
        char value2 = (char) createSecondNonMatchingValue(value1);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomByteValues() {
        byte value1 = valueFactory.createValue(byte.class);
        byte value2 = (byte) createSecondNonMatchingValue(value1);
        assertThat(value1).isNotEqualTo(value2);
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
        Object value1 = valueFactory.createValue(clazz);
        Object value2 = valueFactory.createValue(clazz);
        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();
        assertThat(value1.getClass()).isEqualTo(clazz);
        assertThat(value2.getClass()).isEqualTo(clazz);
        assertThat(value1).isEqualTo(value2);
    }

    private void createAndAssertFixedIntValues() {
        int value1 = valueFactory.createValue(int.class);
        int value2 = valueFactory.createValue(int.class);
        assertThat(value1).isEqualTo(value2);
    }

    private void createAndAssertFixedLongValues() {
        long value1 = valueFactory.createValue(long.class);
        long value2 = valueFactory.createValue(long.class);
        assertThat(value1).isEqualTo(value2);
    }

    private void createAndAssertFixedDoubleValues() {
        double value1 = valueFactory.createValue(double.class);
        double value2 = valueFactory.createValue(double.class);
        assertThat(value1).isEqualTo(value2);
    }

    private void createAndAssertFixedShortValues() {
        short value1 = valueFactory.createValue(short.class);
        short value2 = valueFactory.createValue(short.class);
        assertThat(value1).isEqualTo(value2);
    }

    private void createAndAssertFixedFloatValues() {
        float value1 = valueFactory.createValue(float.class);
        float value2 = valueFactory.createValue(float.class);
        assertThat(value1).isEqualTo(value2);
    }

    private void createAndAssertFixedBooleanValues() {
        boolean value1 = valueFactory.createValue(boolean.class);
        boolean value2 = valueFactory.createValue(boolean.class);
        assertThat(value1).isEqualTo(value2);
    }

    private void createAndAssertFixedCharValues() {
        char value1 = valueFactory.createValue(char.class);
        char value2 = valueFactory.createValue(char.class);
        assertThat(value1).isEqualTo(value2);
    }

    private void createAndAssertFixedByteValues() {
        byte value1 = valueFactory.createValue(byte.class);
        byte value2 = valueFactory.createValue(byte.class);
        assertThat(value1).isEqualTo(value2);
    }
}
