package com.example.hashinfarm.data.DTOs.records;

import java.time.LocalDate;

public record LactationPeriod(int lactationPeriodID, int cattleID, LocalDate startDate, LocalDate endDate) {
}
