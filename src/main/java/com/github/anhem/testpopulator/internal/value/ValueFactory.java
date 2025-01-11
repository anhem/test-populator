package com.github.anhem.testpopulator.internal.value;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.exception.PopulateException;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static com.github.anhem.testpopulator.internal.util.RandomUtil.*;

public class ValueFactory {
    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
    private static final ZonedDateTime ZONED_DATE_TIME = LOCAL_DATE_TIME.atZone(ZoneId.of("UTC"));
    private static final Instant INSTANT = ZONED_DATE_TIME.toInstant();
    private static final LocalDate LOCAL_DATE = LOCAL_DATE_TIME.toLocalDate();
    private static final Date DATE = Date.from(INSTANT);
    private static final String STRING = "string";
    private static final Boolean BOOLEAN = Boolean.TRUE;
    private static final Long LONG = 1L;
    private static final Double DOUBLE = 1D;
    private static final Integer INTEGER = 1;
    private static final Character CHARACTER = 'c';
    private static final String UUID_STRING = "43c6e27d-c0c6-43d6-8462-34ac04c1d5f3";
    private static final BigDecimal BIG_DECIMAL = BigDecimal.ONE;
    private static final byte BYTE = 1;

    private final boolean setRandomValues;
    private final Map<Class<?>, TypeSupplier<?>> typeSuppliers;

    public ValueFactory(boolean setRandomValues, Map<Class<?>, OverridePopulate<?>> overridePopulates) {
        this.setRandomValues = setRandomValues;
        this.typeSuppliers = getDefaultTypeSuppliers();
        this.typeSuppliers.putAll(overridePopulates);
    }

    private Map<Class<?>, TypeSupplier<?>> getDefaultTypeSuppliers() {
        Map<Class<?>, TypeSupplier<?>> typeSuppliers = new HashMap<>();
        typeSuppliers.put(Integer.class, this::getInteger);
        typeSuppliers.put(int.class, this::getInteger);
        typeSuppliers.put(Long.class, this::getLong);
        typeSuppliers.put(long.class, this::getLong);
        typeSuppliers.put(Double.class, this::getDouble);
        typeSuppliers.put(double.class, this::getDouble);
        typeSuppliers.put(Boolean.class, this::getBoolean);
        typeSuppliers.put(boolean.class, this::getBoolean);
        typeSuppliers.put(BigDecimal.class, this::getBigDecimal);
        typeSuppliers.put(String.class, this::getString);
        typeSuppliers.put(LocalDate.class, this::getLocalDate);
        typeSuppliers.put(LocalDateTime.class, this::getLocalDateTime);
        typeSuppliers.put(ZonedDateTime.class, this::getZonedDateTime);
        typeSuppliers.put(Instant.class, this::getInstant);
        typeSuppliers.put(Date.class, this::getDate);
        typeSuppliers.put(Character.class, this::getChar);
        typeSuppliers.put(char.class, this::getChar);
        typeSuppliers.put(UUID.class, this::getUUID);
        typeSuppliers.put(byte.class, this::getByte);
        typeSuppliers.put(Byte.class, this::getByte);
        return typeSuppliers;
    }

    @SuppressWarnings("unchecked")
    public <T> T createValue(Class<T> clazz) {
        if (clazz.isEnum()) {
            return getEnum(clazz);
        }

        return Optional.ofNullable(typeSuppliers.get(clazz))
                .map(supplier -> (T) supplier.create())
                .orElseThrow(() -> new PopulateException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName())));
    }

    public boolean hasType(Class<?> clazz) {
        return clazz.isEnum() || typeSuppliers.containsKey(clazz);
    }

    private <T> T getEnum(Class<T> clazz) {
        if (setRandomValues) {
            return getRandomEnum(clazz);
        }
        return clazz.getEnumConstants()[0];
    }

    private Integer getInteger() {
        return setRandomValues ? Integer.valueOf(getRandomInt()) : INTEGER;
    }

    private Long getLong() {
        return setRandomValues ? Long.valueOf(getRandomInt()) : LONG;
    }

    private Double getDouble() {
        return setRandomValues ? Double.valueOf(getRandomInt()) : DOUBLE;
    }

    private Boolean getBoolean() {
        return setRandomValues ? getRandomBoolean() : BOOLEAN;
    }

    private BigDecimal getBigDecimal() {
        return setRandomValues ? BigDecimal.valueOf(getRandomInt()) : BIG_DECIMAL;
    }

    private String getString() {
        return setRandomValues ? getRandomString() : STRING;
    }

    private LocalDateTime getLocalDateTime() {
        return setRandomValues ? getRandomLocalDateTime() : LOCAL_DATE_TIME;
    }

    private ZonedDateTime getZonedDateTime() {
        return setRandomValues ? getRandomLocalDateTime().atZone(ZoneId.systemDefault()) : ZONED_DATE_TIME;
    }

    private Instant getInstant() {
        return setRandomValues ? getRandomLocalDateTime().atZone(ZoneId.systemDefault()).toInstant() : INSTANT;
    }

    private Date getDate() {
        return setRandomValues ? Date.from(getRandomLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()) : DATE;
    }

    private LocalDate getLocalDate() {
        return setRandomValues ? getRandomLocalDateTime().toLocalDate() : LOCAL_DATE;
    }

    private Character getChar() {
        return setRandomValues ? getRandomCharacter() : CHARACTER;
    }

    private UUID getUUID() {
        return setRandomValues ? UUID.randomUUID() : UUID.fromString(UUID_STRING);
    }

    private Byte getByte() {
        return setRandomValues ? getRandomByte() : BYTE;
    }
}
