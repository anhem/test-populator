package com.github.anhem.testpopulator.model.lombok;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value
@NonFinal
@SuperBuilder
public abstract class LombokAbstractImmutable {

    String stringValue;
    char primitiveCharacterValue;
    Character characterValue;
    int primitiveIntegerValue;
    Integer integerValue;
    long primitiveLongValue;
    Long longValue;
    double primitiveDoubleValue;
    Double doubleValue;
    LocalDate localDate;
    LocalDateTime localDateTime;
    List<String> listOfStrings;
    Set<String> setOfStrings;
    Map<Integer, String> mapOfIntegersToStrings;
    Map<String, Integer> mapOfStringsToIntegers;
    ArbitraryEnum arbitraryEnum;
    String[] arrayOfStrings;
    Date date;
}
