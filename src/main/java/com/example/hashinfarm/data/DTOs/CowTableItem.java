package com.example.hashinfarm.data.DTOs;

public class CowTableItem {
    private String cattleID;
    private final String currentStage;
    private final String selectedStageByDate;
    private final String equivalentSelectedDate;
    private final double todayMY;
    private final double equivalentDayMY;
    private final double currentStageMilkMY;
    private final double selectedStageMilkMY;
    private final double totalDailyMY;
    private final double averageDailyMY;
    private final double relativeMY;
    private final String performanceRating;
    private final String comparisonPerformance;

    public CowTableItem(String cattleID, String currentStage, String selectedStageByDate, String equivalentSelectedDate, double todayMY,
                        double equivalentDayMY, double currentStageMilkMY, double selectedStageMilkMY, double totalDailyMY,
                        double averageDailyMY, double relativeMY, String performanceRating, String comparisonPerformance) {
        this.cattleID = cattleID;
        this.currentStage = currentStage;
        this.selectedStageByDate = selectedStageByDate;
        this.equivalentSelectedDate = equivalentSelectedDate;
        this.todayMY = todayMY;
        this.equivalentDayMY = equivalentDayMY;
        this.currentStageMilkMY = currentStageMilkMY;
        this.selectedStageMilkMY = selectedStageMilkMY;
        this.totalDailyMY = totalDailyMY;
        this.averageDailyMY = averageDailyMY;
        this.relativeMY = relativeMY;
        this.performanceRating = performanceRating;
        this.comparisonPerformance = comparisonPerformance;
    }

    // Getters and Setters

    public String getCattleID() {
        return cattleID;
    }

    public void setCattleID(String cattleID) {
        this.cattleID = cattleID;
    }


}
