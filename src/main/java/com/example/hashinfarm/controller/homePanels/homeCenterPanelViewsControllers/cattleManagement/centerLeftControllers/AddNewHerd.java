package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers;

import com.example.hashinfarm.controller.dao.HerdDAO;
import com.example.hashinfarm.model.Herd;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class AddNewHerd {


    @FXML
    private TextField herdNameTextField, locationTextField;

    @FXML
    private ComboBox<String> animalClassComboBox, breedTypeComboBox, breedSystemComboBox, solutionTypeComboBox, ageClassComboBox, feedBasisComboBox;

    @FXML
    private void initialize() {
        // Set focus to the name text field when the view is initialized
        herdNameTextField.requestFocus();
    }

    @FXML
    private void handleAddHerd() {
        Herd herd = getHerd();

        try {
            // Attempt to add herd to the database
            HerdDAO.addHerd(herd);
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Herd added successfully.");
            // Clear input fields
            clearFields();
            // Set focus to name text field
            herdNameTextField.requestFocus();

            // Refresh the table data
            Stage stage = (Stage) herdNameTextField.getScene().getWindow(); // Get the stage of the current scene
            HerdList herdListController = (HerdList) stage.getUserData(); // Get the controller instance
            herdListController.refreshTable(); // Call the refreshTable method
        } catch (SQLException e) {
            // Show error message
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add herd: " + e.getMessage());
        }
    }

    private @NotNull Herd getHerd() {
        String name = herdNameTextField.getText();
        String location = locationTextField.getText();
        String animalClass = animalClassComboBox.getValue();
        String breedType = breedTypeComboBox.getValue();
        String breedSystem = breedSystemComboBox.getValue();
        String solutionType = solutionTypeComboBox.getValue();
        String ageClass = ageClassComboBox.getValue();
        String feedBasis = feedBasisComboBox.getValue();

        // Perform validation if needed

        return new Herd(0, name, 0, animalClass, breedType, ageClass, breedSystem, solutionType, feedBasis, location, "");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        herdNameTextField.clear();
        locationTextField.clear();
        animalClassComboBox.getSelectionModel().clearSelection();
        breedTypeComboBox.getSelectionModel().clearSelection();
        breedSystemComboBox.getSelectionModel().clearSelection();
        solutionTypeComboBox.getSelectionModel().clearSelection();
        ageClassComboBox.getSelectionModel().clearSelection();
        feedBasisComboBox.getSelectionModel().clearSelection();
    }
}