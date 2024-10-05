package com.github.anhem.testpopulator.model.java.setter;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PojoWithMultipleCustomSetters {

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

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void withPrimitiveCharacterValue(char primitiveCharacterValue) {
        this.primitiveCharacterValue = primitiveCharacterValue;
    }

    public void alsoCharacterValue(Character characterValue) {
        this.characterValue = characterValue;
    }

    public void asPrimitiveIntegerValue(int primitiveIntegerValue) {
        this.primitiveIntegerValue = primitiveIntegerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public void withPrimitiveLongValue(long primitiveLongValue) {
        this.primitiveLongValue = primitiveLongValue;
    }

    public void alsoLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public void asPrimitiveDoubleValue(double primitiveDoubleValue) {
        this.primitiveDoubleValue = primitiveDoubleValue;
    }

    public void withDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public void withLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public void withLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void asListOfStrings(List<String> listOfStrings) {
        this.listOfStrings = listOfStrings;
    }

    public void withOfStrings(Set<String> setOfStrings) {
        this.setOfStrings = setOfStrings;
    }

    public void withMapOfIntegersToStrings(Map<Integer, String> mapOfIntegersToStrings) {
        this.mapOfIntegersToStrings = mapOfIntegersToStrings;
    }

    public void withMapOfStringsToIntegers(Map<String, Integer> mapOfStringsToIntegers) {
        this.mapOfStringsToIntegers = mapOfStringsToIntegers;
    }

    public void withArbitraryEnum(ArbitraryEnum arbitraryEnum) {
        this.arbitraryEnum = arbitraryEnum;
    }

    public void setArrayOfStrings(String[] arrayOfStrings) {
        this.arrayOfStrings = arrayOfStrings;
    }
}
