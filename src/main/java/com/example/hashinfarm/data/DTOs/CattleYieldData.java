package com.example.hashinfarm.data.DTOs;

import javafx.collections.ObservableList;
import java.time.LocalDate;

public class CattleYieldData {
    private int cattleID;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ObservableList<Double> dailyYields;

    public CattleYieldData(int cattleID, LocalDate startDate, LocalDate endDate, ObservableList<Double> dailyYields) {
        this.cattleID = cattleID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyYields = dailyYields;
    }

    public int getCattleID() {
        return cattleID;
    }

    public void setCattleID(int cattleID) {
        this.cattleID = cattleID;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ObservableList<Double> getDailyYields() {
        return dailyYields;
    }
}
