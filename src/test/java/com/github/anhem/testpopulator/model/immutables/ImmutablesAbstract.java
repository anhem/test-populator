package com.github.anhem.testpopulator.model.immutables;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import org.immutables.value.Value;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

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

    public abstract boolean getPrimitiveBooleanValue();

    public abstract Boolean getBooleanValue();

    public abstract BigDecimal getBigDecimal();

    public abstract UUID getUuid();

    public abstract Optional<Integer> getOptionalInteger();

    public abstract Optional<String> getOptionalString();

    public abstract Charset getCharset();

    public abstract Calendar getCalendar();

    public abstract LocalDate getLocalDate();

    public abstract LocalDateTime getLocalDateTime();

    public abstract ZonedDateTime getZonedDateTime();

    public abstract Instant getInstant();

    public abstract List<String> getListOfStrings();

    public abstract Set<String> getSetOfStrings();

    public abstract Queue<String> getQueue();

    public abstract Deque<String> getDeque();

    public abstract SortedSet<String> getSortedSet();

    public abstract NavigableSet<String> getNavigableSet();

    public abstract Map<Integer, String> getMapOfIntegersToStrings();

    public abstract Map<String, Integer> getMapOfStringsToIntegers();

    public abstract SortedMap<String, Integer> getSortedMap();

    public abstract NavigableMap<String, Integer> getNavigableMap();

    public abstract ConcurrentMap<String, String> getConcurrentMap();

    public abstract ConcurrentNavigableMap<String, String> getConcurrentNavigableMap();

    public abstract ConcurrentSkipListMap<String, String> getConcurrentSkipListMap();

    public abstract CopyOnWriteArrayList<String> getCopyOnWriteArrayList();

    public abstract CopyOnWriteArraySet<String> getCopyOnWriteArraySet();

    public abstract ConcurrentSkipListSet<String> getConcurrentSkipListSet();

    public abstract ArbitraryEnum getArbitraryEnum();

    public abstract String[] getArrayOfStrings();

    public abstract ArrayList<String> getArrayList();

    public abstract LinkedList<String> getLinkedList();

    public abstract HashMap<String, String> getHashMap();

    public abstract LinkedHashMap<String, String> getLinkedHashMap();

    public abstract HashSet<String> getHashSet();

    public abstract LinkedHashSet<String> getLinkedHashSet();

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
