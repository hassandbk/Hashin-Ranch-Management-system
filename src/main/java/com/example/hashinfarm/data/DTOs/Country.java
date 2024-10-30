package com.example.hashinfarm.data.DTOs;

public class Country {
    private String name;
    private String callingCode;
    private String flagName;
    private String alpha2;

    // Constructors, getters, and setters
    public Country() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public String getFlagName() {
        return flagName;
    }

    public String getAlpha2() {
        return alpha2;
    }
}
