package com.github.anhem.testpopulator.readme.model;

import java.util.UUID;

public class MyUUID {

    private final UUID uuid;

    public MyUUID(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }
}
