package com.example.hashinfarm.data.DTOs.records;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record HealthCheckupRecord(
        int id,
        int cattleId,
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
        LocalDateTime createdAt,
        List<FollowUpRecommendation> followUpRecommendations // Assuming this class exists
) {
}
