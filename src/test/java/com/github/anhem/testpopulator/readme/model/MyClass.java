package com.github.anhem.testpopulator.readme.model;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MyClass {

    private String stringValue;
    private List<ArbitraryEnum> listWithEnums;
    private InnerClass myInnerClass;

    public static class InnerClass {
        private int integer;
        private Map<String, LocalDate> stringToLocalDateMap;

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
