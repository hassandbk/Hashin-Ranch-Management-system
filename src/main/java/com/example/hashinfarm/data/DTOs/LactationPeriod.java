package com.example.hashinfarm.data.DTOs;

import java.time.LocalDate;

public class LactationPeriod {
    private int lactationPeriodID;
    private int cattleID;
    private LocalDate startDate;
    private LocalDate endDate;


    public LactationPeriod(int lactationPeriodID, int cattleID, LocalDate startDate, LocalDate endDate) {
        this.lactationPeriodID = lactationPeriodID;
        this.cattleID = cattleID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getLactationPeriodID() {
        return lactationPeriodID;
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

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


}
