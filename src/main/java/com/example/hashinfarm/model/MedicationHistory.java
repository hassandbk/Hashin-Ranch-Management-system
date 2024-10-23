package com.example.hashinfarm.model;

import java.time.LocalDate;

public class MedicationHistory {
    private int id;                   // Unique identifier for the medication record
    private int cattleId;             // ID of the cattle receiving the medication
    private String dosage;            // Dosage of the medication
    private String frequency;         // Frequency of administration
    private LocalDate dateTaken;      // Date when the medication was administered
    private LocalDate nextSchedule;   // Date for the next scheduled medication
    private String administeredBy;    // Who administered the medication
    private String telNo;             // Contact number of the person administering
    private String category;          // Category of medication
    private String responseType;      // Response type (Negative, Positive, or None)

    // Constructor
    public MedicationHistory(int id, int cattleId, String dosage, String frequency, LocalDate dateTaken,
                             LocalDate nextSchedule, String administeredBy, String telNo,
                             String category, String responseType) {
        this.id = id;
        this.cattleId = cattleId;
        this.dosage = dosage;
        this.frequency = frequency;
        this.dateTaken = dateTaken;
        this.nextSchedule = nextSchedule;
        this.administeredBy = administeredBy;
        this.telNo = telNo;
        this.category = category;
        this.responseType = responseType; // Set responseType
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCattleId() {
        return cattleId;
    }

    public void setCattleId(int cattleId) {
        this.cattleId = cattleId;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDate getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(LocalDate dateTaken) {
        this.dateTaken = dateTaken;
    }

    public LocalDate getNextSchedule() {
        return nextSchedule;
    }

    public void setNextSchedule(LocalDate nextSchedule) {
        this.nextSchedule = nextSchedule;
    }


    public String getAdministeredBy() {
        return administeredBy;
    }

    public void setAdministeredBy(String administeredBy) {
        this.administeredBy = administeredBy;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getResponseType() {
        return responseType; // Getter for response type
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType; // Setter for response type
    }

    @Override
    public String toString() {
        return "MedicationHistory{" +
                "id=" + id +
                ", cattleId=" + cattleId +
                ", dosage='" + dosage + '\'' +
                ", frequency='" + frequency + '\'' +
                ", dateTaken=" + dateTaken +
                ", nextSchedule=" + nextSchedule +
                ", administeredBy='" + administeredBy + '\'' +
                ", telNo='" + telNo + '\'' +
                ", category='" + category + '\'' +
                ", responseType='" + responseType + '\'' + // Include response type in toString
                '}';
    }
}
