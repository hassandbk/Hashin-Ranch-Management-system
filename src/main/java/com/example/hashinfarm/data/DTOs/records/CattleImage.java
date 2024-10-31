package com.example.hashinfarm.data.DTOs.records;

import java.sql.Timestamp;

public record CattleImage(int imageId, int cattleId, String imagePath, Timestamp createdAt) {}
