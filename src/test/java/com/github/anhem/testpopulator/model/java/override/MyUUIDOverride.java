package com.github.anhem.testpopulator.model.java.override;

import com.github.anhem.testpopulator.config.OverridePopulate;

import java.util.UUID;

public class MyUUIDOverride implements OverridePopulate<MyUUID> {

    @Override
    public MyUUID create() {
        return new MyUUID(UUID.randomUUID().toString());
    }

    @Override
    public String createString() {
        return "UUID.fromString(\"156585fd-4fe5-4ed4-8d59-d8d70d8b96f5\");";
    }
}
