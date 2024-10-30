package com.example.hashinfarm.data.DTOs;

import java.time.LocalDateTime;

public class FollowUpRecommendation {
    private int id;
    private int healthCheckupId;
    private String recommendation;
    private LocalDateTime createdAt;

    // Constructor
    public FollowUpRecommendation(int id, int healthCheckupId, String recommendation, LocalDateTime createdAt) {
        this.id = id;
        this.healthCheckupId = healthCheckupId;
        this.recommendation = recommendation;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHealthCheckupId() {
        return healthCheckupId;
    }

    public void setHealthCheckupId(int healthCheckupId) {
        this.healthCheckupId = healthCheckupId;
    }

    public String getRecommendation() {
        return recommendation;
    }



}
