package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers;

import com.example.hashinfarm.controller.dao.BreedDAO;
import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.controller.interfaces.CattleAdditionCallback;
import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.controller.utility.CattleUtils;
import com.example.hashinfarm.model.Breed;
import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.controller.records.CattleUIInfo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.example.hashinfarm.controller.dao.CattleDAO.getCattleByID;

public class AddNewCattleController {
    private static final Map<Integer, String> BCS_LABELS =
            Map.of(
                    1, "Emaciated",
                    2, "Very thin",
                    3, "Thin",
                    4, "Borderline",
                    5, "Moderate",
                    6, "Good",
                    7, "Very good",
                    8, "Fat",
                    9, "Very fat");
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField colorMakingTextField, tagIDTextField, nameTextField, weightTextField;
    @FXML
    private Button damTagButton, sireTagButton, cattleBreedButton;
    @FXML
    private DatePicker dateOfBirthDatePicker;
    @FXML
    private Slider BCSSlider;
    @FXML
    private Label ageLabel, BCSSliderLabel, damNameLabel, sireNameLabel;
    private Breed selectedCattleBreed;
    private Cattle selectedDam;
    private Cattle selectedSire;
    private int herdId;
    private EditHerdController editHerdController;
    private Cattle newCattle;
    private boolean isInitDataWithSelectedCattle;
    private CattleAdditionCallback cattleAdditionCallback;
    public void setCattleAdditionCallback(CattleAdditionCallback callback) {
        this.cattleAdditionCallback = callback;
    }

    public void initData(int herdId, EditHerdController editHerdController) {
        this.herdId = herdId;
        this.editHerdController = editHerdController;
        isInitDataWithSelectedCattle = false;
    }

    public void initData(int herdId, int selectedCattleId, LocalDate calvingDate) {
        this.herdId = herdId;
        try {
            this.selectedDam = getCattleByID(selectedCattleId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.dateOfBirthDatePicker.setValue(calvingDate);
        dateOfBirthDatePicker.setDayCellFactory(createDisabledCellFactory());
        this.updateAgeLabel(calvingDate);
        damTagButton.setText(selectedDam.getTagId());
        damNameLabel.setText(selectedDam.getName());
        damTagButton.setDisable(true);
        isInitDataWithSelectedCattle = true;
    }



    public void initialize() {
        initListeners();
        initBCSSlider();
    }

    public Callback<DatePicker, DateCell> createDisabledCellFactory() {
        return picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date == null || empty) {
                    setDisable(false);
                    setStyle("");
                } else {
                    disableCell(this);
                }
            }
        };
    }

    private void disableCell(DateCell cell) {
        cell.setDisable(true);
        cell.setStyle("-fx-background-color: #ffc0cb;");
    }


    private void initListeners() {
        dateOfBirthDatePicker
                .valueProperty()
                .addListener((observable, oldValue, newValue) -> updateAgeLabel(newValue));
        BCSSlider.valueProperty()
                .addListener((observable, oldValue, newValue) -> updateBCSSliderLabel(newValue.intValue()));
    }

    private void initBCSSlider() {
        BCSSlider.setMajorTickUnit(1);
        BCSSlider.setMinorTickCount(0);
        BCSSlider.setShowTickLabels(true);
        BCSSlider.setShowTickMarks(true);
        BCSSlider.setSnapToTicks(true);
        BCSSlider.setMin(1);
        BCSSlider.setMax(9);
    }

    private void updateBCSSliderLabel(int value) {
        String label = BCS_LABELS.getOrDefault(value, "");
        BCSSliderLabel.setText(label);
    }

    private void updateAgeLabel(LocalDate dateOfBirth) {
        if (dateOfBirth != null) {
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(dateOfBirth, currentDate);
            int totalMonths = period.getYears() * 12 + period.getMonths();
            ageLabel.setText(totalMonths + " months");
        } else {
            ageLabel.setText("");
        }
    }

    @FXML
    private void selectCattleBreed() {
        selectedCattleBreed = selectBreed();
        if (selectedCattleBreed != null) {
            cattleBreedButton.setText(selectedCattleBreed.getBreedName());
        }
    }

    public void handleCattleTagID(javafx.event.ActionEvent actionEvent) {
        Button buttonClicked = (Button) actionEvent.getSource();
        List<Cattle> cattleList;
        try {
            if (buttonClicked == damTagButton) {
                cattleList = CattleDAO.getCattleForGender("Cow");
            } else if (buttonClicked == sireTagButton) {
                cattleList = CattleDAO.getCattleForGender("Bull");
            } else {
                return;
            }

            // Call the showSelectionDialog method from CattleUtils
            showCattleSelectionDialog(buttonClicked, cattleList);
        } catch (SQLException e) {
            handleException(e);
        }
    }

    private <T> T showSelectionDialog(List<T> items, Function<T, String> displayMapper) {
        return CattleUtils.showSelectionDialog("Select Breed", "Select a breed from the list below", items, displayMapper);
    }

    private void showCattleSelectionDialog(Button buttonClicked, List<Cattle> cattleList) {
        CattleUtils.handleCattleTagID(
                buttonClicked,
                cattleList,
                damTagButton,
                sireTagButton,
                damNameLabel,
                sireNameLabel
        );
    }


    private Breed selectBreed() {
        try {
            List<Breed> breeds = BreedDAO.getAllBreeds();
            return showSelectionDialog(
                    breeds, Breed::getBreedName);
        } catch (SQLException e) {
            handleException(e);
            return null;
        }
    }

    @FXML
    private void addCattleToDatabase() {
        try {
            // Gather cattle information...
            gatherCattleInformationFromUI();

            // Ask for confirmation before adding cattle
            if (confirmCalfAddition()) {
                int newCattleId = addCattleToDB();

                // If cattle addition is successful, invoke the callback
                if (cattleAdditionCallback != null) {
                    cattleAdditionCallback.onCattleAddedSuccessfully(true, null, newCattleId);
                }

                if (!isInitDataWithSelectedCattle) {
                    refreshCattleTable();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle added successfully!");
                }


            } else {
                showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Cattle addition cancelled.");
            }
        } catch (SQLException e) {
            handleDatabaseError(e);
        } catch (NumberFormatException e) {
            handleInvalidWeightFormatError(e);
        }
    }

    private int addCattleToDB() throws SQLException {
        return CattleDAO.addCattle(newCattle);
    }


    private void handleDatabaseError(SQLException e) {
        // Use AppLogger for error handling
        AppLogger.error("Error adding cattle to database", e);
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add cattle. See logs for details.");
    }

    private void gatherCattleInformationFromUI() {
        CattleUIInfo uiInfo = CattleUtils.gatherCommonUIInfo(tagIDTextField, nameTextField, genderComboBox, colorMakingTextField, dateOfBirthDatePicker, weightTextField, BCSSliderLabel);
        int breedId = selectedCattleBreed != null ? selectedCattleBreed.getBreedId() : 0;
        int sireId = selectedSire != null ? selectedSire.getCattleId() : 0;
        int damId = selectedDam != null ? selectedDam.getCattleId() : 0;
        int damsHerd = selectedDam != null ? selectedDam.getHerdId() : 0;
        int siresHerd = selectedSire != null ? selectedSire.getHerdId() : 0;

        newCattle = CattleUtils.createCattle(uiInfo.tagId(), herdId, uiInfo.name(), uiInfo.gender(), uiInfo.colorMarkings(), uiInfo.dateOfBirth(), uiInfo.weightId(), uiInfo.bcs(), breedId, sireId, damId, damsHerd, siresHerd, 0);
    }


    private void refreshCattleTable() {
        editHerdController.handleRefreshCattleFromHerd();
    }

    private void handleException(Exception e) {
        // Use AppLogger for error handling with a more generic message
        AppLogger.error("Unexpected error occurred", e);
        // Update error message in showAlert
        showAlert(
                Alert.AlertType.ERROR, "Error", "An unexpected error occurred. See logs for details.");
    }


    private void handleInvalidWeightFormatError(NumberFormatException e) {
        // Use AppLogger for error handling
        AppLogger.error("Invalid weight format entered", e);
        // Update error message in showAlert
        showAlert(
                Alert.AlertType.ERROR, "Error", "Invalid weight format. Please enter a valid number.");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean confirmCalfAddition() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Cattle Addition");
        confirmationAlert.setHeaderText("Are you sure you want to add this new cattle?");
        confirmationAlert.setContentText("Click OK to confirm adding the cattle.");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        // Check if the Optional contains a value before calling get()
        return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
    }


}