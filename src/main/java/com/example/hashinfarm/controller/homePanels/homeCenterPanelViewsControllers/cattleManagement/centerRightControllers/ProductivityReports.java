package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.controller.dao.OffspringDAO;
import com.example.hashinfarm.controller.dao.CalvingEventDAO;
import com.example.hashinfarm.controller.dao.BreedingAttemptDAO;
import com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.CattleController;
import com.example.hashinfarm.controller.utility.*;
import com.example.hashinfarm.model.BreedingAttempt;
import com.example.hashinfarm.model.CalvingEvent;
import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.model.Offspring;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
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
    private final double maxPosition = 0.7;
    @FXML private SplitPane splitPaneOffSpringInfo,splitPaneCalveEvents,splitPaneBreedAttempts;
    @FXML private Button leftArrowButtonOffSpringInfo, rightArrowButtonOffSpringInfo,leftArrowButtonCalveEvents, rightArrowButtonCalveEvents,leftArrowButtonBreedAttempts,rightArrowButtonBreedAttempts;

    @FXML private Label calvingEventIdLabel;
    @FXML private TextField cattleIdCalveEventsTextField,reproductiveVariableIdTextField,numberOfCalvesBornTextField,calvesBornAliveTextField,stillbirthsTextField,offspringIdTextField;
    @FXML private TextArea assistanceRequiredTextArea, physicalConditionCalfTextArea;
    @FXML private Button modifyCalvingEventDetailsButton,updateCalvingEventsDetailsButton;
    @FXML private TableView<CalvingEvent> calvingEventsTableView;
    @FXML private TableColumn<CalvingEvent, Integer> calvingEventIdColumn,cattleIdCalveEventsColumn,reproductiveVariableIdColumn;
    @FXML private TableColumn<CalvingEvent, Integer> numberOfCalvesBornColumn,calvesBornAliveColumn,stillbirthsColumn;

    @FXML private TableView<BreedingAttempt> breedingAttemptsTableView;
    @FXML private TableColumn<BreedingAttempt, Integer> breedingAttemptIdColumn;
    @FXML private TableColumn<BreedingAttempt, String> estrusDateColumn, breedingMethodBreedingAttemptColumn, sireUsedColumn, attemptDateColumn, attemptStatusColumn;
    @FXML private Label breedingAttemptIdLabel,sireNameLabel;
    @FXML private TextField attemptNumberTextField;
    @FXML private DatePicker estrusDatePicker, attemptDatePicker;
    @FXML private ComboBox<String> breedingMethodBreedingAttemptComboBox, attemptStatusComboBox;
    @FXML private TextArea notesTextArea;
    @FXML private Button modifyBreedingAttemptButton, updateBreedingAttemptButton,sireNameButton;


    @FXML private void initialize() {
        initializeButtons();
        initializeSplitPlane(splitPaneOffSpringInfo,leftArrowButtonOffSpringInfo,rightArrowButtonOffSpringInfo);
        initializeSplitPlane(splitPaneCalveEvents,leftArrowButtonCalveEvents,rightArrowButtonCalveEvents);
        initializeSplitPlane(splitPaneBreedAttempts,leftArrowButtonBreedAttempts,rightArrowButtonBreedAttempts);
        initSelectedCattleListeners();
        initializeTableColumns();
        setupOffspringTableSelectionListener();
        setupCalvingEventTableSelectionListener();
        setupBreedingAttemptsTableSelectionListener();
        addFieldChangeListenersForOffspring();
        addFieldChangeListenersForCalveEvent();
        addFieldChangeListenersForBreedingAttempt();

        breedingMethodBreedingAttemptComboBox.setItems(FXCollections.observableArrayList("Natural Mating", "Artificial Insemination", "Embryo Transfer"));
        attemptStatusComboBox.setItems(FXCollections.observableArrayList("Success", "Failure", "Unknown"));
    }
    private void initializeButtons(){
        modifyOffspringDetailsButton.setDisable(true);
        updateOffSpringDetailsButton.setDisable(true);
        modifyCalvingEventDetailsButton.setDisable(true);
        updateCalvingEventsDetailsButton.setDisable(true);
        modifyBreedingAttemptButton.setDisable(true);
        updateBreedingAttemptButton.setDisable(true);
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
                    loadBreedingAttemptsData();
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
                {stillbirthsColumn, "stillbirths"},

                //breeding Attempts
                {breedingAttemptIdColumn, "breedingAttemptId"},
                {estrusDateColumn, "estrusDate"},
                {breedingMethodBreedingAttemptColumn, "breedingMethod"},
                {sireUsedColumn, "sireId"},
                {attemptDateColumn, "attemptDate"},
                {attemptStatusColumn, "attemptStatus"}
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

            // Retrieve values from properties using getter methods
            initialValuesCalvingEvent.put("cattleId", String.valueOf(selectedCalvingEvent.cattleIdProperty().get()));
            initialValuesCalvingEvent.put("reproductiveVariableId", String.valueOf(selectedCalvingEvent.reproductiveVariableIdProperty().get()));
            initialValuesCalvingEvent.put("offspringIdTextField", String.valueOf(selectedCalvingEvent.offspringIdProperty().get()));
            initialValuesCalvingEvent.put("numberOfCalvesBorn", String.valueOf(selectedCalvingEvent.numberOfCalvesBornProperty().get()));
            initialValuesCalvingEvent.put("calvesBornAlive", String.valueOf(selectedCalvingEvent.calvesBornAliveProperty().get()));
            initialValuesCalvingEvent.put("stillbirths", String.valueOf(selectedCalvingEvent.stillbirthsProperty().get()));
            initialValuesCalvingEvent.put("assistanceRequired", selectedCalvingEvent.assistanceRequiredProperty().get());
            initialValuesCalvingEvent.put("physicalConditionCalf", selectedCalvingEvent.physicalConditionCalfProperty().get());

            // Update UI components
            calvingEventIdLabel.setText(String.valueOf(selectedCalvingEvent.calvingEventIdProperty().get()));
            cattleIdCalveEventsTextField.setText(String.valueOf(selectedCalvingEvent.cattleIdProperty().get()));
            reproductiveVariableIdTextField.setText(String.valueOf(selectedCalvingEvent.reproductiveVariableIdProperty().get()));
            offspringIdTextField.setText(String.valueOf(selectedCalvingEvent.offspringIdProperty().get()));
            numberOfCalvesBornTextField.setText(String.valueOf(selectedCalvingEvent.numberOfCalvesBornProperty().get()));
            calvesBornAliveTextField.setText(String.valueOf(selectedCalvingEvent.calvesBornAliveProperty().get()));
            stillbirthsTextField.setText(String.valueOf(selectedCalvingEvent.stillbirthsProperty().get()));
            assistanceRequiredTextArea.setText(selectedCalvingEvent.assistanceRequiredProperty().get());
            physicalConditionCalfTextArea.setText(selectedCalvingEvent.physicalConditionCalfProperty().get());

            updateCalvingEventsDetailsButton.setDisable(true);
        }
    }



    // Method to validate fields for updating Calving Events
    private boolean areFieldsValidForCalveEvent() {
        try {
            Integer.parseInt(numberOfCalvesBornTextField.getText());
            Integer.parseInt(calvesBornAliveTextField.getText());
            Integer.parseInt(stillbirthsTextField.getText());

            // Check if all fields are filled
            return  !numberOfCalvesBornTextField.getText().isEmpty() &&
                    !calvesBornAliveTextField.getText().isEmpty() &&
                    !stillbirthsTextField.getText().isEmpty() &&
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
        assistanceRequiredTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        physicalConditionCalfTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
    }

    // Method to check for changes in Calving Event fields
    private void checkForCalveEventDetailChanges() {
        boolean hasChanges =
                !numberOfCalvesBornTextField.getText().equals(initialValuesCalvingEvent.get("numberOfCalvesBorn")) ||
                        !calvesBornAliveTextField.getText().equals(initialValuesCalvingEvent.get("calvesBornAlive")) ||
                        !stillbirthsTextField.getText().equals(initialValuesCalvingEvent.get("stillbirths")) ||
                        !assistanceRequiredTextArea.getText().equals(initialValuesCalvingEvent.get("assistanceRequired")) ||
                        !physicalConditionCalfTextArea.getText().equals(initialValuesCalvingEvent.get("physicalConditionCalf"));

        updateCalvingEventsDetailsButton.setDisable(!hasChanges);
    }

    // Method to check if Calving Event fields have changed
    private boolean calvingEventFieldsHaveChanged() {
        CalvingEvent selectedCalvingEvent = calvingEventsTableView.getSelectionModel().getSelectedItem();
        if (selectedCalvingEvent == null) {
            return false;
        }

        return !numberOfCalvesBornTextField.getText().equals(String.valueOf(selectedCalvingEvent.numberOfCalvesBornProperty().get())) ||
                !calvesBornAliveTextField.getText().equals(String.valueOf(selectedCalvingEvent.calvesBornAliveProperty().get())) ||
                !stillbirthsTextField.getText().equals(String.valueOf(selectedCalvingEvent.stillbirthsProperty().get())) ||
                !assistanceRequiredTextArea.getText().equals(selectedCalvingEvent.assistanceRequiredProperty().get()) ||
                !physicalConditionCalfTextArea.getText().equals(selectedCalvingEvent.physicalConditionCalfProperty().get());
    }


    // Method to check if Calving Event fields are populated
    private boolean calvingEventFieldsArePopulated() {
        CalvingEvent selectedCalvingEvent = calvingEventsTableView.getSelectionModel().getSelectedItem();
        if (selectedCalvingEvent == null) {
            return false;
        }

        return cattleIdCalveEventsTextField.getText().equals(String.valueOf(selectedCalvingEvent.cattleIdProperty().get())) &&
                reproductiveVariableIdTextField.getText().equals(String.valueOf(selectedCalvingEvent.reproductiveVariableIdProperty().get())) &&
                numberOfCalvesBornTextField.getText().equals(String.valueOf(selectedCalvingEvent.numberOfCalvesBornProperty().get())) &&
                calvesBornAliveTextField.getText().equals(String.valueOf(selectedCalvingEvent.calvesBornAliveProperty().get())) &&
                stillbirthsTextField.getText().equals(String.valueOf(selectedCalvingEvent.stillbirthsProperty().get())) &&
                assistanceRequiredTextArea.getText().equals(selectedCalvingEvent.assistanceRequiredProperty().get()) &&
                physicalConditionCalfTextArea.getText().equals(selectedCalvingEvent.physicalConditionCalfProperty().get());
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

        if (!areFieldsValidForCalveEvent()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all fields are correctly filled.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Update Record", "Are you sure you want to update the calving event details?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                CalvingEvent updatedCalvingEvent = new CalvingEvent(
                        selectedCalvingEvent.calvingEventIdProperty().get(),
                        Integer.parseInt(cattleIdCalveEventsTextField.getText()),
                        Integer.parseInt(reproductiveVariableIdTextField.getText()),
                        Integer.parseInt(offspringIdTextField.getText()),
                        assistanceRequiredTextArea.getText(),
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
    }


    private void handleDeleteCalvingEvent() {
        Optional<ButtonType> result = showConfirmationAlert("Delete Calving Event", "Are you sure you want to delete this Calving Event?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CalvingEvent selectedCalvingEvent = calvingEventsTableView.getSelectionModel().getSelectedItem();
            if (selectedCalvingEvent != null) {
                try {
                    CalvingEventDAO.deleteCalvingEventById(Integer.parseInt(String.valueOf(selectedCalvingEvent.calvingEventIdProperty().get())));
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






    // Method to load Breeding Attempts data
    private void loadBreedingAttemptsData() throws SQLException {
        try {
            List<BreedingAttempt> breedingAttemptsList = BreedingAttemptDAO.getBreedingAttemptsByCattleId(selectedCattleId);
            ObservableList<BreedingAttempt> tableData = FXCollections.observableArrayList(breedingAttemptsList);

            breedingAttemptsTableView.setItems(tableData);
            if (!tableData.isEmpty()) {
                breedingAttemptsTableView.getSelectionModel().selectFirst();
                populateFieldsWithSelectedBreedingAttempt();
            } else {
                handleClearFieldsForBreedingAttempt();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading breeding attempts.");
        }
    }

    private void setupBreedingAttemptsTableSelectionListener() {
        breedingAttemptsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedBreedingAttempt();
            }
        });
    }

    // Method to populate fields with selected Breeding Attempt data
    private void populateFieldsWithSelectedBreedingAttempt() {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt != null) {
            modifyBreedingAttemptButton.setDisable(false);

            breedingAttemptIdLabel.setText(String.valueOf(selectedAttempt.getBreedingAttemptId()));
            estrusDatePicker.setValue(LocalDate.parse(selectedAttempt.getEstrusDate()));

            if (selectedAttempt.getSireId() != 0) {
                try {
                    Cattle cattle = CattleDAO.getCattleByID(selectedAttempt.getSireId());
                    sireNameLabel.setText(Objects.requireNonNull(cattle).getName());
                    sireNameButton.setText(Objects.requireNonNull(cattle).getTagId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } else {
                sireNameLabel.setText("N/A");
            }
            breedingMethodBreedingAttemptComboBox.setValue(selectedAttempt.getBreedingMethod());
            attemptNumberTextField.setText(computeAttempts());
            attemptDatePicker.setValue(LocalDate.parse(selectedAttempt.getAttemptDate()));
            attemptStatusComboBox.setValue(selectedAttempt.getAttemptStatus());
            notesTextArea.setText(selectedAttempt.getNotes());

            updateBreedingAttemptButton.setDisable(true);
        }
    }

    // Method to validate fields for updating Breeding Attempts
    private boolean areFieldsValidForBreedingAttempt() {
        try {

            return estrusDatePicker.getValue() != null &&
                    !sireNameLabel.getText().isEmpty() &&
                    breedingMethodBreedingAttemptComboBox.getValue() != null &&
                    attemptDatePicker.getValue() != null &&
                    attemptStatusComboBox.getValue() != null &&
                    !notesTextArea.getText().isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addFieldChangeListenersForBreedingAttempt() {
        estrusDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        sireNameLabel.textProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        breedingMethodBreedingAttemptComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        notesTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        attemptDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        attemptStatusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
    }

    private void checkForBreedingAttemptDetailChanges() {
        // Retrieve values from the UI elements, handling potential nulls
        String estrusDateValue = estrusDatePicker.getValue() != null ? estrusDatePicker.getValue().toString() : null;
        String sireUsedValue = sireNameLabel.getText() != null ? sireNameLabel.getText() : null;
        String breedingMethodValue = breedingMethodBreedingAttemptComboBox.getValue() != null ? breedingMethodBreedingAttemptComboBox.getValue() : null;
        String attemptDateValue = attemptDatePicker.getValue() != null ? attemptDatePicker.getValue().toString() : null;
        String attemptStatusValue = attemptStatusComboBox.getValue() != null ? attemptStatusComboBox.getValue() : null;
        String notesValue = notesTextArea.getText() != null ? notesTextArea.getText() : null;

        // Retrieve the selected breeding attempt values
        String selectedEstrusDate = getSelectedBreedingAttemptValue("estrusDate");
        String selectedSireId = getSelectedBreedingAttemptValue("sireId");
        String selectedBreedingMethod = getSelectedBreedingAttemptValue("breedingMethod");
        String selectedAttemptDate = getSelectedBreedingAttemptValue("attemptDate");
        String selectedAttemptStatus = getSelectedBreedingAttemptValue("attemptStatus");
        String selectedNotes = getSelectedBreedingAttemptValue("notes");

        // Check for changes
        boolean hasChanges =
                !Objects.equals(estrusDateValue, selectedEstrusDate) ||
                        !Objects.equals(sireUsedValue, selectedSireId) ||
                        !Objects.equals(breedingMethodValue, selectedBreedingMethod) ||
                        !Objects.equals(attemptDateValue, selectedAttemptDate) ||
                        !Objects.equals(attemptStatusValue, selectedAttemptStatus) ||
                        !Objects.equals(notesValue, selectedNotes);

        // Update the button state based on whether there are changes
        updateBreedingAttemptButton.setDisable(!hasChanges);
    }


    private String getSelectedBreedingAttemptValue(String propertyName) {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt == null) return "";


        return switch (propertyName) {
            case "estrusDate" -> selectedAttempt.getEstrusDate();
            case "sireId" ->  getSireName(selectedAttempt.getSireId());
            case "breedingMethod" -> selectedAttempt.getBreedingMethod();
            case "attemptDate" -> selectedAttempt.getAttemptDate();
            case "attemptStatus" -> selectedAttempt.getAttemptStatus();
            case "notes" -> selectedAttempt.getNotes();
            default -> "";
        };
    }

    private boolean breedingAttemptFieldsHaveChanged() {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt == null) return false;


        return  !estrusDatePicker.getValue().toString().equals(selectedAttempt.getEstrusDate()) ||
                !sireNameLabel.getText().equals( getSireName(selectedAttempt.getSireId())) ||
                !breedingMethodBreedingAttemptComboBox.getValue().equals(selectedAttempt.getBreedingMethod()) ||
                !attemptDatePicker.getValue().toString().equals(selectedAttempt.getAttemptDate()) ||
                !attemptStatusComboBox.getValue().equals(selectedAttempt.getAttemptStatus()) ||
                !notesTextArea.getText().equals(selectedAttempt.getNotes());
    }

    // Method to check if Breeding Attempt fields are populated
    private boolean breedingAttemptFieldsArePopulated() {
        return estrusDatePicker.getValue() != null &&
                !sireNameLabel.getText().isEmpty() &&
                breedingMethodBreedingAttemptComboBox.getValue() != null;
    }

    // Method to handle clearing fields for Breeding Attempt
    private void handleClearFieldsForBreedingAttempt() {
        breedingAttemptIdLabel.setText("N/A");
        estrusDatePicker.setValue(null);
        sireNameLabel.setText("N/A");
        breedingMethodBreedingAttemptComboBox.setValue(null);
        attemptNumberTextField.clear();
        attemptDatePicker.setValue(null);
        attemptStatusComboBox.setValue(null);
        notesTextArea.clear();
        sireNameButton.setText("Select Sire");
        modifyBreedingAttemptButton.setDisable(true);
        updateBreedingAttemptButton.setDisable(true);
    }

    @FXML
    private void modifyBreedingAttempt() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Modify Breeding Attempt");
        alert.setHeaderText(null);

        if (breedingAttemptFieldsArePopulated() && !breedingAttemptFieldsHaveChanged()) {
            showActionDialog(alert, "Delete Record", this::handleClearFieldsForBreedingAttempt, this::handleDeleteBreedingAttempt);
        } else if (breedingAttemptFieldsHaveChanged()) {
            showActionDialog(alert, "Reset Fields", this::handleClearFieldsForBreedingAttempt, this::populateFieldsWithSelectedBreedingAttempt);
        }
    }

    @FXML
    private void updateBreedingAttempt() {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();

        if (selectedAttempt == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a breeding attempt from the table.");
            return;
        }

        if (!areFieldsValidForBreedingAttempt()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all fields are correctly filled.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Update Record", "Are you sure you want to update the breeding attempt details?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int sireId;
            String sireText = sireNameLabel.getText();
            if (sireText == null || sireText.equals("N/A") || sireText.isEmpty()) {
                sireId = 0;
            }else {
                sireId = Integer.parseInt(sireText);
            }

            try {
                // Creating the updated BreedingAttempt object with the corrected parameters
                BreedingAttempt updatedAttempt = new BreedingAttempt(
                        selectedAttempt.getBreedingAttemptId(),
                        selectedAttempt.getCattleId(),
                        estrusDatePicker.getValue().toString(),
                        breedingMethodBreedingAttemptComboBox.getValue(),
                        sireId,
                        notesTextArea.getText(),
                        attemptDatePicker.getValue().toString(),
                        attemptStatusComboBox.getValue()
                );

                // Update the breeding attempt in the database
                BreedingAttemptDAO.updateBreedingAttempt(updatedAttempt);
                loadBreedingAttemptsData();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding attempt details updated successfully.");
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating breeding attempt details.");
            }
        }
    }


    private void handleDeleteBreedingAttempt() {
        Optional<ButtonType> result = showConfirmationAlert("Delete Breeding Attempt", "Are you sure you want to delete this breeding attempt?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
            if (selectedAttempt != null) {
                try {
                    BreedingAttemptDAO.deleteBreedingAttemptById(selectedAttempt.getBreedingAttemptId());
                    breedingAttemptsTableView.getItems().remove(selectedAttempt);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding attempt deleted successfully.");
                    loadBreedingAttemptsData();

                    if (!breedingAttemptsTableView.getItems().isEmpty()) {
                        breedingAttemptsTableView.getSelectionModel().selectFirst();
                        populateFieldsWithSelectedBreedingAttempt();
                    } else {
                        handleClearFieldsForBreedingAttempt();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting breeding attempt.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a breeding attempt from the table.");
            }
        }
    }

    private String computeAttempts(){
        return "N/A";
    }
    @FXML
    private void handleSireTagSelection() {
        List<Cattle> cattleList;
        try {

            cattleList = CattleDAO.getCattleForGender("Male");


            CattleUtils.handleCattleTagID(sireNameButton, cattleList, null, sireNameButton, null, sireNameLabel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private String getSireName(int sireId) {
        String sireName = "N/A";
        if (sireId != 0) {
            try {
                Cattle cattle = CattleDAO.getCattleByID(sireId);
                if (cattle != null) {
                    sireName = cattle.getName();
                }
            } catch (SQLException e) {
                // Handle the exception as per your application's requirements
                throw new RuntimeException(e);
            }
        }
        return sireName;
    }

}
