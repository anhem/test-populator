package com.github.anhem.testpopulator.model.java.setter;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Pojo {

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
    private ZonedDateTime zonedDateTime;
    private Instant instant;
    private List<String> listOfStrings;
    private Set<String> setOfStrings;
    private Map<Integer, String> mapOfIntegersToStrings;
    private Map<String, Integer> mapOfStringsToIntegers;
    private ArbitraryEnum arbitraryEnum;
    private String[] arrayOfStrings;
    private ArrayList<String> arrayList;
    private HashMap<String, String> hashMap;
    private HashSet<String> hashSet;
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
