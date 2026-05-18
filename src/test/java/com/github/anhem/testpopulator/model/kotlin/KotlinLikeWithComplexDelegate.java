package com.github.anhem.testpopulator.model.kotlin;

import lombok.Getter;

@Getter
public class KotlinLikeWithComplexDelegate {

    public static class ComplexDelegate {
        private String value;

        public ComplexDelegate(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final String myProp = "actual_value";
    // Simulated delegate field created by Kotlin compiler
    private ComplexDelegate myProp$delegate = new ComplexDelegate("initial");

    public String getMyProp() {
        return myProp;
    }
}
