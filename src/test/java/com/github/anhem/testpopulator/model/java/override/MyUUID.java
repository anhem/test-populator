package com.github.anhem.testpopulator.model.java.override;

import java.util.UUID;

public class MyUUID {

    private UUID uuid;

    public MyUUID(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }
}
