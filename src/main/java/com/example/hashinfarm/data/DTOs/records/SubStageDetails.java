package com.example.hashinfarm.data.DTOs.records;

public record SubStageDetails(
        String name,
        int startDay,
        int endDay,
        boolean throughoutTrimester) {
}
