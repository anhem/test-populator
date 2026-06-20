package com.github.anhem.testpopulator.model.kotlin;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

@Getter
@EqualsAndHashCode
public class KotlinLikeClass {

    private final String value;
    private final int id;
    private final InnerClass innerClass;
    private final Map<String, ? extends Integer> myMap;

    public KotlinLikeClass(String value, int id, InnerClass innerClass, Map<String, ? extends Integer> myMap, int mask, DefaultConstructorMarker marker) {
        this.value = (mask & 1) != 0 ? "default_value" : value;
        this.id = id;
        this.innerClass = innerClass;
        this.myMap = myMap;
    }

    public KotlinLikeClass(DefaultConstructorMarker marker) {
        this.value = "default";
        this.id = 0;
        this.innerClass = null;
        this.myMap = null;
    }

    @Getter
    @EqualsAndHashCode
    public static class InnerClass {
        private final String innerValue;
        private final ArbitraryEnum arbitraryEnum;

        public InnerClass(String innerValue, ArbitraryEnum arbitraryEnum) {
            this.innerValue = innerValue;
            this.arbitraryEnum = arbitraryEnum;
        }
    }
}
