package com.example.hashinfarm.controller.records;

import java.time.LocalDate;

public record MedicationRecord(
        int id,
        String dosage,
        String frequency,
        LocalDate dateTaken,
        LocalDate nextSchedule,
        String administeredBy,
        String telNo,
        String category
) {
}
