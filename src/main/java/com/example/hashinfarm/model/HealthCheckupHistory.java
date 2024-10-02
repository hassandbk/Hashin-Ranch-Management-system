package com.example.hashinfarm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HealthCheckupHistory {
    private int id;
    private int cattleId;
    private LocalDate checkupDate;
    private String temperature;
    private String heartRate;
    private String respiratoryRate;
    private String bloodPressure;
    private String behavioralObservations;
    private String physicalExaminationFindings;
    private String healthIssues;
    private String specificObservations;
    private String checkupNotes;
    private String chronicConditions;
    private LocalDateTime createdAt; // Added createdAt
    private List<FollowUpRecommendation> followUpRecommendations;

    // Constructor
    public HealthCheckupHistory(int id, int cattleId, LocalDate checkupDate, String temperature,
                                String heartRate, String respiratoryRate, String bloodPressure,
                                String behavioralObservations, String physicalExaminationFindings,
                                String healthIssues, String specificObservations, String checkupNotes,
                                String chronicConditions, LocalDateTime createdAt,
                                List<FollowUpRecommendation> followUpRecommendations) {
        this.id = id;
        this.cattleId = cattleId;
        this.checkupDate = checkupDate;
        this.temperature = temperature;
        this.heartRate = heartRate;
        this.respiratoryRate = respiratoryRate;
        this.bloodPressure = bloodPressure;
        this.behavioralObservations = behavioralObservations;
        this.physicalExaminationFindings = physicalExaminationFindings;
        this.healthIssues = healthIssues;
        this.specificObservations = specificObservations;
        this.checkupNotes = checkupNotes;
        this.chronicConditions = chronicConditions;
        this.createdAt = createdAt;
        this.followUpRecommendations = followUpRecommendations;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCattleId() {
        return cattleId;
    }

    public void setCattleId(int cattleId) {
        this.cattleId = cattleId;
    }

    public LocalDate getCheckupDate() {
        return checkupDate;
    }

    public void setCheckupDate(LocalDate checkupDate) {
        this.checkupDate = checkupDate;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(String respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getBehavioralObservations() {
        return behavioralObservations;
    }

    public void setBehavioralObservations(String behavioralObservations) {
        this.behavioralObservations = behavioralObservations;
    }

    public String getPhysicalExaminationFindings() {
        return physicalExaminationFindings;
    }

    public void setPhysicalExaminationFindings(String physicalExaminationFindings) {
        this.physicalExaminationFindings = physicalExaminationFindings;
    }

    public String getHealthIssues() {
        return healthIssues;
    }

    public void setHealthIssues(String healthIssues) {
        this.healthIssues = healthIssues;
    }

    public String getSpecificObservations() {
        return specificObservations;
    }

    public void setSpecificObservations(String specificObservations) {
        this.specificObservations = specificObservations;
    }

    public String getCheckupNotes() {
        return checkupNotes;
    }

    public void setCheckupNotes(String checkupNotes) {
        this.checkupNotes = checkupNotes;
    }

    public String getChronicConditions() {
        return chronicConditions;
    }

    public void setChronicConditions(String chronicConditions) {
        this.chronicConditions = chronicConditions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<FollowUpRecommendation> getFollowUpRecommendations() {
        return followUpRecommendations;
    }

    public void setFollowUpRecommendations(List<FollowUpRecommendation> followUpRecommendations) {
        this.followUpRecommendations = followUpRecommendations;
    }
}
