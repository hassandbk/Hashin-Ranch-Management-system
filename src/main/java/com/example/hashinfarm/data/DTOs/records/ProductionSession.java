package com.example.hashinfarm.data.DTOs.records;

import java.sql.Timestamp;

public record ProductionSession(
        int sessionID,
        int lactationPeriodID,
        int cattleID,
        Timestamp startTime,
        Timestamp endTime,
        String qualityScore,
        double productionVolume
) {}
