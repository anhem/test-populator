package com.github.anhem.testpopulator.model.java.mutator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Mutator {

    private String stringValue;
    private char primitiveCharacterValue;
    private Character characterValue;
    private int primitiveIntegerValue;
    private Integer integerValue;
    private long primitiveLongValue;
    private List<String> listOfStrings;
    private Set<String> setOfStrings;
    private Map<Integer, String> mapOfIntegersToStrings;
    private Map.Entry<Integer, String> mapEntry;
    private AbstractMap.SimpleEntry<Integer, String> simpleEntry;

    public void setThree(String s, char c, Character character) {
        this.stringValue = s;
        this.primitiveCharacterValue = c;
        this.characterValue = character;
    }

    public void withTwo(Integer integer1, Integer integer2) {
        this.primitiveIntegerValue = integer1 + integer2;
    }

    public Mutator setTwoAndReturn(Integer integer, Long aLong) {
        this.integerValue = integer;
        this.primitiveLongValue = aLong;
        return this;
    }

    public void ListOfStrings(String... strings) {
        this.listOfStrings = List.of(strings);
    }

    public Mutator canSetStringsAndReturn(String... strings) {
        this.setOfStrings = Set.of(strings);
        return this;
    }

    public void somethingRandom(Integer integer, String s) {
        this.mapOfIntegersToStrings = Map.of(integer, s);
    }

    public void someMapEntry(Map.Entry<Integer, String> mapEntry) {
        this.mapEntry = mapEntry;
    }

    public void withSimpleEntry(AbstractMap.SimpleEntry<Integer, String> simpleEntry) {
        this.simpleEntry = simpleEntry;
    }
}
