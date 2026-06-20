package com.github.anhem.testpopulator.internal.object.util;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.exception.ObjectException;

import com.github.anhem.testpopulator.internal.object.ObjectBuilder;
import com.github.anhem.testpopulator.internal.object.TemplateObjectBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static com.github.anhem.testpopulator.internal.object.BuildType.VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueStringifierUtilTest {

    @Test
    void stringifyEnum() {
        ObjectBuilder builder = TemplateObjectBuilder.builder().buildType(VALUE).build();
        PopulateConfig config = PopulateConfig.builder().wildcardFallbackType(String.class).build();
        String result = ValueStringifierUtil.stringify(Month.JANUARY, Month.class, "month", config, builder);
        assertThat(result).isEqualTo("JANUARY");
    }

    @Test
    void stringifyWithFormatter() {
        ObjectBuilder builder = TemplateObjectBuilder.builder()
                .useFullyQualifiedName(true)
                .buildType(VALUE)
                .build();
        PopulateConfig config = PopulateConfig.builder().wildcardFallbackType(String.class).build();
        LocalDate date = LocalDate.of(2023, 1, 1);
        String result = ValueStringifierUtil.stringify(date, LocalDate.class, "date", config, builder);
        assertThat(result).isEqualTo("LocalDate.parse(\"2023-01-01\")");
    }

    @Test
    void stringifyClassOverride() {
        ObjectBuilder builder = TemplateObjectBuilder.builder().buildType(VALUE).build();
        PopulateConfig config = PopulateConfig.builder().wildcardFallbackType(String.class)
                .addOverride(CustomType.class, new CustomOverride())
                .build();
        
        String result = ValueStringifierUtil.stringify(new CustomType(), CustomType.class, "custom", config, builder);
        assertThat(result).isEqualTo("new CustomType()");
    }

    @Test
    void stringifyUnsupportedTypeThrowsException() {
        ObjectBuilder builder = TemplateObjectBuilder.builder().buildType(VALUE).build();
        PopulateConfig config = PopulateConfig.builder().wildcardFallbackType(String.class).build();
        
        assertThatThrownBy(() -> ValueStringifierUtil.stringify(new CustomType(), CustomType.class, "custom", config, builder))
                .isInstanceOf(ObjectException.class)
                .hasMessageContaining("Failed to find type to create value for");
    }

    static class CustomType {}

    static class CustomOverride implements OverridePopulate<CustomType> {
        @Override
        public CustomType create() {
            return new CustomType();
        }

        @Override
        public String createCode() {
            return "new CustomType()";
        }
    }
}
