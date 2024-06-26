package com.example.hashinfarm.model;

public class CowTableItem {
    private String cowId;
    private String currentStage;
    private double milkYield;
    private double stageMilkYield;
    private double relativeMilkYield;
    private String selectedCriteriaProduction;

    // Empty constructor
    public CowTableItem() {
    }

    // Constructor with parameters
    public CowTableItem(String cowId, String currentStage, double milkYield, double stageMilkYield, double relativeMilkYield,  String selectedCriteriaProduction) {
        this.cowId = cowId;
        this.currentStage = currentStage;
        this.milkYield = milkYield;
        this.stageMilkYield = stageMilkYield;
        this.relativeMilkYield = relativeMilkYield;
        this.selectedCriteriaProduction = selectedCriteriaProduction;
    }

    // Getters and setters
    public String getCowId() {
        return cowId;
    }

    public void setCowId(String cowId) {
        this.cowId = cowId;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public double getMilkYield() {
        return milkYield;
    }

    public void setMilkYield(double milkYield) {
        this.milkYield = milkYield;
    }

    public double getStageMilkYield() {
        return stageMilkYield;
    }

    public void setStageMilkYield(double stageMilkYield) {
        this.stageMilkYield = stageMilkYield;
    }

    public double getRelativeMilkYield() {
        return relativeMilkYield;
    }

    public void setRelativeMilkYield(double relativeMilkYield) {
        this.relativeMilkYield = relativeMilkYield;
    }


    public String getSelectedCriteriaProduction() {
        return selectedCriteriaProduction;
    }

    public void setSelectedCriteriaProduction(String selectedCriteriaProduction) {
        this.selectedCriteriaProduction = selectedCriteriaProduction;
    }
}
