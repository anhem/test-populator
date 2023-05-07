package com.github.anhem.testpopulator.model.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NestedCollections {

    private ArrayList<SimpleClass> listOfSimpleClass;
    private HashMap<Integer, SimpleClass> mapOfIntegerToSimpleClass;
    private HashSet<SimpleClass> setOfSimpleClass;
    private ArrayList<ArrayList<SimpleClass>> listOfListsOfSimpleClass;
    private HashMap<Integer, HashMap<SimpleClass, String>> mapOfIntegerToMapOfSimpleClassToString;

    private HashMap<ArrayList<SimpleClass>, HashMap<HashSet<SimpleClass>, ArrayList<HashMap<ArrayList<SimpleClass>, SimpleClass>>>> messy;

    public NestedCollections() {
    }

    public NestedCollections(ArrayList<SimpleClass> listOfSimpleClass, HashMap<Integer, SimpleClass> mapOfIntegerToSimpleClass, HashSet<SimpleClass> setOfSimpleClass, ArrayList<ArrayList<SimpleClass>> listOfListsOfSimpleClass, HashMap<Integer, HashMap<SimpleClass, String>> mapOfIntegerToMapOfSimpleClassToString, HashMap<ArrayList<SimpleClass>, HashMap<HashSet<SimpleClass>, ArrayList<HashMap<ArrayList<SimpleClass>, SimpleClass>>>> messy) {
        this.listOfSimpleClass = listOfSimpleClass;
        this.mapOfIntegerToSimpleClass = mapOfIntegerToSimpleClass;
        this.setOfSimpleClass = setOfSimpleClass;
        this.listOfListsOfSimpleClass = listOfListsOfSimpleClass;
        this.mapOfIntegerToMapOfSimpleClassToString = mapOfIntegerToMapOfSimpleClassToString;
        this.messy = messy;
    }

    public ArrayList<SimpleClass> getListOfSimpleClass() {
        return listOfSimpleClass;
    }

    public void setListOfSimpleClass(ArrayList<SimpleClass> listOfSimpleClass) {
        this.listOfSimpleClass = listOfSimpleClass;
    }

    public HashMap<Integer, SimpleClass> getMapOfIntegerToSimpleClass() {
        return mapOfIntegerToSimpleClass;
    }

    public void setMapOfIntegerToSimpleClass(HashMap<Integer, SimpleClass> mapOfIntegerToSimpleClass) {
        this.mapOfIntegerToSimpleClass = mapOfIntegerToSimpleClass;
    }

    public HashSet<SimpleClass> getSetOfSimpleClass() {
        return setOfSimpleClass;
    }

    public void setSetOfSimpleClass(HashSet<SimpleClass> setOfSimpleClass) {
        this.setOfSimpleClass = setOfSimpleClass;
    }

    public ArrayList<ArrayList<SimpleClass>> getListOfListsOfSimpleClass() {
        return listOfListsOfSimpleClass;
    }

    public void setListOfListsOfSimpleClass(ArrayList<ArrayList<SimpleClass>> listOfListsOfSimpleClass) {
        this.listOfListsOfSimpleClass = listOfListsOfSimpleClass;
    }

    public HashMap<Integer, HashMap<SimpleClass, String>> getMapOfIntegerToMapOfSimpleClassToString() {
        return mapOfIntegerToMapOfSimpleClassToString;
    }

    public void setMapOfIntegerToMapOfSimpleClassToString(HashMap<Integer, HashMap<SimpleClass, String>> mapOfIntegerToMapOfSimpleClassToString) {
        this.mapOfIntegerToMapOfSimpleClassToString = mapOfIntegerToMapOfSimpleClassToString;
    }

    public HashMap<ArrayList<SimpleClass>, HashMap<HashSet<SimpleClass>, ArrayList<HashMap<ArrayList<SimpleClass>, SimpleClass>>>> getMessy() {
        return messy;
    }

    public void setMessy(HashMap<ArrayList<SimpleClass>, HashMap<HashSet<SimpleClass>, ArrayList<HashMap<ArrayList<SimpleClass>, SimpleClass>>>> messy) {
        this.messy = messy;
    }

    public static class SimpleClass {
        private String string;

        public SimpleClass() {
        }

        public SimpleClass(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}
