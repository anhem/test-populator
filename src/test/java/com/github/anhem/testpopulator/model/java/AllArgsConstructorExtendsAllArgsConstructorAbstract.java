package com.github.anhem.testpopulator.model.java;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AllArgsConstructorExtendsAllArgsConstructorAbstract extends AllArgsConstructorAbstract {

    private final String anotherString;

    public AllArgsConstructorExtendsAllArgsConstructorAbstract(String stringValue, char primitiveCharacterValue, Character characterValue, int primitiveIntegerValue, Integer integerValue, long primitiveLongValue, Long longValue, double primitiveDoubleValue, Double doubleValue, LocalDate localDate, LocalDateTime localDateTime, List<String> listOfStrings, Set<String> setOfStrings, Map<Integer, String> mapOfIntegerToString, Map<String, Integer> mapOfStringToInteger, ArbitraryEnum arbitraryEnum, String[] arrayOfStrings, String anotherString, Date date) {
        super(stringValue, primitiveCharacterValue, characterValue, primitiveIntegerValue, integerValue, primitiveLongValue, longValue, primitiveDoubleValue, doubleValue, localDate, localDateTime, listOfStrings, setOfStrings, mapOfIntegerToString, mapOfStringToInteger, arbitraryEnum, arrayOfStrings, date);
        this.anotherString = anotherString;
    }

    public String getAnotherString() {
        return anotherString;
    }
}
