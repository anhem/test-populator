package com.github.anhem.testpopulator.readme.model;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MyClass2 {

    private String stringValue;
    private List<ArbitraryEnum> listWithEnums;
    private InnerClass myInnerClass;
    private MyUUID myUUID;

    public static class InnerClass {
        private int integer;
        private Map<String, LocalDate> stringToLocalDateMap;
    }
}
