package com.github.anhem.testpopulator.model.lombok;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value
@Builder
public class Lombok {

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
}
