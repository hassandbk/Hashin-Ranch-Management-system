package com.example.hashinfarm.data.DTOs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BreedingAttempt {
    private final IntegerProperty breedingAttemptId;
    private final IntegerProperty cattleId;
    private final StringProperty estrusDate;
    private final StringProperty breedingMethod;
    private final IntegerProperty sireId;
    private final StringProperty notes;
    private final StringProperty attemptDate;
    private final StringProperty attemptStatus;

    // Constructor
    public BreedingAttempt(int breedingAttemptId, int cattleId, String estrusDate, String breedingMethod,
                           int sireId, String notes, String attemptDate, String attemptStatus) {
        this.breedingAttemptId = new SimpleIntegerProperty(breedingAttemptId);
        this.cattleId = new SimpleIntegerProperty(cattleId);
        this.estrusDate = new SimpleStringProperty(estrusDate);
        this.breedingMethod = new SimpleStringProperty(breedingMethod);
        this.sireId = new SimpleIntegerProperty(sireId);
        this.notes = new SimpleStringProperty(notes);
        this.attemptDate = new SimpleStringProperty(attemptDate);
        this.attemptStatus = new SimpleStringProperty(attemptStatus);
    }

    // Getters
    public int getBreedingAttemptId() {
        return breedingAttemptId.get();
    }

    public int getCattleId() {
        return cattleId.get();
    }

    public void setCattleId(int cattleId) {
        this.cattleId.set(cattleId);
    }

    public String getEstrusDate() {
        return estrusDate.get();
    }

    public String getBreedingMethod() {
        return breedingMethod.get();
    }

    public int getSireId() {
        return sireId.get();
    }

    public String getNotes() {
        return notes.get();
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public String getAttemptDate() {
        return attemptDate.get();
    }

    public String getAttemptStatus() {
        return attemptStatus.get();
    }

}
