package com.github.anhem.testpopulator.model.java.field;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.Getter;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class Fields {
    private String stringValue;
    private char primitiveCharacterValue;
    private Character characterValue;
    private int primitiveIntegerValue;
    private Integer integerValue;
    private long primitiveLongValue;
    private Long longValue;
    private double primitiveDoubleValue;
    private Double doubleValue;
    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private List<String> listOfStrings;
    private Set<String> setOfStrings;
    private Map<Integer, String> mapOfIntegersToStrings;
    private Map<String, Integer> mapOfStringsToIntegers;
    private ArbitraryEnum arbitraryEnum;
    private String[] arrayOfStrings;
    private Date date;
    private Byte byteValue;
    private byte primitiveByteValue;
    private Short shortValue;
    private short primitiveShortValue;
    private Float floatValue;
    private float primitiveFloatValue;
    private LocalTime localTime;
    private BigInteger bigInteger;
    private OffsetDateTime offsetDateTime;
    private OffsetTime offsetTime;
    private Duration duration;
    private Period period;
    private java.sql.Date sqlDate;
    private Time sqlTime;
    private Timestamp sqlTimestamp;
}
