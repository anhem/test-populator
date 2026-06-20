package com.github.anhem.testpopulator.model.kotlin;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class KotlinLikeValueClass {
    private final String value;

    private KotlinLikeValueClass(String value) {
        this.value = value;
    }

    // Mangles the constructor name to standard static method
    public static final KotlinLikeValueClass constructor_impl(String value) {
        return new KotlinLikeValueClass(value);
    }

    // This is typically how methods are mangled
    public final int length_impl() {
        return value.length();
    }
}
