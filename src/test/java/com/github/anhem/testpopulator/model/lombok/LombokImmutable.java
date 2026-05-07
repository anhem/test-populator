package com.github.anhem.testpopulator.model.lombok;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.Builder;
import lombok.Value;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
public class LombokImmutable {

    String stringValue;
    char primitiveCharacterValue;
    Character characterValue;
    int primitiveIntegerValue;
    Integer integerValue;
    long primitiveLongValue;
    Long longValue;
    double primitiveDoubleValue;
    Double doubleValue;
    boolean primitiveBooleanValue;
    Boolean booleanValue;
    BigDecimal bigDecimal;
    UUID uuid;
    Optional<Integer> optionalInteger;
    Optional<String> optionalString;
    LocalDate localDate;
    LocalDateTime localDateTime;
    ZonedDateTime zonedDateTime;
    Instant instant;
    List<String> listOfStrings;
    Set<String> setOfStrings;
    Map<Integer, String> mapOfIntegersToStrings;
    Map<String, Integer> mapOfStringsToIntegers;
    ArbitraryEnum arbitraryEnum;
    String[] arrayOfStrings;
    ArrayList<String> arrayList;
    HashMap<String, String> hashMap;
    HashSet<String> hashSet;
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
    Currency currency;
    Locale locale;
    TimeZone timeZone;
    ZoneId zoneId;
    ZoneOffset zoneOffset;
    Year year;
    YearMonth yearMonth;
    MonthDay monthDay;
    File file;
    Path path;
    URL url;
    URI uri;
}
