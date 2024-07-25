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
import javafx.beans.value.ObservableValue;
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


    // FXML elements for Cattle Information
    @FXML private Label selectedCattleIdLabel, offspringIdLabel, sireIdOrDamIdLabel;
    @FXML private TextField sireIdTextField, damIdTextField, cattleNameTextField, birthWeightTextField;
    @FXML private ComboBox<String> statusComboBox, breedingMethodComboBox, outcomeComboBox, offspringGenderComboBox, intendedUseComboBox;
    @FXML private Slider easeOfCalvingSlider;
    @FXML private TextField sireIdOrDamIdTextField, gestationLengthTextField, measuredWeightTextField, sireNameTextField, damNameTextField;
    @FXML private DatePicker lastDateWeightTakenDatePicker;
    @FXML private TableView<OffSpringTable> cattleTableView;
    @FXML private TableColumn<OffSpringTable, String> offspringIdColumn, cattleIdColumn, cattleNameColumn, genderColumn, breedingMethodColumn;
    @FXML private Button modifyOffspringDetailsButton, updateOffSpringDetailsButton;

    // FXML elements for Calving Events
    @FXML private Label calvingEventIdLabel;
    @FXML private TextField cattleIdCalveEventsTextField, reproductiveVariableIdTextField, numberOfCalvesBornTextField, calvesBornAliveTextField, stillbirthsTextField, offspringIdTextField;
    @FXML private TextArea assistanceRequiredTextArea, physicalConditionCalfTextArea;
    @FXML private Button modifyCalvingEventDetailsButton, updateCalvingEventsDetailsButton;
    @FXML private TableView<CalvingEvent> calvingEventsTableView;
    @FXML private TableColumn<CalvingEvent, Integer> calvingEventIdColumn, cattleIdCalveEventsColumn, reproductiveVariableIdColumn;
    @FXML private TableColumn<CalvingEvent, Integer> numberOfCalvesBornColumn, calvesBornAliveColumn, stillbirthsColumn;

    // FXML elements for Breeding Attempts
    @FXML private TableView<BreedingAttempt> breedingAttemptsTableView;
    @FXML private TableColumn<BreedingAttempt, Integer> breedingAttemptIdColumn;
    @FXML private TableColumn<BreedingAttempt, String> estrusDateColumn, breedingMethodBreedingAttemptColumn, sireUsedColumn, attemptDateColumn, attemptStatusColumn;
    @FXML private Label breedingAttemptIdLabel, sireNameLabel;
    @FXML private TextField attemptNumberTextField;
    @FXML private DatePicker estrusDatePicker, attemptDatePicker;
    @FXML private ComboBox<String> breedingMethodBreedingAttemptComboBox, attemptStatusComboBox;
    @FXML private TextArea notesTextArea;
    @FXML private Button modifyBreedingAttemptButton, updateBreedingAttemptButton, sireNameButton;

    // SplitPane and Navigation Buttons
    @FXML private SplitPane splitPaneOffSpringInfo, splitPaneCalveEvents, splitPaneBreedAttempts;
    @FXML private Button leftArrowButtonOffSpringInfo, rightArrowButtonOffSpringInfo;
    @FXML private Button leftArrowButtonCalveEvents, rightArrowButtonCalveEvents;
    @FXML private Button leftArrowButtonBreedAttempts, rightArrowButtonBreedAttempts;

    // Other Fields
    private int selectedCattleId;
    private final Map<String, String> initialValuesOffspring = new HashMap<>();
    private final Map<String, String> initialValuesCalvingEvent = new HashMap<>();
    private final double minPosition = 0.1;
    private final double maxPosition = 0.7;

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

        addTextFieldListener(selectedCattleManager.selectedSireIdProperty(), sireIdTextField);
        addTextFieldListener(selectedCattleManager.selectedDamIdProperty(), damIdTextField);
        addTextFieldListener(selectedCattleManager.selectedNameProperty(), cattleNameTextField);
        addTextFieldListener(selectedCattleManager.selectedSireNameProperty(), sireNameTextField);
        addTextFieldListener(selectedCattleManager.selectedDamNameProperty(), damNameTextField);
    }

    private <T> void addTextFieldListener(ObservableValue<T> property, TextField textField) {
        property.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textField.setText(newValue.toString());
            } else {
                textField.clear();
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

//OFFSPRING DATA
// Method to load Offspring data
private void loadOffspringData() throws SQLException {
    try {
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

        // Default to the first record if none is selected
        if (tableData.isEmpty()) {
            handleClearOffspringFields();
        } else {
            if (cattleTableView.getSelectionModel().isEmpty()) {
                cattleTableView.getSelectionModel().selectFirst();
            }
            populateFieldsWithSelectedOffspring();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading offspring data.");
    }
}

    // Method to create OffSpringTable entry
    private OffSpringTable createOffSpringTableEntry(Offspring offspring) {
        String cattleName;
        String gender;
        String breedingMethod;

        try {
            Cattle cattle = CattleDAO.getCattleByID(Integer.parseInt(offspring.getCattleId()));
            cattleName = cattle != null ? cattle.getName() : "Unknown";
            gender = cattle != null ? cattle.getGender() : "Unknown";
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

    // Method to get Breeding Method by ID
    private String getBreedingMethodById(int cattleId) throws SQLException {
        Offspring offspring = OffspringDAO.getOffspringByCattleId(cattleId);
        return offspring != null ? offspring.getBreedingMethod() : "Unknown";
    }


    //Populating and Handling Fields
// Method to populate fields with selected Offspring
    private void populateFieldsWithSelectedOffspring() {
        OffSpringTable selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        if (selectedOffspring != null) {
            modifyOffspringDetailsButton.setDisable(false);
            storeInitialValues(selectedOffspring);
            populateFields(selectedOffspring);
            updateSireIdOrDamIdLabel(selectedOffspring.getGender());
            updateOffSpringDetailsButton.setDisable(true);
        }
    }

    // Method to store initial values
    private void storeInitialValues(OffSpringTable offspring) {
        initialValuesOffspring.put("birthWeight", getStringValue(offspring.getBirthWeight()));
        initialValuesOffspring.put("easeOfCalving", getStringValue(offspring.getEaseOfCalving()));
        initialValuesOffspring.put("gestationLength", getStringValue(offspring.getGestationLength()));
        initialValuesOffspring.put("measuredWeight", getStringValue(offspring.getMeasuredWeight()));
        initialValuesOffspring.put("lastDateWeightTaken", getStringValue(offspring.getLastDateWeightTaken()));
        initialValuesOffspring.put("intendedUse", getStringValue(offspring.getIntendedUse()));
        initialValuesOffspring.put("gender", getStringValue(offspring.getGender()));
        initialValuesOffspring.put("breedingMethod", getStringValue(offspring.getBreedingMethod()));
    }

    // Method to populate fields
    private void populateFields(OffSpringTable offspring) {
        offspringIdLabel.setText(String.valueOf(offspring.getOffspringId()));
        birthWeightTextField.setText(getStringValue(offspring.getBirthWeight()));
        easeOfCalvingSlider.setValue(getSliderValue(offspring.getEaseOfCalving()));
        sireIdOrDamIdTextField.setText(getStringValue(offspring.getSireId()));
        gestationLengthTextField.setText(getStringValue(offspring.getGestationLength()));
        measuredWeightTextField.setText(getStringValue(offspring.getMeasuredWeight()));
        lastDateWeightTakenDatePicker.setValue(offspring.getLastDateWeightTaken());
        intendedUseComboBox.setValue(getStringValue(offspring.getIntendedUse()));
        offspringGenderComboBox.setValue(getStringValue(offspring.getGender()));
        breedingMethodComboBox.setValue(getStringValue(offspring.getBreedingMethod()));
    }

    // Method to update Sire ID or Dam ID label
    private void updateSireIdOrDamIdLabel(String gender) {
        if (gender != null) {
            switch (gender.toLowerCase()) {
                case "male":
                    sireIdOrDamIdLabel.setText("Sire ID");
                    break;
                case "female":
                    sireIdOrDamIdLabel.setText("Dam ID");
                    break;
                default:
                    sireIdOrDamIdLabel.setText("Unknown ID");
                    break;
            }
        } else {
            sireIdOrDamIdLabel.setText("Unknown ID");
        }
    }

    // Utility methods
    private String getStringValue(Object value) {
        return value != null ? String.valueOf(value) : "";
    }

    private double getSliderValue(Number value) {
        return value != null ? value.doubleValue() : 0;
    }


    //Checking Changes and Validations
// Method to check for changes in Offspring details
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

    // Method to check if Offspring fields have changed
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

    // Method to check if Offspring fields are populated
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

    // Method to check if fields are valid for Offspring
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


    //Handling Actions
    // Method to handle modification of Offspring details
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

    // Method to update Offspring details
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

    // Method to handle deletion of Offspring
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


    //Setting Up Listeners
    // Method to set up table selection listener for Offspring
    private void setupOffspringTableSelectionListener() {
        cattleTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedOffspring();
            }
        });
    }

    // Method to add listeners for field changes
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

    // Method to clear Offspring fields
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


    //CALVING EVENT
    // Loading and Populating Data
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

    // Method to populate fields with selected Calving Event data
    private void populateFieldsWithSelectedCalvingEvent() {
        CalvingEvent selectedCalvingEvent = calvingEventsTableView.getSelectionModel().getSelectedItem();
        if (selectedCalvingEvent != null) {
            modifyCalvingEventDetailsButton.setDisable(false);
            updateCalvingEventsDetailsButton.setDisable(true);
            updateInitialValues(selectedCalvingEvent);
            updateUIComponents(selectedCalvingEvent);
        }
    }


    private void updateInitialValues(CalvingEvent event) {
        initialValuesCalvingEvent.put("cattleId", String.valueOf(event.cattleIdProperty().get()));
        initialValuesCalvingEvent.put("reproductiveVariableId", String.valueOf(event.reproductiveVariableIdProperty().get()));
        initialValuesCalvingEvent.put("offspringIdTextField", String.valueOf(event.offspringIdProperty().get()));
        initialValuesCalvingEvent.put("numberOfCalvesBorn", String.valueOf(event.numberOfCalvesBornProperty().get()));
        initialValuesCalvingEvent.put("calvesBornAlive", String.valueOf(event.calvesBornAliveProperty().get()));
        initialValuesCalvingEvent.put("stillbirths", String.valueOf(event.stillbirthsProperty().get()));
        initialValuesCalvingEvent.put("assistanceRequired", event.assistanceRequiredProperty().get());
        initialValuesCalvingEvent.put("physicalConditionCalf", event.physicalConditionCalfProperty().get());
    }

    private void updateUIComponents(CalvingEvent event) {
        calvingEventIdLabel.setText(String.valueOf(event.calvingEventIdProperty().get()));
        cattleIdCalveEventsTextField.setText(String.valueOf(event.cattleIdProperty().get()));
        reproductiveVariableIdTextField.setText(String.valueOf(event.reproductiveVariableIdProperty().get()));
        offspringIdTextField.setText(String.valueOf(event.offspringIdProperty().get()));
        numberOfCalvesBornTextField.setText(String.valueOf(event.numberOfCalvesBornProperty().get()));
        calvesBornAliveTextField.setText(String.valueOf(event.calvesBornAliveProperty().get()));
        stillbirthsTextField.setText(String.valueOf(event.stillbirthsProperty().get()));
        assistanceRequiredTextArea.setText(event.assistanceRequiredProperty().get());
        physicalConditionCalfTextArea.setText(event.physicalConditionCalfProperty().get());
    }



    //Validating and Checking Fields
    private boolean areFieldsValidForCalveEvent() {
        try {
            Integer.parseInt(numberOfCalvesBornTextField.getText());
            Integer.parseInt(calvesBornAliveTextField.getText());
            Integer.parseInt(stillbirthsTextField.getText());

            // Check if all fields are filled
            return !numberOfCalvesBornTextField.getText().isEmpty() &&
                    !calvesBornAliveTextField.getText().isEmpty() &&
                    !stillbirthsTextField.getText().isEmpty() &&
                    !assistanceRequiredTextArea.getText().isEmpty() &&
                    !physicalConditionCalfTextArea.getText().isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
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



//Clearing and Handling Events
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

    // Method to handle the modification of Calving Event details
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

    // Method to handle updating Calving Event details
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

    // Method to handle deletion of Calving Event
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
                        handleClearFieldsForCalveEvent();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the Calving Event.");
                }
            }
        }
    }

    //Utility Methods
// Helper method to parse an integer or return null
    private Integer parseIntegerOrNull(String text) {
        try {
            return (text == null || text.trim().isEmpty()) ? null : Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null; // Or you can handle the exception as needed
        }
    }

    // Method to set up table selection listener for Calving Events
    private void setupCalvingEventTableSelectionListener() {
        calvingEventsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedCalvingEvent();
            }
        });
    }

    // Method to add listeners for field changes
    private void addFieldChangeListenersForCalveEvent() {
        numberOfCalvesBornTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        calvesBornAliveTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        stillbirthsTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        assistanceRequiredTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
        physicalConditionCalfTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForCalveEventDetailChanges());
    }







    //BREEDING ATTEMPT
    // Loading and Populating Data
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

    // Method to populate fields with selected Breeding Attempt data
    private void populateFieldsWithSelectedBreedingAttempt() {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt != null) {
            modifyBreedingAttemptButton.setDisable(false);
            updateBreedingAttemptButton.setDisable(true);
            updateBreedingAttemptDetails(selectedAttempt);
            updateSireDetails(selectedAttempt);
        }
    }

    private void updateBreedingAttemptDetails(BreedingAttempt attempt) {
        breedingAttemptIdLabel.setText(String.valueOf(attempt.getBreedingAttemptId()));
        estrusDatePicker.setValue(LocalDate.parse(attempt.getEstrusDate()));
        breedingMethodBreedingAttemptComboBox.setValue(attempt.getBreedingMethod());
        attemptNumberTextField.setText(computeAttempts());
        attemptDatePicker.setValue(LocalDate.parse(attempt.getAttemptDate()));
        attemptStatusComboBox.setValue(attempt.getAttemptStatus());
        notesTextArea.setText(attempt.getNotes());
    }

    private void updateSireDetails(BreedingAttempt attempt) {
        if (attempt.getSireId() != 0) {
            try {
                Cattle cattle = CattleDAO.getCattleByID(attempt.getSireId());
                sireNameLabel.setText(Objects.requireNonNull(cattle).getName());
                sireNameButton.setText(Objects.requireNonNull(cattle).getTagId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            sireNameLabel.setText("N/A");
        }
    }


    //Validation & Population
    // Method to check if Breeding Attempt fields are populated
    private boolean breedingAttemptFieldsArePopulated() {
        return estrusDatePicker.getValue() != null &&
                !sireNameLabel.getText().isEmpty() &&
                breedingMethodBreedingAttemptComboBox.getValue() != null;
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

    //Handling Fields
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

    // Method to check for changes in Breeding Attempt details
    private void checkForBreedingAttemptDetailChanges() {
        String estrusDateValue = estrusDatePicker.getValue() != null ? estrusDatePicker.getValue().toString() : null;
        String sireUsedValue = sireNameLabel.getText() != null ? sireNameLabel.getText() : null;
        String breedingMethodValue = breedingMethodBreedingAttemptComboBox.getValue() != null ? breedingMethodBreedingAttemptComboBox.getValue() : null;
        String attemptDateValue = attemptDatePicker.getValue() != null ? attemptDatePicker.getValue().toString() : null;
        String attemptStatusValue = attemptStatusComboBox.getValue() != null ? attemptStatusComboBox.getValue() : null;
        String notesValue = notesTextArea.getText() != null ? notesTextArea.getText() : null;

        String selectedEstrusDate = getSelectedBreedingAttemptValue("estrusDate");
        String selectedSireId = getSelectedBreedingAttemptValue("sireId");
        String selectedBreedingMethod = getSelectedBreedingAttemptValue("breedingMethod");
        String selectedAttemptDate = getSelectedBreedingAttemptValue("attemptDate");
        String selectedAttemptStatus = getSelectedBreedingAttemptValue("attemptStatus");
        String selectedNotes = getSelectedBreedingAttemptValue("notes");

        boolean hasChanges =
                !Objects.equals(estrusDateValue, selectedEstrusDate) ||
                        !Objects.equals(sireUsedValue, selectedSireId) ||
                        !Objects.equals(breedingMethodValue, selectedBreedingMethod) ||
                        !Objects.equals(attemptDateValue, selectedAttemptDate) ||
                        !Objects.equals(attemptStatusValue, selectedAttemptStatus) ||
                        !Objects.equals(notesValue, selectedNotes);

        updateBreedingAttemptButton.setDisable(!hasChanges);
    }

    // Helper method to retrieve selected Breeding Attempt value based on property name
    private String getSelectedBreedingAttemptValue(String propertyName) {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt == null) return "";

        return switch (propertyName) {
            case "estrusDate" -> selectedAttempt.getEstrusDate();
            case "sireId" -> getSireName(selectedAttempt.getSireId());
            case "breedingMethod" -> selectedAttempt.getBreedingMethod();
            case "attemptDate" -> selectedAttempt.getAttemptDate();
            case "attemptStatus" -> selectedAttempt.getAttemptStatus();
            case "notes" -> selectedAttempt.getNotes();
            default -> "";
        };
    }

    // Helper method to check if Breeding Attempt fields have changed
    private boolean breedingAttemptFieldsHaveChanged() {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt == null) return false;

        return !estrusDatePicker.getValue().toString().equals(selectedAttempt.getEstrusDate()) ||
                !sireNameLabel.getText().equals(getSireName(selectedAttempt.getSireId())) ||
                !breedingMethodBreedingAttemptComboBox.getValue().equals(selectedAttempt.getBreedingMethod()) ||
                !attemptDatePicker.getValue().toString().equals(selectedAttempt.getAttemptDate()) ||
                !attemptStatusComboBox.getValue().equals(selectedAttempt.getAttemptStatus()) ||
                !notesTextArea.getText().equals(selectedAttempt.getNotes());
    }

    //Update and Delete Operations:
    // Method to handle the modification of a Breeding Attempt
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

    // Method to handle updating a Breeding Attempt
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
            } else {
                sireId = Integer.parseInt(sireText);
            }

            try {
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

                BreedingAttemptDAO.updateBreedingAttempt(updatedAttempt);
                loadBreedingAttemptsData();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding attempt details updated successfully.");
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating breeding attempt details.");
            }
        }
    }

    // Method to handle deletion of a Breeding Attempt
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

    // Method to handle sire tag selection
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

    // Helper method to get the name of the sire based on sire ID
    private String getSireName(int sireId) {
        String sireName = "N/A";
        if (sireId != 0) {
            try {
                Cattle cattle = CattleDAO.getCattleByID(sireId);
                if (cattle != null) {
                    sireName = cattle.getName();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return sireName;
    }


    //Utility and Listener Setup:
    // Method to compute the attempt number
    private String computeAttempts() {
        return "N/A";
    }

    // Method to set up table selection listener for Breeding Attempts
    private void setupBreedingAttemptsTableSelectionListener() {
        breedingAttemptsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedBreedingAttempt();
            }
        });
    }

    // Method to add field change listeners for Breeding Attempt
    private void addFieldChangeListenersForBreedingAttempt() {
        estrusDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        sireNameLabel.textProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        breedingMethodBreedingAttemptComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        notesTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        attemptDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        attemptStatusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
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
}
