package com.example.hashinfarm.model;

public class CowTableItem {
    private String cattleID;
    private String stage;
    private double stageMilkYielded;
    private double totalDailyMilkYielded;
    private double averageDailyMilkYielded;
    private double relativeDailyMilkYielded;
    private String performanceRating;

    public CowTableItem(String cattleID, String stage, double stageMilkYielded, double totalDailyMilkYielded, double averageDailyMilkYielded, double relativeDailyMilkYielded, String performanceRating) {
        this.cattleID = cattleID;
        this.stage = stage;
        this.stageMilkYielded = stageMilkYielded;
        this.totalDailyMilkYielded = totalDailyMilkYielded;
        this.averageDailyMilkYielded = averageDailyMilkYielded;
        this.relativeDailyMilkYielded = relativeDailyMilkYielded;
        this.performanceRating = performanceRating;
    }

    public String getCattleID() {
        return cattleID;
    }

    public void setCattleID(String cattleID) {
        this.cattleID = cattleID;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public double getStageMilkYielded() {
        return stageMilkYielded;
    }

    public void setStageMilkYielded(double stageMilkYielded) {
        this.stageMilkYielded = stageMilkYielded;
    }

    public double getTotalDailyMilkYielded() {
        return totalDailyMilkYielded;
    }

    public void setTotalDailyMilkYielded(double totalDailyMilkYielded) {
        this.totalDailyMilkYielded = totalDailyMilkYielded;
    }

    public double getAverageDailyMilkYielded() {
        return averageDailyMilkYielded;
    }

    public void setAverageDailyMilkYielded(double averageDailyMilkYielded) {
        this.averageDailyMilkYielded = averageDailyMilkYielded;
    }

    public double getRelativeDailyMilkYielded() {
        return relativeDailyMilkYielded;
    }

    public void setRelativeDailyMilkYielded(double relativeDailyMilkYielded) {
        this.relativeDailyMilkYielded = relativeDailyMilkYielded;
    }

    public String getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(String performanceRating) {
        this.performanceRating = performanceRating;
    }
}
