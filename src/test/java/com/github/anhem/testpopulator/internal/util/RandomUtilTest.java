package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.github.anhem.testpopulator.internal.util.RandomUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class RandomUtilTest {

    @Test
    void getRandomIntIsDifferentEachTime() {
        int random_1 = getRandomInt();
        int random_2 = getRandomInt();

        assertThat(random_1).isPositive();
        assertThat(random_2).isPositive();
        assertThat(random_1).isNotEqualTo(random_2);
    }

    @Test
    void getRandomStringIsDifferentEachTime() {
        String random_1 = getRandomString();
        String random_2 = getRandomString();

        assertThat(random_1).isNotNull();
        assertThat(random_2).isNotNull();
        assertThat(random_1).hasSize(STRING_LENGTH);
        assertThat(random_2).hasSize(STRING_LENGTH);
        assertThat(random_1).isNotEqualTo(random_2);
    }

    @Test
    void getRandomBooleanCanBeDifferent() {
        Boolean random_1 = getRandomBoolean();
        Boolean random_2 = getRandomBoolean();
        int retry = 0;
        while (random_1.equals(random_2) && retry < 10) {
            random_2 = getRandomBoolean();
            retry++;
        }

        assertThat(random_1).isNotNull();
        assertThat(random_2).isNotNull();
        assertThat(random_1).isNotEqualTo(random_2);
    }

    @Test
    void getRandomLocalDateIsDifferentEachTime() {
        LocalDate random_1 = getRandomLocalDate();
        LocalDate random_2 = getRandomLocalDate();

        assertThat(random_1).isNotNull();
        assertThat(random_2).isNotNull();
        assertThat(random_1).isNotEqualTo(random_2);
    }

    @Test
    void getRandomLocalDateTimeIsDifferentEachTime() {
        LocalDateTime random_1 = getRandomLocalDateTime();
        LocalDateTime random_2 = getRandomLocalDateTime();

        assertThat(random_1).isNotNull();
        assertThat(random_2).isNotNull();
        assertThat(random_1).isNotEqualTo(random_2);
    }

    @Test
    void getRandomCharacterCanBeDifferent() {
        Character random_1 = getRandomCharacter();
        Character random_2 = getRandomCharacter();
        int retry = 0;
        while (random_1.equals(random_2) && retry < 10) {
            random_2 = getRandomCharacter();
            retry++;
        }

        assertThat(random_1).isNotNull();
        assertThat(random_2).isNotNull();
        assertThat(random_1).isNotEqualTo(random_2);
    }

    @Test
    void getRandomEnumCanBeDifferent() {
        ArbitraryEnum random_1 = getRandomEnum(ArbitraryEnum.class);
        ArbitraryEnum random_2 = getRandomEnum(ArbitraryEnum.class);
        int retry = 0;
        while (random_1.equals(random_2) && retry < 10) {
            random_2 = getRandomEnum(ArbitraryEnum.class);
            retry++;
        }

        assertThat(random_1).isNotNull();
        assertThat(random_2).isNotNull();
        assertThat(random_1).isNotEqualTo(random_2);
    }

}
