package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.cattleDetailsMoreButtonsControllers;

import com.example.hashinfarm.controller.dao.BreedDAO;
import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.controller.utility.SelectedCattleManager;
import com.example.hashinfarm.model.Breed;
import com.example.hashinfarm.model.Cattle;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField cattleIDTextField, colorMarkingTextField, tagIDTextField, nameTextField, weightTextField;
    @FXML private Button damTagButton, sireTagButton, cattleBreedButton;
    @FXML private DatePicker dateOfBirthDatePicker;
    @FXML private Slider BCSSlider;
    @FXML private Label ageLabel, BCSSliderLabel, damNameLabel, sireNameLabel;
    private Breed selectedCattleBreed;
    private Cattle selectedDam;
    private Cattle selectedSire;
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
        cattleIDTextField.setText(String.valueOf(cattleManager.getSelectedCattleID()));
        nameTextField.setText(cattleManager.getSelectedName());
        tagIDTextField.setText(cattleManager.getSelectedTagId());
        dateOfBirthDatePicker.setValue(cattleManager.getSelectedDateOfBirth());
        ageLabel.setText(String.valueOf(cattleManager.getSelectedAge()));
        cattleBreedButton.setText(cattleManager.getSelectedBreedName());
        genderComboBox.setValue(cattleManager.getSelectedGender());
        colorMarkingTextField.setText(cattleManager.getSelectedColorMarkings());
        weightTextField.setText(String.valueOf(cattleManager.getSelectedWeightId()));
        BCSSliderLabel.setText(cattleManager.getSelectedBcs());

        try {
            setCattleTag(damTagButton, selectedDam, cattleManager.getSelectedDamId(), cattleManager.getSelectedDamName(),damNameLabel);
            setCattleTag(sireTagButton, selectedSire, cattleManager.getSelectedSireId(), cattleManager.getSelectedSireName(),sireNameLabel);
        } catch (SQLException e) {
            handleException(e);
        }
    }

    private void setCattleTag(Button button, Cattle selectedCattle, int selectedId, String selectedName,Label CattleNameLabel) throws SQLException {
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
    private void selectCattleBreed(ActionEvent event) {
        selectedCattleBreed = selectBreed();
        if (selectedCattleBreed != null) {
            cattleBreedButton.setText(selectedCattleBreed.getBreedName());
        }
    }

    private Breed selectBreed() {
        try {
            List<Breed> breeds = BreedDAO.getAllBreeds();
            return showSelectionDialog("Select Breed", "Select a breed from the list below", breeds, Breed::getBreedName);
        } catch (SQLException e) {
            handleException(e);
            return null;
        }
    }

    private <T> T showSelectionDialog(String title, String headerText, List<T> items, Function<T, String> displayMapper) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);
        ListView<T> listView = new ListView<>();
        listView.getItems().addAll(items);
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || displayMapper.apply(item) == null) {
                    setText(null);
                } else {
                    setText(displayMapper.apply(item));
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                dialog.setResult(listView.getSelectionModel().getSelectedItem());
                dialog.close();
            }
        });

        dialog.getDialogPane().setContent(listView);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });
        Optional<T> result = dialog.showAndWait();
        return result.orElse(null);
    }

    @FXML
    private void handleCattleTagID(ActionEvent actionEvent) {
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

            showCattleSelectionDialog(buttonClicked, cattleList);
        } catch (SQLException e) {
            handleException(e);
        }
    }

    private void showCattleSelectionDialog(Button buttonClicked, List<Cattle> cattleList) {
        Cattle selectedCattle = showSelectionDialog("Select Cattle", "Select a cattle from the list below", cattleList, Cattle::getTagId);
        if (selectedCattle != null) {
            if (buttonClicked == damTagButton) {
                selectedDam = selectedCattle;
                damTagButton.setText(selectedDam.getTagId());
                damNameLabel.setText(selectedDam.getName());
            } else if (buttonClicked == sireTagButton) {
                selectedSire = selectedCattle;
                sireTagButton.setText(selectedSire.getTagId());
                sireNameLabel.setText(selectedSire.getName());
            }
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

        // Optionally, display a user-friendly error message
        showAlert("Error", "An unexpected error occurred. Please try again later.");
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void modifyCattleInDB(ActionEvent event) {
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
            int cattleId = Integer.parseInt(cattleIDTextField.getText());
            String tagId = tagIDTextField.getText();
            String name = nameTextField.getText();
            String gender = genderComboBox.getValue();
            String colorMarkings = colorMarkingTextField.getText();
            LocalDate dateOfBirth = dateOfBirthDatePicker.getValue();
            int ageComputed = Integer.parseInt(ageLabel.getText());
            int weightId = Integer.parseInt(weightTextField.getText());
            String bcs = BCSSliderLabel.getText();

            int breedId = (selectedCattleBreed != null) ? selectedCattleBreed.getBreedId() : cattleManager.getSelectedBreedId();
            int sireId = (selectedSire != null) ? selectedSire.getCattleId() : cattleManager.getSelectedSireId();
            int damId = (selectedDam != null) ? selectedDam.getCattleId() : cattleManager.getSelectedDamId();
            int damsHerd = (selectedDam != null) ? selectedDam.getHerdId() : cattleManager.getSelectedDamsHerd();
            int siresHerd = (selectedSire != null) ? selectedSire.getHerdId() : cattleManager.getSelectedSiresHerd();


            return createModifiedCattle(tagId, name, gender, colorMarkings, dateOfBirth, ageComputed, weightId, bcs, breedId, sireId, damId, damsHerd, siresHerd,cattleId);
        } catch (NumberFormatException e) {
            handleException(e);
            return null;
        }
    }

    private Cattle createModifiedCattle( String tagId, String name, String gender, String colorMarkings, LocalDate dateOfBirth,int ageOfCattle, int weightId, String bcs, int breedId, int sireId, int damId, int damsHerd, int siresHerd,int cattleId) {
        SelectedCattleManager cattleManager = SelectedCattleManager.getInstance();
        int herdId = cattleManager.getSelectedHerdId();
        return new Cattle(cattleId, tagId, herdId, colorMarkings, name, gender, Date.valueOf(dateOfBirth), ageOfCattle,
                weightId, bcs, breedId, selectedCattleBreed != null ? selectedCattleBreed.getBreedName() : "",
                sireId, selectedSire != null ? selectedSire.getName() : "", damId,
                selectedDam != null ? selectedDam.getName() : "", damsHerd, siresHerd,
                selectedDam != null ? selectedDam.getDamHerdName() : "",
                selectedSire != null ? selectedSire.getSireHerdName() : "",
                selectedSire != null ? selectedSire.getSireBreedName() : "",
                selectedDam != null ? selectedDam.getDamBreedName() : "");
    }
}

