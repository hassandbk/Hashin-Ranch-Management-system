package com.example.hashinfarm.helpers;

import com.example.hashinfarm.data.DTOs.records.LactationPeriodWithSelection;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class RadioButtonTableCell extends TableCell<LactationPeriodWithSelection, Boolean> {
    private final RadioButton radioButton = new RadioButton();

    public RadioButtonTableCell() {
        // Add a listener to handle radio button selection
        radioButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                // Clear other selections in the table
                getTableView().getItems().forEach(item -> {
                    if (item != getTableView().getItems().get(getIndex())) {
                        item.setSelected(false); // Deselect other items
                    }
                });
                // Select this row in the table
                getTableView().getSelectionModel().select(getIndex());
            }
        });

        // Create an HBox to center the radio button
        HBox hBox = new HBox(radioButton);
        hBox.setAlignment(javafx.geometry.Pos.CENTER);
        HBox.setHgrow(radioButton, Priority.ALWAYS);
        setGraphic(hBox);
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            radioButton.setSelected(item);
            setGraphic(radioButton);
        }
    }
}
