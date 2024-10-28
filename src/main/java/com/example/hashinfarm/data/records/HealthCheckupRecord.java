package com.example.hashinfarm.data.records;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HealthCheckupRecord(
        int id,
        LocalDate checkupDate,
        String temperature,
        String heartRate,
        String respiratoryRate,
        String bloodPressure,
        String behavioralObservations,
        String physicalExaminationFindings,
        String healthIssues,
        String specificObservations,
        String checkupNotes,
        String chronicConditions,
        LocalDateTime createdAt
) {
}
