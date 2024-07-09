package com.example.hashinfarm.controller.utility;

import javafx.collections.ObservableList;
import java.time.LocalDate;

public class CattleYieldData {
    private int cattleID;
    private String lactationStage;
    private LocalDate startDate;
    private LocalDate endDate;
    private ObservableList<Double> dailyYields;

    public CattleYieldData(int cattleID, String lactationStage, LocalDate startDate, LocalDate endDate, ObservableList<Double> dailyYields) {
        this.cattleID = cattleID;
        this.lactationStage = lactationStage;
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

    public String getLactationStage() {
        return lactationStage;
    }

    public void setLactationStage(String lactationStage) {
        this.lactationStage = lactationStage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ObservableList<Double> getDailyYields() {
        return dailyYields;
    }

    public void setDailyYields(ObservableList<Double> dailyYields) {
        this.dailyYields = dailyYields;
    }
}
