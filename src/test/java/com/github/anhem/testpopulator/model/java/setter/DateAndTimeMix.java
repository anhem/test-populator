package com.github.anhem.testpopulator.model.java.setter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class DateAndTimeMix {

    private DateAndTimeMix.Date myDate;
    private java.util.Date date;
    private java.sql.Date dateSql;
    private java.time.LocalTime localTime;
    private DateAndTimeMix.LocalTime myLocalTime;
    private java.time.Instant instant;
    private DateAndTimeMix.Instant myInstant;
    private Set<java.time.Instant> instants;
    private Set<DateAndTimeMix.Instant> myInstants;
    private Map<java.time.Instant, DateAndTimeMix.Instant> instantsMap;
    private Map<DateAndTimeMix.Instant, java.time.Instant> myInstantsMap;

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class LocalTime {
        private java.time.LocalTime localTime;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Date {
        private java.util.Date date;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Instant {
        private java.time.Instant instant;
    }
}
