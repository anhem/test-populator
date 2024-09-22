package com.github.anhem.testpopulator.model.java.override;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class MyUUID {

    private final UUID uuid;

    public MyUUID(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

}
