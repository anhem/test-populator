package com.github.anhem.testpopulator.model.kotlin;

import com.github.anhem.testpopulator.model.java.mutator.Mutator;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class InPlaceSetterComponent {

    private final Mutator mutator = new Mutator();

    public Mutator getMutator() {
        return mutator;
    }
}
