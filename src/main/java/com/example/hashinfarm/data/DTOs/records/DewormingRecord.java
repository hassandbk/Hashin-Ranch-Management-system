package com.example.hashinfarm.data.DTOs.records;

import java.time.LocalDate;

public record DewormingRecord(
        int id,
        int cattleId,
        String dewormerType,
        double dosage,
        Double weightAtTime, // Allows for NULL values
        String administeredBy,
        String routeOfAdministration,
        LocalDate dateOfDeworming,
        String manufacturerDetails,
        String contactDetails
) {
    // You can add custom methods if needed
}
