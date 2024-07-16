package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.controller.dao.OffspringDAO;
import com.example.hashinfarm.controller.utility.CattleUtils;
import com.example.hashinfarm.controller.utility.OffSpringTable;
import com.example.hashinfarm.controller.utility.SelectedCattleManager;
import com.example.hashinfarm.controller.utility.TableColumnUtils;
import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.model.Offspring;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductivityReports {



    // FXML elements from the UI
    @FXML private Label  selectedCattleIdLabel, offspringIdLabel,sireIdOrDamIdLabel;
    @FXML private TextField sireIdTextField, damIdTextField, cattleNameTextField, birthWeightTextField;
    @FXML private ComboBox<String> statusComboBox, breedingMethodComboBox, outcomeComboBox, offspringGenderComboBox, intendedUseComboBox;
    @FXML private Slider easeOfCalvingSlider;
    @FXML private TextField sireIdOrDamIdTextField, gestationLengthTextField, measuredWeightTextField, sireNameTextField, damNameTextField;
    @FXML private DatePicker lastDateWeightTakenDatePicker;
    @FXML private TableView<OffSpringTable> cattleTableView;
    @FXML private TableColumn<OffSpringTable, String> offspringIdColumn,cattleIdColumn, cattleNameColumn, genderColumn, breedingMethodColumn;
    @FXML private Button modifyOffspringDetailsButton;
    private int selectedCattleId;

    @FXML
    private void initialize() {
        initSelectedCattleListeners();
        initializeTableColumns();
        setupTableSelectionListener();
    }

    private void initSelectedCattleListeners() {
        SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();

        // Listener for selectedCattleID
        selectedCattleManager.selectedCattleIDProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.intValue() != 0) {
                selectedCattleId = newValue.intValue();
                try {
                    loadOffspringData();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                selectedCattleIdLabel.setText(String.valueOf(newValue.intValue()));
            }
        });

        // Listener for selectedSireId
        selectedCattleManager.selectedSireIdProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sireIdTextField.setText(String.valueOf(newValue.intValue()));
            } else {
                sireIdTextField.clear();
            }
        });

        // Listener for selectedDamId
        selectedCattleManager.selectedDamIdProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                damIdTextField.setText(String.valueOf(newValue.intValue()));
            } else {
                damIdTextField.clear();
            }
        });

        // Listener for selectedName
        selectedCattleManager.selectedNameProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cattleNameTextField.setText(newValue);
            } else {
                cattleNameTextField.clear();
            }
        });

        // Listener for selectedSireName
        selectedCattleManager.selectedSireNameProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sireNameTextField.setText(newValue);
            } else {
                sireNameTextField.clear();
            }
        });

        // Listener for selectedDamName
        selectedCattleManager.selectedDamNameProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                damNameTextField.setText(newValue);
            } else {
                damNameTextField.clear();
            }
        });
    }


    private void initializeTableColumns() {
        cattleIdColumn.setCellValueFactory(new PropertyValueFactory<>("cattleId"));
        cattleNameColumn.setCellValueFactory(new PropertyValueFactory<>("cattleName"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        breedingMethodColumn.setCellValueFactory(new PropertyValueFactory<>("breedingMethod"));
        offspringIdColumn.setCellValueFactory(new PropertyValueFactory<>("offspringId")); // Add this

        // Center align the cells
        TableColumnUtils.centerAlignColumn(cattleIdColumn);
        TableColumnUtils.centerAlignColumn(cattleNameColumn);
        TableColumnUtils.centerAlignColumn(genderColumn);
        TableColumnUtils.centerAlignColumn(breedingMethodColumn);
        TableColumnUtils.centerAlignColumn(offspringIdColumn);
    }

    private void loadOffspringData() throws SQLException {
        try {
            int selectedIndex = cattleTableView.getSelectionModel().getSelectedIndex();

            List<Cattle> cattleList = CattleDAO.getProgenyByCattleId(selectedCattleId);
            List<Offspring> offspringList = new ArrayList<>();

            for (Cattle cattle : cattleList) {
                if (OffspringDAO.hasOffspring(cattle.getCattleId())) {
                    Offspring offspring = OffspringDAO.getOffspringByCattleId(cattle.getCattleId());
                    if (offspring != null) {
                        offspringList.add(offspring);
                    }
                }
            }

            ObservableList<OffSpringTable> tableData = FXCollections.observableArrayList();
            for (Offspring offspring : offspringList) {
                OffSpringTable tableEntry = createOffSpringTableEntry(offspring);
                tableData.add(tableEntry);
            }

            cattleTableView.setItems(tableData);

            // Restore the previous selection if valid
            if (selectedIndex >= 0 && selectedIndex < tableData.size()) {
                cattleTableView.getSelectionModel().select(selectedIndex);
                populateFieldsWithSelectedOffspring();
            } else {
                // Handle the case where the selection is no longer valid
                handleClearFields();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception as needed
        }
    }

    private OffSpringTable createOffSpringTableEntry(Offspring offspring) {
        String cattleName;
        String gender;
        String breedingMethod;

        try {
            cattleName = getCattleNameById(Integer.parseInt(offspring.getCattleId()));
            gender = getGenderById(Integer.parseInt(offspring.getCattleId()));
            breedingMethod = getBreedingMethodById(Integer.parseInt(offspring.getCattleId()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new OffSpringTable(
                offspring.getOffspringId(),
                offspring.getCattleId(),
                cattleName,
                gender,
                breedingMethod,
                offspring.getBirthWeight(),
                offspring.getEaseOfCalving(),
                offspring.getGestationLength(),
                offspring.getMeasuredWeight(),
                offspring.getLastDateWeightTaken(),
                offspring.getIntendedUse(),
                offspring.getCattleId()
        );
    }


    private String getCattleNameById(int cattleId) throws SQLException {
        Cattle cattle = CattleDAO.getCattleByID(cattleId);
        return cattle != null ? cattle.getName() : "Unknown";
    }

    private String getGenderById(int cattleId) throws SQLException {
        Cattle cattle = CattleDAO.getCattleByID(cattleId);
        return cattle != null ? cattle.getGender() : "Unknown";
    }

    private String getBreedingMethodById(int cattleId) throws SQLException {
        Offspring offspring = OffspringDAO.getOffspringByCattleId(cattleId);
        return offspring != null ? offspring.getBreedingMethod() : "Unknown";
    }

    private void setupTableSelectionListener() {
        cattleTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedOffspring();
            }
        });
    }

    private void populateFieldsWithSelectedOffspring() {
        OffSpringTable selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        if (selectedOffspring != null) {
            // Populate the fields with selected offspring data
            offspringIdLabel.setText(String.valueOf(selectedOffspring.getOffspringId())); // Change to display offspringId
            birthWeightTextField.setText(String.valueOf(selectedOffspring.getBirthWeight()));
            easeOfCalvingSlider.setValue(selectedOffspring.getEaseOfCalving());
            sireIdOrDamIdTextField.setText(selectedOffspring.getSireId());
            gestationLengthTextField.setText(String.valueOf(selectedOffspring.getGestationLength()));
            measuredWeightTextField.setText(String.valueOf(selectedOffspring.getMeasuredWeight()));
            lastDateWeightTakenDatePicker.setValue(selectedOffspring.getLastDateWeightTaken());
            intendedUseComboBox.setValue(selectedOffspring.getIntendedUse());
            offspringGenderComboBox.setValue(selectedOffspring.getGender());
            breedingMethodComboBox.setValue(selectedOffspring.getBreedingMethod());

            // Update the label based on gender
            String gender = selectedOffspring.getGender();
            if ("Male".equalsIgnoreCase(gender)) {
                sireIdOrDamIdLabel.setText("Sire ID");
            } else if ("Female".equalsIgnoreCase(gender)) {
                sireIdOrDamIdLabel.setText("Dam ID");
            } else {
                sireIdOrDamIdLabel.setText("Unknown ID");  // Fallback text if gender is not recognized
            }
        }
    }


    @FXML
    private void updateOffSpringDetails() {
        // Get selected offspring from the table view
        OffSpringTable selectedOffspringTable = cattleTableView.getSelectionModel().getSelectedItem();

        if (selectedOffspringTable == null) {
            CattleUtils.showAlert("No Selection", "Please select an offspring from the table.");
            return;
        }

        try {
            // Validate input fields
            if (!areFieldsValid()) {
                CattleUtils.showAlert("Invalid Input", "Please ensure all fields are correctly filled.");
                return;
            }

            // Create an Offspring object with updated values
            Offspring updatedOffspring = new Offspring(
                    selectedOffspringTable.getOffspringId(), // Existing Offspring ID
                    Double.parseDouble(birthWeightTextField.getText()),
                    (int) easeOfCalvingSlider.getValue(),
                    Integer.parseInt(gestationLengthTextField.getText()),
                    Double.parseDouble(measuredWeightTextField.getText()),
                    lastDateWeightTakenDatePicker.getValue(),
                    intendedUseComboBox.getValue(),
                    selectedOffspringTable.getCattleId(), // Cattle ID should be the same as before
                    breedingMethodComboBox.getValue()
            );

            // Update the offspring in the database
            OffspringDAO.updateOffspring(updatedOffspring);

            // Refresh the table view
            loadOffspringData();

            // Show success message
            CattleUtils.showAlert("Success", "Offspring details updated successfully.");

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            CattleUtils.showAlert("Error", "An error occurred while updating the offspring details.");
        }
    }

    private boolean areFieldsValid() {
        try {
            // Check if all fields are filled and valid
            Double.parseDouble(birthWeightTextField.getText());
            Integer.parseInt(gestationLengthTextField.getText());
            Double.parseDouble(measuredWeightTextField.getText());

            // Additional validation can be added as needed
            return !birthWeightTextField.getText().isEmpty() &&
                    !gestationLengthTextField.getText().isEmpty() &&
                    !measuredWeightTextField.getText().isEmpty() &&
                    lastDateWeightTakenDatePicker.getValue() != null &&
                    intendedUseComboBox.getValue() != null &&
                    offspringGenderComboBox.getValue() != null &&
                    breedingMethodComboBox.getValue() != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @FXML
    private void modifyOffspringDetails() {
        OffSpringTable selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();

        if (selectedOffspring == null) {
            showAlert();
            modifyOffspringDetailsButton.setDisable(true); // Disable button if no selection
            return;
        } else {
            modifyOffspringDetailsButton.setDisable(false); // Enable button if a row is selected
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Modify Offspring Details");
        alert.setHeaderText(null);

        if (fieldsArePopulated() && !fieldsHaveChanged()) {
            showActionDialog(alert, "Delete Record");
        } else if (fieldsHaveChanged()) {
            showActionDialog(alert, "Reset Fields");
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText(null);
        alert.setContentText("Please select an offspring from the table.");
        alert.showAndWait();
    }

    private void showActionDialog(Alert alert, String button2Text) {
        alert.setContentText("Choose an action:");
        ButtonType button1 = new ButtonType("Clear");
        ButtonType button2 = new ButtonType(button2Text);
        ButtonType button3 = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE); // Specify cancel button

        alert.getButtonTypes().setAll(button1, button2, button3);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == button1) {
                handleClearFields();
            } else if (result.get() == button2) {
                if (button2Text.equals("Delete Record")) {
                    handleDeleteOffspring();
                } else if (button2Text.equals("Reset Fields")) {
                    populateFieldsWithSelectedOffspring();
                }
            }
            // No need to handle the Cancel button explicitly, showAndWait() will close the alert when Cancel is clicked
        }
    }


    private boolean fieldsHaveChanged() {
        OffSpringTable selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        return !birthWeightTextField.getText().equals(String.valueOf(selectedOffspring.getBirthWeight())) ||
                easeOfCalvingSlider.getValue() != selectedOffspring.getEaseOfCalving() ||
                !gestationLengthTextField.getText().equals(String.valueOf(selectedOffspring.getGestationLength())) ||
                !measuredWeightTextField.getText().equals(String.valueOf(selectedOffspring.getMeasuredWeight())) ||
                !lastDateWeightTakenDatePicker.getValue().equals(selectedOffspring.getLastDateWeightTaken()) ||
                !intendedUseComboBox.getValue().equals(selectedOffspring.getIntendedUse()) ||
                !offspringGenderComboBox.getValue().equals(selectedOffspring.getGender()) ||
                !breedingMethodComboBox.getValue().equals(selectedOffspring.getBreedingMethod());
    }

    private boolean fieldsArePopulated() {
        OffSpringTable selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        return birthWeightTextField.getText().equals(String.valueOf(selectedOffspring.getBirthWeight())) &&
                easeOfCalvingSlider.getValue() == selectedOffspring.getEaseOfCalving() &&
                gestationLengthTextField.getText().equals(String.valueOf(selectedOffspring.getGestationLength())) &&
                measuredWeightTextField.getText().equals(String.valueOf(selectedOffspring.getMeasuredWeight())) &&
                lastDateWeightTakenDatePicker.getValue().equals(selectedOffspring.getLastDateWeightTaken()) &&
                intendedUseComboBox.getValue().equals(selectedOffspring.getIntendedUse()) &&
                offspringGenderComboBox.getValue().equals(selectedOffspring.getGender()) &&
                breedingMethodComboBox.getValue().equals(selectedOffspring.getBreedingMethod());
    }

    private void handleClearFields() {
        birthWeightTextField.clear();
        easeOfCalvingSlider.setValue(0);
        gestationLengthTextField.clear();
        measuredWeightTextField.clear();
        lastDateWeightTakenDatePicker.setValue(null);
        intendedUseComboBox.setValue(null);
        offspringGenderComboBox.setValue(null);
        breedingMethodComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleDeleteOffspring() {
        Optional<ButtonType> result = showConfirmationAlert();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            OffSpringTable selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
            if (selectedOffspring != null) {
                try {
                    OffspringDAO.deleteOffspringById(Integer.parseInt(String.valueOf(selectedOffspring.getOffspringId())));
                    cattleTableView.getItems().remove(selectedOffspring);

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Offspring deleted successfully.");

                    loadOffspringData();

                    if (!cattleTableView.getItems().isEmpty()) {
                        cattleTableView.getSelectionModel().selectFirst();
                        populateFieldsWithSelectedOffspring();
                    } else {
                        handleClearFields();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the offspring.");
                }
            }
        }
    }

    private Optional<ButtonType> showConfirmationAlert() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Offspring");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete this offspring?");
        return confirmationAlert.showAndWait();
    }





}
