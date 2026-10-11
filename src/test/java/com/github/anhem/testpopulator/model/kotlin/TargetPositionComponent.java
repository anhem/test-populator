package com.github.anhem.testpopulator.model.kotlin;

import com.github.anhem.testpopulator.model.java.mutator.Vector;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TargetPositionComponent {

    private final Vector target = new Vector();

    public Vector getTarget() {
        return target;
    }
}
