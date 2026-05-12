package com.github.anhem.testpopulator.internal.object;

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

import static com.github.anhem.testpopulator.internal.util.ObjectBuilderUtil.formatBytes;

public class ValueFormatter {

    private static final Map<Class<?>, Function<Object, String>> stringSuppliers = new HashMap<>();

    static {
        stringSuppliers.put(Integer.class, Object::toString);
        stringSuppliers.put(int.class, Object::toString);
        stringSuppliers.put(Long.class, object -> object + "L");
        stringSuppliers.put(long.class, object -> object + "L");
        stringSuppliers.put(Double.class, Object::toString);
        stringSuppliers.put(double.class, Object::toString);
        stringSuppliers.put(Boolean.class, Object::toString);
        stringSuppliers.put(boolean.class, Object::toString);
        stringSuppliers.put(BigDecimal.class, object -> String.format("BigDecimal.valueOf(%d)", ((BigDecimal) object).intValue()));
        stringSuppliers.put(String.class, object -> "\"" + object + "\"");
        stringSuppliers.put(Character.class, object -> "'" + object + "'");
        stringSuppliers.put(char.class, object -> "'" + object + "'");
        stringSuppliers.put(LocalDate.class, object -> String.format("LocalDate.parse(\"%s\")", object));
        stringSuppliers.put(LocalDateTime.class, object -> String.format("LocalDateTime.parse(\"%s\")", object));
        stringSuppliers.put(ZonedDateTime.class, object -> String.format("ZonedDateTime.parse(\"%s\")", object));
        stringSuppliers.put(Instant.class, object -> String.format("Instant.parse(\"%s\")", object));
        stringSuppliers.put(Date.class, object -> String.format("new Date(%sL)", ((Date) object).getTime()));
        stringSuppliers.put(UUID.class, object -> String.format("UUID.fromString(\"%s\")", object));
        stringSuppliers.put(Byte.class, object -> String.format("Byte.parseByte(\"%s\")", object));
        stringSuppliers.put(byte.class, object -> String.format("Byte.parseByte(\"%s\")", object));
        stringSuppliers.put(Short.class, object -> String.format("Short.parseShort(\"%s\")", object));
        stringSuppliers.put(short.class, object -> String.format("Short.parseShort(\"%s\")", object));
        stringSuppliers.put(Float.class, object -> object + "f");
        stringSuppliers.put(float.class, object -> object + "f");
        stringSuppliers.put(LocalTime.class, object -> String.format("LocalTime.parse(\"%s\")", object));
        stringSuppliers.put(BigInteger.class, object -> String.format("BigInteger.valueOf(%d)", ((BigInteger) object).intValue()));
        stringSuppliers.put(OffsetDateTime.class, object -> String.format("OffsetDateTime.parse(\"%s\")", object));
        stringSuppliers.put(OffsetTime.class, object -> String.format("OffsetTime.parse(\"%s\")", object));
        stringSuppliers.put(Duration.class, object -> String.format("Duration.ofSeconds(%d)", ((Duration) object).getSeconds()));
        stringSuppliers.put(Period.class, object -> String.format("Period.ofDays(%d)", ((Period) object).getDays()));
        stringSuppliers.put(java.sql.Date.class, object -> String.format("Date.valueOf(\"%s\")", object.toString()));
        stringSuppliers.put(Time.class, object -> String.format("Time.valueOf(\"%s\")", object.toString()));
        stringSuppliers.put(Timestamp.class, object -> String.format("Timestamp.valueOf(\"%s\")", object.toString()));
        stringSuppliers.put(Currency.class, object -> String.format("Currency.getInstance(\"%s\")", object));
        stringSuppliers.put(Locale.class, object -> String.format("Locale.forLanguageTag(\"%s\")", ((Locale) object).toLanguageTag()));
        stringSuppliers.put(TimeZone.class, object -> String.format("TimeZone.getTimeZone(\"%s\")", ((TimeZone) object).getID()));
        stringSuppliers.put(ZoneId.class, object -> String.format("ZoneId.of(\"%s\")", object));
        stringSuppliers.put(ZoneOffset.class, object -> String.format("ZoneOffset.of(\"%s\")", object));
        stringSuppliers.put(Year.class, object -> String.format("Year.of(%d)", ((Year) object).getValue()));
        stringSuppliers.put(YearMonth.class, object -> String.format("YearMonth.of(%d, %d)", ((YearMonth) object).getYear(), ((YearMonth) object).getMonthValue()));
        stringSuppliers.put(MonthDay.class, object -> String.format("MonthDay.of(%d, %d)", ((MonthDay) object).getMonthValue(), ((MonthDay) object).getDayOfMonth()));
        stringSuppliers.put(Month.class, object -> String.format("Month.%s", object));
        stringSuppliers.put(DayOfWeek.class, object -> String.format("DayOfWeek.%s", object));
        stringSuppliers.put(File.class, object -> String.format("new File(\"%s\")", ((File) object).getPath()));
        stringSuppliers.put(Path.class, object -> String.format("Path.of(\"%s\")", object.toString()));
        stringSuppliers.put(URL.class, object -> String.format("toUrl(\"%s\")", object));
        stringSuppliers.put(URI.class, object -> String.format("URI.create(\"%s\")", object));
        stringSuppliers.put(Charset.class, object -> String.format("Charset.forName(\"%s\")", ((Charset) object).name()));
        stringSuppliers.put(Calendar.class, object -> String.format("new Calendar.Builder().setInstant(%sL).build()", ((Calendar) object).getTimeInMillis()));
        stringSuppliers.put(BitSet.class, object -> String.format("BitSet.valueOf(new long[]{%sL})", ((BitSet) object).toLongArray()[0]));
        stringSuppliers.put(Throwable.class, object -> String.format("new Throwable(\"%s\")", ((Throwable) object).getMessage()));
        stringSuppliers.put(Exception.class, object -> String.format("new Exception(\"%s\")", ((Exception) object).getMessage()));
        stringSuppliers.put(RuntimeException.class, object -> String.format("new RuntimeException(\"%s\")", ((RuntimeException) object).getMessage()));
        stringSuppliers.put(Error.class, object -> String.format("new Error(\"%s\")", ((Error) object).getMessage()));
        stringSuppliers.put(ByteBuffer.class, object -> String.format("ByteBuffer.wrap(new byte[]{%s})", formatBytes(((ByteBuffer) object).array())));
        stringSuppliers.put(InetAddress.class, object -> String.format("toInetAddress(\"%s\")", ((InetAddress) object).getHostAddress()));
        stringSuppliers.put(Inet4Address.class, object -> String.format("(Inet4Address) toInetAddress(\"%s\")", ((Inet4Address) object).getHostAddress()));
        stringSuppliers.put(Inet6Address.class, object -> String.format("(Inet6Address) toInetAddress(\"%s\")", ((Inet6Address) object).getHostAddress()));
        stringSuppliers.put(InetSocketAddress.class, object -> String.format("new InetSocketAddress(toInetAddress(\"%s\"), %d)", ((InetSocketAddress) object).getAddress().getHostAddress(), ((InetSocketAddress) object).getPort()));
        stringSuppliers.put(CharSequence.class, object -> String.format("\"%s\"", object));
    }

    public static String format(Object object, Class<?> clazz) {
        Function<Object, String> stringSupplier = stringSuppliers.get(clazz);
        if (stringSupplier == null) {
            stringSupplier = stringSuppliers.get(object.getClass());
        }

        if (stringSupplier != null) {
            return stringSupplier.apply(object);
        }
        return null;
    }
}
