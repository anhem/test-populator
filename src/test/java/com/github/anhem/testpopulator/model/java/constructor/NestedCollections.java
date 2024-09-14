package com.github.anhem.testpopulator.model.java.constructor;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class NestedCollections {

    private ArrayList<SimpleClass> listOfSimpleClass;
    private HashMap<Integer, SimpleClass> mapOfIntegerToSimpleClass;
    private HashSet<SimpleClass> setOfSimpleClass;
    private ArrayList<ArrayList<SimpleClass>> listOfListsOfSimpleClass;
    private HashMap<Integer, HashMap<SimpleClass, String>> mapOfIntegerToMapOfSimpleClassToString;

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class SimpleClass {
        private String string;
    }
}
