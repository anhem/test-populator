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

    boolean getPrimitiveBooleanValue();

    Boolean getBooleanValue();

    BigDecimal getBigDecimal();

    UUID getUuid();

    Optional<Integer> getOptionalInteger();

    Optional<String> getOptionalString();

    OptionalInt getOptionalInt();

    OptionalLong getOptionalLong();

    OptionalDouble getOptionalDouble();

    Charset getCharset();

    Calendar getCalendar();

    LocalDate getLocalDate();

    LocalDateTime getLocalDateTime();

    ZonedDateTime getZonedDateTime();

    Instant getInstant();

    List<String> getListOfStrings();

    List<ArbitraryEnum> getListOfEnums();

    Set<String> getSetOfStrings();

    EnumSet<ArbitraryEnum> getEnumSet();

    BitSet getBitSet();

    Queue<String> getQueue();

    Deque<String> getDeque();

    SortedSet<String> getSortedSet();

    NavigableSet<String> getNavigableSet();

    Map<Integer, String> getMapOfIntegersToStrings();

    Map<String, Integer> getMapOfStringsToIntegers();

    Map<String, ArbitraryEnum> getMapOfStringsToEnums();

    EnumMap<ArbitraryEnum, String> getEnumMap();

    SortedMap<String, Integer> getSortedMap();

    NavigableMap<String, Integer> getNavigableMap();

    ConcurrentMap<String, String> getConcurrentMap();

    ConcurrentNavigableMap<String, String> getConcurrentNavigableMap();

    ConcurrentSkipListMap<String, String> getConcurrentSkipListMap();

    CopyOnWriteArrayList<String> getCopyOnWriteArrayList();

    CopyOnWriteArraySet<String> getCopyOnWriteArraySet();

    ConcurrentSkipListSet<String> getConcurrentSkipListSet();

    ArbitraryEnum getArbitraryEnum();

    String[] getArrayOfStrings();

    byte[] getArrayOfBytes();

    char[] getArrayOfChars();

    int[] getArrayOfInts();

    long[] getArrayOfLongs();

    float[] getArrayOfFloats();

    double[] getArrayOfDoubles();

    short[] getArrayOfShorts();

    boolean[] getArrayOfBooleans();

    ArrayList<String> getArrayList();

    LinkedList<String> getLinkedList();

    HashMap<String, String> getHashMap();

    LinkedHashMap<String, String> getLinkedHashMap();

    HashSet<String> getHashSet();

    LinkedHashSet<String> getLinkedHashSet();

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

    Map.Entry<String, String> getMapEntry();

    Properties getProperties();

    Hashtable<String, Integer> getHashtable();

    Vector<String> getVector();

    Map<String, List<String>> getNestedMap();

    List<List<String>> getNestedList();

    Set<Set<String>> getNestedSet();

    Map<String, Map<Integer, String>> getNestedMap2();

    Map<String, Optional<String>> getMapOfOptionals();

    List<Map.Entry<String, Integer>> getListOfEntries();

    Stack<String> getStack();

    Integer[] getArrayOfIntegerObjects();

    Long[] getArrayOfLongObjects();

    File getFile();

    Path getPath();

    URL getUrl();

    URI getUri();

}
