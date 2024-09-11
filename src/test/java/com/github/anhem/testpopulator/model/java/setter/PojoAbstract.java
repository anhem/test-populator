package com.github.anhem.testpopulator.model.java.setter;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class PojoAbstract {

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

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public char getPrimitiveCharacterValue() {
        return primitiveCharacterValue;
    }

    public void setPrimitiveCharacterValue(char primitiveCharacterValue) {
        this.primitiveCharacterValue = primitiveCharacterValue;
    }

    public Character getCharacterValue() {
        return characterValue;
    }

    public void setCharacterValue(Character characterValue) {
        this.characterValue = characterValue;
    }

    public int getPrimitiveIntegerValue() {
        return primitiveIntegerValue;
    }

    public void setPrimitiveIntegerValue(int primitiveIntegerValue) {
        this.primitiveIntegerValue = primitiveIntegerValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public long getPrimitiveLongValue() {
        return primitiveLongValue;
    }

    public void setPrimitiveLongValue(long primitiveLongValue) {
        this.primitiveLongValue = primitiveLongValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public double getPrimitiveDoubleValue() {
        return primitiveDoubleValue;
    }

    public void setPrimitiveDoubleValue(double primitiveDoubleValue) {
        this.primitiveDoubleValue = primitiveDoubleValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public List<String> getListOfStrings() {
        return listOfStrings;
    }

    public void setListOfStrings(List<String> listOfStrings) {
        this.listOfStrings = listOfStrings;
    }

    public Set<String> getSetOfStrings() {
        return setOfStrings;
    }

    public void setSetOfStrings(Set<String> setOfStrings) {
        this.setOfStrings = setOfStrings;
    }

    public Map<Integer, String> getMapOfIntegersToStrings() {
        return mapOfIntegersToStrings;
    }

    public void setMapOfIntegersToStrings(Map<Integer, String> mapOfIntegersToStrings) {
        this.mapOfIntegersToStrings = mapOfIntegersToStrings;
    }

    public Map<String, Integer> getMapOfStringsToIntegers() {
        return mapOfStringsToIntegers;
    }

    public void setMapOfStringsToIntegers(Map<String, Integer> mapOfStringsToIntegers) {
        this.mapOfStringsToIntegers = mapOfStringsToIntegers;
    }

    public ArbitraryEnum getArbitraryEnum() {
        return arbitraryEnum;
    }

    public void setArbitraryEnum(ArbitraryEnum arbitraryEnum) {
        this.arbitraryEnum = arbitraryEnum;
    }

    public String[] getArrayOfStrings() {
        return arrayOfStrings;
    }

    public void setArrayOfStrings(String[] arrayOfStrings) {
        this.arrayOfStrings = arrayOfStrings;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
