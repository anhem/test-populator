package com.github.anhem.testpopulator.internal.object;

import com.github.anhem.testpopulator.config.Language;
import com.github.anhem.testpopulator.exception.ObjectException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ValueFormatterTest {

    @Test
    void canFormatJavaTypes() {
        assertThat(ValueFormatter.format("test", String.class, Language.JAVA)).isEqualTo("\"test\"");
        assertThat(ValueFormatter.format(1, Integer.class, Language.JAVA)).isEqualTo("1");
        assertThat(ValueFormatter.format(1L, Long.class, Language.JAVA)).isEqualTo("1L");
        assertThat(ValueFormatter.format(BigDecimal.ONE, BigDecimal.class, Language.JAVA)).isEqualTo("BigDecimal.valueOf(1)");
        assertThat(ValueFormatter.format(LocalDate.parse("2023-01-01"), LocalDate.class, Language.JAVA)).isEqualTo("LocalDate.parse(\"2023-01-01\")");
        
        Date date = new Date(1672531200000L);
        assertThat(ValueFormatter.format(date, Date.class, Language.JAVA)).isEqualTo("new Date(1672531200000L)");
        
        File file = new File("test.txt");
        assertThat(ValueFormatter.format(file, File.class, Language.JAVA)).isEqualTo(String.format("new File(\"%s\")", file.getAbsolutePath()));
        
        Exception exception = new Exception("test exception");
        assertThat(ValueFormatter.format(exception, Exception.class, Language.JAVA)).isEqualTo("new Exception(\"test exception\")");
        
        ObjectException objectException = new ObjectException("test object exception");
        assertThat(ValueFormatter.format(objectException, ObjectException.class, Language.JAVA)).isEqualTo("new ObjectException(\"test object exception\")");
    }

    @Test
    void canFormatKotlinTypes() {
        assertThat(ValueFormatter.format("test", String.class, Language.KOTLIN)).isEqualTo("\"test\"");
        assertThat(ValueFormatter.format(1, Integer.class, Language.KOTLIN)).isEqualTo("1");
        assertThat(ValueFormatter.format(1L, Long.class, Language.KOTLIN)).isEqualTo("1L");
        assertThat(ValueFormatter.format(BigDecimal.ONE, BigDecimal.class, Language.KOTLIN)).isEqualTo("BigDecimal.valueOf(1)");
        assertThat(ValueFormatter.format(LocalDate.parse("2023-01-01"), LocalDate.class, Language.KOTLIN)).isEqualTo("LocalDate.parse(\"2023-01-01\")");

        Date date = new Date(1672531200000L);
        assertThat(ValueFormatter.format(date, Date.class, Language.KOTLIN)).isEqualTo("java.util.Date(1672531200000L)");

        File file = new File("test.txt");
        assertThat(ValueFormatter.format(file, File.class, Language.KOTLIN)).isEqualTo(String.format("java.io.File(\"%s\")", file.getAbsolutePath()));

        Exception exception = new Exception("test exception");
        assertThat(ValueFormatter.format(exception, Exception.class, Language.KOTLIN)).isEqualTo("Exception(\"test exception\")");
        
        ObjectException objectException = new ObjectException("test object exception");
        assertThat(ValueFormatter.format(objectException, ObjectException.class, Language.KOTLIN)).isEqualTo("com.github.anhem.testpopulator.exception.ObjectException(\"test object exception\")");
    }

    @Test
    void returnsNullForUnknownType() {
        assertThat(ValueFormatter.format(new Object(), Object.class, Language.JAVA)).isEqualTo("new java.lang.Object()");
        assertThat(ValueFormatter.format(new Object(), Object.class, Language.KOTLIN)).isEqualTo("new java.lang.Object()");
    }
    
    @Test
    void fallsBackToObjectClassIfClazzNotConfigured() {
        assertThat(ValueFormatter.format("test", CharSequence.class, Language.JAVA)).isEqualTo("\"test\"");
    }
}
