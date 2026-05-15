package com.github.anhem.testpopulator.model.kotlin;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class KotlinLikeClass {

    private final String value;
    private final int id;
    private final InnerClass innerClass;

    public KotlinLikeClass(String value, int id, InnerClass innerClass, int mask, DefaultConstructorMarker marker) {
        this.value = (mask & 1) != 0 ? "default_value" : value;
        this.id = id;
        this.innerClass = innerClass;
    }

    public KotlinLikeClass(DefaultConstructorMarker marker) {
        this.value = "default";
        this.id = 0;
        this.innerClass = null;
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
