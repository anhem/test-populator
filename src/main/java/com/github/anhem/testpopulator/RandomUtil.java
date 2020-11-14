package com.github.anhem.testpopulator;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

class RandomUtil {

    private static final int RANDOM_INT_MAX_VALUE = 1000000;
    private static final int LEFT_LIMIT = 97; // letter 'a'
    private static final int RIGHT_LIMIT = 122; // letter 'z'
    private static final int STRING_LENGTH = 10;
    private static final Random random = new SecureRandom();

    private RandomUtil() {
    }

    public static int getRandomInt() {
        return random.nextInt(RANDOM_INT_MAX_VALUE);
    }

    public static String getRandomString() {
        StringBuilder buffer = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomLimitedInt = LEFT_LIMIT + ((random.nextInt() * (RIGHT_LIMIT - LEFT_LIMIT + 1)));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }

    public static Boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    public static LocalDate getRandomLocalDate() {
        return getRandomLocalDateTime().toLocalDate();
    }

    public static LocalDateTime getRandomLocalDateTime() {
        return LocalDateTime.now().minusYears(1).plusDays(random.nextInt(730));
    }

    public static Character getRandomCharacter() {
        return getRandomString().charAt(0);
    }

    public static <T> T getRandomEnum(Class<T> clazz) {
        List<T> enumValues = List.of(clazz.getEnumConstants());
        return enumValues.get(random.nextInt(enumValues.size()));
    }
}
