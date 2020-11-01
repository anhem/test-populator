package com.github.anhem.testpopulator.model.java;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OddAllArgsConstructor {

    private final AllArgsConstructor allArgsConstructor;
    private final List<AllArgsConstructor> allArgsConstructorList;
    private final Set<AllArgsConstructor> allArgsConstructorSet;
    private final Map<AllArgsConstructor, AllArgsConstructor> allArgsConstructorMap;

    public OddAllArgsConstructor(AllArgsConstructor allArgsConstructor, List<AllArgsConstructor> allArgsConstructorList, Set<AllArgsConstructor> allArgsConstructorSet, Map<AllArgsConstructor, AllArgsConstructor> allArgsConstructorMap) {
        this.allArgsConstructor = allArgsConstructor;
        this.allArgsConstructorList = allArgsConstructorList;
        this.allArgsConstructorSet = allArgsConstructorSet;
        this.allArgsConstructorMap = allArgsConstructorMap;
    }

    public AllArgsConstructor getAllArgsConstructor() {
        return allArgsConstructor;
    }

    public List<AllArgsConstructor> getAllArgsConstructorList() {
        return allArgsConstructorList;
    }

    public Set<AllArgsConstructor> getAllArgsConstructorSet() {
        return allArgsConstructorSet;
    }

    public Map<AllArgsConstructor, AllArgsConstructor> getAllArgsConstructorMap() {
        return allArgsConstructorMap;
    }
}
