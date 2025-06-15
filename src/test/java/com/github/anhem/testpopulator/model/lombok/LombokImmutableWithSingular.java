package com.github.anhem.testpopulator.model.lombok;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value
@Builder
public class LombokImmutableWithSingular {

    String stringValue;
    char primitiveCharacterValue;
    Character characterValue;
    int primitiveIntegerValue;
    Integer integerValue;
    long primitiveLongValue;
    Long longValue;
    double primitiveDoubleValue;
    Double doubleValue;
    LocalDate localDate;
    LocalDateTime localDateTime;
    @Singular
    List<String> listOfStrings;
    @Singular
    Set<String> setOfStrings;
    @Singular("setOfIntegerOverride")
    Set<Integer> setOfIntegers;
    @Singular
    Map<Integer, String> mapOfIntegersToStrings;
    @Singular
    Map<String, Integer> mapOfStringsToIntegers;
    ArbitraryEnum arbitraryEnum;
    String[] arrayOfStrings;
    Date date;
    Byte byteValue;
    byte primitiveByteValue;
    Short shortValue;
    short primitiveShortValue;
    Float floatValue;
    float primitiveFloatValue;
    LocalTime localTime;
    BigInteger bigInteger;
    OffsetDateTime offsetDateTime;
    OffsetTime offsetTime;
    Duration duration;
    Period period;
    java.sql.Date sqlDate;
    Time sqlTime;
    Timestamp sqlTimestamp;
}
