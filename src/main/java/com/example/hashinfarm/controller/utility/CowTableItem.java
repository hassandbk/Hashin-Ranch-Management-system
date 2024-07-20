package com.example.hashinfarm.controller.utility;

public class CowTableItem {
    private String cattleID;
    private String currentStage;
    private String selectedStageByDate;
    private String equivalentSelectedDate;
    private double todayMY;
    private double equivalentDayMY;
    private double currentStageMilkMY;
    private double selectedStageMilkMY;
    private double totalDailyMY;
    private double averageDailyMY;
    private double relativeMY;
    private String performanceRating;
    private String comparisonPerformance;

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

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getSelectedStageByDate() {
        return selectedStageByDate;
    }

    public void setSelectedStageByDate(String selectedStageByDate) {
        this.selectedStageByDate = selectedStageByDate;
    }

    public String getEquivalentSelectedDate() {
        return equivalentSelectedDate;
    }

    public void setEquivalentSelectedDate(String equivalentSelectedDate) {
        this.equivalentSelectedDate = equivalentSelectedDate;
    }

    public double getTodayMY() {
        return todayMY;
    }

    public void setTodayMY(double todayMY) {
        this.todayMY = todayMY;
    }

    public double getEquivalentDayMY() {
        return equivalentDayMY;
    }

    public void setEquivalentDayMY(double equivalentDayMY) {
        this.equivalentDayMY = equivalentDayMY;
    }

    public double getCurrentStageMilkMY() {
        return currentStageMilkMY;
    }

    public void setCurrentStageMilkMY(double currentStageMilkMY) {
        this.currentStageMilkMY = currentStageMilkMY;
    }

    public double getSelectedStageMilkMY() {
        return selectedStageMilkMY;
    }

    public void setSelectedStageMilkMY(double selectedStageMilkMY) {
        this.selectedStageMilkMY = selectedStageMilkMY;
    }

    public double getTotalDailyMY() {
        return totalDailyMY;
    }

    public void setTotalDailyMY(double totalDailyMY) {
        this.totalDailyMY = totalDailyMY;
    }

    public double getAverageDailyMY() {
        return averageDailyMY;
    }

    public void setAverageDailyMY(double averageDailyMY) {
        this.averageDailyMY = averageDailyMY;
    }

    public double getRelativeMY() {
        return relativeMY;
    }

    public void setRelativeMY(double relativeMY) {
        this.relativeMY = relativeMY;
    }

    public String getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(String performanceRating) {
        this.performanceRating = performanceRating;
    }

    public String getComparisonPerformance() {
        return comparisonPerformance;
    }

    public void setComparisonPerformance(String comparisonPerformance) {
        this.comparisonPerformance = comparisonPerformance;
    }
}
