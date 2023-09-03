package com.github.anhem.testpopulator.model.immutables;

import com.github.anhem.testpopulator.model.java.ArbitraryEnum;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

}
