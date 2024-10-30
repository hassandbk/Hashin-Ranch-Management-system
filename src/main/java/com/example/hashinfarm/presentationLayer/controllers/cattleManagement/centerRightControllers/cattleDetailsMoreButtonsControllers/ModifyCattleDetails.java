package com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers.cattleDetailsMoreButtonsControllers;

import com.example.hashinfarm.data.DAOs.BreedDAO;
import com.example.hashinfarm.data.DAOs.CattleDAO;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.utils.CattleDialogUtils;
import com.example.hashinfarm.businessLogic.services.SelectedCattleManager;
import com.example.hashinfarm.data.DTOs.records.Breed;
import com.example.hashinfarm.data.DTOs.Cattle;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.example.hashinfarm.data.DTOs.records.CattleUIInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ModifyCattleDetails {
    private static final Map<Integer, String> BCS_VALUES = Map.of(
            1, "Emaciated",
            2, "Very thin",
            3, "Thin",
            4, "Borderline",
            5, "Moderate",
            6, "Good",
            7, "Very good",
            8, "Fat",
            9, "Very fat"
    );
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField cattleIDTextField, colorMarkingTextField, tagIDTextField, nameTextField, weightTextField;
    @FXML
    private Button damTagButton, sireTagButton, cattleBreedButton;
    @FXML
    private DatePicker dateOfBirthDatePicker;
    @FXML
    private Slider BCSSlider;
    @FXML
    private Label ageLabel, BCSSliderLabel, damNameLabel, sireNameLabel;
    private Breed selectedCattleBreed;
    private final Cattle selectedDam = new Cattle();
    private final Cattle selectedSire = new Cattle();

    private int age;

    public void initialize() {
        SelectedCattleManager cattleManager = SelectedCattleManager.getInstance();
        bindUIProperties(cattleManager);
        initBCSSlider();
        initBCSSliderListener();
        dateOfBirthDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            age = calculateAgeInMonths(newValue);
            ageLabel.setText(String.valueOf(age));
        });
    }

    private void initBCSSlider() {
        BCSSlider.setMajorTickUnit(1);
        BCSSlider.setMinorTickCount(0);
        BCSSlider.setShowTickLabels(true);
        BCSSlider.setShowTickMarks(true);
        BCSSlider.setSnapToTicks(true);
        BCSSlider.setMin(1);
        BCSSlider.setMax(9);
        BCSSlider.setValue(getBCSValue(BCSSliderLabel.getText()));
    }

    private void initBCSSliderListener() {
        BCSSlider.valueProperty().addListener((observable, oldValue, newValue) -> BCSSliderLabel.setText(BCS_VALUES.get(newValue.intValue())));
    }

    private int getBCSValue(String label) {
        return BCS_VALUES.entrySet().stream()
                .filter(entry -> entry.getValue().equals(label))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(1); // Default to "Emaciated" if not found
    }

    private void bindUIProperties(SelectedCattleManager cattleManager) {
        Cattle selectedCattle = cattleManager.getSelectedCattle(); // Get the Cattle object

        if (selectedCattle != null) {
            cattleIDTextField.setText(String.valueOf(selectedCattle.getCattleId()));
            nameTextField.setText(selectedCattle.getName());
            tagIDTextField.setText(selectedCattle.getTagId());
            dateOfBirthDatePicker.setValue(selectedCattle.getDateOfBirth());
            ageLabel.setText(String.valueOf(cattleManager.getComputedAge()));
            cattleBreedButton.setText(selectedCattle.getBreedName());
            genderComboBox.setValue(selectedCattle.getGender());
            colorMarkingTextField.setText(selectedCattle.getColorMarkings());
            weightTextField.setText(String.valueOf(selectedCattle.getWeightId()));
            BCSSliderLabel.setText(selectedCattle.getBcs());

            try {
                setCattleTag(damTagButton, selectedDam, selectedCattle.getDamId(), selectedCattle.getDamName(), damNameLabel);
                setCattleTag(sireTagButton, selectedSire, selectedCattle.getSireId(), selectedCattle.getSireName(), sireNameLabel);
            } catch (SQLException e) {
                handleException(e);
            }
        }
    }


    private void setCattleTag(Button button, Cattle selectedCattle, int selectedId, String selectedName, Label CattleNameLabel) throws SQLException {
        if (selectedId != 0) {
            String tagId = CattleDAO.getTagIdByCattleId(selectedId);
            button.setText(tagId);
            if (selectedCattle != null) {
                button.setText(selectedCattle.getTagId());
                selectedName = selectedCattle.getName();
            }
            CattleNameLabel.setText(selectedName);
        }
    }

    @FXML
    private void selectCattleBreed() {
        selectedCattleBreed = selectBreed();
        if (selectedCattleBreed != null) {
            cattleBreedButton.setText(selectedCattleBreed.breedName());
        }
    }

    private Breed selectBreed() {
        try {
            List<Breed> breeds = BreedDAO.getAllBreeds();
            return CattleDialogUtils.showSelectionDialog("Select Breed", "Select a breed from the list below", breeds, Breed::breedName);
        } catch (SQLException e) {
            handleException(e);
            return null;
        }
    }

    @FXML
    private void handleCattleTagID(ActionEvent actionEvent) {
        Button buttonClicked = (Button) actionEvent.getSource();
        List<Cattle> cattleList;
        try {
            if (buttonClicked == damTagButton) {
                cattleList = CattleDAO.getCattleForGender("Female");
            } else if (buttonClicked == sireTagButton) {
                cattleList = CattleDAO.getCattleForGender("Male");
            } else {
                return;
            }

            CattleDialogUtils.handleCattleTagID(buttonClicked, cattleList, damTagButton, sireTagButton, damNameLabel, sireNameLabel);
        } catch (SQLException e) {
            handleException(e);
        }
    }

    private int calculateAgeInMonths(LocalDate dateOfBirth) {
        if (dateOfBirth != null) {
            LocalDate currentDate = LocalDate.now();
            return (int) dateOfBirth.until(currentDate).toTotalMonths();
        }
        return 0;
    }

    private void handleException(Exception e) {
        // Log the exception with AppLogger (error level)
        AppLogger.error("Unhandled exception:", e);

        // Display an alert using CattleUtils method
        CattleDialogUtils.showAlert("Error", "An unexpected error occurred. Please try again later.");
    }

    private void showAlert(String title, String content) {
        CattleDialogUtils.showAlert(title, content);
    }

    @FXML
    private void modifyCattleInDB() {
        Cattle cattle = gatherCattleInformationFromUI();
        if (cattle != null) {
            CattleDAO.updateCattle(cattle);
            SelectedCattleManager.getInstance().setSelectedCattle(cattle);
            showAlert("Success", "Cattle details updated successfully.");
        } else {
            showAlert("Error", "Unable to update cattle details. Please ensure all fields are filled correctly.");
        }
    }

    private Cattle gatherCattleInformationFromUI() {
        SelectedCattleManager cattleManager = SelectedCattleManager.getInstance();

        try {
            // Parse the cattle ID from the text field, ensuring valid input
            int cattleId = Integer.parseInt(cattleIDTextField.getText().trim()); // Trim whitespace

            // Gather common UI info using CattleUtils
            CattleUIInfo uiInfo = CattleDialogUtils.gatherCommonUIInfo(
                    tagIDTextField,
                    nameTextField,
                    genderComboBox,
                    colorMarkingTextField,
                    dateOfBirthDatePicker,
                    weightTextField,
                    BCSSliderLabel
            );

            // Retrieve breed, sire, and dam IDs, defaulting to those in the SelectedCattleManager
            int breedId = (selectedCattleBreed != null) ? selectedCattleBreed.breedId() : cattleManager.getSelectedCattle() != null ? cattleManager.getSelectedCattle().getBreedId() : 0;
            int sireId = selectedSire.getCattleId();
            int damId = selectedDam.getCattleId();

            // Get the herds based on selected sire and dam, or from the selected cattle manager
            int damsHerd = selectedDam.getHerdId();
            int siresHerd = selectedSire.getHerdId();

            // Create and return a Cattle object using the gathered information
            return CattleDialogUtils.createCattle(
                    uiInfo.tagId(),
                    cattleManager.getSelectedCattle() != null ? cattleManager.getSelectedCattle().getHerdId() : 0,
                    uiInfo.name(),
                    uiInfo.gender(),
                    uiInfo.colorMarkings(),
                    uiInfo.dateOfBirth(),
                    uiInfo.weightId(),
                    uiInfo.bcs(),
                    breedId,
                    sireId,
                    damId,
                    damsHerd,
                    siresHerd,
                    cattleId
            );

        } catch (NumberFormatException e) {
            handleException(e);
            return null; // Return null if there's an error
        }
    }

}
