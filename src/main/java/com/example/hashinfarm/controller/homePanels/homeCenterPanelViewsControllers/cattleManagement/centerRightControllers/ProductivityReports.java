package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.controller.dao.OffspringDAO;
import com.example.hashinfarm.controller.dao.CalvingEventDAO;
import com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.CattleController;
import com.example.hashinfarm.controller.utility.*;
import com.example.hashinfarm.model.CalvingEvent;
import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.model.Offspring;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("CallToPrintStackTrace")
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
    @FXML private Button modifyOffspringDetailsButton,updateOffSpringDetailsButton;
    private int selectedCattleId;
    private final Map<String, String> initialValuesOffspring = new HashMap<>();
    private final Map<String, String> initialValuesCalvingEvent = new HashMap<>();
    private final double minPosition = 0.1;
    private final double maxPosition = 0.5;
    @FXML private SplitPane splitPaneOffSpringInfo,splitPaneCalveEvents,splitPaneBreedAttempts;
    @FXML private Button leftArrowButtonOffSpringInfo, rightArrowButtonOffSpringInfo,leftArrowButtonCalveEvents, rightArrowButtonCalveEvents,leftArrowButtonBreedAttempts,rightArrowButtonBreedAttempts;

    @FXML private Label calvingEventIdLabel;
    @FXML private TextField cattleIdCalveEventsTextField,reproductiveVariableIdTextField,numberOfCalvesBornTextField,calvesBornAliveTextField,stillbirthsTextField,offspringIdTextField;
    @FXML private TextArea assistanceRequiredTextArea;
    @FXML private TextField durationOfCalvingTextField;
    @FXML private TextArea physicalConditionCalfTextArea;
    @FXML private Button modifyCalvingEventDetailsButton,updateCalvingEventsDetailsButton;
    @FXML private TableView<CalvingEvent> calvingEventsTableView;
    @FXML private TableColumn<CalvingEvent, Integer> calvingEventIdColumn,cattleIdCalveEventsColumn,reproductiveVariableIdColumn;
    @FXML private TableColumn<CalvingEvent, Integer> numberOfCalvesBornColumn,calvesBornAliveColumn,stillbirthsColumn;

    @FXML private void initialize() {
        initializeButtons();
        initializeSplitPlane(splitPaneOffSpringInfo,leftArrowButtonOffSpringInfo,rightArrowButtonOffSpringInfo);
        initializeSplitPlane(splitPaneCalveEvents,leftArrowButtonCalveEvents,rightArrowButtonCalveEvents);
        initializeSplitPlane(splitPaneBreedAttempts,leftArrowButtonBreedAttempts,rightArrowButtonBreedAttempts);
        initSelectedCattleListeners();
        initializeTableColumns();
        setupOffspringTableSelectionListener();
        setupCalvingEventTableSelectionListener();
        addFieldChangeListenersForOffspring();
        addFieldChangeListenersForCalveEvent();
    }
    private void initializeButtons(){
        modifyOffspringDetailsButton.setDisable(true);
        updateOffSpringDetailsButton.setDisable(true);
        modifyCalvingEventDetailsButton.setDisable(true);
        updateCalvingEventsDetailsButton.setDisable(true);
    }
    private void initializeSplitPlane(SplitPane splitpane,Button leftArrowButton,Button rightArrowButton) {
        SplitPaneDividerEnforcer dividerEnforcer = new SplitPaneDividerEnforcer(minPosition, maxPosition);
        dividerEnforcer.enforceConstraints(splitpane);
        com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.CattleController cattleController = new CattleController();
        leftArrowButton.setOnAction(event -> cattleController.animateSplitPane(minPosition, splitpane,minPosition,maxPosition, leftArrowButton, rightArrowButton));
        rightArrowButton.setOnAction(event -> cattleController.animateSplitPane(maxPosition, splitpane,minPosition,maxPosition, leftArrowButton, rightArrowButton));
        splitpane.getDividers().getFirst().positionProperty().addListener((obs, oldPos, newPos) -> cattleController.updateButtonsPosition(newPos.doubleValue(), splitpane, leftArrowButton, rightArrowButton));

    }

    private void initSelectedCattleListeners() {
        SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();

        // Listener for selectedCattleID
        selectedCattleManager.selectedCattleIDProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.intValue() != 0) {
                selectedCattleId = newValue.intValue();
                try {
                    loadOffspringData();
                    loadCalvingEventsData();
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
        // Define all columns and their properties
        Object[][] columns = {
                //offspring table
                {cattleIdColumn, "cattleId"},
                {cattleNameColumn, "cattleName"},
                {genderColumn, "gender"},
                {breedingMethodColumn, "breedingMethod"},
                {offspringIdColumn, "offspringId"},

                //calving event table
                {calvingEventIdColumn, "calvingEventId"},
                {cattleIdCalveEventsColumn, "cattleId"},
                {reproductiveVariableIdColumn, "reproductiveVariableId"},
                {numberOfCalvesBornColumn, "numberOfCalvesBorn"},
                {calvesBornAliveColumn, "calvesBornAlive"},
                {stillbirthsColumn, "stillbirths"}
        };

        // Set cell value factories and center align columns
        for (Object[] column : columns) {
            setCellValueFactoryAndAlign((TableColumn<?, ?>) column[0], (String) column[1]);
        }
    }

    private <T> void setCellValueFactoryAndAlign(TableColumn<T, ?> column, String property) {
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        TableColumnUtils.centerAlignColumn(column);
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
                handleClearOffspringFields();
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

    private void setupOffspringTableSelectionListener() {
        cattleTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedOffspring();
            }
        });
    }
    private void populateFieldsWithSelectedOffspring() {
        OffSpringTable selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        if (selectedOffspring != null) {
            modifyOffspringDetailsButton.setDisable(false);

            // Store initial values with null checks
            initialValuesOffspring.put("birthWeight", selectedOffspring.getBirthWeight() != null ? String.valueOf(selectedOffspring.getBirthWeight()) : "");
            initialValuesOffspring.put("easeOfCalving", selectedOffspring.getEaseOfCalving() != null ? String.valueOf(selectedOffspring.getEaseOfCalving()) : "");
            initialValuesOffspring.put("gestationLength", selectedOffspring.getGestationLength() != null ? String.valueOf(selectedOffspring.getGestationLength()) : "");
            initialValuesOffspring.put("measuredWeight", selectedOffspring.getMeasuredWeight() != null ? String.valueOf(selectedOffspring.getMeasuredWeight()) : "");
            initialValuesOffspring.put("lastDateWeightTaken", selectedOffspring.getLastDateWeightTaken() != null ? selectedOffspring.getLastDateWeightTaken().toString() : "");
            initialValuesOffspring.put("intendedUse", selectedOffspring.getIntendedUse() != null ? selectedOffspring.getIntendedUse() : "");
            initialValuesOffspring.put("gender", selectedOffspring.getGender() != null ? selectedOffspring.getGender() : "");
            initialValuesOffspring.put("breedingMethod", selectedOffspring.getBreedingMethod() != null ? selectedOffspring.getBreedingMethod() : "");

            // Populate the fields with selected offspring data with null checks
            offspringIdLabel.setText(String.valueOf(selectedOffspring.getOffspringId()));
            birthWeightTextField.setText(selectedOffspring.getBirthWeight() != null ? String.valueOf(selectedOffspring.getBirthWeight()) : "");
            easeOfCalvingSlider.setValue(selectedOffspring.getEaseOfCalving() != null ? selectedOffspring.getEaseOfCalving() : 0);
            sireIdOrDamIdTextField.setText(selectedOffspring.getSireId() != null ? selectedOffspring.getSireId() : "");
            gestationLengthTextField.setText(selectedOffspring.getGestationLength() != null ? String.valueOf(selectedOffspring.getGestationLength()) : "");
            measuredWeightTextField.setText(selectedOffspring.getMeasuredWeight() != null ? String.valueOf(selectedOffspring.getMeasuredWeight()) : "");
            lastDateWeightTakenDatePicker.setValue(selectedOffspring.getLastDateWeightTaken()); // DatePicker can handle null
            intendedUseComboBox.setValue(selectedOffspring.getIntendedUse() != null ? selectedOffspring.getIntendedUse() : "");
            offspringGenderComboBox.setValue(selectedOffspring.getGender() != null ? selectedOffspring.getGender() : "");
            breedingMethodComboBox.setValue(selectedOffspring.getBreedingMethod() != null ? selectedOffspring.getBreedingMethod() : "");

            // Update the label based on gender with null checks
            String gender = selectedOffspring.getGender();
            if (gender != null) {
                if ("Male".equalsIgnoreCase(gender)) {
                    sireIdOrDamIdLabel.setText("Sire ID");
                } else if ("Female".equalsIgnoreCase(gender)) {
                    sireIdOrDamIdLabel.setText("Dam ID");
                } else {
                    sireIdOrDamIdLabel.setText("Unknown ID");  // Fallback text if gender is not recognized
                }
            } else {
                sireIdOrDamIdLabel.setText("Unknown ID");
            }

            // Disable the update button until changes are detected
            updateOffSpringDetailsButton.setDisable(true);
        }
    }

    private void checkForOffspringDetailChanges() {
        boolean hasChanges = !birthWeightTextField.getText().equals(initialValuesOffspring.get("birthWeight")) ||
                easeOfCalvingSlider.getValue() != Double.parseDouble(initialValuesOffspring.get("easeOfCalving")) ||
                !gestationLengthTextField.getText().equals(initialValuesOffspring.get("gestationLength")) ||
                !measuredWeightTextField.getText().equals(initialValuesOffspring.get("measuredWeight")) ||
                (lastDateWeightTakenDatePicker.getValue() != null && !lastDateWeightTakenDatePicker.getValue().toString().equals(initialValuesOffspring.get("lastDateWeightTaken"))) ||
                (intendedUseComboBox.getValue() != null && !intendedUseComboBox.getValue().equals(initialValuesOffspring.get("intendedUse"))) ||
                (offspringGenderComboBox.getValue() != null && !offspringGenderComboBox.getValue().equals(initialValuesOffspring.get("gender"))) ||
                (breedingMethodComboBox.getValue() != null && !breedingMethodComboBox.getValue().equals(initialValuesOffspring.get("breedingMethod")));

        updateOffSpringDetailsButton.setDisable(!hasChanges);
    }

    private boolean offspringFieldsHaveChanged() {
        OffSpringTable selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        return !birthWeightTextField.getText().equals(String.valueOf(selectedOffspring.getBirthWeight())) ||
                easeOfCalvingSlider.getValue() != selectedOffspring.getEaseOfCalving() ||
                !gestationLengthTextField.getText().equals(String.valueOf(selectedOffspring.getGestationLength())) ||
                !measuredWeightTextField.getText().equals(String.valueOf(selectedOffspring.getMeasuredWeight())) ||
                (lastDateWeightTakenDatePicker.getValue() != null && !lastDateWeightTakenDatePicker.getValue().equals(selectedOffspring.getLastDateWeightTaken())) ||
                (intendedUseComboBox.getValue() != null && !intendedUseComboBox.getValue().equals(selectedOffspring.getIntendedUse())) ||
                (offspringGenderComboBox.getValue() != null && !offspringGenderComboBox.getValue().equals(selectedOffspring.getGender())) ||
                (breedingMethodComboBox.getValue() != null && !breedingMethodComboBox.getValue().equals(selectedOffspring.getBreedingMethod()));
    }

    private boolean offspringFieldsArePopulated() {
        OffSpringTable selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        return birthWeightTextField.getText().equals(String.valueOf(selectedOffspring.getBirthWeight())) &&
                easeOfCalvingSlider.getValue() == selectedOffspring.getEaseOfCalving() &&
                gestationLengthTextField.getText().equals(String.valueOf(selectedOffspring.getGestationLength())) &&
                measuredWeightTextField.getText().equals(String.valueOf(selectedOffspring.getMeasuredWeight())) &&
                (lastDateWeightTakenDatePicker.getValue() != null && lastDateWeightTakenDatePicker.getValue().equals(selectedOffspring.getLastDateWeightTaken())) &&
                (intendedUseComboBox.getValue() != null && intendedUseComboBox.getValue().equals(selectedOffspring.getIntendedUse())) &&
                (offspringGenderComboBox.getValue() != null && offspringGenderComboBox.getValue().equals(selectedOffspring.getGender())) &&
                (breedingMethodComboBox.getValue() != null && breedingMethodComboBox.getValue().equals(selectedOffspring.getBreedingMethod()));
    }

    private void handleClearOffspringFields() {
        birthWeightTextField.clear();
        easeOfCalvingSlider.setValue(0);
        gestationLengthTextField.clear();
        measuredWeightTextField.clear();
        lastDateWeightTakenDatePicker.setValue(null);
        intendedUseComboBox.setValue(null);
        offspringGenderComboBox.setValue(null);
        breedingMethodComboBox.setValue(null);
        modifyOffspringDetailsButton.setDisable(true);
        updateOffSpringDetailsButton.setDisable(true);
    }


    private void addFieldChangeListenersForOffspring() {
        birthWeightTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        easeOfCalvingSlider.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        gestationLengthTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        measuredWeightTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        lastDateWeightTakenDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        intendedUseComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        offspringGenderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        breedingMethodComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
    }

    private boolean areFieldsValidForOffspring() {
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



    private void showActionDialog(Alert alert, String button2Text, Runnable onClear, Runnable onButton2) {
        alert.setContentText("Choose an action:");
        ButtonType button1 = new ButtonType("Clear");
        ButtonType button2 = new ButtonType(button2Text);
        ButtonType button3 = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(button1, button2, button3);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == button1) {
                onClear.run();
            } else if (result.get() == button2) {
                onButton2.run();
            }
        }
    }

    private Optional<ButtonType> showConfirmationAlert(String title, String content) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(title);
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(content);
        return confirmationAlert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    private void modifyOffspringDetails() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Modify Offspring Details");
        alert.setHeaderText(null);

        if (offspringFieldsArePopulated() && !offspringFieldsHaveChanged()) {
            showActionDialog(alert, "Delete Record", this::handleClearOffspringFields, this::handleDeleteOffspring);
        } else if (offspringFieldsHaveChanged()) {
            showActionDialog(alert, "Reset Fields", this::handleClearOffspringFields, this::populateFieldsWithSelectedOffspring);
        }
    }

    @FXML
    private void updateOffSpringDetails() {
        OffSpringTable selectedOffspringTable = cattleTableView.getSelectionModel().getSelectedItem();

        if (selectedOffspringTable == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an offspring from the table.");
            return;
        }

        try {
            if (!areFieldsValidForOffspring()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all fields are correctly filled.");
                return;
            }

            Offspring updatedOffspring = new Offspring(
                    selectedOffspringTable.getOffspringId(),
                    Double.parseDouble(birthWeightTextField.getText()),
                    (int) easeOfCalvingSlider.getValue(),
                    Integer.parseInt(gestationLengthTextField.getText()),
                    Double.parseDouble(measuredWeightTextField.getText()),
                    lastDateWeightTakenDatePicker.getValue(),
                    intendedUseComboBox.getValue(),
                    selectedOffspringTable.getCattleId(),
                    breedingMethodComboBox.getValue()
            );

            OffspringDAO.updateOffspring(updatedOffspring);

            loadOffspringData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Offspring details updated successfully.");

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the offspring details.");
        }
    }

    private void handleDeleteOffspring() {
        Optional<ButtonType> result = showConfirmationAlert("Delete Offspring", "Are you sure you want to delete this offspring?");
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
                        handleClearOffspringFields();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the offspring.");
                }
            }
        }
    }











    // Method to load Calving Events data
    private void loadCalvingEventsData() throws SQLException {
        try {
            List<CalvingEvent> calvingEventsList = CalvingEventDAO.getCalvingEventsBySireOrDam(selectedCattleId);
            ObservableList<CalvingEvent> tableData = FXCollections.observableArrayList(calvingEventsList);

            calvingEventsTableView.setItems(tableData);
            if (!tableData.isEmpty()) {
                calvingEventsTableView.getSelectionModel().selectFirst();
                populateFieldsWithSelectedCalvingEvent();
            } else {
                handleClearFieldsForCalveEvent();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading calving events.");
        }
    }
    private void setupCalvingEventTableSelectionListener() {
        calvingEventsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedCalvingEvent();
            }
        });
    }

    // Method to populate fields with selected Calving Event data
    private void populateFieldsWithSelectedCalvingEvent() {
        CalvingEvent selectedCalvingEvent = calvingEventsTableView.getSelectionModel().getSelectedItem();
        if (selectedCalvingEvent != null) {
            modifyCalvingEventDetailsButton.setDisable(false);

            initialValuesCalvingEvent.put("cattleId", String.valueOf(selectedCalvingEvent.getCattleId()));
            initialValuesCalvingEvent.put("reproductiveVariableId", String.valueOf(selectedCalvingEvent.getReproductiveVariableId()));
            initialValuesCalvingEvent.put("offspringIdTextField", String.valueOf(selectedCalvingEvent.getOffspringId()));
            initialValuesCalvingEvent.put("numberOfCalvesBorn", String.valueOf(selectedCalvingEvent.getNumberOfCalvesBorn()));
            initialValuesCalvingEvent.put("calvesBornAlive", String.valueOf(selectedCalvingEvent.getCalvesBornAlive()));
            initialValuesCalvingEvent.put("stillbirths", String.valueOf(selectedCalvingEvent.getStillbirths()));
            initialValuesCalvingEvent.put("durationOfCalving", String.valueOf(selectedCalvingEvent.getDurationOfCalving()));
            initialValuesCalvingEvent.put("assistanceRequired", selectedCalvingEvent.getAssistanceRequired());
            initialValuesCalvingEvent.put("physicalConditionCalf", selectedCalvingEvent.getPhysicalConditionCalf());

            calvingEventIdLabel.setText(String.valueOf(selectedCalvingEvent.getCalvingEventId()));
            cattleIdCalveEventsTextField.setText(String.valueOf(selectedCalvingEvent.getCattleId()));
            reproductiveVariableIdTextField.setText(String.valueOf(selectedCalvingEvent.getReproductiveVariableId()));
            offspringIdTextField.setText(String.valueOf(selectedCalvingEvent.getOffspringId()));
            numberOfCalvesBornTextField.setText(String.valueOf(selectedCalvingEvent.getNumberOfCalvesBorn()));
            calvesBornAliveTextField.setText(String.valueOf(selectedCalvingEvent.getCalvesBornAlive()));
            stillbirthsTextField.setText(String.valueOf(selectedCalvingEvent.getStillbirths()));
            durationOfCalvingTextField.setText(String.valueOf(selectedCalvingEvent.getDurationOfCalving()));
            assistanceRequiredTextArea.setText(selectedCalvingEvent.getAssistanceRequired());
            physicalConditionCalfTextArea.setText(selectedCalvingEvent.getPhysicalConditionCalf());

            updateCalvingEventsDetailsButton.setDisable(true);
        }
    }


    // Method to validate fields for updating Calving Events
    private boolean areFieldsValidForCalveEvent() {
        try {
            Integer.parseInt(numberOfCalvesBornTextField.getText());
            Integer.parseInt(calvesBornAliveTextField.getText());
            Integer.parseInt(stillbirthsTextField.getText());
            Double.parseDouble(durationOfCalvingTextField.getText());

            // Check if all fields are filled
            return  !numberOfCalvesBornTextField.getText().isEmpty() &&
                    !calvesBornAliveTextField.getText().isEmpty() &&
                    !stillbirthsTextField.getText().isEmpty() &&
                    !durationOfCalvingTextField.getText().isEmpty() &&
                    !assistanceRequiredTextArea.getText().isEmpty() &&
                    !physicalConditionCalfTextArea.getText().isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Method to add listeners for field changes
    private void addFieldChangeListenersForCalveEvent() {
        numberOfCalvesBornTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        calvesBornAliveTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        stillbirthsTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        durationOfCalvingTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        assistanceRequiredTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        physicalConditionCalfTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
    }

    // Method to check for changes in Calving Event fields
    private void checkForCalveEventDetailChanges() {
        boolean hasChanges =
                !numberOfCalvesBornTextField.getText().equals(initialValuesCalvingEvent.get("numberOfCalvesBorn")) ||
                        !calvesBornAliveTextField.getText().equals(initialValuesCalvingEvent.get("calvesBornAlive")) ||
                        !stillbirthsTextField.getText().equals(initialValuesCalvingEvent.get("stillbirths")) ||
                        !durationOfCalvingTextField.getText().equals(initialValuesCalvingEvent.get("durationOfCalving")) ||
                        !assistanceRequiredTextArea.getText().equals(initialValuesCalvingEvent.get("assistanceRequired")) ||
                        !physicalConditionCalfTextArea.getText().equals(initialValuesCalvingEvent.get("physicalConditionCalf"));

        updateCalvingEventsDetailsButton.setDisable(!hasChanges);
    }

    // Method to check if Calving Event fields have changed
    private boolean calvingEventFieldsHaveChanged() {
        CalvingEvent selectedCalvingEvent = calvingEventsTableView.getSelectionModel().getSelectedItem();
        return  !numberOfCalvesBornTextField.getText().equals(String.valueOf(selectedCalvingEvent.getNumberOfCalvesBorn())) ||
                !calvesBornAliveTextField.getText().equals(String.valueOf(selectedCalvingEvent.getCalvesBornAlive())) ||
                !stillbirthsTextField.getText().equals(String.valueOf(selectedCalvingEvent.getStillbirths())) ||
                !durationOfCalvingTextField.getText().equals(String.valueOf(selectedCalvingEvent.getDurationOfCalving())) ||
                !assistanceRequiredTextArea.getText().equals(selectedCalvingEvent.getAssistanceRequired()) ||
                !physicalConditionCalfTextArea.getText().equals(selectedCalvingEvent.getPhysicalConditionCalf());
    }

    // Method to check if Calving Event fields are populated
    private boolean calvingEventFieldsArePopulated() {
        CalvingEvent selectedCalvingEvent = calvingEventsTableView.getSelectionModel().getSelectedItem();
        return cattleIdCalveEventsTextField.getText().equals(String.valueOf(selectedCalvingEvent.getCattleId())) &&
                reproductiveVariableIdTextField.getText().equals(String.valueOf(selectedCalvingEvent.getReproductiveVariableId())) &&
                numberOfCalvesBornTextField.getText().equals(String.valueOf(selectedCalvingEvent.getNumberOfCalvesBorn())) &&
                calvesBornAliveTextField.getText().equals(String.valueOf(selectedCalvingEvent.getCalvesBornAlive())) &&
                stillbirthsTextField.getText().equals(String.valueOf(selectedCalvingEvent.getStillbirths())) &&
                durationOfCalvingTextField.getText().equals(String.valueOf(selectedCalvingEvent.getDurationOfCalving())) &&
                assistanceRequiredTextArea.getText().equals(selectedCalvingEvent.getAssistanceRequired()) &&
                physicalConditionCalfTextArea.getText().equals(selectedCalvingEvent.getPhysicalConditionCalf());
    }


    // Method to handle clearing fields for Calving Event
    private void handleClearFieldsForCalveEvent() {
        calvingEventIdLabel.setText("N/A");  // Resetting to default text
        cattleIdCalveEventsTextField.clear();
        reproductiveVariableIdTextField.clear();
        offspringIdTextField.clear();
        numberOfCalvesBornTextField.clear();
        calvesBornAliveTextField.clear();
        stillbirthsTextField.clear();
        durationOfCalvingTextField.clear();
        assistanceRequiredTextArea.clear();
        physicalConditionCalfTextArea.clear();

        initialValuesCalvingEvent.clear();
        modifyCalvingEventDetailsButton.setDisable(true);
        updateCalvingEventsDetailsButton.setDisable(true);
    }

    @FXML
    private void modifyCalvingEventDetails() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Modify Calving Event Details");
        alert.setHeaderText(null);

        if (calvingEventFieldsArePopulated() && !calvingEventFieldsHaveChanged()) {
            showActionDialog(alert, "Delete Record", this::handleClearFieldsForCalveEvent, this::handleDeleteCalvingEvent);
        } else if (calvingEventFieldsHaveChanged()) {
            showActionDialog(alert, "Reset Fields", this::handleClearFieldsForCalveEvent, this::populateFieldsWithSelectedCalvingEvent);
        }
    }

    @FXML
    private void updateCalvingEventsDetails() {
        CalvingEvent selectedCalvingEvent = calvingEventsTableView.getSelectionModel().getSelectedItem();

        if (selectedCalvingEvent == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a calving event from the table.");
            return;
        }

        try {
            if (!areFieldsValidForCalveEvent()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all fields are correctly filled.");
                return;
            }

            CalvingEvent updatedCalvingEvent = new CalvingEvent(
                    selectedCalvingEvent.getCalvingEventId(),
                    Integer.parseInt(cattleIdCalveEventsTextField.getText()),
                    Integer.parseInt(reproductiveVariableIdTextField.getText()),
                    Integer.parseInt(offspringIdTextField.getText()),
                    assistanceRequiredTextArea.getText(),
                    parseIntegerOrNull(durationOfCalvingTextField.getText()),
                    physicalConditionCalfTextArea.getText(),
                    Integer.parseInt(numberOfCalvesBornTextField.getText()),
                    Integer.parseInt(calvesBornAliveTextField.getText()),
                    parseIntegerOrNull(stillbirthsTextField.getText())
            );

            CalvingEventDAO.updateCalvingEvent(updatedCalvingEvent);

            loadCalvingEventsData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Calving event details updated successfully.");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating calving event details.");
        }
    }

    private void handleDeleteCalvingEvent() {
        Optional<ButtonType> result = showConfirmationAlert("Delete Calving Event", "Are you sure you want to delete this Calving Event?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CalvingEvent selectedCalvingEvent = calvingEventsTableView.getSelectionModel().getSelectedItem();
            if (selectedCalvingEvent != null) {
                try {
                    CalvingEventDAO.deleteCalvingEventById(Integer.parseInt(String.valueOf(selectedCalvingEvent.getCalvingEventId())));
                    calvingEventsTableView.getItems().remove(selectedCalvingEvent);

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Calving Event deleted successfully.");

                    loadCalvingEventsData();

                    if (!calvingEventsTableView.getItems().isEmpty()) {
                        calvingEventsTableView.getSelectionModel().selectFirst();
                        populateFieldsWithSelectedCalvingEvent();
                    } else {
                        populateFieldsWithSelectedCalvingEvent();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the Calving Event.");
                }
            }
        }
    }
    private Integer parseIntegerOrNull(String text) {
        try {
            return (text == null || text.trim().isEmpty()) ? null : Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null; // Or you can handle the exception as needed
        }
    }





}
