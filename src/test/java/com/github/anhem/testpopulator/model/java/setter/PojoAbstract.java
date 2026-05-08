package com.github.anhem.testpopulator.model.java.setter;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public abstract class PojoAbstract {

    private String stringValue;
    private char primitiveCharacterValue;
    private Character characterValue;
    private int primitiveIntegerValue;
    private Integer integerValue;
    private long primitiveLongValue;
    private Long longValue;
    private double primitiveDoubleValue;
    private Double doubleValue;
    private boolean primitiveBooleanValue;
    private Boolean booleanValue;
    private BigDecimal bigDecimal;
    private UUID uuid;
    private Optional<Integer> optionalInteger;
    private Optional<String> optionalString;
    private OptionalInt optionalInt;
    private OptionalLong optionalLong;
    private OptionalDouble optionalDouble;
    private Charset charset;
    private Calendar calendar;
    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private ZonedDateTime zonedDateTime;
    private Instant instant;
    private List<String> listOfStrings;
    private Set<String> setOfStrings;
    private Queue<String> queue;
    private Deque<String> deque;
    private SortedSet<String> sortedSet;
    private NavigableSet<String> navigableSet;
    private Map<Integer, String> mapOfIntegersToStrings;
    private Map<String, Integer> mapOfStringsToIntegers;
    private SortedMap<String, Integer> sortedMap;
    private NavigableMap<String, Integer> navigableMap;
    private ConcurrentMap<String, String> concurrentMap;
    private ConcurrentNavigableMap<String, String> concurrentNavigableMap;
    private ConcurrentSkipListMap<String, String> concurrentSkipListMap;
    private CopyOnWriteArrayList<String> copyOnWriteArrayList;
    private CopyOnWriteArraySet<String> copyOnWriteArraySet;
    private ConcurrentSkipListSet<String> concurrentSkipListSet;
    private ArbitraryEnum arbitraryEnum;
    private String[] arrayOfStrings;
    private byte[] arrayOfBytes;
    private int[] arrayOfInts;
    private long[] arrayOfLongs;
    private double[] arrayOfDoubles;
    private boolean[] arrayOfBooleans;
    private ArrayList<String> arrayList;
    private LinkedList<String> linkedList;
    private HashMap<String, String> hashMap;
    private LinkedHashMap<String, String> linkedHashMap;
    private HashSet<String> hashSet;
    private LinkedHashSet<String> linkedHashSet;
    private Date date;
    private Byte byteValue;
    private byte primitiveByteValue;
    private Short shortValue;
    private short primitiveShortValue;
    private Float floatValue;
    private float primitiveFloatValue;
    private LocalTime localTime;
    private BigInteger bigInteger;
    private OffsetDateTime offsetDateTime;
    private OffsetTime offsetTime;
    private Duration duration;
    private Period period;
    private java.sql.Date sqlDate;
    private Time sqlTime;
    private Timestamp sqlTimestamp;
    private Currency currency;
    private Locale locale;
    private TimeZone timeZone;
    private ZoneId zoneId;
    private ZoneOffset zoneOffset;
    private Year year;
    private YearMonth yearMonth;
    private MonthDay monthDay;
    private File file;
    private Path path;
    private URL url;
    private URI uri;

}
