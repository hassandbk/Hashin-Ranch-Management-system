package com.example.hashinfarm.data.records;

public record SubStageDetails(
        String name,
        int startDay,
        int endDay,
        boolean throughoutTrimester) {
}
