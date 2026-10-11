package com.github.anhem.testpopulator.model.kotlin;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class NestedTargetPositionComponent {

    private final TargetPositionComponent position = new TargetPositionComponent();

    public TargetPositionComponent getPosition() {
        return position;
    }
}
