package com.github.anhem.testpopulator.model.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PojoWithCustomSetters {

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

    public String getStringValue() {
        return stringValue;
    }

    public void withStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public char getPrimitiveCharacterValue() {
        return primitiveCharacterValue;
    }

    public void withPrimitiveCharacterValue(char primitiveCharacterValue) {
        this.primitiveCharacterValue = primitiveCharacterValue;
    }

    public Character getCharacterValue() {
        return characterValue;
    }

    public void withCharacterValue(Character characterValue) {
        this.characterValue = characterValue;
    }

    public int getPrimitiveIntegerValue() {
        return primitiveIntegerValue;
    }

    public void withPrimitiveIntegerValue(int primitiveIntegerValue) {
        this.primitiveIntegerValue = primitiveIntegerValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void withIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public long getPrimitiveLongValue() {
        return primitiveLongValue;
    }

    public void withPrimitiveLongValue(long primitiveLongValue) {
        this.primitiveLongValue = primitiveLongValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void withLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public double getPrimitiveDoubleValue() {
        return primitiveDoubleValue;
    }

    public void withPrimitiveDoubleValue(double primitiveDoubleValue) {
        this.primitiveDoubleValue = primitiveDoubleValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void withDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void withLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void withLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public List<String> getListOfStrings() {
        return listOfStrings;
    }

    public void withListOfStrings(List<String> listOfStrings) {
        this.listOfStrings = listOfStrings;
    }

    public Set<String> getSetOfStrings() {
        return setOfStrings;
    }

    public void withOfStrings(Set<String> setOfStrings) {
        this.setOfStrings = setOfStrings;
    }

    public Map<Integer, String> getMapOfIntegersToStrings() {
        return mapOfIntegersToStrings;
    }

    public void withMapOfIntegersToStrings(Map<Integer, String> mapOfIntegersToStrings) {
        this.mapOfIntegersToStrings = mapOfIntegersToStrings;
    }

    public Map<String, Integer> getMapOfStringsToIntegers() {
        return mapOfStringsToIntegers;
    }

    public void withMapOfStringsToIntegers(Map<String, Integer> mapOfStringsToIntegers) {
        this.mapOfStringsToIntegers = mapOfStringsToIntegers;
    }

    public ArbitraryEnum getArbitraryEnum() {
        return arbitraryEnum;
    }

    public void withArbitraryEnum(ArbitraryEnum arbitraryEnum) {
        this.arbitraryEnum = arbitraryEnum;
    }

    public String[] getArrayOfStrings() {
        return arrayOfStrings;
    }

    public void withArrayOfStrings(String[] arrayOfStrings) {
        this.arrayOfStrings = arrayOfStrings;
    }
}
