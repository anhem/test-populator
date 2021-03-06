package com.github.anhem.testpopulator.model.java;

public class DifferentConstructorModifiers {

    private String publicConstructorField;
    private Integer privateConstructorField;

    public DifferentConstructorModifiers(String publicConstructorField) {
        this.publicConstructorField = publicConstructorField;
    }

    private DifferentConstructorModifiers(String publicConstructorField, Integer privateConstructorField) {
        this.publicConstructorField = publicConstructorField;
        this.privateConstructorField = privateConstructorField;
    }
}
