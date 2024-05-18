package com.example.hashinfarm.controller.utility;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DateUtil {
    public static void datePickerFormat(DatePicker datePicker) {
        if (datePicker != null) {
            datePicker.setConverter(new StringConverter<>() {
                final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                @Override
                public String toString(LocalDate date) {
                    if (date != null) {
                        return dateFormatter.format(date);
                    } else {
                        return "";
                    }
                }

                @Override
                public LocalDate fromString(String string) {
                    if (string != null && !string.isEmpty()) {
                        return LocalDate.parse(string, dateFormatter);
                    } else {
                        return null;
                    }
                }
            });
        }
    }

    public static Optional<String> validateCalvingDate(LocalDate calvingDate, LocalDate lastBreedingDate, int minGestationDays, int maxGestationDays) {
        if (lastBreedingDate == null) {
            return Optional.of("Missing last breeding date.");
        }

        LocalDate minCalvingDate = lastBreedingDate.plusDays(minGestationDays);
        LocalDate maxCalvingDate = lastBreedingDate.plusDays(maxGestationDays);
        LocalDate today = LocalDate.now();

        if (calvingDate.isBefore(minCalvingDate)) {
            return Optional.of("Calving date is before the minimum allowed date. Valid range is between " + minCalvingDate + " and " + maxCalvingDate + ".");
        } else if (calvingDate.isAfter(today)) {
            return Optional.of("Calving date is in the future. Current date is " + today + ".");
        } else {
            return Optional.empty(); // Valid
        }
    }

    public static String calculateDays(LocalDate KnownDate) {

        if (KnownDate == null) {
            throw new IllegalArgumentException("Input date cannot be null");
        }

        LocalDate today = LocalDate.now();
        Period period = Period.between(KnownDate, today);

        // Handle case where all calculations result in 0
        if (period.getYears() == 0 && period.getMonths() == 0 && period.getDays() == 0) {
            return "Today";
        }

        List<String> parts = new ArrayList<>();
        if (period.getYears() > 0) {
            parts.add(period.getYears() + " year" + (period.getYears() > 1 ? "s" : ""));
        }
        if (period.getMonths() > 0) {
            parts.add(period.getMonths() + " month" + (period.getMonths() > 1 ? "s" : ""));
        }
        if (period.getDays() > 0) {
            parts.add(period.getDays() + " day" + (period.getDays() > 1 ? "s" : ""));
        }

        return String.join(" and ", parts);
    }

    public static String calculateAge(LocalDate dateOfBirth, LocalDate calvingDate) {
        if (dateOfBirth == null || calvingDate == null) {
            throw new IllegalArgumentException("Both dateOfBirth and calvingDate cannot be null");
        }

        if (calvingDate.isBefore(dateOfBirth)) {
            throw new IllegalArgumentException("Calving date cannot be before date of birth");
        }

        Period period = Period.between(dateOfBirth, calvingDate);

        StringBuilder sb = new StringBuilder();
        if (period.getYears() > 0) {
            sb.append(period.getYears()).append(" year").append(period.getYears() > 1 ? "s" : "");
        }
        if (period.getMonths() > 0) {
            if (!sb.isEmpty()) {
                sb.append(" and ");
            }
            sb.append(period.getMonths()).append(" month").append(period.getMonths() > 1 ? "s" : "");
        }
        if (period.getDays() > 0) {
            if (!sb.isEmpty()) {
                sb.append(" and ");
            }
            sb.append(period.getDays()).append(" day").append(period.getDays() > 1 ? "s" : "");
        }

        return sb.toString().isEmpty() ? "0 days" : sb.toString();
    }
}
