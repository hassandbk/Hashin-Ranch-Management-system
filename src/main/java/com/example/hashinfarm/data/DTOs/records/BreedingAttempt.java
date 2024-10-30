package com.example.hashinfarm.data.DTOs.records;

public record BreedingAttempt(
        int breedingAttemptId,
        int cattleId,
        String estrusDate,
        String breedingMethod,
        int sireId,
        String notes,
        String attemptDate,
        String attemptStatus
) {}
