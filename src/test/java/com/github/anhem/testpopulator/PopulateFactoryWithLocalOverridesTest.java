package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.java.constructor.AllArgsConstructor;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import com.github.anhem.testpopulator.readme.model.MyUUID;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryWithLocalOverridesTest {

    @Test
    void localOverrideTakesPrecedenceOverGlobalConfig() {
        String globalValue = "global";
        String localValue = "local";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(String.class, () -> globalValue)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        assertThat(populateFactory.populate(String.class)).isEqualTo(globalValue);
        assertThat(populateFactory.populate(String.class, Map.of(String.class, () -> localValue))).isEqualTo(localValue);
        assertThat(populateFactory.populate(String.class)).isEqualTo(globalValue);
    }

    @Test
    void globalAndLocalOverridesAreMerged() {
        String globalString = "globalString";
        Integer localInteger = 999;
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(String.class, () -> globalString)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        AllArgsConstructor result = populateFactory.populate(AllArgsConstructor.class, Map.of(Integer.class, () -> localInteger));

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
    void emptyLocalOverridesMap() {
        PopulateFactory populateFactory = new PopulateFactory();

        assertThat(populateFactory.populate(String.class, Map.of())).isNotNull();
    }

    @Test
    void multipleLocalOverrides() {
        PopulateConfig populateConfig = PopulateConfig.builder().build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);

        Map<Class<?>, Object> overrides = Map.of(
                String.class, "localString",
                Integer.class, 123
        );

        assertThat(populateFactory.populate(String.class, Map.of(
                String.class, () -> overrides.get(String.class),
                Integer.class, () -> overrides.get(Integer.class)
        ))).isEqualTo(overrides.get(String.class));

        assertThat(populateFactory.populate(Integer.class, Map.of(
                String.class, () -> overrides.get(String.class),
                Integer.class, () -> overrides.get(Integer.class)
        ))).isEqualTo(overrides.get(Integer.class));
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
