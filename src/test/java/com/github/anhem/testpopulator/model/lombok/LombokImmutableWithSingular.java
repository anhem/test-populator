package com.github.anhem.testpopulator.model.lombok;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

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
    boolean primitiveBooleanValue;
    Boolean booleanValue;
    BigDecimal bigDecimal;
    UUID uuid;
    Optional<Integer> optionalInteger;
    Optional<String> optionalString;
    OptionalInt optionalInt;
    OptionalLong optionalLong;
    OptionalDouble optionalDouble;
    Charset charset;
    Calendar calendar;
    LocalDate localDate;
    LocalDateTime localDateTime;
    ZonedDateTime zonedDateTime;
    Instant instant;
    @Singular
    List<String> listOfStrings;
    @Singular
    List<ArbitraryEnum> listOfEnums;
    @Singular
    Set<String> setOfStrings;
    EnumSet<ArbitraryEnum> enumSet;
    BitSet bitSet;
    @Singular("setOfIntegerOverride")
    Set<Integer> setOfIntegers;
    Queue<String> queue;
    Deque<String> deque;
    @Singular("sortedSet")
    SortedSet<String> sortedSet;
    @Singular("navigableSet")
    NavigableSet<String> navigableSet;
    @Singular
    Map<Integer, String> mapOfIntegersToStrings;
    @Singular
    Map<String, Integer> mapOfStringsToIntegers;
    @Singular
    Map<String, ArbitraryEnum> mapOfStringsToEnums;
    EnumMap<ArbitraryEnum, String> enumMap;
    @Singular("sortedMap")
    SortedMap<String, Integer> sortedMap;
    @Singular("navigableMap")
    NavigableMap<String, Integer> navigableMap;
    ConcurrentMap<String, String> concurrentMap;
    ConcurrentNavigableMap<String, String> concurrentNavigableMap;
    ConcurrentSkipListMap<String, String> concurrentSkipListMap;
    CopyOnWriteArrayList<String> copyOnWriteArrayList;
    CopyOnWriteArraySet<String> copyOnWriteArraySet;
    ConcurrentSkipListSet<String> concurrentSkipListSet;
    ArbitraryEnum arbitraryEnum;
    String[] arrayOfStrings;
    byte[] arrayOfBytes;
    char[] arrayOfChars;
    int[] arrayOfInts;
    long[] arrayOfLongs;
    float[] arrayOfFloats;
    double[] arrayOfDoubles;
    short[] arrayOfShorts;
    boolean[] arrayOfBooleans;
    ArrayList<String> arrayList;
    LinkedList<String> linkedList;
    HashMap<String, String> hashMap;
    LinkedHashMap<String, String> linkedHashMap;
    HashSet<String> hashSet;
    LinkedHashSet<String> linkedHashSet;
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
    Map.Entry<String, String> mapEntry;
    Properties properties;
    Hashtable<String, Integer> hashtable;
    Vector<String> vector;
    Map<String, List<String>> nestedMap;
    List<List<String>> nestedList;
    Set<Set<String>> nestedSet;
    Map<String, Map<Integer, String>> nestedMap2;
    Map<String, Optional<String>> mapOfOptionals;
    List<Map.Entry<String, Integer>> listOfEntries;
    Stack<String> stack;
    Integer[] arrayOfIntegerObjects;
    Long[] arrayOfLongObjects;
    File file;
    Path path;
    URL url;
    URI uri;
}
