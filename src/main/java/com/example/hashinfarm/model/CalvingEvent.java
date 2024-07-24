package com.example.hashinfarm.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CalvingEvent {
    private final IntegerProperty calvingEventId;
    private final IntegerProperty cattleId;
    private final IntegerProperty reproductiveVariableId;
    private final IntegerProperty offspringId;
    private final StringProperty assistanceRequired;
    private final StringProperty physicalConditionCalf;
    private final IntegerProperty numberOfCalvesBorn;
    private final IntegerProperty calvesBornAlive;
    private final IntegerProperty stillbirths;

    // Constructor with all fields
    public CalvingEvent(int calvingEventId, int cattleId, int reproductiveVariableId, int offspringId,
                        String assistanceRequired, String physicalConditionCalf, int numberOfCalvesBorn, int calvesBornAlive, Integer stillbirths) {
        this.calvingEventId = new SimpleIntegerProperty(calvingEventId);
        this.cattleId = new SimpleIntegerProperty(cattleId);
        this.reproductiveVariableId = new SimpleIntegerProperty(reproductiveVariableId);
        this.offspringId = new SimpleIntegerProperty(offspringId);
        this.assistanceRequired = new SimpleStringProperty(assistanceRequired);
        this.physicalConditionCalf = new SimpleStringProperty(physicalConditionCalf);
        this.numberOfCalvesBorn = new SimpleIntegerProperty(numberOfCalvesBorn);
        this.calvesBornAlive = new SimpleIntegerProperty(calvesBornAlive);
        this.stillbirths = new SimpleIntegerProperty(stillbirths != null ? stillbirths : 0);
    }

    // Getters for properties
    public IntegerProperty calvingEventIdProperty() {
        return calvingEventId;
    }

    public IntegerProperty cattleIdProperty() {
        return cattleId;
    }

    public IntegerProperty reproductiveVariableIdProperty() {
        return reproductiveVariableId;
    }

    public IntegerProperty offspringIdProperty() {
        return offspringId;
    }

    public StringProperty assistanceRequiredProperty() {
        return assistanceRequired;
    }

    public StringProperty physicalConditionCalfProperty() {
        return physicalConditionCalf;
    }

    public IntegerProperty numberOfCalvesBornProperty() {
        return numberOfCalvesBorn;
    }

    public IntegerProperty calvesBornAliveProperty() {
        return calvesBornAlive;
    }

    public IntegerProperty stillbirthsProperty() {
        return stillbirths;
    }

    // Setters
    public void setCalvingEventId(int calvingEventId) {
        this.calvingEventId.set(calvingEventId);
    }

    public void setCattleId(int cattleId) {
        this.cattleId.set(cattleId);
    }

    public void setReproductiveVariableId(int reproductiveVariableId) {
        this.reproductiveVariableId.set(reproductiveVariableId);
    }

    public void setOffspringId(int offspringId) {
        this.offspringId.set(offspringId);
    }

    public void setAssistanceRequired(String assistanceRequired) {
        this.assistanceRequired.set(assistanceRequired);
    }

    public void setPhysicalConditionCalf(String physicalConditionCalf) {
        this.physicalConditionCalf.set(physicalConditionCalf);
    }

    public void setNumberOfCalvesBorn(int numberOfCalvesBorn) {
        this.numberOfCalvesBorn.set(numberOfCalvesBorn);
    }

    public void setCalvesBornAlive(int calvesBornAlive) {
        this.calvesBornAlive.set(calvesBornAlive);
    }

    public void setStillbirths(Integer stillbirths) {
        this.stillbirths.set(stillbirths != null ? stillbirths : 0);
    }
}
