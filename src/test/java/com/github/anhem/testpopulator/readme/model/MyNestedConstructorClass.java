package com.github.anhem.testpopulator.readme.model;

import java.io.File;

public class MyNestedConstructorClass {
    private File value;

    public MyNestedConstructorClass() {
    }

    public File getValue() {
        return value;
    }

    public void setValue(File value) {
        this.value = value;
    }
}
