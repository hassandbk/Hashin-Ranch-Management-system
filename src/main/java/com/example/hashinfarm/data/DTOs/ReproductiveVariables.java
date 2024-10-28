package com.example.hashinfarm.data.DTOs;

import java.time.LocalDate;

public class ReproductiveVariables {
    private int reproductiveVariableID;
    private int cattleID;
    private LocalDate breedingDate;
    private int gestationPeriod;
    private LocalDate calvingDate;
    private int calvingInterval;

    // Constructors
    public ReproductiveVariables() {
    }

    public ReproductiveVariables(int reproductiveVariableID, int cattleID, LocalDate breedingDate, int gestationPeriod, LocalDate calvingDate, int calvingInterval) {
        this.reproductiveVariableID = reproductiveVariableID;
        this.cattleID = cattleID;
        this.breedingDate = breedingDate;
        this.gestationPeriod = gestationPeriod;
        this.calvingDate = calvingDate;
        this.calvingInterval = calvingInterval;
    }

    // Getters and Setters
    public int getReproductiveVariableID() {
        return reproductiveVariableID;
    }

    public void setReproductiveVariableID(int reproductiveVariableID) {
        this.reproductiveVariableID = reproductiveVariableID;
    }

    public int getCattleID() {
        return cattleID;
    }

    public void setCattleID(int cattleID) {
        this.cattleID = cattleID;
    }

    public LocalDate getBreedingDate() {
        return breedingDate;
    }

    public void setBreedingDate(LocalDate breedingDate) {
        this.breedingDate = breedingDate;
    }

    public int getGestationPeriod() {
        return gestationPeriod;
    }

    public void setGestationPeriod(int gestationPeriod) {
        this.gestationPeriod = gestationPeriod;
    }

    public LocalDate getCalvingDate() {
        return calvingDate;
    }

    public void setCalvingDate(LocalDate calvingDate) {
        this.calvingDate = calvingDate;
    }

    public int getCalvingInterval() {
        return calvingInterval;
    }

    public void setCalvingInterval(int calvingInterval) {
        this.calvingInterval = calvingInterval;
    }

    // toString() method
    @Override
    public String toString() {
        return "ReproductiveVariables{" +
                "reproductiveVariableID=" + reproductiveVariableID +
                ", cattleID=" + cattleID +
                ", breedingDate=" + breedingDate +
                ", gestationPeriod=" + gestationPeriod +
                ", calvingDate=" + calvingDate +
                ", calvingInterval=" + calvingInterval +
                '}';
    }
}
