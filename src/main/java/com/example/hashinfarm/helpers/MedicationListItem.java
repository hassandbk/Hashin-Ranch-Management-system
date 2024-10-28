package com.example.hashinfarm.helpers;

import com.example.hashinfarm.data.DTOs.MedicationHistory;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;

public class MedicationListItem extends HBox {
    private final RadioButton radioButton;
    private final MedicationHistory medicationHistory;

    public MedicationListItem(MedicationHistory medicationHistory) {
        this.medicationHistory = medicationHistory;
        this.radioButton = new RadioButton();
        this.getChildren().add(radioButton);
        this.getChildren().add(new Label(medicationHistory.getCategory() + " - " +
                medicationHistory.getAdministeredBy() + " - " +
                medicationHistory.getTelNo()));
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public MedicationHistory getMedicationHistory() {
        return medicationHistory;
    }
}