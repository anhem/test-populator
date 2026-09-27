package com.github.anhem.testpopulator.internal.object.util;

import com.github.anhem.testpopulator.internal.object.BuildType;

import com.github.anhem.testpopulator.internal.object.ObjectBuilder;
import com.github.anhem.testpopulator.internal.object.TemplateObjectBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArgumentFormatterUtilTest {

    @Test
    void formatEmptyListReturnsEmptyString() {
        String result = ArgumentFormatterUtil.format(List.of(), BuildType.CONSTRUCTOR, "name", false, false);
        assertThat(result).isEmpty();
    }

    @Test
    void formatSingleArgument() {
        ObjectBuilder child = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .buildType(BuildType.VALUE)
                .build();
        child.setValue("1");

        String result = ArgumentFormatterUtil.format(List.of(child), BuildType.CONSTRUCTOR, "name", false, false);
        assertThat(result).isEqualTo("1");
    }

    @Test
    void formatMultipleArguments() {
        ObjectBuilder child1 = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .buildType(BuildType.VALUE)
                .build();
        child1.setValue("1");
        ObjectBuilder child2 = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .buildType(BuildType.VALUE)
                .build();
        child2.setValue("2");

        String result = ArgumentFormatterUtil.format(List.of(child1, child2), BuildType.CONSTRUCTOR, "name", false, false);
        assertThat(result).isEqualTo("1, 2");
    }

    @Test
    void formatMultilineArguments() {
        ObjectBuilder child1 = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .buildType(BuildType.VALUE)
                .build();
        String longStr = "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901";
        child1.setValue(longStr);
        ObjectBuilder child2 = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .buildType(BuildType.VALUE)
                .build();
        child2.setValue(longStr);

        String result = ArgumentFormatterUtil.format(List.of(child1, child2), BuildType.CONSTRUCTOR, "name", false, false);
        String expected = System.lineSeparator() + "\t\t\t" + longStr + "," + System.lineSeparator() + "\t\t\t" + longStr + System.lineSeparator() + "\t";
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void formatMapArgumentsJavaStyleUsesComma() {
        ObjectBuilder key = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .buildType(BuildType.VALUE)
                .build();
        key.setValue("\"myKey\"");
        ObjectBuilder value = TemplateObjectBuilder.builder()
                .clazz(Integer.class)
                .buildType(BuildType.VALUE)
                .build();
        value.setValue("42");

        String result = ArgumentFormatterUtil.format(List.of(key, value), BuildType.MAP, "MAP_0", false, false);
        assertThat(result).isEqualTo("\"myKey\", 42");
    }

    @Test
    void formatMapArgumentsKotlinStyleUsesToInfix() {
        ObjectBuilder key = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .buildType(BuildType.VALUE)
                .build();
        key.setValue("\"myKey\"");
        ObjectBuilder value = TemplateObjectBuilder.builder()
                .clazz(Integer.class)
                .buildType(BuildType.VALUE)
                .build();
        value.setValue("42");

        String result = ArgumentFormatterUtil.format(List.of(key, value), BuildType.MAP, "MAP_0", false, true);
        assertThat(result).isEqualTo("\"myKey\" to 42");
    }
}
