package com.example.hashinfarm.model;

public class CalvingEvent {
    private int calvingEventId;
    private int cattleId;
    private int reproductiveVariableId;
    private int offspringId;
    private String assistanceRequired;
    private Integer durationOfCalving;
    private String physicalConditionCalf;
    private int numberOfCalvesBorn;
    private int calvesBornAlive;
    private Integer stillbirths;

    // Constructor with all fields
    public CalvingEvent(int calvingEventId, int cattleId, int reproductiveVariableId, int offspringId,
                        String assistanceRequired, Integer durationOfCalving, String physicalConditionCalf, int numberOfCalvesBorn, int calvesBornAlive, Integer stillbirths) {
        this.calvingEventId = calvingEventId;
        this.cattleId = cattleId;
        this.reproductiveVariableId = reproductiveVariableId;
        this.offspringId = offspringId;
        this.assistanceRequired = assistanceRequired;
        this.durationOfCalving = durationOfCalving;
        this.physicalConditionCalf = physicalConditionCalf;
        this.numberOfCalvesBorn = numberOfCalvesBorn;
        this.calvesBornAlive = calvesBornAlive;
        this.stillbirths = stillbirths;
    }

    // Getters and Setters
    public int getCalvingEventId() {
        return calvingEventId;
    }

    public void setCalvingEventId(int calvingEventId) {
        this.calvingEventId = calvingEventId;
    }

    public int getCattleId() {
        return cattleId;
    }

    public void setCattleId(int cattleId) {
        this.cattleId = cattleId;
    }

    public int getReproductiveVariableId() {
        return reproductiveVariableId;
    }

    public void setReproductiveVariableId(int reproductiveVariableId) {
        this.reproductiveVariableId = reproductiveVariableId;
    }

    public int getOffspringId() {
        return offspringId;
    }

    public void setOffspringId(int offspringId) {
        this.offspringId = offspringId;
    }

    public String getAssistanceRequired() {
        return assistanceRequired;
    }

    public void setAssistanceRequired(String assistanceRequired) {
        this.assistanceRequired = assistanceRequired;
    }

    public Integer getDurationOfCalving() {
        return durationOfCalving;
    }

    public void setDurationOfCalving(Integer durationOfCalving) {
        this.durationOfCalving = durationOfCalving;
    }

    public String getPhysicalConditionCalf() {
        return physicalConditionCalf;
    }

    public void setPhysicalConditionCalf(String physicalConditionCalf) {
        this.physicalConditionCalf = physicalConditionCalf;
    }


    public int getNumberOfCalvesBorn() {
        return numberOfCalvesBorn;
    }

    public void setNumberOfCalvesBorn(int numberOfCalvesBorn) {
        this.numberOfCalvesBorn = numberOfCalvesBorn;
    }

    public int getCalvesBornAlive() {
        return calvesBornAlive;
    }

    public void setCalvesBornAlive(int calvesBornAlive) {
        this.calvesBornAlive = calvesBornAlive;
    }

    public Integer getStillbirths() {
        return stillbirths;
    }

    public void setStillbirths(Integer stillbirths) {
        this.stillbirths = stillbirths;
    }
}
