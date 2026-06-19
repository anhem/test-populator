package com.github.anhem.testpopulator.internal.object.util;

import com.github.anhem.testpopulator.internal.object.BuildType;
import com.github.anhem.testpopulator.internal.object.FormattingContext;
import com.github.anhem.testpopulator.internal.object.ObjectBuilder;
import com.github.anhem.testpopulator.internal.object.TemplateObjectBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArgumentFormatterUtilTest {

    @Test
    void formatEmptyListReturnsEmptyString() {
        String result = ArgumentFormatterUtil.format(List.of(), BuildType.CONSTRUCTOR, "name", new FormattingContext(false, 3));
        assertThat(result).isEmpty();
    }

    @Test
    void formatSingleArgument() {
        ObjectBuilder child = TemplateObjectBuilder.builder()
                .clazz(String.class)
                .buildType(BuildType.VALUE)
                .build();
        child.setValue("1");
        
        String result = ArgumentFormatterUtil.format(List.of(child), BuildType.CONSTRUCTOR, "name", new FormattingContext(false, 3));
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
        
        String result = ArgumentFormatterUtil.format(List.of(child1, child2), BuildType.CONSTRUCTOR, "name", new FormattingContext(false, 5));
        assertThat(result).isEqualTo("1, 2");
    }

    @Test
    void formatMultilineArguments() {
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
        
        String result = ArgumentFormatterUtil.format(List.of(child1, child2), BuildType.CONSTRUCTOR, "name", new FormattingContext(false, 1));
        String expected = System.lineSeparator() + "\t\t\t1," + System.lineSeparator() + "\t\t\t2" + System.lineSeparator() + "\t";
        assertThat(result).isEqualTo(expected);
    }
}
