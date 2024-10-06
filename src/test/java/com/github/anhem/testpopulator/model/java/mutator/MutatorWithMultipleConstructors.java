package com.github.anhem.testpopulator.model.java.mutator;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MutatorWithMultipleConstructors {

    private String stringValue;
    private char primitiveCharacterValue;
    private Character characterValue;
    private int primitiveIntegerValue;
    private Integer integerValue;
    private long primitiveLongValue;
    private List<String> listOfStrings;
    private Set<String> setOfStrings;
    private Map<Integer, String> mapOfIntegersToStrings;
    private ArbitraryEnum arbitraryEnum;
    private LocalDate localDate;

    public MutatorWithMultipleConstructors(ArbitraryEnum arbitraryEnum) {
        this.arbitraryEnum = arbitraryEnum;
    }

    public void setThree(String s, char c, Character character) {
        this.stringValue = s;
        this.primitiveCharacterValue = c;
        this.characterValue = character;
    }

    public void withTwo(Integer integer1, Integer integer2) {
        this.primitiveIntegerValue = integer1 + integer2;
    }

    public MutatorWithMultipleConstructors setTwoAndReturn(Integer integer, Long aLong) {
        this.integerValue = integer;
        this.primitiveLongValue = aLong;
        return this;
    }

    public void ListOfStrings(String... strings) {
        this.listOfStrings = List.of(strings);
    }

    public MutatorWithMultipleConstructors canSetStringsAndReturn(String... strings) {
        this.setOfStrings = Set.of(strings);
        return this;
    }

    public void somethingRandom(Integer integer, String s) {
        this.mapOfIntegersToStrings = Map.of(integer, s);
    }
}
