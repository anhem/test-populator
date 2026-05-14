package com.github.anhem.testpopulator.model.kotlin;

import lombok.Getter;

@Getter
public class KotlinLikeWithCompanion {

    private final String value;

    private KotlinLikeWithCompanion(String value) {
        this.value = value;
    }

    public static final Companion Companion = new Companion();

    public static class Companion {
        public KotlinLikeWithCompanion create(String value) {
            return new KotlinLikeWithCompanion(value);
        }
    }
}
