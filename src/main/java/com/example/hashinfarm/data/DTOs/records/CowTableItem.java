package com.example.hashinfarm.data.DTOs.records;

public record CowTableItem(
        String cattleID,
        String currentStage,
        String selectedStageByDate,
        String equivalentSelectedDate,
        double todayMY,
        double equivalentDayMY,
        double currentStageMilkMY,
        double selectedStageMilkMY,
        double totalDailyMY,
        double averageDailyMY,
        double relativeMY,
        String performanceRating,
        String comparisonPerformance
) {}
