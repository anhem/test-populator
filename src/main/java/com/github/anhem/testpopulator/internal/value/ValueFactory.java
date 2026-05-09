package com.github.anhem.testpopulator.internal.value;

import com.github.anhem.testpopulator.config.BuilderPattern;
import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.OverrideTarget;
import com.github.anhem.testpopulator.exception.PopulateException;
import com.github.anhem.testpopulator.internal.util.PopulateUtil;
import com.github.anhem.testpopulator.internal.util.RandomUtil;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

import static com.github.anhem.testpopulator.internal.util.RandomUtil.*;

public class ValueFactory {
    static final String UNSUPPORTED_TYPE = "Failed to find type to create value for %s. Not implemented?";
    private static final List<Currency> AVAILABLE_CURRENCIES = new ArrayList<>(Currency.getAvailableCurrencies());
    private static final Locale[] AVAILABLE_LOCALES = Arrays.stream(Locale.getAvailableLocales())
            .filter(l -> l.equals(Locale.forLanguageTag(l.toLanguageTag())))
            .toArray(Locale[]::new);
    private static final String[] AVAILABLE_TIMEZONE_IDS = TimeZone.getAvailableIDs();
    private static final List<String> AVAILABLE_ZONE_IDS = new ArrayList<>(ZoneId.getAvailableZoneIds());
    private static final List<Charset> AVAILABLE_CHARSETS = List.of(
            StandardCharsets.UTF_8,
            StandardCharsets.US_ASCII,
            StandardCharsets.ISO_8859_1,
            StandardCharsets.UTF_16,
            StandardCharsets.UTF_16BE,
            StandardCharsets.UTF_16LE
    );
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
    private static final ZonedDateTime ZONED_DATE_TIME = LOCAL_DATE_TIME.atZone(ZoneId.of("UTC"));
    private static final Instant INSTANT = ZONED_DATE_TIME.toInstant();
    private static final LocalDate LOCAL_DATE = LOCAL_DATE_TIME.toLocalDate();
    private static final LocalTime LOCAL_TIME = LOCAL_DATE_TIME.toLocalTime();
    private static final URL URL = PopulateUtil.toUrl("http://example.com");
    private static final URI URI = java.net.URI.create("http://example.com");
    private static final String STRING = "string";
    private static final Boolean BOOLEAN = Boolean.TRUE;
    private static final Long LONG = 1L;
    private static final Double DOUBLE = 1D;
    private static final Integer INTEGER = 1;
    private static final Character CHARACTER = 'c';
    private static final String UUID_STRING = "43c6e27d-c0c6-43d6-8462-34ac04c1d5f3";
    private static final BigDecimal BIG_DECIMAL = BigDecimal.ONE;
    private static final byte BYTE = 1;
    private static final short SHORT = 1;
    private static final float FLOAT = 1;
    private static final BigInteger BIG_INTEGER = BigInteger.ONE;
    private static final OffsetDateTime OFFSET_DATE_TIME = ZONED_DATE_TIME.toOffsetDateTime();
    private static final OffsetTime OFFSET_TIME = ZONED_DATE_TIME.toOffsetDateTime().toOffsetTime();
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Period PERIOD = Period.ofDays(1);
    private static final Currency CURRENCY = Currency.getInstance("XTS");
    private static final Locale LOCALE = Locale.ROOT;
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
    private static final Year YEAR = Year.of(1970);
    private static final YearMonth YEAR_MONTH = YearMonth.of(1970, 1);
    private static final MonthDay MONTH_DAY = MonthDay.of(1, 1);
    private static final File FILE = new File("test-file");
    private static final Path PATH = Paths.get("test-path");
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final OptionalInt OPTIONAL_INT = OptionalInt.of(INTEGER);
    private static final OptionalLong OPTIONAL_LONG = OptionalLong.of(LONG);
    private static final OptionalDouble OPTIONAL_DOUBLE = OptionalDouble.of(DOUBLE);


    private final boolean setRandomValues;
    private final Map<Class<?>, TypeSupplier<?>> classTypeSuppliers;
    private final Map<OverrideTarget, TypeSupplier<?>> nameTypeSuppliers;
    private final BuilderPattern builderPattern;

    public ValueFactory(
            boolean setRandomValues,
            Map<Class<?>, OverridePopulate<?>> classOverrides,
            Map<OverrideTarget, OverridePopulate<?>> nameOverrides,
            BuilderPattern builderPattern
    ) {
        this.setRandomValues = setRandomValues;
        this.classTypeSuppliers = setClassTypeSuppliers(classOverrides);
        this.nameTypeSuppliers = new HashMap<>(nameOverrides);
        this.builderPattern = builderPattern;
    }

    private Map<Class<?>, TypeSupplier<?>> setClassTypeSuppliers(Map<Class<?>, OverridePopulate<?>> classOverrides) {
        Map<Class<?>, TypeSupplier<?>> typeSuppliers = new HashMap<>();
        typeSuppliers.put(Integer.class, this::getInteger);
        typeSuppliers.put(int.class, this::getInteger);
        typeSuppliers.put(Long.class, this::getLong);
        typeSuppliers.put(long.class, this::getLong);
        typeSuppliers.put(Double.class, this::getDouble);
        typeSuppliers.put(double.class, this::getDouble);
        typeSuppliers.put(Short.class, this::getShort);
        typeSuppliers.put(short.class, this::getShort);
        typeSuppliers.put(Float.class, this::getFloat);
        typeSuppliers.put(float.class, this::getFloat);
        typeSuppliers.put(Boolean.class, this::getBoolean);
        typeSuppliers.put(boolean.class, this::getBoolean);
        typeSuppliers.put(BigDecimal.class, this::getBigDecimal);
        typeSuppliers.put(BigInteger.class, this::getBigInteger);
        typeSuppliers.put(String.class, this::getString);
        typeSuppliers.put(LocalDate.class, this::getLocalDate);
        typeSuppliers.put(LocalTime.class, this::getLocalTime);
        typeSuppliers.put(LocalDateTime.class, this::getLocalDateTime);
        typeSuppliers.put(OffsetTime.class, this::getOffsetTime);
        typeSuppliers.put(OffsetDateTime.class, this::getOffsetDateTime);
        typeSuppliers.put(ZonedDateTime.class, this::getZonedDateTime);
        typeSuppliers.put(Instant.class, this::getInstant);
        typeSuppliers.put(Duration.class, this::getDuration);
        typeSuppliers.put(Period.class, this::getPeriod);
        typeSuppliers.put(Date.class, this::getDate);
        typeSuppliers.put(java.sql.Date.class, this::getSqlDate);
        typeSuppliers.put(Time.class, this::getSqlTime);
        typeSuppliers.put(Timestamp.class, this::getSqlTimestamp);
        typeSuppliers.put(Character.class, this::getChar);
        typeSuppliers.put(char.class, this::getChar);
        typeSuppliers.put(UUID.class, this::getUUID);
        typeSuppliers.put(byte.class, this::getByte);
        typeSuppliers.put(Byte.class, this::getByte);
        typeSuppliers.put(Currency.class, this::getCurrency);
        typeSuppliers.put(Locale.class, this::getLocale);
        typeSuppliers.put(TimeZone.class, this::getTimeZone);
        typeSuppliers.put(ZoneId.class, this::getZoneId);
        typeSuppliers.put(ZoneOffset.class, this::getZoneOffset);
        typeSuppliers.put(Year.class, this::getYear);
        typeSuppliers.put(YearMonth.class, this::getYearMonth);
        typeSuppliers.put(MonthDay.class, this::getMonthDay);
        typeSuppliers.put(File.class, this::getFile);
        typeSuppliers.put(Path.class, this::getPath);
        typeSuppliers.put(URL.class, this::getUrl);
        typeSuppliers.put(URI.class, this::getUri);
        typeSuppliers.put(Charset.class, this::getCharset);
        typeSuppliers.put(Calendar.class, this::getCalendar);
        typeSuppliers.put(BitSet.class, this::getBitSet);
        typeSuppliers.put(OptionalInt.class, this::getOptionalInt);
        typeSuppliers.put(OptionalLong.class, this::getOptionalLong);
        typeSuppliers.put(OptionalDouble.class, this::getOptionalDouble);
        typeSuppliers.putAll(classOverrides);
        return typeSuppliers;
    }

    public <T> T createValue(Class<T> clazz) {
        return createValue(clazz, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T createValue(Class<T> clazz, String name) {
        if (clazz.isEnum()) {
            return getEnum(clazz);
        }

        if (name != null) {
            OverrideTarget overrideTarget = OverrideTarget.of(name, clazz);
            if (nameTypeSuppliers.containsKey(overrideTarget)) {
                return (T) nameTypeSuppliers.get(overrideTarget).create();
            }
        }

        return Optional.ofNullable(classTypeSuppliers.get(clazz))
                .map(supplier -> (T) supplier.create())
                .orElseThrow(() -> new PopulateException(String.format(UNSUPPORTED_TYPE, clazz.getTypeName())));
    }

    public boolean hasType(Class<?> clazz) {
        return clazz.isEnum() || classTypeSuppliers.containsKey(clazz);
    }

    public boolean hasOverridePopulateName(String name, Class<?> clazz) {
        return nameTypeSuppliers.containsKey(OverrideTarget.of(name, clazz));
    }

    private <T> T getEnum(Class<T> clazz) {
        if (setRandomValues) {
            return getRandomEnum(clazz, builderPattern.equals(BuilderPattern.PROTOBUF));
        }
        return clazz.getEnumConstants()[0];
    }

    private Integer getInteger() {
        return setRandomValues ? Integer.valueOf(getRandomInt()) : INTEGER;
    }

    private Long getLong() {
        return setRandomValues ? Long.valueOf(getRandomInt()) : LONG;
    }

    private Double getDouble() {
        return setRandomValues ? Double.valueOf(getRandomInt()) : DOUBLE;
    }

    private Short getShort() {
        return setRandomValues ? RandomUtil.getRandomShort() : SHORT;
    }

    private Float getFloat() {
        return setRandomValues ? RandomUtil.getRandomFloat() : FLOAT;
    }

    private Boolean getBoolean() {
        return setRandomValues ? getRandomBoolean() : BOOLEAN;
    }

    private BigDecimal getBigDecimal() {
        return setRandomValues ? BigDecimal.valueOf(getRandomInt()) : BIG_DECIMAL;
    }

    private String getString() {
        return setRandomValues ? getRandomString() : STRING;
    }

    private LocalDateTime getLocalDateTime() {
        return setRandomValues ? getRandomLocalDateTime() : LOCAL_DATE_TIME;
    }

    private ZonedDateTime getZonedDateTime() {
        return setRandomValues ? getRandomLocalDateTime().atZone(ZoneId.systemDefault()) : ZONED_DATE_TIME;
    }

    private Instant getInstant() {
        return setRandomValues ? getRandomLocalDateTime().atZone(ZoneId.systemDefault()).toInstant() : INSTANT;
    }

    private Date getDate() {
        return setRandomValues ? Date.from(getRandomLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()) : Date.from(INSTANT);
    }

    private LocalDate getLocalDate() {
        return setRandomValues ? getRandomLocalDateTime().toLocalDate() : LOCAL_DATE;
    }

    private Character getChar() {
        return setRandomValues ? getRandomCharacter() : CHARACTER;
    }

    private UUID getUUID() {
        return setRandomValues ? UUID.randomUUID() : UUID.fromString(UUID_STRING);
    }

    private Byte getByte() {
        return setRandomValues ? getRandomByte() : BYTE;
    }

    private BigInteger getBigInteger() {
        return setRandomValues ? BigInteger.valueOf(getLong()) : BIG_INTEGER;
    }

    private LocalTime getLocalTime() {
        return setRandomValues ? getRandomLocalTime() : LOCAL_TIME;
    }

    private OffsetDateTime getOffsetDateTime() {
        return setRandomValues ? getZonedDateTime().toOffsetDateTime() : OFFSET_DATE_TIME;
    }

    private OffsetTime getOffsetTime() {
        return setRandomValues ? getZonedDateTime().toOffsetDateTime().toOffsetTime() : OFFSET_TIME;
    }

    private Duration getDuration() {
        return setRandomValues ? Duration.ofSeconds(getLong()) : DURATION;
    }

    private Period getPeriod() {
        return setRandomValues ? Period.ofDays(getRandomInt()) : PERIOD;
    }

    private java.sql.Date getSqlDate() {
        return setRandomValues ? java.sql.Date.valueOf(getLocalDate()) : java.sql.Date.valueOf(LOCAL_DATE);
    }

    private Time getSqlTime() {
        return setRandomValues ? Time.valueOf(getRandomLocalTime()) : Time.valueOf(LOCAL_TIME);
    }

    private Timestamp getSqlTimestamp() {
        return setRandomValues ? Timestamp.from(getInstant()) : Timestamp.from(INSTANT);
    }

    private Currency getCurrency() {
        return setRandomValues ? AVAILABLE_CURRENCIES.get(getRandomInt(AVAILABLE_CURRENCIES.size())) : CURRENCY;
    }

    private Locale getLocale() {
        return setRandomValues ? AVAILABLE_LOCALES[getRandomInt(AVAILABLE_LOCALES.length)] : LOCALE;
    }

    private TimeZone getTimeZone() {
        return setRandomValues ? TimeZone.getTimeZone(AVAILABLE_TIMEZONE_IDS[getRandomInt(AVAILABLE_TIMEZONE_IDS.length)]) : TimeZone.getTimeZone("UTC");
    }

    private ZoneId getZoneId() {
        return setRandomValues ? ZoneId.of(AVAILABLE_ZONE_IDS.get(getRandomInt(AVAILABLE_ZONE_IDS.size()))) : ZONE_ID;
    }

    private ZoneOffset getZoneOffset() {
        return setRandomValues ? ZoneOffset.ofHours(getRandomInt(37) - 18) : ZONE_OFFSET;
    }

    private Year getYear() {
        return setRandomValues ? Year.from(getRandomLocalDate(5)) : YEAR;
    }

    private YearMonth getYearMonth() {
        return setRandomValues ? YearMonth.from(getRandomLocalDate(5)) : YEAR_MONTH;
    }

    private MonthDay getMonthDay() {
        return setRandomValues ? MonthDay.from(getRandomLocalDate(5)) : MONTH_DAY;
    }

    private File getFile() {
        return setRandomValues ? new File(getRandomString()) : FILE;
    }

    private Path getPath() {
        return setRandomValues ? Paths.get(getRandomString()) : PATH;
    }

    private URL getUrl() {
        return setRandomValues ? PopulateUtil.toUrl("http://example.com/" + getRandomString()) : URL;
    }

    private URI getUri() {
        return setRandomValues ? java.net.URI.create("http://example.com/" + getRandomString()) : URI;
    }

    private Charset getCharset() {
        return setRandomValues ? AVAILABLE_CHARSETS.get(getRandomInt(AVAILABLE_CHARSETS.size())) : CHARSET;
    }

    private Calendar getCalendar() {
        return setRandomValues ? new Calendar.Builder().setInstant(RandomUtil.getRandomLong()).build() : new GregorianCalendar(1970, Calendar.JANUARY, 1);
    }

    private BitSet getBitSet() {
        return setRandomValues ? BitSet.valueOf(new long[]{RandomUtil.getRandomLong()}) : BitSet.valueOf(new long[]{1L});
    }

    private OptionalInt getOptionalInt() {
        return setRandomValues ? OptionalInt.of(getRandomInt()) : OPTIONAL_INT;
    }

    private OptionalLong getOptionalLong() {
        return setRandomValues ? OptionalLong.of(getRandomInt()) : OPTIONAL_LONG;
    }

    private OptionalDouble getOptionalDouble() {
        return setRandomValues ? OptionalDouble.of(getRandomInt()) : OPTIONAL_DOUBLE;
    }

}