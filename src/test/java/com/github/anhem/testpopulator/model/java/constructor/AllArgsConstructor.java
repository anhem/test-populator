package com.github.anhem.testpopulator.model.java.constructor;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AllArgsConstructor {

    private final String stringValue;
    private final char primitiveCharacterValue;
    private final Character characterValue;
    private final int primitiveIntegerValue;
    private final Integer integerValue;
    private final long primitiveLongValue;
    private final Long longValue;
    private final double primitiveDoubleValue;
    private final Double doubleValue;
    private final LocalDate localDate;
    private final LocalDateTime localDateTime;
    private final List<String> listOfStrings;
    private final Set<String> setOfStrings;
    private final Map<Integer, String> mapOfIntegersToStrings;
    private final Map<String, Integer> mapOfStringsToIntegers;
    private final ArbitraryEnum arbitraryEnum;
    private final String[] arrayOfStrings;
    private final Date date;

    public AllArgsConstructor(
            String stringValue,
            char primitiveCharacterValue,
            Character characterValue,
            int primitiveIntegerValue,
            Integer integerValue,
            long primitiveLongValue,
            Long longValue,
            double primitiveDoubleValue,
            Double doubleValue,
            LocalDate localDate,
            LocalDateTime localDateTime,
            List<String> listOfStrings,
            Set<String> setOfStrings,
            Map<Integer, String> mapOfIntegersToStrings,
            Map<String, Integer> mapOfStringsToIntegers,
            ArbitraryEnum arbitraryEnum,
            String[] arrayOfStrings,
            Date date
    ) {
        this.stringValue = stringValue;
        this.primitiveCharacterValue = primitiveCharacterValue;
        this.characterValue = characterValue;
        this.primitiveIntegerValue = primitiveIntegerValue;
        this.integerValue = integerValue;
        this.primitiveLongValue = primitiveLongValue;
        this.longValue = longValue;
        this.primitiveDoubleValue = primitiveDoubleValue;
        this.doubleValue = doubleValue;
        this.localDate = localDate;
        this.localDateTime = localDateTime;
        this.listOfStrings = listOfStrings;
        this.setOfStrings = setOfStrings;
        this.mapOfIntegersToStrings = mapOfIntegersToStrings;
        this.mapOfStringsToIntegers = mapOfStringsToIntegers;
        this.arbitraryEnum = arbitraryEnum;
        this.arrayOfStrings = arrayOfStrings;
        this.date = date;
    }

    public String getStringValue() {
        return stringValue;
    }

    public char getPrimitiveCharacterValue() {
        return primitiveCharacterValue;
    }

    public Character getCharacterValue() {
        return characterValue;
    }

    public int getPrimitiveIntegerValue() {
        return primitiveIntegerValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public long getPrimitiveLongValue() {
        return primitiveLongValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public double getPrimitiveDoubleValue() {
        return primitiveDoubleValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public List<String> getListOfStrings() {
        return listOfStrings;
    }

    public Set<String> getSetOfStrings() {
        return setOfStrings;
    }

    public Map<Integer, String> getMapOfIntegersToStrings() {
        return mapOfIntegersToStrings;
    }

    public Map<String, Integer> getMapOfStringsToIntegers() {
        return mapOfStringsToIntegers;
    }

    public ArbitraryEnum getArbitraryEnum() {
        return arbitraryEnum;
    }

    public String[] getArrayOfStrings() {
        return arrayOfStrings;
    }

    public Date getDate() {
        return date;
    }
}
