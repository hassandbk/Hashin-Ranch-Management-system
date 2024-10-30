package com.example.hashinfarm.data.DTOs.records;

import java.time.LocalDate;

public record UnifiedOffspring(
        int offspringId,
        String cattleId,
        String cattleName,
        String gender,
        String breedingMethod,
        Double birthWeight,
        Integer easeOfCalving,
        Integer gestationLength,
        Double measuredWeight,
        LocalDate lastDateWeightTaken,
        String dateOfBirth,
        String intendedUse,
        String sireId
) {}
