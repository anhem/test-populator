package com.github.anhem.testpopulator.model.java.mutator;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@EqualsAndHashCode
public class MutatorWithConstructor {

    private String stringValue;
    private char primitiveCharacterValue;
    private Character characterValue;
    private int primitiveIntegerValue;
    private Integer integerValue;
    private long primitiveLongValue;
    private List<String> listOfStrings;
    private Set<String> setOfStrings;
    private Map<Integer, String> mapOfIntegersToStrings;
    private final ArbitraryEnum arbitraryEnum;

    public MutatorWithConstructor(ArbitraryEnum arbitraryEnum) {
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

    public MutatorWithConstructor setTwoAndReturn(Integer integer, Long aLong) {
        this.integerValue = integer;
        this.primitiveLongValue = aLong;
        return this;
    }

    public void ListOfStrings(String... strings) {
        this.listOfStrings = List.of(strings);
    }

    public MutatorWithConstructor canSetStringsAndReturn(String... strings) {
        this.setOfStrings = Set.of(strings);
        return this;
    }

    public void somethingRandom(Integer integer, String s) {
        this.mapOfIntegersToStrings = Map.of(integer, s);
    }
}
