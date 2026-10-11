package com.github.anhem.testpopulator.model.kotlin;

import com.github.anhem.testpopulator.model.java.mutator.Vector;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class NamedTargetComponent {

    private String name;
    private final Vector target = new Vector();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector getTarget() {
        return target;
    }
}
