package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers;

import com.example.hashinfarm.controller.dao.BreedDAO;
import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.model.Breed;
import com.example.hashinfarm.model.Cattle;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AddNewCattleController {
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
    private int age;

    private EditHerdController editHerdController;
    private Cattle newCattle;

    public void initData(int herdId, EditHerdController editHerdController) {
        this.herdId = herdId;
        this.editHerdController = editHerdController;
    }

    public void initialize() {
        initListeners();
        initBCSSlider();
    }

    private void initListeners() {
        dateOfBirthDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> updateAgeLabel(newValue));
        BCSSlider.valueProperty().addListener((observable, oldValue, newValue) -> updateBCSSliderLabel(newValue.intValue()));
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

    private static final Map<Integer, String> BCS_LABELS = Map.of(
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

    private void updateBCSSliderLabel(int value) {
        String label = BCS_LABELS.getOrDefault(value, "");
        BCSSliderLabel.setText(label);
    }

    private void updateAgeLabel(LocalDate dateOfBirth) {
        if (dateOfBirth != null) {
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(dateOfBirth, currentDate);
            int totalMonths = period.getYears() * 12 + period.getMonths();
            age = totalMonths;
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

            showCattleSelectionDialog(buttonClicked, cattleList);
        } catch (SQLException e) {
            handleException(e);
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

        // Enable double-click selection
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

    private Breed selectBreed() {
        try {
            List<Breed> breeds = BreedDAO.getAllBreeds();
            return showSelectionDialog("Select Breed", "Select a breed from the list below", breeds, Breed::getBreedName);
        } catch (SQLException e) {
            handleException(e);
            return null;
        }
    }


    @FXML
    private void addCattleToDatabase() {
        try {
            gatherCattleInformationFromUI();
            addCattleToDB();
            refreshCattleTable();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle added successfully!");
        } catch (SQLException e) {
            handleDatabaseError(e);
        } catch (NumberFormatException e) {
            handleInvalidWeightFormatError(e);
        }
    }

    private void gatherCattleInformationFromUI() {
        String tagId = tagIDTextField.getText();
        String name = nameTextField.getText();
        String gender = genderComboBox.getValue();
        String colorMarkings = colorMakingTextField.getText();
        LocalDate dateOfBirth = dateOfBirthDatePicker.getValue();
        int weightId = Integer.parseInt(weightTextField.getText());
        String bcs = BCSSliderLabel.getText();
        int breedId = selectedCattleBreed != null ? selectedCattleBreed.getBreedId() : 0;
        int sireId = selectedSire != null ? selectedSire.getCattleId() : 0;
        int damId = selectedDam != null ? selectedDam.getCattleId() : 0;
        int damsHerd = selectedDam != null ? selectedDam.getHerdId() : 0;
        int siresHerd = selectedSire != null ? selectedSire.getHerdId() : 0;

        age = calculateAgeInMonths(dateOfBirth);

        newCattle = createNewCattle(tagId, name, gender, colorMarkings, dateOfBirth, weightId, bcs, breedId, sireId, damId, damsHerd, siresHerd);
    }

    private int calculateAgeInMonths(LocalDate dateOfBirth) {
        if (dateOfBirth != null) {
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(dateOfBirth, currentDate);
            return period.getYears() * 12 + period.getMonths();
        }
        return 0;
    }

    private Cattle createNewCattle(String tagId, String name, String gender, String colorMarkings, LocalDate dateOfBirth, int weightId, String bcs, int breedId, int sireId, int damId, int damsHerd, int siresHerd) {
        return new Cattle(0, tagId, herdId, colorMarkings, name, gender, Date.valueOf(dateOfBirth), age,
                weightId, bcs, breedId, selectedCattleBreed != null ? selectedCattleBreed.getBreedName() : "",
                sireId, selectedSire != null ? selectedSire.getName() : "", damId,
                selectedDam != null ? selectedDam.getName() : "", damsHerd, siresHerd,
                selectedDam != null ? selectedDam.getDamHerdName() : "",
                selectedSire != null ? selectedSire.getSireHerdName() : "",
                selectedSire != null ? selectedSire.getSireBreedName() : "",
                selectedDam != null ? selectedDam.getDamBreedName() : "");
    }

    private void addCattleToDB() throws SQLException {
        CattleDAO.addCattle(newCattle);
    }

    private void refreshCattleTable() {
        editHerdController.handleRefreshCattleFromHerd();
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
    }

    private void handleDatabaseError(SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add cattle to database!");
    }

    private void handleInvalidWeightFormatError(NumberFormatException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Invalid weight format!");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
