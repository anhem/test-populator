package com.github.anhem.testpopulator.model.java.constructor;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@EqualsAndHashCode
@lombok.AllArgsConstructor
public class AllArgsConstructor {

    private final String stringValue;
    private final char primitiveCharacterValue;
    private final Character characterValue;
    private final int primitiveIntegerValue;
    private final Integer integerValue;
    private final long primitiveLongValue;
    private final Long longValue;
    private final double primitiveDoubleValue;
    private final Double doubleValue;
    private final LocalDate localDate;
    private final LocalDateTime localDateTime;
    private final List<String> listOfStrings;
    private final Set<String> setOfStrings;
    private final Map<Integer, String> mapOfIntegersToStrings;
    private final Map<String, Integer> mapOfStringsToIntegers;
    private final ArbitraryEnum arbitraryEnum;
    private final String[] arrayOfStrings;
    private final Date date;
    private final Byte byteValue;
    private final byte primitiveByteValue;
    private final Short shortValue;
    private final short primitiveShortValue;
    private final Float floatValue;
    private final float primitiveFloatValue;
    private final LocalTime localTime;
    private final BigInteger bigInteger;
    private final OffsetDateTime offsetDateTime;
    private final OffsetTime offsetTime;
    private final Duration duration;
    private final Period period;
    private final java.sql.Date sqlDate;
    private final Time sqlTime;
    private final Timestamp sqlTimestamp;

}
