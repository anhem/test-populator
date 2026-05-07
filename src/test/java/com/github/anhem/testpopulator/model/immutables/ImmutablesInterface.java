package com.github.anhem.testpopulator.model.immutables;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import org.immutables.value.Value;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Value.Immutable
public interface ImmutablesInterface {

    String getStringValue();

    char getPrimitiveCharacterValue();

    Character getCharacterValue();

    int getPrimitiveIntegerValue();

    Integer getIntegerValue();

    long getPrimitiveLongValue();

    Long longValue();

    double getPrimitiveDoubleValue();

    Double getDoubleValue();

    LocalDate getLocalDate();

    LocalDateTime getLocalDateTime();

    ZonedDateTime getZonedDateTime();

    Instant getInstant();

    List<String> getListOfStrings();

    Set<String> getSetOfStrings();

    Map<Integer, String> getMapOfIntegersToStrings();

    Map<String, Integer> getMapOfStringsToIntegers();

    ArbitraryEnum getArbitraryEnum();

    String[] getArrayOfStrings();

    ArrayList<String> getArrayList();

    HashMap<String, String> getHashMap();

    HashSet<String> getHashSet();

    Date getDate();

    Byte getByteValue();

    byte getPrimitiveByteValue();

    Short getShortValue();

    short getPrimitiveShortValue();

    Float getFloatValue();

    float getPrimitiveFloatValue();

    LocalTime getLocalTime();

    BigInteger getBigInteger();

    OffsetDateTime getOffsetDateTime();

    OffsetTime getOffsetTime();

    Duration duration();

    Period period();

    java.sql.Date getSqlDate();

    Time getSqlTime();

    Timestamp getSqlTimestamp();

    Currency getCurrency();

    Locale getLocale();

    TimeZone getTimeZone();

    ZoneId getZoneId();

    ZoneOffset getZoneOffset();

    Year getYear();

    YearMonth getYearMonth();

    MonthDay getMonthDay();

    File getFile();

    Path getPath();

    URL getUrl();

    URI getUri();

}
