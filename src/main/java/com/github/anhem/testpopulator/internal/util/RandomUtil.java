package com.github.anhem.testpopulator.internal.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomUtil {

    private static final int RANDOM_INT_MAX_VALUE = 1000000;
    private static final int LEFT_LIMIT = 97; // letter 'a'
    private static final int RIGHT_LIMIT = 122; // letter 'z'
    static final int STRING_LENGTH = 10;
    private static final int DAY_IN_SECONDS = 86400;
    private static final Random RANDOM = new SecureRandom();

    private RandomUtil() {
    }

    public static int getRandomInt() {
        return RANDOM.nextInt(RANDOM_INT_MAX_VALUE);
    }

    public static int getRandomInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    public static int getRandomInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static short getRandomShort() {
        return (short) RANDOM.nextInt(Short.MAX_VALUE + 1);
    }

    public static Float getRandomFloat() {
        return RANDOM.nextFloat();
    }

    public static String getRandomString() {
        return RANDOM.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .limit(STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static Boolean getRandomBoolean() {
        return RANDOM.nextBoolean();
    }

    public static LocalDate getRandomLocalDate() {
        return getRandomLocalDate(1);
    }

    public static LocalDate getRandomLocalDate(int yearRange) {
        return LocalDate.now().minusYears(yearRange).plusDays(RANDOM.nextInt(yearRange * 2 * 365 + yearRange));
    }

    public static LocalDateTime getRandomLocalDateTime() {
        return getRandomLocalDate().atTime(getRandomLocalTime());
    }

    public static Character getRandomCharacter() {
        return getRandomString().charAt(0);
    }

    public static <T> T getRandomEnum(Class<T> clazz, boolean removeUnrecognized) {
        List<T> enumValues = Stream.of(clazz.getEnumConstants())
                .filter(enumValue -> !removeUnrecognized || !enumValue.toString().equals("UNRECOGNIZED"))
                .collect(Collectors.toList());
        return enumValues.get(RANDOM.nextInt(enumValues.size()));
    }

    public static Byte getRandomByte() {
        byte[] bytes = new byte[1];
        RANDOM.nextBytes(bytes);
        return bytes[0];
    }

    public static LocalTime getRandomLocalTime() {
        return LocalTime.ofSecondOfDay(RANDOM.nextInt(DAY_IN_SECONDS));
    }
}
