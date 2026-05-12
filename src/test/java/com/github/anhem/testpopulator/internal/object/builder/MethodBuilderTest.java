package com.github.anhem.testpopulator.internal.object.builder;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MethodBuilderTest {

    @Test
    void build() {
        MethodBuilder builder = new MethodBuilder("setFoo", 1);
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .expectedChildren(0)
                .skipIfNull(true)
                .build();
        val.setValue("\"bar\"");
        builder.addChild(val);

        List<String> result = builder.build();

        assertThat(result).isEmpty();
    }
}
