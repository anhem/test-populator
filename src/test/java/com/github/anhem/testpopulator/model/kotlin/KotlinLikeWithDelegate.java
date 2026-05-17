package com.github.anhem.testpopulator.model.kotlin;

import lombok.Getter;

@Getter
public class KotlinLikeWithDelegate {
    private final String myProp = "actual_value";
    // Simulated delegate field created by Kotlin compiler
    private final Object myProp$delegate = new Object(); 

    public String getMyProp() {
        return myProp;
    }
}
