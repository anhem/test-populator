package com.github.anhem.testpopulator.model.immutables;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value.Immutable
public interface ImmutablesInterface {

    String getStringValue();

    char getPrimitiveCharacterValue();

    Character getCharacterValue();

    int getPrimitiveIntegerValue();

    Integer getIntegerValue();

    long getPrimitiveLongValue();

    Long getLongValue();

    double getPrimitiveDoubleValue();

    Double getDoubleValue();

    LocalDate getLocalDate();

    LocalDateTime getLocalDateTime();

    List<String> getListOfStrings();

    Set<String> getSetOfStrings();

    Map<Integer, String> getMapOfIntegersToStrings();

    Map<String, Integer> getMapOfStringsToIntegers();

    ArbitraryEnum getArbitraryEnum();

    String[] getArrayOfStrings();

    Date getDate();

}
