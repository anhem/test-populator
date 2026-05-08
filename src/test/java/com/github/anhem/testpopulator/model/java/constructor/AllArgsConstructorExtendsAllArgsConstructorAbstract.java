package com.github.anhem.testpopulator.model.java.constructor;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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

@Getter
@EqualsAndHashCode(callSuper = true)
public class AllArgsConstructorExtendsAllArgsConstructorAbstract extends AllArgsConstructorAbstract {

    private final String anotherString;

    public AllArgsConstructorExtendsAllArgsConstructorAbstract(
            String stringValue,
            char primitiveCharacterValue,
            Character characterValue,
            int primitiveIntegerValue,
            Integer integerValue,
            long primitiveLongValue,
            Long longValue,
            double primitiveDoubleValue,
            Double doubleValue,
            boolean primitiveBooleanValue,
            Boolean booleanValue,
            BigDecimal bigDecimal,
            UUID uuid,
            Optional<Integer> optionalInteger,
            Optional<String> optionalString,
            Charset charset,
            Calendar calendar,
            LocalDate localDate,
            LocalDateTime localDateTime,
            ZonedDateTime zonedDateTime,
            Instant instant,
            List<String> listOfStrings,
            Set<String> setOfStrings,
            Queue<String> queue,
            Deque<String> deque,
            SortedSet<String> sortedSet,
            NavigableSet<String> navigableSet,
            Map<Integer, String> mapOfIntegersToStrings,
            Map<String, Integer> mapOfStringsToIntegers,
            SortedMap<String, Integer> sortedMap,
            NavigableMap<String, Integer> navigableMap,
            ArbitraryEnum arbitraryEnum,
            String[] arrayOfStrings,
            ArrayList<String> arrayList,
            LinkedList<String> linkedList,
            HashMap<String, String> hashMap,
            LinkedHashMap<String, String> linkedHashMap,
            HashSet<String> hashSet,
            LinkedHashSet<String> linkedHashSet,
            Date date,
            Byte byteValue,
            byte primitiveByteValue,
            Short shortValue,
            short primitiveShortValue,
            Float floatValue,
            float primitiveFloatValue,
            LocalTime localTime,
            BigInteger bigInteger,
            OffsetDateTime offsetDateTime,
            OffsetTime offsetTime,
            Duration duration,
            Period period,
            java.sql.Date sqlDate,
            Time sqlTime,
            Timestamp sqlTimestamp,
            Currency currency,
            Locale locale,
            TimeZone timeZone,
            ZoneId zoneId,
            ZoneOffset zoneOffset,
            Year year,
            YearMonth yearMonth,
            MonthDay monthDay,
            File file,
            Path path,
            URL url,
            URI uri,
            String anotherString
    ) {
        super(
                stringValue,
                primitiveCharacterValue,
                characterValue,
                primitiveIntegerValue,
                integerValue,
                primitiveLongValue,
                longValue,
                primitiveDoubleValue,
                doubleValue,
                primitiveBooleanValue,
                booleanValue,
                bigDecimal,
                uuid,
                optionalInteger,
                optionalString,
                charset,
                calendar,
                localDate,
                localDateTime,
                zonedDateTime,
                instant,
                listOfStrings,
                setOfStrings,
                queue,
                deque,
                sortedSet,
                navigableSet,
                mapOfIntegersToStrings,
                mapOfStringsToIntegers,
                sortedMap,
                navigableMap,
                arbitraryEnum,
                arrayOfStrings,
                arrayList,
                linkedList,
                hashMap,
                linkedHashMap,
                hashSet,
                linkedHashSet,
                date,
                byteValue,
                primitiveByteValue,
                shortValue,
                primitiveShortValue,
                floatValue,
                primitiveFloatValue,
                localTime,
                bigInteger,
                offsetDateTime,
                offsetTime,
                duration,
                period,
                sqlDate,
                sqlTime,
                sqlTimestamp,
                currency,
                locale,
                timeZone,
                zoneId,
                zoneOffset,
                year,
                yearMonth,
                monthDay,
                file,
                path,
                url,
                uri
        );
        this.anotherString = anotherString;
    }
}
