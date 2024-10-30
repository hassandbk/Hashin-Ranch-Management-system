package com.example.hashinfarm.data.DTOs.records;

import java.util.List;

public record StageDetails(
        String stageName,
        List<SubStageDetails> subStages) {
}
