package com.github.anhem.testpopulator.model.java.mutator;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class MutatorWithBothSettersAndFields {

    public float x;
    public float y;

    public void setX(float x) {
        this.x = x + 1000f;
    }
}
