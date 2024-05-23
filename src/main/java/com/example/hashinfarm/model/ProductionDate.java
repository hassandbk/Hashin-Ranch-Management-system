package com.example.hashinfarm.model;

import java.sql.Date;

public class ProductionDate {
    private int productionDateID;
    private int cattleID;
    private Date date;

    public ProductionDate(int productionDateID, int cattleID, Date date) {
        this.productionDateID = productionDateID;
        this.cattleID = cattleID;
        this.date = date;
    }

    // Getters and Setters
    public int getProductionDateID() {
        return productionDateID;
    }

    public void setProductionDateID(int productionDateID) {
        this.productionDateID = productionDateID;
    }

    public int getCattleID() {
        return cattleID;
    }

    public void setCattleID(int cattleID) {
        this.cattleID = cattleID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
