package com.github.anhem.testpopulator.internal.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class RandomUtil {

    private static final int RANDOM_INT_MAX_VALUE = 1000000;
    private static final int LEFT_LIMIT = 97; // letter 'a'
    private static final int RIGHT_LIMIT = 122; // letter 'z'
    static final int STRING_LENGTH = 10;
    private static final Random random = new SecureRandom();

    private RandomUtil() {
    }

    public static int getRandomInt() {
        return random.nextInt(RANDOM_INT_MAX_VALUE);
    }

    public static String getRandomString() {
        return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .limit(STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static Boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    public static LocalDate getRandomLocalDate() {
        return getRandomLocalDateTime().toLocalDate();
    }

    public static LocalDateTime getRandomLocalDateTime() {
        return LocalDateTime.now().minusYears(1).plusDays(random.nextInt(365));
    }

    public static Character getRandomCharacter() {
        return getRandomString().charAt(0);
    }

    public static <T> T getRandomEnum(Class<T> clazz) {
        List<T> enumValues = List.of(clazz.getEnumConstants());
        return enumValues.get(random.nextInt(enumValues.size()));
    }

    public static Byte getRandomByte() {
        byte[] bytes = new byte[1];
        random.nextBytes(bytes);
        return bytes[0];
    }
}
