package com.github.anhem.testpopulator.model.java.constructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class DifferentConstructorModifiers {

    private final String publicConstructorField;
    private Integer privateConstructorField;

    public DifferentConstructorModifiers(String publicConstructorField) {
        this.publicConstructorField = publicConstructorField;
    }

    private DifferentConstructorModifiers(String publicConstructorField, Integer privateConstructorField) {
        this.publicConstructorField = publicConstructorField;
        this.privateConstructorField = privateConstructorField;
    }
}
