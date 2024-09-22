package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.override.MyUUID;
import com.github.anhem.testpopulator.model.java.override.MyUUIDOverridePopulate;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.github.anhem.testpopulator.testutil.AssertTestUtil.assertRandomlyPopulatedValues;
import static com.github.anhem.testpopulator.testutil.GeneratedCodeUtil.assertGeneratedCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PopulateFactoryWithOverridePopulateTest {

    private PopulateFactory populateFactory;
    private PopulateConfig populateConfig;

    @BeforeEach
    void setUp() {
        populateConfig = PopulateConfig.builder()
                .overridePopulate(MyUUID.class, new MyUUIDOverridePopulate())
                .overridePopulate(Integer.class, () -> -1)
                .overridePopulate(ZonedDateTime.class, ZonedDateTime::now)
                .objectFactoryEnabled(true)
                .build();
        populateFactory = new PopulateFactory(populateConfig);
    }

    @Test
    void myUUID() {
        MyUUID value_1 = populateAndAssertWithGeneratedCode(MyUUID.class);
        MyUUID value_2 = populateAndAssertWithGeneratedCode(MyUUID.class);

        assertThat(value_1).usingRecursiveAssertion().isEqualTo(value_2);
    }

    @Test
    void PojoWithOverrides() {
        Pojo value_1 = populateAndAssertWithGeneratedCode(Pojo.class);
        Pojo value_2 = populateAndAssertWithGeneratedCode(Pojo.class);

        assertRandomlyPopulatedValues(value_1, value_2);
        assertThat(value_1.getIntegerValue()).isEqualTo(-1);
        assertThat(value_1.getZonedDateTime()).isCloseTo(ZonedDateTime.now(), within(1, ChronoUnit.SECONDS));
        assertThat(value_2.getIntegerValue()).isEqualTo(-1);
        assertThat(value_2.getZonedDateTime()).isCloseTo(ZonedDateTime.now(), within(1, ChronoUnit.SECONDS));
        assertThat(value_1.getZonedDateTime()).isNotEqualTo(value_2.getZonedDateTime());
    }

    private <T> T populateAndAssertWithGeneratedCode(Class<T> clazz) {
        assertThat(populateConfig.isObjectFactoryEnabled()).isTrue();
        T value = populateFactory.populate(clazz);
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(clazz);
        assertGeneratedCode(value, populateConfig);

        return value;
    }
}
