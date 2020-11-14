package com.github.anhem.testpopulator.model.java;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OddPojo {

    private Pojo pojo;
    private List<Pojo> pojoList;
    private Set<Pojo> pojoSet;
    private Map<Pojo, Pojo> pojoToPojoMap;

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
}
