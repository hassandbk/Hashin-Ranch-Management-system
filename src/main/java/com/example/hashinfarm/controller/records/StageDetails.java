package com.example.hashinfarm.controller.records;

import java.util.List;

public record StageDetails(String stageName, List<SubStageDetails> subStages) {
}
