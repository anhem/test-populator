package com.github.anhem.testpopulator.model.java;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ComplexTypes {

    private AtomicInteger atomicInteger;
    private AtomicLong atomicLong;
    private AtomicBoolean atomicBoolean;
    private StringBuilder stringBuilder;
    private StringBuffer stringBuffer;
    private OptionalInt optionalInt;
    private OptionalLong optionalLong;
    private OptionalDouble optionalDouble;
    private InetSocketAddress inetSocketAddress;

}
