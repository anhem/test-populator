package com.github.anhem.testpopulator.model.kotlin;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class KotlinLikeOuterClass {

    private final String outerValue;

    public KotlinLikeOuterClass(String outerValue) {
        this.outerValue = outerValue;
    }

    @Getter
    @EqualsAndHashCode
    public class KotlinLikeInnerClass {

        private final String innerValue;

        public KotlinLikeInnerClass(String innerValue) {
            this.innerValue = innerValue;
        }
    }
}
