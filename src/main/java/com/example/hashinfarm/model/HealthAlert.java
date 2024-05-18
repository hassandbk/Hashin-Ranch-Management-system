package com.example.hashinfarm.model;

public class HealthAlert {
    private int alertID;
    private int cattleID;
    private String alertType;
    private String notes;

    public HealthAlert(int alertID, int cattleID, String alertType, String notes) {
        this.alertID = alertID;
        this.cattleID = cattleID;
        this.alertType = alertType;
        this.notes = notes;
    }

    // Getters and Setters
    public int getAlertID() {
        return alertID;
    }

    public void setAlertID(int alertID) {
        this.alertID = alertID;
    }

    public int getCattleID() {
        return cattleID;
    }

    public void setCattleID(int cattleID) {
        this.cattleID = cattleID;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
