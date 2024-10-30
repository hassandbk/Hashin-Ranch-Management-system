package com.example.hashinfarm.data.DTOs;

public class FilteredCattle {
    private int cattleID;
    private String tagID;
    private String name;
    private int herdID;
    // Constructor
    public FilteredCattle(int cattleID, String tagID, String name, int herdID) {
        this.cattleID = cattleID;
        this.tagID = tagID;
        this.name = name;
        this.herdID = herdID;
    }

    // Getters
    public int getCattleID() {
        return cattleID;
    }


    public String getName() {
        return name;
    }

    public void setCattleID(int cattleID) {
        this.cattleID = cattleID;
    }


    public void setName(String name) {
        this.name = name;
    }



}
