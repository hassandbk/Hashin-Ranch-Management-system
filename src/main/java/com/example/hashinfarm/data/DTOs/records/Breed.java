package com.example.hashinfarm.data.DTOs.records;

public record Breed(
        int breedId,
        String breedName,
        String origin,
        boolean recognition,
        String comments
) {}
