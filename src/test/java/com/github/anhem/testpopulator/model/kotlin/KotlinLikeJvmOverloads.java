package com.github.anhem.testpopulator.model.kotlin;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class KotlinLikeJvmOverloads {
    private final String a;
    private final int b;
    private final boolean c;

    public KotlinLikeJvmOverloads(String a, int b, boolean c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public KotlinLikeJvmOverloads(String a, int b) {
        this(a, b, false);
    }

    public KotlinLikeJvmOverloads(String a) {
        this(a, 0, false);
    }

    public KotlinLikeJvmOverloads() {
        this("", 0, false);
    }

    public KotlinLikeJvmOverloads(String a, int b, boolean c, int mask, DefaultConstructorMarker marker) {
        this.a = (mask & 1) != 0 ? "" : a;
        this.b = (mask & 2) != 0 ? 0 : b;
        this.c = (mask & 4) != 0 ? false : c;
    }
}
