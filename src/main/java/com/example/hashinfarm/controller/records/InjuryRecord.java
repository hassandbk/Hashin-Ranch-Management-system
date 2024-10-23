package com.example.hashinfarm.controller.records;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InjuryRecord(
        int id,                              // Unique identifier for the injury report
        int cattleId,                        // ID of the cattle associated with the report
        LocalDate dateOfOccurrence,          // Date of the injury occurrence
        String typeOfInjury,                 // Type of injury
        String specificBodyPart,             // Specific body part affected
        String severity,                      // Severity of the injury
        String causeOfInjury,                // Cause of the injury
        String firstAidMeasures,              // Initial first aid measures taken
        String followUpTreatmentType,         // Follow-up treatment type
        String monitoringInstructions,        // Monitoring instructions after treatment
        String scheduledProcedures,           // Scheduled procedures for follow-up
        String followUpMedications,           // Follow-up medications prescribed
        BigDecimal medicationCost,            // Cost of the medication
        int medicationHistoryId            // Foreign key reference to medication history
) {}
