package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductivityAndCalving {
    @FXML
    private Label productionStageLabel;

    @FXML
    private TextField daysInLactationTextField;

    @FXML
    private Label volumeLabel1;

    @FXML
    private Label volumeLabel2;

    @FXML
    private Label volumeLabel3;


    public void updateProductionStageLabel(ActionEvent actionEvent) {
        try {
            int daysInLactation = Integer.parseInt(daysInLactationTextField.getText());

            if (daysInLactation >= 0 && daysInLactation <= 5) {
                productionStageLabel.setText("Colostrum Stage");
                updateVolumeLabels("Colostrum Volume");
            } else if (daysInLactation > 5 && daysInLactation <= 15) {
                productionStageLabel.setText("Transition Stage");
                updateVolumeLabels("Transitional Milk Volume");
            } else if (daysInLactation > 15 && daysInLactation <= 60) {
                productionStageLabel.setText("Peak Milk Harvesting");
                updateVolumeLabels("Peak Milk Production Volume");
            } else if (daysInLactation > 60 && daysInLactation <= 150) {
                productionStageLabel.setText("Mid-Lactation");
                updateVolumeLabels("Regular Milk Production Volume");
            } else if (daysInLactation > 150 && daysInLactation <= 305) {
                productionStageLabel.setText("Late Lactation");
                updateVolumeLabels("Decreasing Milk Harvesting Volume");
            } else {
                productionStageLabel.setText("Dry Period");
                updateVolumeLabels("Dry Period Milk Production Volume");
            }
        } catch (NumberFormatException e) {
            // Handle the case where the entered value is not a valid integer
            // Display an error message to the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid integer for Days in Current Lactation Stage.");
            alert.showAndWait();

            // You may choose to clear the TextField or perform other actions as needed
            daysInLactationTextField.clear();
        }
    }

    private void updateVolumeLabels(String volumeLabelText) {
        volumeLabel1.setText(volumeLabelText);
        volumeLabel2.setText(volumeLabelText);
        volumeLabel3.setText(volumeLabelText);
    }
}
