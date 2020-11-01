package com.github.anhem.testpopulator.model.java.override;

import com.github.anhem.testpopulator.config.OverridePopulate;

import java.util.UUID;

public class MyUUIDOverride implements OverridePopulate<MyUUID> {

    @Override
    public MyUUID create() {
        return new MyUUID(UUID.randomUUID().toString());
    }
}
