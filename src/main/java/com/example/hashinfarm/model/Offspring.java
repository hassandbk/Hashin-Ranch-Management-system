package com.example.hashinfarm.model;

import java.time.LocalDate;

public class Offspring {
    private int offspringId;
    private double birthWeight;
    private int easeOfCalving;
    private int gestationLength;
    private double measuredWeight;
    private LocalDate lastDateWeightTaken;
    private String intendedUse;
    private String cattleId;
    private String breedingMethod; // New field

    // Constructor
    public Offspring(int offspringId, double birthWeight, int easeOfCalving, int gestationLength,
                     double measuredWeight, LocalDate lastDateWeightTaken, String intendedUse, String cattleId,
                     String breedingMethod) {
        this.offspringId = offspringId;
        this.birthWeight = birthWeight;
        this.easeOfCalving = easeOfCalving;
        this.gestationLength = gestationLength;
        this.measuredWeight = measuredWeight;
        this.lastDateWeightTaken = lastDateWeightTaken;
        this.intendedUse = intendedUse;
        this.cattleId = cattleId;
        this.breedingMethod = breedingMethod; // Initialize new field
    }

    // Getters and Setters
    public int getOffspringId() { return offspringId; }
    public void setOffspringId(int offspringId) { this.offspringId = offspringId; }

    public double getBirthWeight() { return birthWeight; }
    public void setBirthWeight(double birthWeight) { this.birthWeight = birthWeight; }

    public int getEaseOfCalving() { return easeOfCalving; }
    public void setEaseOfCalving(int easeOfCalving) { this.easeOfCalving = easeOfCalving; }

    public int getGestationLength() { return gestationLength; }
    public void setGestationLength(int gestationLength) { this.gestationLength = gestationLength; }

    public double getMeasuredWeight() { return measuredWeight; }
    public void setMeasuredWeight(double measuredWeight) { this.measuredWeight = measuredWeight; }

    public LocalDate getLastDateWeightTaken() { return lastDateWeightTaken; }
    public void setLastDateWeightTaken(LocalDate lastDateWeightTaken) { this.lastDateWeightTaken = lastDateWeightTaken; }

    public String getIntendedUse() { return intendedUse; }
    public void setIntendedUse(String intendedUse) { this.intendedUse = intendedUse; }

    public String getCattleId() { return cattleId; }
    public void setCattleId(String cattleId) { this.cattleId = cattleId; }

    public String getBreedingMethod() { return breedingMethod; }
    public void setBreedingMethod(String breedingMethod) { this.breedingMethod = breedingMethod; }
}
