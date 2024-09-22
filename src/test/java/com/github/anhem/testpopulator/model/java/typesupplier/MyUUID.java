package com.github.anhem.testpopulator.model.java.typesupplier;

import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode
public class MyUUID {

    private final UUID uuid;

    public MyUUID(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }
}
