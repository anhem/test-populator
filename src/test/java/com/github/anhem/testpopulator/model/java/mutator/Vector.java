package com.github.anhem.testpopulator.model.java.mutator;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Vector {

    public static final Vector ZERO = new Vector(0, 0);

    public float x;
    public float y;

    public Vector() {
    }

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
