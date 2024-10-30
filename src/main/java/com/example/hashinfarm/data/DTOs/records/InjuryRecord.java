package com.example.hashinfarm.data.DTOs.records;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InjuryRecord(
        int id,                               // Unique identifier for the injury report
        Integer cattleId,                     // Identifier for the cattle (nullable)
        LocalDate dateOfOccurrence,           // Date of the injury occurrence
        String typeOfInjury,                  // Type of injury (e.g., Contusion, Laceration)
        String specificBodyPart,              // Specific body part affected
        String severity,                      // Severity of the injury (e.g., Acute, Chronic)
        String causeOfInjury,                 // Description of how the injury occurred
        String firstAidMeasures,              // Initial first aid measures taken
        String followUpTreatmentType,         // Follow-up treatment type required
        String monitoringInstructions,        // Instructions for monitoring the injury
        String scheduledProcedures,           // Any scheduled procedures for follow-up
        String followUpMedications,           // Medications prescribed for follow-up
        BigDecimal medicationCost,            // Cost associated with the medications
        int medicationHistoryId               // Foreign key reference to medication history
) {}
