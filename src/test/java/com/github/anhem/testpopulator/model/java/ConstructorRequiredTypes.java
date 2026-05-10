package com.github.anhem.testpopulator.model.java;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ConstructorRequiredTypes {

    private AtomicInteger atomicInteger;
    private AtomicLong atomicLong;
    private AtomicBoolean atomicBoolean;
    private StringBuilder stringBuilder;
    private StringBuffer stringBuffer;
    private File file;
    private UUID uuid;

}
