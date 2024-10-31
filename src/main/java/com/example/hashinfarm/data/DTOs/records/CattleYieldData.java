package com.example.hashinfarm.data.DTOs.records;

import javafx.collections.ObservableList;
import java.time.LocalDate;

public record CattleYieldData(
        int cattleID,
        LocalDate startDate,
        LocalDate endDate,
        ObservableList<Double> dailyYields
) {}
