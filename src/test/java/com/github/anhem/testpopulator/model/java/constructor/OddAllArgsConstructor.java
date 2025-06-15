package com.github.anhem.testpopulator.model.java.constructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@EqualsAndHashCode
@lombok.AllArgsConstructor
public class OddAllArgsConstructor {

    private final AllArgsConstructor allArgsConstructor;
    private final List<AllArgsConstructor> allArgsConstructorList;
    private final Set<AllArgsConstructor> allArgsConstructorSet;
    private final Map<AllArgsConstructor, AllArgsConstructor> allArgsConstructorMap;
    private final AllArgsConstructorDateAndTimeMix allArgsConstructorDateAndTimeMix;

}
