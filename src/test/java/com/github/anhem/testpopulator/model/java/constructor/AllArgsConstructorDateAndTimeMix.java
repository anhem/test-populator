package com.github.anhem.testpopulator.model.java.constructor;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class AllArgsConstructorDateAndTimeMix {

    private final AllArgsConstructorDateAndTimeMix.Date myDate;
    private final java.util.Date date;
    private final java.sql.Date dateSql;
    private final java.time.LocalTime localTime;
    private final AllArgsConstructorDateAndTimeMix.LocalTime myLocalTime;
    private final java.time.Instant instant;
    private final AllArgsConstructorDateAndTimeMix.Instant myInstant;
    private final Set<java.time.Instant> instants;
    private final Set<AllArgsConstructorDateAndTimeMix.Instant> myInstants;
    private final Map<java.time.Instant, AllArgsConstructorDateAndTimeMix.Instant> instantsMap;
    private final Map<AllArgsConstructorDateAndTimeMix.Instant, java.time.Instant> myInstantsMap;

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class LocalTime {
        private final java.time.LocalTime localTime;
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Date {
        private final java.util.Date date;
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Instant {
        private final java.time.Instant instant;
    }
}
