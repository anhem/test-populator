package com.github.anhem.testpopulator.model.java.override;

import com.github.anhem.testpopulator.config.OverridePopulate;

public class MyUUIDOverride implements OverridePopulate<MyUUID> {

    private static final String UUID = "156585fd-4fe5-4ed4-8d59-d8d70d8b96f5";

    @Override
    public MyUUID create() {
        return new MyUUID(UUID);
    }

    @Override
    public String createString() {
        return String.format("UUID.fromString(\"%s\")", UUID);
    }
}
