package com.example.hashinfarm.controller.records;

import java.time.LocalDate;

public record CattleUIInfo(String tagId, String name, String gender, String colorMarkings, LocalDate dateOfBirth,
                           int weightId, String bcs) {
}