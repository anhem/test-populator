package com.github.anhem.testpopulator.model.java.field;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
@EqualsAndHashCode
public class FieldsDateAndTimeMix {

    private Date myDate;
    private java.util.Date date;
    private java.sql.Date dateSql;
    private java.time.LocalTime localTime;
    private LocalTime myLocalTime;
    private java.time.Instant instant;
    private Instant myInstant;
    private Set<java.time.Instant> instants;
    private Set<Instant> myInstants;
    private Map<java.time.Instant, Instant> instantsMap;
    private Map<Instant, java.time.Instant> myInstantsMap;

    @Getter
    @EqualsAndHashCode
    public static class LocalTime {
        private java.time.LocalTime localTime;
    }

    @Getter
    @EqualsAndHashCode
    public static class Date {
        private java.util.Date date;
    }

    @Getter
    @EqualsAndHashCode
    public static class Instant {
        private java.time.Instant instant;
    }
}
