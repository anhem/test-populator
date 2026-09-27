package com.github.anhem.testpopulator.model.kotlin;

import lombok.EqualsAndHashCode;
import lombok.Getter;

public abstract class KotlinLikeSealedClass {
    private final String sealedProp;

    private KotlinLikeSealedClass(String sealedProp) {
        this.sealedProp = sealedProp;
    }

    public KotlinLikeSealedClass(String sealedProp, DefaultConstructorMarker marker) {
        this.sealedProp = sealedProp;
    }

    public String getSealedProp() {
        return sealedProp;
    }

    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class SubClassA extends KotlinLikeSealedClass {
        private final int aProp;

        public SubClassA(String sealedProp, int aProp) {
            super(sealedProp, null);
            this.aProp = aProp;
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class SubClassB extends KotlinLikeSealedClass {
        private final boolean bProp;

        public SubClassB(String sealedProp, boolean bProp) {
            super(sealedProp, null);
            this.bProp = bProp;
        }
    }
}
