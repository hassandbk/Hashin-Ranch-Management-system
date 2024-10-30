package com.example.hashinfarm.data.DTOs.records;

import java.time.LocalDate;

public record MedicationRecord(
        int id,                   // Unique identifier for the medication record
        int cattleId,             // ID of the cattle receiving the medication
        String dosage,            // Dosage of the medication
        String frequency,         // Frequency of administration
        LocalDate dateTaken,      // Date when the medication was administered
        LocalDate nextSchedule,   // Date for the next scheduled medication
        String administeredBy,    // Who administered the medication
        String telNo,             // Contact number of the person administering
        String category,          // Category of medication
        String responseType       // Response type (Negative, Positive, or None)
) {
    // The default toString is usually sufficient, but you can customize if necessary
}
