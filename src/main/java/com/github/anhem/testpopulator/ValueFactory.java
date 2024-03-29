package com.github.anhem.testpopulator;

import java.math.BigDecimal;
import java.time.*;
import java.util.Date;
import java.util.UUID;

import static com.github.anhem.testpopulator.RandomUtil.*;

class ValueFactory {
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

    private final boolean setRandomValues;

    ValueFactory(boolean setRandomValues) {
        this.setRandomValues = setRandomValues;
    }

    @SuppressWarnings("unchecked")
    public <T> T createValue(Class<T> clazz) {
        if (clazz.isEnum()) {
            return getEnum(clazz);
        }
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return (T) getInteger();
        }
        if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return (T) getLong();
        }
        if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return (T) getDouble();
        }
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return (T) getBoolean();
        }
        if (clazz.equals(BigDecimal.class)) {
            return (T) getBigDecimal();
        }
        if (clazz.equals(String.class)) {
            return (T) getString();
        }
        if (clazz.equals(LocalDate.class)) {
            return (T) getLocalDate();
        }
        if (clazz.equals(LocalDateTime.class)) {
            return (T) getLocalDateTime();
        }
        if (clazz.equals(ZonedDateTime.class)) {
            return (T) getZonedDateTime();
        }
        if (clazz.equals(Instant.class)) {
            return (T) getInstant();
        }
        if (clazz.equals(Date.class)) {
            return (T) getDate();
        }
        if (clazz.equals(Character.class) || clazz.equals(char.class)) {
            return (T) getChar();
        }
        if (clazz.equals(UUID.class)) {
            return (T) getUUID();
        }

        throw new PopulateException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName()));
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

    private <T> T getEnum(Class<T> clazz) {
        if (setRandomValues) {
            return getRandomEnum(clazz);
        }
        return clazz.getEnumConstants()[0];
    }

    private UUID getUUID() {
        return setRandomValues ? UUID.randomUUID() : UUID.fromString(UUID_STRING);
    }
}
