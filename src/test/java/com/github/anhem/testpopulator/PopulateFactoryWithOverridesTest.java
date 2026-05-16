package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.OverrideTarget;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import com.github.anhem.testpopulator.readme.model.MyUUID;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryWithOverridesTest {

    @Test
    void localOverrideTakesPrecedenceOverGlobalConfig() {
        String globalValue = "global";
        String localValue = "local";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(String.class, () -> globalValue)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        assertThat(populateFactory.populate(String.class)).isEqualTo(globalValue);
        assertThat(populateFactory.populate(String.class, String.class, () -> localValue)).isEqualTo(localValue);
        assertThat(populateFactory.populate(String.class)).isEqualTo(globalValue);
    }

    @Test
    void globalAndOverridesAreMerged() {
        String globalString = "globalString";
        Integer localInteger = 999;
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(String.class, () -> globalString)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        AllArgsConstructor result = populateFactory.populate(AllArgsConstructor.class, Integer.class, () -> localInteger);

        assertThat(result.getStringValue()).isEqualTo(globalString);
        assertThat(result.getIntegerValue()).isEqualTo(localInteger);
    }

    @Test
    void singleLocalOverride() {
        String localValue = "local";
        PopulateFactory populateFactory = new PopulateFactory();

        assertThat(populateFactory.populate(String.class, String.class, () -> localValue)).isEqualTo(localValue);
    }

    @Test
    void localOverridesWorkWithObjectFactory() {
        MyUUID localValue = new MyUUID(UUID.randomUUID().toString());
        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactoryEnabled(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        assertThat(populateFactory.populate(MyUUID.class, MyUUID.class, new OverridePopulate<>() {
            @Override
            public MyUUID create() {
                return localValue;
            }

            @Override
            public String createCode() {
                return "new MyUUID(\"uuid\")";
            }
        })).isEqualTo(localValue);
    }

    @Test
    void emptyOverridesMap() {
        PopulateFactory populateFactory = new PopulateFactory();

        assertThat(populateFactory.populate(String.class, Collections.emptyMap(), Collections.emptyMap())).isNotNull();
    }

    @Test
    void multipleOverrides() {
        PopulateConfig populateConfig = PopulateConfig.builder().build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        String localString = "localString";
        Integer localInteger = 123;

        Map<Object, OverridePopulate<?>> overrides = Map.of(
                String.class, () -> localString,
                Integer.class, () -> localInteger
        );

        assertThat(populateFactory.populate(String.class, overrides)).isEqualTo(localString);

        assertThat(populateFactory.populate(Integer.class, overrides)).isEqualTo(localInteger);
    }

    @Test
    void mixedOverrides() {
        PopulateFactory populateFactory = new PopulateFactory();
        Integer localInt = 123;
        String localString = "local";

        Map<Object, OverridePopulate<?>> overrides = Map.of(
                Integer.class, () -> localInt,
                OverrideTarget.of("setStringValue", String.class), () -> localString
        );

        Pojo pojo = populateFactory.populate(Pojo.class, overrides);

        assertThat(pojo.getIntegerValue()).isEqualTo(localInt);
        assertThat(pojo.getStringValue()).isEqualTo(localString);
    }

    @Test
    void pojoWithKotlinSupportEnabled() {
        PopulateConfig populateConfig = PopulateConfig.builder()
                .kotlinSupport(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);
        Pojo value = populateFactory.populate(Pojo.class);
        assertThat(value).isNotNull();
    }
}
