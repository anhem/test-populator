package com.github.anhem.testpopulator.readme.model;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MyClass2 {

    private final String stringValue;
    private final List<ArbitraryEnum> listWithEnums;
    private final InnerClass myInnerClass;
    private final MyUUID myUUID;
    private final LocalDate date;

    public MyClass2(String stringValue, List<ArbitraryEnum> listWithEnums, InnerClass myInnerClass, MyUUID myUUID, LocalDate date) {
        this.stringValue = stringValue;
        this.listWithEnums = listWithEnums;
        this.myInnerClass = myInnerClass;
        this.myUUID = myUUID;
        this.date = date;
    }

    public String getStringValue() {
        return stringValue;
    }

    public List<ArbitraryEnum> getListWithEnums() {
        return listWithEnums;
    }

    public InnerClass getMyInnerClass() {
        return myInnerClass;
    }

    public MyUUID getMyUUID() {
        return myUUID;
    }

    public LocalDate getDate() {
        return date;
    }

    public static class InnerClass {
        private final int integer;
        private final Map<String, LocalDate> stringToLocalDateMap;

        public InnerClass(int integer, Map<String, LocalDate> stringToLocalDateMap) {
            this.integer = integer;
            this.stringToLocalDateMap = stringToLocalDateMap;
        }

        public int getInteger() {
            return integer;
        }

        public Map<String, LocalDate> getStringToLocalDateMap() {
            return stringToLocalDateMap;
        }
    }
}
