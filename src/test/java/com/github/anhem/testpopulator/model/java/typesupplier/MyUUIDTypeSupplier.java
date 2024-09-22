package com.github.anhem.testpopulator.model.java.typesupplier;

import com.github.anhem.testpopulator.config.TypeSupplier;

public class MyUUIDTypeSupplier implements TypeSupplier<MyUUID> {

    private static final String UUID = "156585fd-4fe5-4ed4-8d59-d8d70d8b96f5";

    @Override
    public MyUUID get() {
        return new MyUUID(UUID);
    }

    @Override
    public String createString() {
        return String.format("new MyUUID(java.util.UUID.fromString(\"%s\").toString())", UUID);
    }
}
