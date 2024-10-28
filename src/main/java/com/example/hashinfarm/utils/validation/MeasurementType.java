package com.example.hashinfarm.utils.validation;

public enum MeasurementType {
    DOSAGE("  ML", 1000),  // Dosage in mL
    WEIGHT("  KG", 2000),  // Weight in KG
    TEMPERATURE("  °C", 50), // Temperature in °C
    HEART_RATE(" BPM", 300), // Heart Rate in BPM
    RESPIRATORY_RATE(" BPM", 300), // Respiratory Rate in BPM
    BLOOD_PRESSURE(" mmHg", 300); // Blood Pressure in mmHg

    private final String suffix;
    private final double max;

    MeasurementType(String suffix, double max) {
        this.suffix = suffix;
        this.max = max;
    }

    public String getSuffix() {
        return suffix;
    }

    public double getMax() {
        return max;
    }
}
