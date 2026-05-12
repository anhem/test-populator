package com.github.anhem.testpopulator.internal.object;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateObjectBuilderTest {

    @Test
    void buildParameterized() {
        TemplateObjectBuilder builder = TemplateObjectBuilder.builder()
                .clazz(Optional.class)
                .name("optional")
                .buildType(BuildType.STATIC_METHOD)
                .codeTemplate(CodeTemplate.OPTIONAL)
                .expectedChildren(1)
                .parameterized(true)
                .factoryClassName("Optional")
                .methodName("ofNullable")
                .build();
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .build();
        val.setValue("\"foo\"");
        builder.addChild(val);

        List<String> result = builder.build();

        assertThat(result).containsExactly("public static final Optional<String> optional = Optional.ofNullable(\"foo\");");
    }

    @Test
    void buildNonParameterized() {
        TemplateObjectBuilder builder = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string")
                .buildType(BuildType.VALUE)
                .codeTemplate(CodeTemplate.VALUE)
                .expectedChildren(0)
                .build();
        builder.setValue("\"foo\"");

        List<String> result = builder.build();

        assertThat(result).containsExactly("public static final String string = \"foo\";");
    }

    @Test
    void buildArray() {
        TemplateObjectBuilder builder = TemplateObjectBuilder.builder()
                .clazz(int.class)
                .name("myArray")
                .buildType(BuildType.ARRAY)
                .codeTemplate(CodeTemplate.ARRAY)
                .expectedChildren(1)
                .build();
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(int.class)
                .name("int_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .build();
        val.setValue("1");
        builder.addChild(val);

        List<String> result = builder.build();

        assertThat(result).containsExactly("public static final int[] myArray = new int[]{1};");
    }

    @Test
    void buildWithClearArgsIfNullChild() {
        TemplateObjectBuilder builder = TemplateObjectBuilder.builder()
                .clazz(List.class)
                .name("listOf")
                .buildType(BuildType.STATIC_METHOD)
                .codeTemplate(CodeTemplate.IMMUTABLE)
                .expectedChildren(1)
                .parameterized(true)
                .factoryClassName("List")
                .methodName("of")
                .clearArgsIfNullChild(true)
                .build();
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .build();
        val.setValue("null");
        builder.addChild(val);

        List<String> result = builder.build();

        assertThat(result).containsExactly("public static final List<String> listOf = List.of();");
    }

    @Test
    void buildMethod() {
        TemplateObjectBuilder builder = TemplateObjectBuilder.builder()
                .name("setFoo")
                .buildType(BuildType.METHOD)
                .expectedChildren(1)
                .build();
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .expectedChildren(0)
                .build();
        val.setValue("\"bar\"");
        builder.addChild(val);

        List<String> result = builder.build();

        assertThat(result).isEmpty();
    }

    @Test
    void buildFluentBuilder() {
        TemplateObjectBuilder builder = TemplateObjectBuilder.builder()
                .clazz(Object.class)
                .name("myObject")
                .buildType(BuildType.BUILDER)
                .codeTemplate(CodeTemplate.BUILDER)
                .expectedChildren(1)
                .methodName("builder")
                .buildMethodName("build")
                .build();
        TemplateObjectBuilder method = TemplateObjectBuilder.builder()
                .name("name")
                .buildType(BuildType.METHOD)
                .expectedChildren(1)
                .build();
        TemplateObjectBuilder val = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .name("string_0")
                .buildType(BuildType.VALUE)
                .expectedChildren(0)
                .build();
        val.setValue("\"foo\"");
        method.addChild(val);
        builder.addChild(method);

        List<String> result = builder.build();

        assertThat(result).containsExactly(
                "public static final Object myObject = Object.builder()",
                "    .name(\"foo\")",
                "    .build();"
        );
    }
}
