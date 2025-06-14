package com.github.anhem.testpopulator.model.immutables;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import org.immutables.value.Value;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value.Immutable
public abstract class ImmutablesAbstract {

    public abstract String getStringValue();

    public abstract char getPrimitiveCharacterValue();

    public abstract Character getCharacterValue();

    public abstract int getPrimitiveIntegerValue();

    public abstract Integer getIntegerValue();

    public abstract long getPrimitiveLongValue();

    public abstract Long getLongValue();

    public abstract double getPrimitiveDoubleValue();

    public abstract Double getDoubleValue();

    public abstract LocalDate getLocalDate();

    public abstract LocalDateTime getLocalDateTime();

    public abstract List<String> getListOfStrings();

    public abstract Set<String> getSetOfStrings();

    public abstract Map<Integer, String> getMapOfIntegersToStrings();

    public abstract Map<String, Integer> getMapOfStringsToIntegers();

    public abstract ArbitraryEnum getArbitraryEnum();

    public abstract String[] getArrayOfStrings();

    public abstract Date date();

    public abstract Byte getByteValue();

    public abstract byte getPrimitiveByteValue();

    public abstract Short getShortValue();

    public abstract short getPrimitiveShortValue();

    public abstract Float getFloatValue();

    public abstract float getPrimitiveFloatValue();

    public abstract LocalTime getLocalTime();

    public abstract BigInteger getBigInteger();

    public abstract OffsetDateTime getOffsetDateTime();

    public abstract OffsetTime getOffsetTime();

    public abstract Duration getDuration();

    public abstract Period getPeriod();

    public abstract java.sql.Date getSqlDate();

    public abstract Time getSqlTime();

    public abstract Timestamp getSqlTimestamp();

}
