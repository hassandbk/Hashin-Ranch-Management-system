package com.example.hashinfarm.data.DTOs;

import java.time.LocalDate;

public class DewormingHistory {
    private int id;
    private int cattleId;
    private String dewormerType;
    private double dosage;
    private Double weightAtTime; // Double allows for NULL values
    private String administeredBy;
    private String routeOfAdministration;
    private LocalDate dateOfDeworming;
    private String manufacturerDetails;
    private String contactDetails;

    // Constructor
    public DewormingHistory(int id, int cattleId, String dewormerType, double dosage, Double weightAtTime,
                            String administeredBy, String routeOfAdministration, LocalDate dateOfDeworming,
                            String manufacturerDetails, String contactDetails) {
        this.id = id;
        this.cattleId = cattleId;
        this.dewormerType = dewormerType;
        this.dosage = dosage;
        this.weightAtTime = weightAtTime;
        this.administeredBy = administeredBy;
        this.routeOfAdministration = routeOfAdministration;
        this.dateOfDeworming = dateOfDeworming;
        this.manufacturerDetails = manufacturerDetails;
        this.contactDetails = contactDetails;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getCattleId() { return cattleId; }
    public void setCattleId(Integer cattleId) { this.cattleId = cattleId; }

    public String getDewormerType() { return dewormerType; }
    public void setDewormerType(String dewormerType) { this.dewormerType = dewormerType; }

    public double getDosage() { return dosage; }
    public void setDosage(double dosage) { this.dosage = dosage; }

    public Double getWeightAtTime() { return weightAtTime; }
    public void setWeightAtTime(Double weightAtTime) { this.weightAtTime = weightAtTime; }

    public String getAdministeredBy() { return administeredBy; }
    public void setAdministeredBy(String administeredBy) { this.administeredBy = administeredBy; }

    public String getRouteOfAdministration() { return routeOfAdministration; }
    public void setRouteOfAdministration(String routeOfAdministration) { this.routeOfAdministration = routeOfAdministration; }

    public LocalDate getDateOfDeworming() { return dateOfDeworming; }
    public void setDateOfDeworming(LocalDate dateOfDeworming) { this.dateOfDeworming = dateOfDeworming; }

    public String getManufacturerDetails() { return manufacturerDetails; }
    public void setManufacturerDetails(String manufacturerDetails) { this.manufacturerDetails = manufacturerDetails; }

    public String getContactDetails() { return contactDetails; }
    public void setContactDetails(String contactDetails) { this.contactDetails = contactDetails; }
}
