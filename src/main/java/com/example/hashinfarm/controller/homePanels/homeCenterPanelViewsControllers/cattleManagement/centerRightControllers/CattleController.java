package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.utility.SelectedCattleManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CattleController {

    @FXML
    private Tab productivityAndCalvingTab;

    @FXML
    private TabPane mainTabPane;

    private SelectedCattleManager selectedCattleManager;

    @FXML
    private void initialize() {
        selectedCattleManager = SelectedCattleManager.getInstance();

        // Listen for changes in selected cattle properties
        selectedCattleManager.selectedGenderProperty().addListener((observable, oldValue, newValue) -> updateTabVisibility());
        selectedCattleManager.selectedDateOfBirthProperty().addListener((observable, oldValue, newValue) -> updateTabVisibility());

        // Initial update based on the currently selected cattle
        updateTabVisibility();
    }

    private void updateTabVisibility() {
        String gender = selectedCattleManager.getSelectedGender();
        LocalDate dateOfBirth = selectedCattleManager.getSelectedDateOfBirth();

        if (gender == null || dateOfBirth == null) {
            // No cattle selected or date of birth is not set, hide the tab
            mainTabPane.getTabs().remove(productivityAndCalvingTab);
            return;
        }

        long ageInMonths = ChronoUnit.MONTHS.between(dateOfBirth, LocalDate.now());
        boolean isValidAge = ageInMonths >= 14;

        // Update tab visibility and style based on gender and age
        updateTabState(gender, isValidAge, ageInMonths);
    }

    private void updateTabState(String gender, boolean isValidAge, long ageInMonths) {
        if ("Female".equals(gender) && isValidAge) {
            showTab();
        } else if (!isValidAge) {
            showInfoAndHideTab(ageInMonths);
        } else {
            // Other genders, hide the tab
            mainTabPane.getTabs().remove(productivityAndCalvingTab);
        }
    }

    private void showInfoAndHideTab(long ageInMonths) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Heifer Age Advisory");

        // Calculate remaining months
        Label headerLabel = getLabel(ageInMonths);

        alert.setHeaderText(null); // Clear default header text
        alert.getDialogPane().setContent(headerLabel); // Use custom header

        String contentText = "Expected breeding/calving age: 14 months.\n" +
                "**Note:** Productivity & Calving tab will be hidden.";
        alert.setContentText(contentText);

        // Display alert and hide tab upon confirmation
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                mainTabPane.getTabs().remove(productivityAndCalvingTab);
            }
        });
    }

    private static @NotNull Label getLabel(long ageInMonths) {
        long remainingMonths = 14 - ageInMonths;

        // Create Text for colored and bolded numbers
        Text ageText = new Text(ageInMonths + " months old");
        ageText.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TextFlow headerTextFlow = getTextFlow(remainingMonths, ageText);
        Label headerLabel = new Label();
        headerLabel.setGraphic(headerTextFlow);
        return headerLabel;
    }

    private static @NotNull TextFlow getTextFlow(long remainingMonths, Text ageText) {
        Text remainingText = new Text(remainingMonths + " more months");
        remainingText.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Set red color for urgency if remaining months are high
        if (remainingMonths > 6) {
            remainingText.setStyle(remainingText.getStyle() + "; -fx-fill: red;");
        }

        // Create a TextFlow to combine styled Text objects
        return new TextFlow(ageText, new Text(" Needs "), remainingText, new Text(" to reach breeding/calving age."));
    }

    private void showTab() {
        if (!mainTabPane.getTabs().contains(productivityAndCalvingTab)) {
            mainTabPane.getTabs().add(1,productivityAndCalvingTab); // Add the tab if not already present
        }
    }
}
