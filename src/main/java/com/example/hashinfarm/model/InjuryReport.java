package com.example.hashinfarm.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InjuryReport {
    private int id; // Unique identifier for the injury report
    private Integer cattleId; // Identifier for the cattle
    private LocalDate dateOfOccurrence; // Date when the injury occurred
    private String typeOfInjury; // Type of injury (e.g., Contusion, Laceration)
    private String specificBodyPart; // Specific body part affected
    private String severity; // Severity of the injury (e.g., Acute, Chronic)
    private String causeOfInjury; // Description of how the injury occurred
    private String firstAidMeasures; // First aid measures taken
    private String followUpTreatmentType; // Type of follow-up treatment required
    private String monitoringInstructions; // Instructions for monitoring the injury
    private String scheduledProcedures; // Any scheduled procedures for the injury
    private String followUpMedications; // Medications to be followed up with
    private BigDecimal medicationCost; // Cost associated with the medications
    private int medicationHistoryId; // ID of the related medication history entry

    // Constructor to initialize all fields
    public InjuryReport(int id, Integer cattleId, LocalDate dateOfOccurrence, String typeOfInjury,
                        String specificBodyPart, String severity, String causeOfInjury,
                        String firstAidMeasures, String followUpTreatmentType, String monitoringInstructions,
                        String scheduledProcedures, String followUpMedications, BigDecimal medicationCost,
                        int medicationHistoryId) {
        this.id = id;
        this.cattleId = cattleId;
        this.dateOfOccurrence = dateOfOccurrence;
        this.typeOfInjury = typeOfInjury;
        this.specificBodyPart = specificBodyPart;
        this.severity = severity;
        this.causeOfInjury = causeOfInjury;
        this.firstAidMeasures = firstAidMeasures;
        this.followUpTreatmentType = followUpTreatmentType;
        this.monitoringInstructions = monitoringInstructions;
        this.scheduledProcedures = scheduledProcedures;
        this.followUpMedications = followUpMedications;
        this.medicationCost = medicationCost;
        this.medicationHistoryId = medicationHistoryId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getCattleId() {
        return cattleId;
    }

    public void setCattleId(Integer cattleId) {
        this.cattleId = cattleId;
    }

    public LocalDate getDateOfOccurrence() {
        return dateOfOccurrence;
    }

    public void setDateOfOccurrence(LocalDate dateOfOccurrence) {
        this.dateOfOccurrence = dateOfOccurrence;
    }

    public String getTypeOfInjury() {
        return typeOfInjury;
    }

    public void setTypeOfInjury(String typeOfInjury) {
        this.typeOfInjury = typeOfInjury;
    }

    public String getSpecificBodyPart() {
        return specificBodyPart;
    }

    public void setSpecificBodyPart(String specificBodyPart) {
        this.specificBodyPart = specificBodyPart;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCauseOfInjury() {
        return causeOfInjury;
    }

    public void setCauseOfInjury(String causeOfInjury) {
        this.causeOfInjury = causeOfInjury;
    }

    public String getFirstAidMeasures() {
        return firstAidMeasures;
    }

    public void setFirstAidMeasures(String firstAidMeasures) {
        this.firstAidMeasures = firstAidMeasures;
    }

    public String getFollowUpTreatmentType() {
        return followUpTreatmentType;
    }

    public void setFollowUpTreatmentType(String followUpTreatmentType) {
        this.followUpTreatmentType = followUpTreatmentType;
    }

    public String getMonitoringInstructions() {
        return monitoringInstructions;
    }

    public void setMonitoringInstructions(String monitoringInstructions) {
        this.monitoringInstructions = monitoringInstructions;
    }

    public String getScheduledProcedures() {
        return scheduledProcedures;
    }

    public void setScheduledProcedures(String scheduledProcedures) {
        this.scheduledProcedures = scheduledProcedures;
    }

    public String getFollowUpMedications() {
        return followUpMedications;
    }

    public void setFollowUpMedications(String followUpMedications) {
        this.followUpMedications = followUpMedications;
    }

    public BigDecimal getMedicationCost() {
        return medicationCost;
    }

    public void setMedicationCost(BigDecimal medicationCost) {
        this.medicationCost = medicationCost;
    }

    public int getMedicationHistoryId() {
        return medicationHistoryId;
    }

    public void setMedicationHistoryId(int medicationHistoryId) {
        this.medicationHistoryId = medicationHistoryId;
    }

    @Override
    public String toString() {
        return "InjuryReport{" +
                "id=" + id +
                ", cattleId=" + cattleId +
                ", dateOfOccurrence=" + dateOfOccurrence +
                ", typeOfInjury='" + typeOfInjury + '\'' +
                ", specificBodyPart='" + specificBodyPart + '\'' +
                ", severity='" + severity + '\'' +
                ", causeOfInjury='" + causeOfInjury + '\'' +
                ", firstAidMeasures='" + firstAidMeasures + '\'' +
                ", followUpTreatmentType='" + followUpTreatmentType + '\'' +
                ", monitoringInstructions='" + monitoringInstructions + '\'' +
                ", scheduledProcedures='" + scheduledProcedures + '\'' +
                ", followUpMedications='" + followUpMedications + '\'' +
                ", medicationCost=" + medicationCost +
                ", medicationHistoryId=" + medicationHistoryId +
                '}';
    }
}
