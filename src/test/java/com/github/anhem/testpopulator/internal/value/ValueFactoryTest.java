package com.github.anhem.testpopulator.internal.value;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.anhem.testpopulator.config.BuilderPattern.CUSTOM;
import static com.github.anhem.testpopulator.internal.value.ValueFactory.UNSUPPORTED_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueFactoryTest {

    private static final List<Class<?>> CLASSES_TO_IGNORE = List.of(
            String.class,
            CharSequence.class,
            BigInteger.class,
            BigDecimal.class,
            Date.class,
            java.sql.Date.class,
            Time.class,
            Timestamp.class,
            UUID.class,
            Calendar.class,
            BitSet.class,
            Throwable.class,
            Exception.class,
            RuntimeException.class,
            Error.class,
            InetSocketAddress.class,
            Boolean.class,
            Character.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            java.net.URL.class,
            java.net.URI.class,
            Locale.class
    );

    private ValueFactory valueFactory;

    @Test
    void randomValuesAreCreated() {
        valueFactory = new ValueFactory(true, Map.of(), Map.of(), CUSTOM);
        getTestableTypes().forEach(this::createAndAssertRandomValues);
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
        valueFactory = new ValueFactory(false, Map.of(), Map.of(), CUSTOM);
        getTestableTypes().forEach(this::createAndAssertFixedValues);
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
        valueFactory = new ValueFactory(true, Map.of(), Map.of(), CUSTOM);
        assertThatThrownBy(() -> valueFactory.createValue(Pojo.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(String.format(UNSUPPORTED_TYPE, Pojo.class.getTypeName()));
    }

    @Test
    void onlyBaseTypesAreRegisteredInValueFactory() {
        valueFactory = new ValueFactory(false, Map.of(), Map.of(), CUSTOM);
        Set<Class<?>> registeredTypes = valueFactory.getRegisteredTypes();

        List<Class<?>> typesThatCouldBeSolvedByConstructorStrategy = registeredTypes.stream()
                .filter(clazz -> !clazz.isPrimitive())
                .filter(clazz -> !CLASSES_TO_IGNORE.contains(clazz))
                .filter(clazz -> Arrays.stream(clazz.getConstructors())
                        .anyMatch(constructor -> Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterCount() > 0))
                .collect(Collectors.toList());

        assertThat(typesThatCouldBeSolvedByConstructorStrategy).isEmpty();
    }

    private Set<Class<?>> getTestableTypes() {
        Set<Class<?>> types = valueFactory.getRegisteredTypes().stream()
                .filter(c -> !c.isPrimitive())
                .filter(c -> !c.equals(Class.class))
                .collect(Collectors.toSet());
        types.add(ArbitraryEnum.class);
        return types;
    }

    private void createAndAssertRandomValues(Class<?> clazz) {
        Object value1 = valueFactory.createValue(clazz);
        Object value2 = createSecondNonMatchingValue(value1, clazz);
        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();
        assertThat(value1).isInstanceOf(clazz);
        assertThat(value2).isInstanceOf(clazz);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomIntValues() {
        int value1 = valueFactory.createValue(int.class);
        int value2 = (int) createSecondNonMatchingValue(value1, int.class);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomLongValues() {
        long value1 = valueFactory.createValue(long.class);
        long value2 = (long) createSecondNonMatchingValue(value1, long.class);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomDoubleValues() {
        double value1 = valueFactory.createValue(double.class);
        double value2 = (double) createSecondNonMatchingValue(value1, double.class);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomShortValues() {
        short value1 = valueFactory.createValue(short.class);
        short value2 = (short) createSecondNonMatchingValue(value1, short.class);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomFloatValues() {
        float value1 = valueFactory.createValue(float.class);
        float value2 = (float) createSecondNonMatchingValue(value1, float.class);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomBooleanValues() {
        boolean value1 = valueFactory.createValue(boolean.class);
        boolean value2 = (boolean) createSecondNonMatchingValue(value1, boolean.class);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomCharValues() {
        char value1 = valueFactory.createValue(char.class);
        char value2 = (char) createSecondNonMatchingValue(value1, char.class);
        assertThat(value1).isNotEqualTo(value2);
    }

    private void createAndAssertRandomByteValues() {
        byte value1 = valueFactory.createValue(byte.class);
        byte value2 = (byte) createSecondNonMatchingValue(value1, byte.class);
        assertThat(value1).isNotEqualTo(value2);
    }

    private Object createSecondNonMatchingValue(Object value, Class<?> clazz) {
        Object secondValue = valueFactory.createValue(clazz);
        int retry = 0;
        while (value.equals(secondValue) && retry < 10) {
            secondValue = valueFactory.createValue(clazz);
            retry++;
        }
        return secondValue;
    }

    private void createAndAssertFixedValues(Class<?> clazz) {
        Object value1 = valueFactory.createValue(clazz);
        Object value2 = valueFactory.createValue(clazz);
        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();
        assertThat(value1).isInstanceOf(clazz);
        assertThat(value2).isInstanceOf(clazz);
        if (hasOverriddenEquals(clazz)) {
            assertThat(value1).isEqualTo(value2);
        } else {
            assertThat(value1.toString()).isEqualTo(value2.toString());
        }
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

    private boolean hasOverriddenEquals(Class<?> clazz) {
        try {
            Method equalsMethod = clazz.getMethod("equals", Object.class);
            return !equalsMethod.getDeclaringClass().equals(Object.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
