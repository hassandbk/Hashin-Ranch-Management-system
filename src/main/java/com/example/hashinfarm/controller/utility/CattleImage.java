package com.example.hashinfarm.controller.utility;

import java.sql.Timestamp;

public class CattleImage {
    private int imageId;
    private int cattleId;
    private String imagePath;
    private Timestamp createdAt;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getCattleId() {
        return cattleId;
    }

    public void setCattleId(int cattleId) {
        this.cattleId = cattleId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
