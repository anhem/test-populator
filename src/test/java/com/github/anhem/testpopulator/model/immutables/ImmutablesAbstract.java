package com.github.anhem.testpopulator.model.immutables;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import org.immutables.value.Value;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
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

    public abstract OptionalInt getOptionalInt();

    public abstract OptionalLong getOptionalLong();

    public abstract OptionalDouble getOptionalDouble();

    public abstract Charset getCharset();

    public abstract Calendar getCalendar();

    public abstract LocalDate getLocalDate();

    public abstract LocalDateTime getLocalDateTime();

    public abstract ZonedDateTime getZonedDateTime();

    public abstract Instant getInstant();

    public abstract List<String> getListOfStrings();

    public abstract List<ArbitraryEnum> getListOfEnums();

    public abstract Set<String> getSetOfStrings();

    public abstract EnumSet<ArbitraryEnum> getEnumSet();

    public abstract BitSet getBitSet();

    public abstract Queue<String> getQueue();

    public abstract Deque<String> getDeque();

    public abstract SortedSet<String> getSortedSet();

    public abstract NavigableSet<String> getNavigableSet();

    public abstract Map<Integer, String> getMapOfIntegersToStrings();

    public abstract Map<String, Integer> getMapOfStringsToIntegers();

    public abstract Map<String, ArbitraryEnum> getMapOfStringsToEnums();

    public abstract EnumMap<ArbitraryEnum, String> getEnumMap();

    public abstract SortedMap<String, Integer> getSortedMap();

    public abstract NavigableMap<String, Integer> getNavigableMap();

    public abstract ConcurrentMap<String, String> getConcurrentMap();

    public abstract ConcurrentNavigableMap<String, String> getConcurrentNavigableMap();

    public abstract ConcurrentSkipListMap<String, String> getConcurrentSkipListMap();

    public abstract CopyOnWriteArrayList<String> getCopyOnWriteArrayList();

    public abstract CopyOnWriteArraySet<String> getCopyOnWriteArraySet();

    public abstract ConcurrentSkipListSet<String> getConcurrentSkipListSet();

    public abstract TreeMap<String, String> getTreeMap();

    public abstract TreeSet<String> getTreeSet();

    public abstract ArbitraryEnum getArbitraryEnum();

    public abstract String[] getArrayOfStrings();

    public abstract byte[] getArrayOfBytes();

    public abstract char[] getArrayOfChars();

    public abstract int[] getArrayOfInts();

    public abstract long[] getArrayOfLongs();

    public abstract float[] getArrayOfFloats();

    public abstract double[] getArrayOfDoubles();

    public abstract short[] getArrayOfShorts();

    public abstract boolean[] getArrayOfBooleans();

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

    public abstract Month getMonth();

    public abstract DayOfWeek getDayOfWeek();

    public abstract Map.Entry<String, String> getMapEntry();

    public abstract Properties getProperties();

    public abstract Hashtable<String, Integer> getHashtable();

    public abstract Vector<String> getVector();

    public abstract Map<String, List<String>> getNestedMap();

    public abstract List<List<String>> getNestedList();

    public abstract Set<Set<String>> getNestedSet();

    public abstract Map<String, Map<Integer, String>> getNestedMap2();

    public abstract Map<String, Optional<String>> getMapOfOptionals();

    public abstract List<Map.Entry<String, Integer>> getListOfEntries();

    public abstract Stack<String> getStack();

    public abstract Integer[] getArrayOfIntegerObjects();

    public abstract Long[] getArrayOfLongObjects();

    public abstract File getFile();

    public abstract Path getPath();

    public abstract URL getUrl();

    public abstract URI getUri();

    public abstract ByteBuffer getByteBuffer();

    public abstract InetAddress getInetAddress();

    public abstract Inet4Address getInet4Address();

    public abstract Inet6Address getInet6Address();

    public abstract InetSocketAddress getInetSocketAddress();

}
