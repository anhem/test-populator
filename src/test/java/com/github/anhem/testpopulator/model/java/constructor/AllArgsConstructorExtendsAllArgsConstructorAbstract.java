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
@EqualsAndHashCode(callSuper = true)
public class AllArgsConstructorExtendsAllArgsConstructorAbstract extends AllArgsConstructorAbstract {

    private final String anotherString;

    public AllArgsConstructorExtendsAllArgsConstructorAbstract(String stringValue, char primitiveCharacterValue, Character characterValue, int primitiveIntegerValue, Integer integerValue, long primitiveLongValue, Long longValue, double primitiveDoubleValue, Double doubleValue, LocalDate localDate, LocalDateTime localDateTime, List<String> listOfStrings, Set<String> setOfStrings, Map<Integer, String> mapOfIntegerTosStrings, Map<String, Integer> mapOfStringsToIntegers, ArbitraryEnum arbitraryEnum, String[] arrayOfStrings, Date date, LocalTime localTime, BigInteger bigInteger, OffsetDateTime offsetDateTime, OffsetTime offsetTime, Duration duration, Period period, java.sql.Date sqlDate, Time sqlTime, Timestamp sqlTimestamp, String anotherString) {
        super(stringValue, primitiveCharacterValue, characterValue, primitiveIntegerValue, integerValue, primitiveLongValue, longValue, primitiveDoubleValue, doubleValue, localDate, localDateTime, listOfStrings, setOfStrings, mapOfIntegerTosStrings, mapOfStringsToIntegers, arbitraryEnum, arrayOfStrings, date, localTime, bigInteger, offsetDateTime, offsetTime, duration, period, sqlDate, sqlTime, sqlTimestamp);
        this.anotherString = anotherString;
    }
}
