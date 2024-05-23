package com.example.hashinfarm.model;

import java.sql.Timestamp;

public class ProductionSession {
    private int sessionID;
    private int lactationPeriodID;
    private int productionDateID; // New attribute for production date
    private Timestamp startTime;
    private Timestamp endTime;
    private int duration;
    private String qualityScore; // Changed to String to match the enum in the database

    public ProductionSession(int sessionID, int lactationPeriodID, int productionDateID, Timestamp startTime, Timestamp endTime, int duration, String qualityScore) {
        this.sessionID = sessionID;
        this.lactationPeriodID = lactationPeriodID;
        this.productionDateID = productionDateID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.qualityScore = qualityScore;
    }

    // Getters and Setters
    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public int getLactationPeriodID() {
        return lactationPeriodID;
    }

    public void setLactationPeriodID(int lactationPeriodID) {
        this.lactationPeriodID = lactationPeriodID;
    }

    public int getProductionDateID() {
        return productionDateID;
    }

    public void setProductionDateID(int productionDateID) {
        this.productionDateID = productionDateID;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(String qualityScore) {
        this.qualityScore = qualityScore;
    }
}
