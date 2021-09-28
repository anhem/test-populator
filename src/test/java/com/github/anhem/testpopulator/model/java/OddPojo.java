package com.github.anhem.testpopulator.model.java;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OddPojo {

    private Pojo pojo;
    private List<Pojo> pojoList;
    private List<List<Pojo>> listOfPojoLists;
    private Set<Pojo> pojoSet;
    private Set<Set<Pojo>> setOfPojoSets;
    private Map<Pojo, Pojo> pojoToPojoMap;
    private Map<String, List<Pojo>> mapOfStringToPojoList;
    private Map<List<String>, List<Pojo>> mapOfStringListToPojoMap;
    private List<List<List<Pojo>>> weirdPojoList;

    public Pojo getPojo() {
        return pojo;
    }

    public void setPojo(Pojo pojo) {
        this.pojo = pojo;
    }

    public List<Pojo> getPojoList() {
        return pojoList;
    }

    public void setPojoList(List<Pojo> pojoList) {
        this.pojoList = pojoList;
    }

    public Set<Pojo> getPojoSet() {
        return pojoSet;
    }

    public void setPojoSet(Set<Pojo> pojoSet) {
        this.pojoSet = pojoSet;
    }

    public Map<Pojo, Pojo> getPojoToPojoMap() {
        return pojoToPojoMap;
    }

    public void setPojoToPojoMap(Map<Pojo, Pojo> pojoToPojoMap) {
        this.pojoToPojoMap = pojoToPojoMap;
    }

    public Map<String, List<Pojo>> getMapOfStringToPojoList() {
        return mapOfStringToPojoList;
    }

    public void setMapOfStringToPojoList(Map<String, List<Pojo>> mapOfStringToPojoList) {
        this.mapOfStringToPojoList = mapOfStringToPojoList;
    }

    public Map<List<String>, List<Pojo>> getMapOfStringListToPojoMap() {
        return mapOfStringListToPojoMap;
    }

    public void setMapOfStringListToPojoMap(Map<List<String>, List<Pojo>> mapOfStringListToPojoMap) {
        this.mapOfStringListToPojoMap = mapOfStringListToPojoMap;
    }

    public List<List<Pojo>> getListOfPojoLists() {
        return listOfPojoLists;
    }

    public void setListOfPojoLists(List<List<Pojo>> listOfPojoLists) {
        this.listOfPojoLists = listOfPojoLists;
    }

    public Set<Set<Pojo>> getSetOfPojoSets() {
        return setOfPojoSets;
    }

    public void setSetOfPojoSets(Set<Set<Pojo>> setOfPojoSets) {
        this.setOfPojoSets = setOfPojoSets;
    }

    public List<List<List<Pojo>>> getWeirdPojoList() {
        return weirdPojoList;
    }

    public void setWeirdPojoList(List<List<List<Pojo>>> weirdPojoList) {
        this.weirdPojoList = weirdPojoList;
    }
}
