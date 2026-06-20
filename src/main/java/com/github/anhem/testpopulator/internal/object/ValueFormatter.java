package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.config.Language;
import com.github.anhem.testpopulator.exception.ObjectException;

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
import java.util.function.Function;

import static com.github.anhem.testpopulator.internal.object.util.ObjectBuilderUtil.formatBytes;

public class ValueFormatter {

    private static final Map<Class<?>, Function<Object, String>> javaStringSuppliers = new HashMap<>();
    private static final Map<Class<?>, Function<Object, String>> kotlinStringSuppliers = new HashMap<>();
    static {
        javaStringSuppliers.put(Integer.class, Object::toString);
        javaStringSuppliers.put(int.class, Object::toString);
        javaStringSuppliers.put(Long.class, object -> object + "L");
        javaStringSuppliers.put(long.class, object -> object + "L");
        javaStringSuppliers.put(Double.class, Object::toString);
        javaStringSuppliers.put(double.class, Object::toString);
        javaStringSuppliers.put(Boolean.class, Object::toString);
        javaStringSuppliers.put(boolean.class, Object::toString);
        javaStringSuppliers.put(BigDecimal.class, object -> String.format("BigDecimal.valueOf(%d)", ((BigDecimal) object).intValue()));
        javaStringSuppliers.put(String.class, object -> "\"" + object + "\"");
        javaStringSuppliers.put(Character.class, object -> "'" + object + "'");
        javaStringSuppliers.put(char.class, object -> "'" + object + "'");
        javaStringSuppliers.put(LocalDate.class, object -> String.format("LocalDate.parse(\"%s\")", object));
        javaStringSuppliers.put(LocalDateTime.class, object -> String.format("LocalDateTime.parse(\"%s\")", object));
        javaStringSuppliers.put(ZonedDateTime.class, object -> String.format("ZonedDateTime.parse(\"%s\")", object));
        javaStringSuppliers.put(Instant.class, object -> String.format("Instant.parse(\"%s\")", object));
        javaStringSuppliers.put(Date.class, object -> String.format("new Date(%sL)", ((Date) object).getTime()));
        javaStringSuppliers.put(UUID.class, object -> String.format("UUID.fromString(\"%s\")", object));
        javaStringSuppliers.put(Byte.class, object -> String.format("Byte.parseByte(\"%s\")", object));
        javaStringSuppliers.put(byte.class, object -> String.format("Byte.parseByte(\"%s\")", object));
        javaStringSuppliers.put(Short.class, object -> String.format("Short.parseShort(\"%s\")", object));
        javaStringSuppliers.put(short.class, object -> String.format("Short.parseShort(\"%s\")", object));
        javaStringSuppliers.put(Float.class, object -> object + "f");
        javaStringSuppliers.put(float.class, object -> object + "f");
        javaStringSuppliers.put(LocalTime.class, object -> String.format("LocalTime.parse(\"%s\")", object));
        javaStringSuppliers.put(BigInteger.class, object -> String.format("BigInteger.valueOf(%d)", ((BigInteger) object).intValue()));
        javaStringSuppliers.put(OffsetDateTime.class, object -> String.format("OffsetDateTime.parse(\"%s\")", object));
        javaStringSuppliers.put(OffsetTime.class, object -> String.format("OffsetTime.parse(\"%s\")", object));
        javaStringSuppliers.put(Duration.class, object -> String.format("Duration.ofSeconds(%d)", ((Duration) object).getSeconds()));
        javaStringSuppliers.put(Period.class, object -> String.format("Period.ofDays(%d)", ((Period) object).getDays()));
        javaStringSuppliers.put(java.sql.Date.class, object -> String.format("Date.valueOf(\"%s\")", object.toString()));
        javaStringSuppliers.put(Time.class, object -> String.format("Time.valueOf(\"%s\")", object.toString()));
        javaStringSuppliers.put(Timestamp.class, object -> String.format("Timestamp.valueOf(\"%s\")", object.toString()));
        javaStringSuppliers.put(Currency.class, object -> String.format("Currency.getInstance(\"%s\")", object));
        javaStringSuppliers.put(Locale.class, object -> String.format("Locale.forLanguageTag(\"%s\")", ((Locale) object).toLanguageTag()));
        javaStringSuppliers.put(TimeZone.class, object -> String.format("TimeZone.getTimeZone(\"%s\")", ((TimeZone) object).getID()));
        javaStringSuppliers.put(ZoneId.class, object -> String.format("ZoneId.of(\"%s\")", object));
        javaStringSuppliers.put(ZoneOffset.class, object -> String.format("ZoneOffset.of(\"%s\")", object));
        javaStringSuppliers.put(Year.class, object -> String.format("Year.of(%d)", ((Year) object).getValue()));
        javaStringSuppliers.put(YearMonth.class, object -> String.format("YearMonth.of(%d, %d)", ((YearMonth) object).getYear(), ((YearMonth) object).getMonthValue()));
        javaStringSuppliers.put(MonthDay.class, object -> String.format("MonthDay.of(%d, %d)", ((MonthDay) object).getMonthValue(), ((MonthDay) object).getDayOfMonth()));
        javaStringSuppliers.put(Month.class, object -> String.format("Month.%s", object));
        javaStringSuppliers.put(DayOfWeek.class, object -> String.format("DayOfWeek.%s", object));
        javaStringSuppliers.put(File.class, object -> String.format("new File(\"%s\")", ((File) object).getAbsolutePath()));
        javaStringSuppliers.put(Path.class, object -> String.format("Path.of(\"%s\")", object.toString()));
        javaStringSuppliers.put(URL.class, object -> String.format("toUrl(\"%s\")", object));
        javaStringSuppliers.put(URI.class, object -> String.format("URI.create(\"%s\")", object));
        javaStringSuppliers.put(Charset.class, object -> String.format("Charset.forName(\"%s\")", ((Charset) object).name()));
        javaStringSuppliers.put(Calendar.class, object -> String.format("new Calendar.Builder().setInstant(%sL).build()", ((Calendar) object).getTimeInMillis()));
        javaStringSuppliers.put(BitSet.class, object -> String.format("BitSet.valueOf(new long[]{%sL})", ((BitSet) object).toLongArray()[0]));
        javaStringSuppliers.put(Throwable.class, object -> String.format("new Throwable(\"%s\")", ((Throwable) object).getMessage()));
        javaStringSuppliers.put(Exception.class, object -> String.format("new Exception(\"%s\")", ((Exception) object).getMessage()));
        javaStringSuppliers.put(RuntimeException.class, object -> String.format("new RuntimeException(\"%s\")", ((RuntimeException) object).getMessage()));
        javaStringSuppliers.put(Error.class, object -> String.format("new Error(\"%s\")", ((Error) object).getMessage()));
        javaStringSuppliers.put(ByteBuffer.class, object -> String.format("ByteBuffer.wrap(new byte[]{%s})", formatBytes(((ByteBuffer) object).array())));
        javaStringSuppliers.put(InetAddress.class, object -> String.format("toInetAddress(\"%s\")", ((InetAddress) object).getHostAddress()));
        javaStringSuppliers.put(Inet4Address.class, object -> String.format("(Inet4Address) toInetAddress(\"%s\")", ((Inet4Address) object).getHostAddress()));
        javaStringSuppliers.put(Inet6Address.class, object -> String.format("(Inet6Address) toInetAddress(\"%s\")", ((Inet6Address) object).getHostAddress()));
        javaStringSuppliers.put(InetSocketAddress.class, object -> String.format("new InetSocketAddress(toInetAddress(\"%s\"), %d)", ((InetSocketAddress) object).getAddress().getHostAddress(), ((InetSocketAddress) object).getPort()));
        javaStringSuppliers.put(CharSequence.class, object -> String.format("\"%s\"", object));
        javaStringSuppliers.put(Class.class, object -> String.format("%s.class", ((Class<?>) object).getName()));
        javaStringSuppliers.put(ObjectException.class, object -> String.format("new ObjectException(\"%s\")", ((ObjectException) object).getMessage()));
        javaStringSuppliers.put(Enum.class, object -> String.format("%s.%s", object.getClass().getSimpleName(), object));
        javaStringSuppliers.put(byte[].class, object -> String.format("new byte[]{%s}", formatBytes((byte[]) object)));

        kotlinStringSuppliers.putAll(javaStringSuppliers);
        kotlinStringSuppliers.put(Date.class, object -> String.format("java.util.Date(%sL)", ((Date) object).getTime()));
        kotlinStringSuppliers.put(File.class, object -> String.format("java.io.File(\"%s\")", ((File) object).getAbsolutePath()));
        kotlinStringSuppliers.put(ObjectException.class, object -> String.format("com.github.anhem.testpopulator.exception.ObjectException(\"%s\")", ((ObjectException) object).getMessage()));
        kotlinStringSuppliers.put(RuntimeException.class, object -> String.format("RuntimeException(\"%s\")", ((RuntimeException) object).getMessage()));
        kotlinStringSuppliers.put(Throwable.class, object -> String.format("Throwable(\"%s\")", ((Throwable) object).getMessage()));
        kotlinStringSuppliers.put(Exception.class, object -> String.format("Exception(\"%s\")", ((Exception) object).getMessage()));
        kotlinStringSuppliers.put(Error.class, object -> String.format("Error(\"%s\")", ((Error) object).getMessage()));
        kotlinStringSuppliers.put(Enum.class, object -> String.format("%s.%s", object.getClass().getSimpleName(), object));
    }

    private ValueFormatter() {
    }

    public static String format(Object object, Class<?> clazz, Language language) {
        Map<Class<?>, Function<Object, String>> stringSuppliers = language == Language.KOTLIN ? kotlinStringSuppliers : javaStringSuppliers;
        Function<Object, String> function = stringSuppliers.get(clazz);
        if (function == null) {
            function = stringSuppliers.get(object.getClass());
        }

        if (function != null) {
            return function.apply(object);
        }
        return null;
    }
}
