package com.example.hashinfarm.controller.records;

import java.time.LocalDate;

public record DewormingRecord(
        int id,
        LocalDate dewormingDate,
        String dewormerType,
        String dosage,
        String administeredBy,
        String routeOfAdministration,
        String weightAtTime,
        String contactDetails,
        String manufacturerDetails
) {
}
