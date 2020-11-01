package com.github.anhem.testpopulator.model.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private List<String> listOfStrings;
    private Set<String> setOfStrings;
    private Map<Integer, String> mapOfIntegerToString;
    private Map<String, Integer> mapOfStringToInteger;
    private ArbitraryEnum arbitraryEnum;
    private String[] arrayOfStrings;

    public String getStringValue() {
        return stringValue;
    }

    void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public char getPrimitiveCharacterValue() {
        return primitiveCharacterValue;
    }

    void setPrimitiveCharacterValue(char primitiveCharacterValue) {
        this.primitiveCharacterValue = primitiveCharacterValue;
    }

    public Character getCharacterValue() {
        return characterValue;
    }

    void setCharacterValue(Character characterValue) {
        this.characterValue = characterValue;
    }

    public int getPrimitiveIntegerValue() {
        return primitiveIntegerValue;
    }

    void setPrimitiveIntegerValue(int primitiveIntegerValue) {
        this.primitiveIntegerValue = primitiveIntegerValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public long getPrimitiveLongValue() {
        return primitiveLongValue;
    }

    void setPrimitiveLongValue(long primitiveLongValue) {
        this.primitiveLongValue = primitiveLongValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public double getPrimitiveDoubleValue() {
        return primitiveDoubleValue;
    }

    void setPrimitiveDoubleValue(double primitiveDoubleValue) {
        this.primitiveDoubleValue = primitiveDoubleValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public List<String> getListOfStrings() {
        return listOfStrings;
    }

    void setListOfStrings(List<String> listOfStrings) {
        this.listOfStrings = listOfStrings;
    }

    public Set<String> getSetOfStrings() {
        return setOfStrings;
    }

    void setSetOfStrings(Set<String> setOfStrings) {
        this.setOfStrings = setOfStrings;
    }

    public Map<Integer, String> getMapOfIntegerToString() {
        return mapOfIntegerToString;
    }

    void setMapOfIntegerToString(Map<Integer, String> mapOfIntegerToString) {
        this.mapOfIntegerToString = mapOfIntegerToString;
    }

    public Map<String, Integer> getMapOfStringToInteger() {
        return mapOfStringToInteger;
    }

    void setMapOfStringToInteger(Map<String, Integer> mapOfStringToInteger) {
        this.mapOfStringToInteger = mapOfStringToInteger;
    }

    public ArbitraryEnum getArbitraryEnum() {
        return arbitraryEnum;
    }

    void setArbitraryEnum(ArbitraryEnum arbitraryEnum) {
        this.arbitraryEnum = arbitraryEnum;
    }

    public String[] getArrayOfStrings() {
        return arrayOfStrings;
    }

    void setArrayOfStrings(String[] arrayOfStrings) {
        this.arrayOfStrings = arrayOfStrings;
    }

}
