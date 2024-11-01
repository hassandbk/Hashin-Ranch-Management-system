package com.example.hashinfarm.data.DTOs.records;

import java.time.LocalDate;

public record ReproductiveVariables(
        int reproductiveVariableID,
        int cattleID,
        LocalDate breedingDate,
        int gestationPeriod,
        LocalDate calvingDate,
        int calvingInterval
) {}
