package com.example.hashinfarm.controller.records;

public record SubStageDetails(
        String name,
        int startDay,
        int endDay,
        boolean throughoutTrimester) {
}
