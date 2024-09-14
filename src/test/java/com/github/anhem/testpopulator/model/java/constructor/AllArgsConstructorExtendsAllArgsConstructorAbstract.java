package com.github.anhem.testpopulator.model.java.constructor;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class AllArgsConstructorExtendsAllArgsConstructorAbstract extends AllArgsConstructorAbstract {

    private final String anotherString;

    public AllArgsConstructorExtendsAllArgsConstructorAbstract(String stringValue, char primitiveCharacterValue, Character characterValue, int primitiveIntegerValue, Integer integerValue, long primitiveLongValue, Long longValue, double primitiveDoubleValue, Double doubleValue, LocalDate localDate, LocalDateTime localDateTime, List<String> listOfStrings, Set<String> setOfStrings, Map<Integer, String> mapOfIntegerToString, Map<String, Integer> mapOfStringToInteger, ArbitraryEnum arbitraryEnum, String[] arrayOfStrings, String anotherString, Date date) {
        super(stringValue, primitiveCharacterValue, characterValue, primitiveIntegerValue, integerValue, primitiveLongValue, longValue, primitiveDoubleValue, doubleValue, localDate, localDateTime, listOfStrings, setOfStrings, mapOfIntegerToString, mapOfStringToInteger, arbitraryEnum, arrayOfStrings, date);
        this.anotherString = anotherString;
    }

}
