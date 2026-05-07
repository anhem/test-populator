package com.github.anhem.testpopulator.model.java.constructor;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Getter
@EqualsAndHashCode(callSuper = true)
public class AllArgsConstructorExtendsAllArgsConstructorAbstract extends AllArgsConstructorAbstract {

    private final String anotherString;

    public AllArgsConstructorExtendsAllArgsConstructorAbstract(String stringValue, char primitiveCharacterValue, Character characterValue, int primitiveIntegerValue, Integer integerValue, long primitiveLongValue, Long longValue, double primitiveDoubleValue, Double doubleValue, LocalDate localDate, LocalDateTime localDateTime, ZonedDateTime zonedDateTime, Instant instant, List<String> listOfStrings, Set<String> setOfStrings, Map<Integer, String> mapOfIntegersToStrings, Map<String, Integer> mapOfStringsToIntegers, ArbitraryEnum arbitraryEnum, String[] arrayOfStrings, ArrayList<String> arrayList, HashMap<String, String> hashMap, HashSet<String> hashSet, Date date, Byte byteValue, byte primitiveByteValue, Short shortValue, short primitiveShortValue, Float floatValue, float primitiveFloatValue, LocalTime localTime, BigInteger bigInteger, OffsetDateTime offsetDateTime, OffsetTime offsetTime, Duration duration, Period period, java.sql.Date sqlDate, Time sqlTime, Timestamp sqlTimestamp, Currency currency, Locale locale, TimeZone timeZone, ZoneId zoneId, ZoneOffset zoneOffset, Year year, YearMonth yearMonth, MonthDay monthDay, File file, Path path, URL url, URI uri, String anotherString) {
        super(stringValue, primitiveCharacterValue, characterValue, primitiveIntegerValue, integerValue, primitiveLongValue, longValue, primitiveDoubleValue, doubleValue, localDate, localDateTime, zonedDateTime, instant, listOfStrings, setOfStrings, mapOfIntegersToStrings, mapOfStringsToIntegers, arbitraryEnum, arrayOfStrings, arrayList, hashMap, hashSet, date, byteValue, primitiveByteValue, shortValue, primitiveShortValue, floatValue, primitiveFloatValue, localTime, bigInteger, offsetDateTime, offsetTime, duration, period, sqlDate, sqlTime, sqlTimestamp, currency, locale, timeZone, zoneId, zoneOffset, year, yearMonth, monthDay, file, path, url, uri);
        this.anotherString = anotherString;
    }
}
