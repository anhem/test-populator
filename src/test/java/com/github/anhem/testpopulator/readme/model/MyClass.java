package com.github.anhem.testpopulator.readme.model;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MyClass {

    private final String stringValue;
    private final List<ArbitraryEnum> listWithEnums;
    private final InnerClass myInnerClass;

    public MyClass(String stringValue, List<ArbitraryEnum> listWithEnums, InnerClass myInnerClass) {
        this.stringValue = stringValue;
        this.listWithEnums = listWithEnums;
        this.myInnerClass = myInnerClass;
    }

    public static class InnerClass {
        private final int integer;
        private final Map<String, LocalDate> stringToLocalDateMap;

        public InnerClass(int integer, Map<String, LocalDate> stringToLocalDateMap) {
            this.integer = integer;
            this.stringToLocalDateMap = stringToLocalDateMap;
        }

        @Override
        public String toString() {
            return "InnerClass{" +
                    "integer=" + integer +
                    ", stringToLocalDateMap=" + stringToLocalDateMap +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MyClass{" +
                "stringValue='" + stringValue + '\'' +
                ", listWithEnums=" + listWithEnums +
                ", myInnerClass=" + myInnerClass +
                '}';
    }
}
