package com.github.anhem.testpopulator.model.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AllArgsConstructorAbstract {

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
    private final Map<Integer, String> mapOfIntegerToString;
    private final Map<String, Integer> mapOfStringToInteger;
    private final ArbitraryEnum arbitraryEnum;
    private final String[] arrayOfStrings;

    public AllArgsConstructorAbstract(String stringValue, char primitiveCharacterValue, Character characterValue, int primitiveIntegerValue, Integer integerValue, long primitiveLongValue, Long longValue, double primitiveDoubleValue, Double doubleValue, LocalDate localDate, LocalDateTime localDateTime, List<String> listOfStrings, Set<String> setOfStrings, Map<Integer, String> mapOfIntegerToString, Map<String, Integer> mapOfStringToInteger, ArbitraryEnum arbitraryEnum, String[] arrayOfStrings) {
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
        this.mapOfIntegerToString = mapOfIntegerToString;
        this.mapOfStringToInteger = mapOfStringToInteger;
        this.arbitraryEnum = arbitraryEnum;
        this.arrayOfStrings = arrayOfStrings;
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

    public Map<Integer, String> getMapOfIntegerToString() {
        return mapOfIntegerToString;
    }

    public Map<String, Integer> getMapOfStringToInteger() {
        return mapOfStringToInteger;
    }

    public ArbitraryEnum getArbitraryEnum() {
        return arbitraryEnum;
    }

    public String[] getArrayOfStrings() {
        return arrayOfStrings;
    }
}
