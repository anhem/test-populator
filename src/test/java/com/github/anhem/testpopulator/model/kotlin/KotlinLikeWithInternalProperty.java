package com.github.anhem.testpopulator.model.kotlin;

import lombok.Getter;

@Getter
public class KotlinLikeWithInternalProperty {
    private String myProp = "initial";

    // Simulated mangled setter for 'internal var myProp'
    public void setMyProp$test_populator(String value) {
        this.myProp = value;
    }
}
