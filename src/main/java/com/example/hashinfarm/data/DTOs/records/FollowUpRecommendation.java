package com.example.hashinfarm.data.DTOs.records;

import java.time.LocalDateTime;

public record FollowUpRecommendation(
        int id,
        int healthCheckupId,
        String recommendation,
        LocalDateTime createdAt
) {}
