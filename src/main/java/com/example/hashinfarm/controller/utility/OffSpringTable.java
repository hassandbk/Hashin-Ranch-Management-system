package com.example.hashinfarm.controller.utility;

import java.time.LocalDate;

public class OffSpringTable {
    private int offspringId; // Add this field
    private String cattleId;
    private String cattleName;
    private String gender;
    private String breedingMethod;
    private double birthWeight;
    private int easeOfCalving;
    private int gestationLength;
    private double measuredWeight;
    private LocalDate lastDateWeightTaken;
    private String intendedUse;
    private String sireId;

    // Updated Constructor
    public OffSpringTable(int offspringId, String cattleId, String cattleName, String gender, String breedingMethod,
                          double birthWeight, int easeOfCalving, int gestationLength, double measuredWeight,
                          LocalDate lastDateWeightTaken, String intendedUse, String sireId) {
        this.offspringId = offspringId; // Initialize the new field
        this.cattleId = cattleId;
        this.cattleName = cattleName;
        this.gender = gender;
        this.breedingMethod = breedingMethod;
        this.birthWeight = birthWeight;
        this.easeOfCalving = easeOfCalving;
        this.gestationLength = gestationLength;
        this.measuredWeight = measuredWeight;
        this.lastDateWeightTaken = lastDateWeightTaken;
        this.intendedUse = intendedUse;
        this.sireId = sireId;
    }

    // Getters and Setters
    public int getOffspringId() { return offspringId; }
    public void setOffspringId(int offspringId) { this.offspringId = offspringId; }
    // Getters and Setters for all properties
    public String getCattleId() { return cattleId; }
    public void setCattleId(String cattleId) { this.cattleId = cattleId; }

    public String getCattleName() { return cattleName; }
    public void setCattleName(String cattleName) { this.cattleName = cattleName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBreedingMethod() { return breedingMethod; }
    public void setBreedingMethod(String breedingMethod) { this.breedingMethod = breedingMethod; }

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

    public String getSireId() { return sireId; }
    public void setSireId(String sireId) { this.sireId = sireId; }
}
