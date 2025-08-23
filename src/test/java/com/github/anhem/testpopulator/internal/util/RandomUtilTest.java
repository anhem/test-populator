package com.github.anhem.testpopulator.internal.util;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.github.anhem.testpopulator.internal.util.RandomUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class RandomUtilTest {

    @Test
    void getRandomIntIsDifferentEachTime() {
        int random1 = getRandomInt();
        int random2 = getRandomInt();

        assertThat(random1).isPositive();
        assertThat(random2).isPositive();
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomShortIsDifferentEachTime() {
        short random1 = getRandomShort();
        short random2 = getRandomShort();

        assertThat(random1).isPositive();
        assertThat(random2).isPositive();
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomFloatIsDifferentEachTime() {
        float random1 = getRandomFloat();
        float random2 = getRandomFloat();

        assertThat(random1).isPositive();
        assertThat(random2).isPositive();
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomStringIsDifferentEachTime() {
        String random1 = getRandomString();
        String random2 = getRandomString();

        assertThat(random1).isNotNull();
        assertThat(random2).isNotNull();
        assertThat(random1).hasSize(STRING_LENGTH);
        assertThat(random2).hasSize(STRING_LENGTH);
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomBooleanCanBeDifferent() {
        Boolean random1 = getRandomBoolean();
        Boolean random2 = getRandomBoolean();
        int retry = 0;
        while (random1.equals(random2) && retry < 10) {
            random2 = getRandomBoolean();
            retry++;
        }

        assertThat(random1).isNotNull();
        assertThat(random2).isNotNull();
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomLocalDateIsDifferentEachTime() {
        LocalDate random1 = getRandomLocalDate();
        LocalDate random2 = getRandomLocalDate();

        assertThat(random1).isNotNull();
        assertThat(random2).isNotNull();
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomLocalDateTimeIsDifferentEachTime() {
        LocalDateTime random1 = getRandomLocalDateTime();
        LocalDateTime random2 = getRandomLocalDateTime();

        assertThat(random1).isNotNull();
        assertThat(random2).isNotNull();
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomCharacterCanBeDifferent() {
        Character random1 = getRandomCharacter();
        Character random2 = getRandomCharacter();
        int retry = 0;
        while (random1.equals(random2) && retry < 10) {
            random2 = getRandomCharacter();
            retry++;
        }

        assertThat(random1).isNotNull();
        assertThat(random2).isNotNull();
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomEnumCanBeDifferent() {
        ArbitraryEnum random1 = getRandomEnum(ArbitraryEnum.class, false);
        ArbitraryEnum random2 = getRandomEnum(ArbitraryEnum.class, false);
        int retry = 0;
        while (random1.equals(random2) && retry < 10) {
            random2 = getRandomEnum(ArbitraryEnum.class, false);
            retry++;
        }

        assertThat(random1).isNotNull();
        assertThat(random2).isNotNull();
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomByteIsDifferentEachTime() {
        Byte random1 = getRandomByte();
        Byte random2 = getRandomByte();

        assertThat(random1).isNotNull();
        assertThat(random2).isNotNull();
        assertThat(random1).isNotEqualTo(random2);
    }

    @Test
    void getRandomLocalTimeIsDifferentEachTime() {
        LocalTime random1 = getRandomLocalTime();
        LocalTime random2 = getRandomLocalTime();

        assertThat(random1).isNotNull();
        assertThat(random2).isNotNull();
        assertThat(random1).isNotEqualTo(random2);
    }
}
