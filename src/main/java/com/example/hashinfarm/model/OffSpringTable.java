package com.example.hashinfarm.model;

import java.time.LocalDate;

public class OffSpringTable {
    private int offspringId;
    private String cattleId;
    private String cattleName;
    private String gender;
    private String breedingMethod;
    private Double birthWeight;
    private Integer easeOfCalving;
    private Integer gestationLength;
    private Double measuredWeight;
    private LocalDate lastDateWeightTaken;
    private String dateOfBirth;
    private String intendedUse;
    private String sireId;

    // Updated Constructor
    public OffSpringTable(int offspringId, String cattleId,String dateOfBirth, String cattleName, String gender, String breedingMethod,
                          Double birthWeight, Integer easeOfCalving, Integer gestationLength, Double measuredWeight,
                          LocalDate lastDateWeightTaken, String intendedUse, String sireId) {
        this.offspringId = offspringId;
        this.cattleId = cattleId;
        this.dateOfBirth= dateOfBirth;
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

    public String getCattleId() { return cattleId; }
    public void setCattleId(String cattleId) { this.cattleId = cattleId; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getCattleName() { return cattleName; }
    public void setCattleName(String cattleName) { this.cattleName = cattleName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBreedingMethod() { return breedingMethod; }
    public void setBreedingMethod(String breedingMethod) { this.breedingMethod = breedingMethod; }

    public Double getBirthWeight() { return birthWeight; }
    public void setBirthWeight(Double birthWeight) { this.birthWeight = birthWeight; }

    public Integer getEaseOfCalving() { return easeOfCalving; }
    public void setEaseOfCalving(Integer easeOfCalving) { this.easeOfCalving = easeOfCalving; }

    public Integer getGestationLength() { return gestationLength; }
    public void setGestationLength(Integer gestationLength) { this.gestationLength = gestationLength; }

    public Double getMeasuredWeight() { return measuredWeight; }
    public void setMeasuredWeight(Double measuredWeight) { this.measuredWeight = measuredWeight; }

    public LocalDate getLastDateWeightTaken() { return lastDateWeightTaken; }
    public void setLastDateWeightTaken(LocalDate lastDateWeightTaken) { this.lastDateWeightTaken = lastDateWeightTaken; }

    public String getIntendedUse() { return intendedUse; }
    public void setIntendedUse(String intendedUse) { this.intendedUse = intendedUse; }

    public String getSireId() { return sireId; }
    public void setSireId(String sireId) { this.sireId = sireId; }
}
