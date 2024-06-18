package com.example.hashinfarm.model;

import java.sql.Timestamp;

public class ProductionSession {
    private int sessionID;
    private int lactationPeriodID;
    private int cattleID;
    private Timestamp startTime;
    private Timestamp endTime;
    private String qualityScore;
    private double productionVolume; // New attribute for ProductionVolume

    public ProductionSession(int sessionID, int lactationPeriodID, int cattleID, Timestamp startTime, Timestamp endTime, String qualityScore, double productionVolume) {
        this.sessionID = sessionID;
        this.lactationPeriodID = lactationPeriodID;
        this.cattleID = cattleID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.qualityScore = qualityScore;
        this.productionVolume = productionVolume;
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

    public int getCattleID() {
        return cattleID;
    }

    public void setCattleID(int cattleID) {
        this.cattleID = cattleID;
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

     public String getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(String qualityScore) {
        this.qualityScore = qualityScore;
    }

    public double getProductionVolume() {
        return productionVolume;
    }

    public void setProductionVolume(double productionVolume) {
        this.productionVolume = productionVolume;
    }
}
