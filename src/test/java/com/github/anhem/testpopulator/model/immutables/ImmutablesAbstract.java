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
public abstract class ImmutablesAbstract {

    public abstract String getStringValue();

    public abstract char getPrimitiveCharacterValue();

    public abstract Character getCharacterValue();

    public abstract int getPrimitiveIntegerValue();

    public abstract Integer getIntegerValue();

    public abstract long getPrimitiveLongValue();

    public abstract Long getLongValue();

    public abstract double getPrimitiveDoubleValue();

    public abstract Double getDoubleValue();

    public abstract LocalDate getLocalDate();

    public abstract LocalDateTime getLocalDateTime();

    public abstract ZonedDateTime getZonedDateTime();

    public abstract Instant getInstant();

    public abstract List<String> getListOfStrings();

    public abstract Set<String> getSetOfStrings();

    public abstract Map<Integer, String> getMapOfIntegersToStrings();

    public abstract Map<String, Integer> getMapOfStringsToIntegers();

    public abstract ArbitraryEnum getArbitraryEnum();

    public abstract String[] getArrayOfStrings();

    public abstract ArrayList<String> getArrayList();

    public abstract HashMap<String, String> getHashMap();

    public abstract HashSet<String> getHashSet();

    public abstract Date date();

    public abstract Byte getByteValue();

    public abstract byte getPrimitiveByteValue();

    public abstract Short getShortValue();

    public abstract short getPrimitiveShortValue();

    public abstract Float getFloatValue();

    public abstract float getPrimitiveFloatValue();

    public abstract LocalTime getLocalTime();

    public abstract BigInteger getBigInteger();

    public abstract OffsetDateTime getOffsetDateTime();

    public abstract OffsetTime getOffsetTime();

    public abstract Duration getDuration();

    public abstract Period getPeriod();

    public abstract java.sql.Date getSqlDate();

    public abstract Time getSqlTime();

    public abstract Timestamp getSqlTimestamp();

    public abstract Currency getCurrency();

    public abstract Locale getLocale();

    public abstract TimeZone getTimeZone();

    public abstract ZoneId getZoneId();

    public abstract ZoneOffset getZoneOffset();

    public abstract Year getYear();

    public abstract YearMonth getYearMonth();

    public abstract MonthDay getMonthDay();

    public abstract File getFile();

    public abstract Path getPath();

    public abstract URL getUrl();

    public abstract URI getUri();

}
