package com.github.anhem.testpopulator.model.java;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class NamedDates {
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Date previousDate;
    private Date nextDate;
}
