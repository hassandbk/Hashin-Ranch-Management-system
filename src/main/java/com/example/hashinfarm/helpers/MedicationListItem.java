package com.example.hashinfarm.helpers;


import com.example.hashinfarm.data.DTOs.records.MedicationRecord;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;

public class MedicationListItem extends HBox {
    private final RadioButton radioButton;
    private final MedicationRecord medicationRecord; // Updated field to use MedicationRecord

    public MedicationListItem(MedicationRecord medicationRecord) { // Updated constructor parameter
        this.medicationRecord = medicationRecord;
        this.radioButton = new RadioButton();
        this.getChildren().add(radioButton);
        this.getChildren().add(new Label(medicationRecord.category() + " - " + // Updated to use MedicationRecord methods
                medicationRecord.administeredBy() + " - " +
                medicationRecord.telNo()));
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public MedicationRecord getMedicationRecord() { // Updated method to return MedicationRecord
        return medicationRecord;
    }
}
