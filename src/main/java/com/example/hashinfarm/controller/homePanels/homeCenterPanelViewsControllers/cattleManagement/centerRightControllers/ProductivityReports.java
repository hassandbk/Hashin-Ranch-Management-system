package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.controller.dao.OffspringDAO;
import com.example.hashinfarm.controller.dao.CalvingEventDAO;
import com.example.hashinfarm.controller.dao.BreedingAttemptDAO;
import com.example.hashinfarm.controller.dao.ReproductiveVariablesDAO;
import com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.CattleController;
import com.example.hashinfarm.controller.utility.*;
import com.example.hashinfarm.model.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@SuppressWarnings("CallToPrintStackTrace")
public class ProductivityReports {


    
    // FXML elements for Cattle Information
    @FXML
    private Label selectedCattleIdLabel, offspringIdLabel, sireIdOrDamIdLabel;
    @FXML
    private TextField sireIdTextField, damIdTextField, cattleNameTextField, birthWeightTextField;
    @FXML
    private ComboBox<String> statusComboBox, breedingMethodComboBox, outcomeComboBox, offspringGenderComboBox, intendedUseComboBox;
    @FXML
    private Slider easeOfCalvingSlider;
    @FXML
    private TextField sireIdOrDamIdTextField, measuredWeightTextField, sireNameTextField, damNameTextField;
    @FXML
    private DatePicker lastDateWeightTakenDatePicker;
    @FXML
    private TableView<OffSpringTable> cattleTableView;
    @FXML
    private TableColumn<OffSpringTable, String> offspringIdColumn, cattleIdColumn, cattleNameColumn, genderColumn, breedingMethodColumn;
    @FXML
    private Button modifyOffspringDetailsButton, updateOffSpringDetailsButton;
    @FXML private Spinner<Integer> estimatedGestationSpinner;

    // FXML elements for Calving Events
    @FXML
    private Label calvingEventIdLabel;
    @FXML
    private TextField cattleIdCalveEventsTextField, reproductiveVariableIdTextField, numberOfCalvesBornTextField, calvesBornAliveTextField, stillbirthsTextField, offspringIdTextField;
    @FXML
    private TextArea assistanceRequiredTextArea, physicalConditionCalfTextArea;
    @FXML
    private Button modifyCalvingEventDetailsButton, updateCalvingEventsDetailsButton;
    @FXML
    private TableView<CalvingEvent> calvingEventsTableView;
    @FXML
    private TableColumn<CalvingEvent, Integer> calvingEventIdColumn, cattleIdCalveEventsColumn, reproductiveVariableIdColumn;
    @FXML
    private TableColumn<CalvingEvent, Integer> numberOfCalvesBornColumn, calvesBornAliveColumn, stillbirthsColumn;

    // FXML elements for Breeding Attempts
    @FXML
    private TableView<BreedingAttempt> breedingAttemptsTableView;
    @FXML
    private TableColumn<BreedingAttempt, Integer> breedingAttemptIdColumn;
    @FXML
    private TableColumn<BreedingAttempt, String> estrusDateColumn, breedingMethodBreedingAttemptColumn, sireUsedColumn, attemptDateColumn, attemptStatusColumn;
    @FXML
    private Label breedingAttemptIdLabel, sireNameLabel;
    @FXML
    private TextField attemptNumberTextField;
    @FXML
    private DatePicker estrusDatePicker, attemptDatePicker;
    @FXML
    private ComboBox<String> breedingMethodBreedingAttemptComboBox, attemptStatusComboBox;
    @FXML
    private TextArea notesTextArea;
    @FXML
    private Button modifyBreedingAttemptButton, updateBreedingAttemptButton, sireNameButton;
    @FXML
    private HBox estimatedGestationPeriodHBox;
    @FXML
    private Spinner<Integer> estimatedGestationPeriodSpinner;
    // SplitPane and Navigation Buttons
    @FXML
    private SplitPane splitPaneOffSpringInfo, splitPaneCalveEvents, splitPaneBreedAttempts;
    @FXML
    private Button leftArrowButtonOffSpringInfo, rightArrowButtonOffSpringInfo;
    @FXML
    private Button leftArrowButtonCalveEvents, rightArrowButtonCalveEvents;
    @FXML
    private Button leftArrowButtonBreedAttempts, rightArrowButtonBreedAttempts;

    // Other Fields
    private int selectedCattleId;
    private final Map<String, String> initialValuesOffspring = new HashMap<>();
    private final Map<String, String> initialValuesCalvingEvent = new HashMap<>();
    private final double minPosition = 0.1;
    private final double maxPosition = 0.7;

    private static final int MATURITY_AGE_MONTHS = 14; // Assuming cattle maturity age of 14 months
    private static final int POST_CALVING_INTERVAL = 45;
    private final ReproductiveVariablesDAO reproductiveVariablesDAO = new ReproductiveVariablesDAO();
    private final BooleanProperty addingReproductiveVariables = new SimpleBooleanProperty(false);
    private ChangeListener<LocalDate> estrusDateChangeListener;

    @FXML
    private void initialize() {
        initializeButtons();
        setDatePickerFormat(estrusDatePicker);
        setDatePickerFormat(attemptDatePicker);
        initializeSplitPlane(splitPaneOffSpringInfo, leftArrowButtonOffSpringInfo, rightArrowButtonOffSpringInfo);
        initializeSplitPlane(splitPaneCalveEvents, leftArrowButtonCalveEvents, rightArrowButtonCalveEvents);
        initializeSplitPlane(splitPaneBreedAttempts, leftArrowButtonBreedAttempts, rightArrowButtonBreedAttempts);
        initSelectedCattleListeners();
        initializeTableColumns();
        initializeGestationPeriod();
        setupOffspringTableSelectionListener();
        setupCalvingEventTableSelectionListener();
        setupBreedingAttemptsTableSelectionListener();
        addFieldChangeListenersForOffspring();
        addFieldChangeListenersForCalveEvent();
        addFieldChangeListenersForBreedingAttempt();

        breedingMethodBreedingAttemptComboBox.setItems(FXCollections.observableArrayList("Natural Mating", "Artificial Insemination", "Embryo Transfer"));
        attemptStatusComboBox.setItems(FXCollections.observableArrayList("Success", "Failure", "Unknown"));
    }

    private void initializeButtons() {
        modifyOffspringDetailsButton.setDisable(true);
        updateOffSpringDetailsButton.setDisable(true);
        modifyCalvingEventDetailsButton.setDisable(true);
        updateCalvingEventsDetailsButton.setDisable(true);
        modifyBreedingAttemptButton.setDisable(true);
        updateBreedingAttemptButton.setDisable(true);
    }

    private void initializeSplitPlane(SplitPane splitpane, Button leftArrowButton, Button rightArrowButton) {
        SplitPaneDividerEnforcer dividerEnforcer = new SplitPaneDividerEnforcer(minPosition, maxPosition);
        dividerEnforcer.enforceConstraints(splitpane);
        com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.CattleController cattleController = new CattleController();
        leftArrowButton.setOnAction(event -> cattleController.animateSplitPane(minPosition, splitpane, minPosition, maxPosition, leftArrowButton, rightArrowButton));
        rightArrowButton.setOnAction(event -> cattleController.animateSplitPane(maxPosition, splitpane, minPosition, maxPosition, leftArrowButton, rightArrowButton));
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

    private void setDatePickerFormat(DatePicker datePicker) {
        DateUtil.datePickerFormat(datePicker);
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

    private void initializeGestationPeriod() {
        int initialGestationDays = 283;
        int minGestationDays = 265;
        int maxGestationDays = 295;
        estimatedGestationPeriodHBox.visibleProperty().bind(addingReproductiveVariables);
        estimatedGestationPeriodSpinner.visibleProperty().bind(addingReproductiveVariables);

        estimatedGestationPeriodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minGestationDays, maxGestationDays, initialGestationDays));
        estimatedGestationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minGestationDays, maxGestationDays, initialGestationDays));
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
                }else {
                    // Cattle doesn't have offspring entry, so add a new entry
                    Offspring newOffspring = new Offspring(
                            0,                 // offspringId
                            0.0,               // birthWeight
                            1,                 // easeOfCalving
                            283,                 // gestationLength
                            0.0,               // measuredWeight
                            null,              // lastDateWeightTaken
                            "",                // intendedUse
                            String.valueOf(cattle.getCattleId()), // cattleId
                            ""                 // breedingMethod
                    );

                    // Add the new offspring to the database
                    OffspringDAO.insertOffspring(newOffspring);
                    offspringList.add(newOffspring); // Also add it to the list for the table
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
        estimatedGestationSpinner.getValueFactory().setValue(offspring.getGestationLength());
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
                estimatedGestationSpinner.getValue() != Integer.parseInt(initialValuesOffspring.get("gestationLength")) ||
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
                !Objects.equals(estimatedGestationSpinner.getValue(), selectedOffspring.getGestationLength()) ||
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
                Objects.equals(estimatedGestationSpinner.getValue(), selectedOffspring.getGestationLength()) &&
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
            estimatedGestationSpinner.getValue();
            Double.parseDouble(measuredWeightTextField.getText());

            // Additional validation can be added as needed
            return !birthWeightTextField.getText().isEmpty() &&
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

        if (!areFieldsValidForOffspring()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all fields are correctly filled.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Update");
        confirmationAlert.setHeaderText("You are about to update the offspring details.");
        confirmationAlert.setContentText("Do you want to proceed with the update?");

        ButtonType confirmButton = new ButtonType("Yes");
        ButtonType cancelButton = new ButtonType("No");
        confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            // Proceed with update if user confirms
            try {
                // Create updated Offspring object with the current field values
                Offspring updatedOffspring = new Offspring(
                        selectedOffspringTable.getOffspringId(),
                        Double.parseDouble(birthWeightTextField.getText()),
                        (int) easeOfCalvingSlider.getValue(),
                        estimatedGestationSpinner.getValue(),
                        Double.parseDouble(measuredWeightTextField.getText()),
                        lastDateWeightTakenDatePicker.getValue(),
                        intendedUseComboBox.getValue(),
                        selectedOffspringTable.getCattleId(),
                        breedingMethodComboBox.getValue()
                );

                // Check if the gender or gestation length has changed
                boolean genderChanged = !offspringGenderComboBox.getValue().equals(initialValuesOffspring.get("gender"));
                boolean gestationLengthChanged = !estimatedGestationSpinner.getValue().toString().equals(initialValuesOffspring.get("gestationLength"));

                // Update the database tables if necessary
                if (genderChanged) {
                    if (!updateCattleTableGender(Integer.parseInt(selectedOffspringTable.getCattleId()), offspringGenderComboBox.getValue())) {
                        return; // Early return if updating gender failed
                    }
                }

                if (gestationLengthChanged) {
                    if (!updateReproductiveVariablesTable(Integer.parseInt(selectedOffspringTable.getCattleId()), estimatedGestationSpinner.getValue())) {
                        return; // Early return if updating gestation length failed
                    }
                }

                // Update offspring details in the Offspring table
                OffspringDAO.updateOffspring(updatedOffspring);

                // Reload data to reflect changes
                loadOffspringData();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Offspring details updated successfully.");
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the offspring details.");
            }
        } else {
            // User chose not to update
            showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Update cancelled by the user.");
        }
    }


    private boolean updateCattleTableGender(int cattleId, String newGender) {
        try {
            boolean success = CattleDAO.updateCattleGender(cattleId, newGender);
            if (!success) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Failed to update cattle gender. Please check if the cattle ID is correct.");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the cattle gender.");
            return false;
        }
    }

    private boolean updateReproductiveVariablesTable(int cattleId, int newGestationLength) {
        try {
            boolean success = ReproductiveVariablesDAO.updateGestationPeriod(cattleId, newGestationLength);
            if (!success) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Failed to update gestation period. Please check if the cattle ID is correct.");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the gestation period.");
            return false;
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
        estimatedGestationSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
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
        estimatedGestationSpinner.getValueFactory().setValue(283);
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
            // Remove the listener if it exists
            if (estrusDateChangeListener != null) {
                estrusDatePicker.valueProperty().removeListener(estrusDateChangeListener);
            }
            modifyBreedingAttemptButton.setDisable(false);
            updateBreedingAttemptButton.setDisable(true);
            updateBreedingAttemptButton.setText("Update");
            initializeEstrusDatePicker(estrusDatePicker, selectedCattleId, selectedAttempt);
            initializeAttemptDatePicker(attemptDatePicker,selectedAttempt);
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
            // Check for non-null values for all required fields
            boolean isEstrusDateValid = estrusDatePicker.getValue() != null;
            boolean isBreedingMethodValid = breedingMethodBreedingAttemptComboBox.getValue() != null;
            boolean isAttemptDateValid = attemptDatePicker.getValue() != null;
            boolean isAttemptStatusValid = attemptStatusComboBox.getValue() != null;

            // Check if breeding method is Natural Mating
            String currentBreedingMethod = breedingMethodBreedingAttemptComboBox.getValue();
            boolean isNaturalMating = "Natural Mating".equals(currentBreedingMethod);

            // Validate sireNameLabel based on breeding method
            boolean isSireNameLabelValid = !isNaturalMating || !sireNameLabel.getText().isEmpty();

            // Combine all validation checks
            return isEstrusDateValid &&
                    isSireNameLabelValid &&  // Only apply validation for sireNameLabel if Natural Mating
                    isBreedingMethodValid &&
                    isAttemptDateValid &&
                    isAttemptStatusValid;
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
        attemptNumberTextField.setText("N/A");
        attemptDatePicker.setValue(null);
        attemptStatusComboBox.setValue(null);
        notesTextArea.clear();
        sireNameButton.setText("Select Sire");
        updateBreedingAttemptButton.setText("Save");
        modifyBreedingAttemptButton.setDisable(true);
        updateBreedingAttemptButton.setDisable(true);

        initializeEstrusDatePickerForNewEntry(estrusDatePicker);
        // Set selected attempt to null
        breedingAttemptsTableView.getSelectionModel().clearSelection();

        // Ensure the listener is defined and add it
        if (estrusDateChangeListener == null) {
            estrusDateChangeListener = (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    attemptDatePicker.setDayCellFactory(null);
                    updateAttemptDatePicker(attemptDatePicker, newValue);
                }
            };
        }
        estrusDatePicker.valueProperty().addListener(estrusDateChangeListener);


    }
    private void updateAttemptDatePicker(DatePicker attemptDatePicker, LocalDate newEstrusDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate maxDate = newEstrusDate.plusDays(23).isAfter(currentDate) ? currentDate : newEstrusDate.plusDays(23);

        configureDatePicker(attemptDatePicker, newEstrusDate, maxDate);
    }
    // Method to check for changes or validate fields based on button text
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

        boolean areFieldsValid = areFieldsValidForBreedingAttempt();

        String buttonText = updateBreedingAttemptButton.getText();
        if ("Update".equals(buttonText)) {
            // Enable the button if there are changes
            updateBreedingAttemptButton.setDisable(!hasChanges);
        } else if ("Save".equals(buttonText)) {
            // Enable the button if all fields are valid
            updateBreedingAttemptButton.setDisable(!areFieldsValid);
        }
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

        String currentBreedingMethod = breedingMethodBreedingAttemptComboBox.getValue();
        boolean isNaturalMating = "Natural Mating".equals(currentBreedingMethod);

        boolean sireNameLabelChanged = !isNaturalMating || !sireNameLabel.getText().equals(getSireName(selectedAttempt.getSireId()));

        return !estrusDatePicker.getValue().toString().equals(selectedAttempt.getEstrusDate()) ||
                !sireNameLabelChanged ||  // Include sireNameLabel change check
                !currentBreedingMethod.equals(selectedAttempt.getBreedingMethod()) ||
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
        }else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a breeding attempt from the table.");
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
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt != null) {
            int cattleId = selectedAttempt.getCattleId();
            LocalDate estrusDate = LocalDate.parse(selectedAttempt.getEstrusDate());
            LocalDate attemptDate = LocalDate.parse(selectedAttempt.getAttemptDate());

            List<BreedingAttempt> breedingAttempts;
            try {
                breedingAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleIdAndEstrusDate(cattleId, estrusDate);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            int attemptNumber = 0;
            for (BreedingAttempt attempt : breedingAttempts) {
                LocalDate currentAttemptDate = LocalDate.parse(attempt.getAttemptDate());
                if (!currentAttemptDate.isAfter(attemptDate)) {
                    attemptNumber++;
                }
            }

            return ordinal(attemptNumber) + " Attempt";
        }
        return "N/A";
    }

    private String ordinal(int number) {
        String[] suffixes = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        if (number % 100 >= 11 && number % 100 <= 13) {
            return number + "th";
        } else {
            return number + suffixes[number % 10];
        }
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
        estrusDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Check if attemptDatePicker is not null and clear its value if necessary
            if (attemptDatePicker.getValue() != null) {
                attemptDatePicker.setValue(null);
            }
            // Call the method to check for other breeding attempt detail changes
            checkForBreedingAttemptDetailChanges();
        });
        sireNameLabel.textProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        breedingMethodBreedingAttemptComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        notesTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        attemptDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        attemptStatusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        estimatedGestationPeriodSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
    }










    @FXML
    private void updateBreedingAttempt() {

        if (Objects.equals(updateBreedingAttemptButton.getText(), "Save")) {
            showSaveDialog();
            return;
        }
        if (Objects.equals(updateBreedingAttemptButton.getText(), "Update")) {
            BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();

            if (selectedAttempt == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a breeding attempt from the table.");
                return;
            }

            if (areFieldsValidForBreedingAttempt()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all fields are correctly filled.");
                return;
            }

            Optional<ButtonType> result = showConfirmationAlert("Update Record", "Are you sure you want to update the breeding attempt details?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int sireId;
                String sireNameText = sireNameLabel.getText();
                String sireTagText = sireNameButton.getText();
                if (sireNameText == null || sireNameText.equals("N/A") || sireNameText.isEmpty()) {
                    sireId = 0;
                } else {
                    try {
                        Cattle cattle = CattleDAO.getCattleByTagAndName(sireTagText, sireNameText);
                        sireId = Objects.requireNonNull(cattle).getCattleId();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }


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

    }
    private void showSaveDialog() {
        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
        saveAlert.setTitle("Save Breeding Attempt");
        saveAlert.setHeaderText(null);
        saveAlert.setContentText("Do you want to save the new breeding attempt details?");

        ButtonType saveButton = new ButtonType("Save");
        ButtonType clearButton = new ButtonType("Clear");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        saveAlert.getButtonTypes().setAll(saveButton, clearButton, cancelButton);

        Optional<ButtonType> result = saveAlert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == saveButton) {
                saveBreedingAttempt();
            } else if (result.get() == clearButton) {
                handleClearFieldsForBreedingAttempt();
            }
        }
    }

    private void saveBreedingAttempt() {
        if (!areFieldsValidForBreedingAttempt()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all Mandatory fields are correctly filled.");
            return;
        }

        try {
            int sireId;
            String sireNameText = sireNameLabel.getText();
            String sireTagText = sireNameButton.getText();
            if (sireNameText == null || sireNameText.equals("N/A") || sireNameText.isEmpty()) {
                sireId = 0;
            } else {
                Cattle cattle = CattleDAO.getCattleByTagAndName(sireTagText, sireNameText);
                sireId = Objects.requireNonNull(cattle).getCattleId();
            }

            BreedingAttempt newAttempt = new BreedingAttempt(
                    0,
                    selectedCattleId,
                    estrusDatePicker.getValue().toString(),
                    breedingMethodBreedingAttemptComboBox.getValue(),
                    sireId,
                    notesTextArea.getText(),
                    attemptDatePicker.getValue().toString(),
                    attemptStatusComboBox.getValue()
            );

            BreedingAttemptDAO.saveBreedingAttempt(newAttempt);
            loadBreedingAttemptsData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding attempt saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while saving breeding attempt details.");
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



    public void initializeEstrusDatePickerForNewEntry(DatePicker estrusDatePicker) {
        try {
            LocalDate currentDate = LocalDate.now();

            // Fetch the cattle's birthdate and determine maturity date
            Cattle cattle = CattleDAO.getCattleByID(selectedCattleId);
            if (cattle == null) {
                throw new SQLException("Cattle not found for ID: " + selectedCattleId);
            }
            LocalDate maturityDate = cattle.getDateOfBirth().plusMonths(MATURITY_AGE_MONTHS);


            // Fetch existing estrus cycles and disable specific dates
            List<LocalDate> disabledDateRanges = getDisabledDateRanges(selectedCattleId);

            // Configure the date picker
            configureDatePicker(estrusDatePicker, maturityDate, currentDate, disabledDateRanges);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            // Handle the error, e.g., show an error message to the user
        }
    }

    private List<LocalDate> getDisabledDateRanges(int cattleId) throws SQLException {
        List<LocalDate> disabledRanges = new ArrayList<>();

        List<BreedingAttempt> breedingAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleId(cattleId);
        for (BreedingAttempt attempt : breedingAttempts) {

            if ("Success".equals(attempt.getAttemptStatus())) {
                LocalDate breedingDate =LocalDate.parse(attempt.getAttemptDate());
                LocalDate estrusDate = LocalDate.parse(attempt.getEstrusDate());

                ReproductiveVariables reproductiveVariables = reproductiveVariablesDAO.getReproductiveVariableByCattleIdAndBreedingDate(cattleId, breedingDate);

                if (reproductiveVariables == null) {
                    handleMissingReproductiveVariables(cattleId, breedingDate);
                }

                if (Objects.requireNonNull(reproductiveVariables).getCalvingDate() != null) {
                    LocalDate endDate = reproductiveVariables.getCalvingDate().plusDays(POST_CALVING_INTERVAL);
                    disabledRanges.add(estrusDate);
                    disabledRanges.add(endDate);
                } else {
                    LocalDate endDate = estrusDate.plusDays(24); // Default estrus cycle length
                    disabledRanges.add(estrusDate);
                    disabledRanges.add(endDate);
                }

            }
        }

        return disabledRanges;
    }



    private boolean isDateInDisabledRanges(LocalDate date, List<LocalDate> disabledRanges) {
        for (int i = 0; i < disabledRanges.size(); i += 2) {
            LocalDate start = disabledRanges.get(i);
            LocalDate end = disabledRanges.get(i + 1);
            if (!date.isBefore(start) && !date.isAfter(end)) {
                return true;
            }
        }
        return false;
    }

    // Method to initialize DatePicker for an existing breeding attempt
    public void initializeEstrusDatePicker(DatePicker estrusDatePicker, int cattleId, BreedingAttempt selectedAttempt) {
        try {
            LocalDate minDate = calculateMinimumEstrusDate(cattleId, selectedAttempt);
            LocalDate maxDate = calculateMaximumDate(cattleId, selectedAttempt, minDate);
            configureDatePicker(estrusDatePicker, minDate, maxDate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public LocalDate calculateMinimumEstrusDate(int cattleId, BreedingAttempt selectedAttempt) throws SQLException {
        LocalDate currentEstrusDate = LocalDate.parse(selectedAttempt.getEstrusDate());
        
        LocalDate previousEstrusDate = BreedingAttemptDAO.getPreviousEstrusDate(selectedCattleId, currentEstrusDate);

        return calculateMinDateBasedOnPreviousEstrus(cattleId, previousEstrusDate);
    }

    private LocalDate calculateMinDateBasedOnPreviousEstrus(int cattleId, LocalDate previousEstrusDate) throws SQLException {
        if (previousEstrusDate == null) {
            return calculateMinDateForNoPreviousEstrus(cattleId);
        }

        List<BreedingAttempt> previousAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleIdAndEstrusDate(cattleId, previousEstrusDate);
        Optional<BreedingAttempt> successfulAttempt = previousAttempts.stream()
                .filter(attempt -> "Success".equals(attempt.getAttemptStatus()))
                .findFirst();

        if (successfulAttempt.isPresent()) {
            LocalDate breedingDate = LocalDate.parse(successfulAttempt.get().getAttemptDate());
            return calculateMinDateBasedOnSuccessfulAttempt(cattleId, breedingDate);
        } else {
            return previousEstrusDate.plusDays(24); // Handle case where no successful breeding attempt found
        }
    }

    private LocalDate calculateMinDateForNoPreviousEstrus(int cattleId) throws SQLException {
        Cattle cattle = CattleDAO.getCattleByID(cattleId);
        if (cattle == null) {
            throw new SQLException("Cattle not found for ID: " + cattleId);
        }
        return cattle.getDateOfBirth().plusMonths(MATURITY_AGE_MONTHS);
    }

    private LocalDate calculateMinDateBasedOnSuccessfulAttempt(int cattleId, LocalDate breedingDate) throws SQLException {
        ReproductiveVariables reproductiveVariables = reproductiveVariablesDAO.getReproductiveVariableByCattleIdAndBreedingDate(cattleId, breedingDate);

        if (reproductiveVariables == null) {
            handleMissingReproductiveVariables(cattleId, breedingDate);
            //The time between heat cycles (called the estrous cycle) is usually around 21 days, but can range from 18 to 24 days.
            return breedingDate.plusDays(24); // Use a default minimum based on previous estrus date successful attempt
        }

        if (reproductiveVariables.getCalvingDate() != null) {
            return reproductiveVariables.getCalvingDate().plusDays(POST_CALVING_INTERVAL);
        } else {
            return calculateMinDateBasedOnNextAttemptOrEstimatedCalving(cattleId, breedingDate, reproductiveVariables);
        }
    }

    private LocalDate calculateMinDateBasedOnNextAttemptOrEstimatedCalving(int cattleId, LocalDate breedingDate, ReproductiveVariables reproductiveVariables) throws SQLException {
        ReproductiveVariables nextAttempt = reproductiveVariablesDAO.getNextBreedingAttempt(cattleId, breedingDate);
        if (nextAttempt != null) {
            //The time between heat cycles (called the estrous cycle) is usually around 21 days, but can range from 18 to 24 days.
            return nextAttempt.getBreedingDate().minusDays(24);
        } else {
            LocalDate estimatedCalvingDate = breedingDate.plusDays(reproductiveVariables.getGestationPeriod());
            return estimatedCalvingDate.plusDays(POST_CALVING_INTERVAL);
        }
    }


    private void handleMissingReproductiveVariables(int cattleId, LocalDate breedingDate) {
        // Add missing reproductive variables with default values
        ReproductiveVariables missingReproductiveVariables = new ReproductiveVariables();
        missingReproductiveVariables.setCattleID(cattleId);
        int gestationPeriod = estimatedGestationPeriodSpinner.getValue();

        addingReproductiveVariables.set(true);

        missingReproductiveVariables.setBreedingDate(breedingDate);
        missingReproductiveVariables.setGestationPeriod(gestationPeriod);
        int reproductiveVariableId = reproductiveVariablesDAO.addReproductiveVariableAndGetId(missingReproductiveVariables);

        if (reproductiveVariableId != -1) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "This missing Reproductive Data added successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add reproductive data.");
        }
        addingReproductiveVariables.set(false);
    }


    public LocalDate calculateMaximumDate(int cattleId, BreedingAttempt selectedAttempt, LocalDate minDate) throws SQLException {
        LocalDate currentEstrusDate = LocalDate.parse(selectedAttempt.getEstrusDate());
        LocalDate previousEstrusDate = BreedingAttemptDAO.getPreviousEstrusDate(cattleId, currentEstrusDate);

        return calculateMaxDateBasedOnNextEstrus(cattleId, previousEstrusDate, minDate, currentEstrusDate);
    }

    private LocalDate calculateMaxDateBasedOnNextEstrus(int cattleId, LocalDate previousEstrusDate, LocalDate minDate, LocalDate currentEstrusDate) throws SQLException {
        LocalDate nextEstrusDate = BreedingAttemptDAO.getSubsequentEstrusDate(cattleId, currentEstrusDate);


            if(nextEstrusDate== null && previousEstrusDate == null){
                int reasonableMonths = 6;  // Set this value based on your requirements
                return minDate.plusMonths(reasonableMonths);
            }else {
                if (nextEstrusDate!= null) {
                    return calculateMaxDateForBasedOnNextEstrus(cattleId, nextEstrusDate, minDate);
                }else return minDate.plusMonths(6);
            }


    }

    private LocalDate calculateMaxDateForBasedOnNextEstrus(int cattleId, LocalDate nextEstrusDate, LocalDate minDate) throws SQLException {

        List<BreedingAttempt> nextAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleIdAndEstrusDate(cattleId, nextEstrusDate);
        Optional<BreedingAttempt> successfulAttempt = nextAttempts.stream()
                .filter(attempt -> "Success".equals(attempt.getAttemptStatus()))
                .findFirst();

        return successfulAttempt.map(breedingAttempt -> LocalDate.parse(breedingAttempt.getEstrusDate())).orElseGet(() -> minDate.plusMonths(3));


    }
    // Private helper method to set the DayCellFactory for the DatePicker
    private void configureDatePicker(DatePicker datePicker, LocalDate minDate, LocalDate maxDate) {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(minDate) || item.isAfter(maxDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                    setTooltip(new Tooltip("Date outside the valid range"));
                }
            }
        };

        datePicker.setDayCellFactory(dayCellFactory);
    }
    private void configureDatePicker(DatePicker datePicker, LocalDate minDate, LocalDate maxDate, List<LocalDate> disabledDates) {
        datePicker.setDayCellFactory(cellDatePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean isDisabled = empty || date.isBefore(minDate) || date.isAfter(maxDate) || isDateInDisabledRanges(date, disabledDates);
                setDisable(isDisabled);

                if (isDisabled) {
                    setStyle("-fx-background-color: #ffc0cb;"); // Pink color for disabled cells
                } else {
                    setStyle(""); // Reset style for enabled cells
                }
            }
        });
    }

    public void initializeAttemptDatePicker(DatePicker attemptDatePicker, BreedingAttempt selectedAttempt) {

            // Existing attempt, proceed with normal logic
            try {
                // Fetch all existing breeding attempts for the specified cattle
                List<BreedingAttempt> filteredAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleIdAndEstrusDate(
                        selectedCattleId, LocalDate.parse(selectedAttempt.getEstrusDate()));

                LocalDate currentDate = LocalDate.now();
                LocalDate selectedEstrusDate = LocalDate.parse(selectedAttempt.getEstrusDate());
                LocalDate selectedAttemptDate = LocalDate.parse(selectedAttempt.getAttemptDate());

                // Sort the filtered attempts by attemptDate in ascending order
                filteredAttempts.sort(Comparator.comparing(attempt -> LocalDate.parse(attempt.getAttemptDate())));

                // Initialize previous and subsequent attempts
                BreedingAttempt previousAttempt = null;
                BreedingAttempt subsequentAttempt = null;

                // Find the index of the selected attempt
                int selectedIndex = -1;
                for (int i = 0; i < filteredAttempts.size(); i++) {
                    BreedingAttempt attempt = filteredAttempts.get(i);
                    LocalDate attemptDate = LocalDate.parse(attempt.getAttemptDate());

                    if (attemptDate.isEqual(selectedAttemptDate)) {
                        selectedIndex = i;
                        break;
                    }
                }

                // Determine the previous and subsequent attempts
                if (selectedIndex != -1) {
                    if (selectedIndex > 0) {
                        previousAttempt = filteredAttempts.get(selectedIndex - 1);
                    }

                    if (selectedIndex < filteredAttempts.size() - 1) {
                        subsequentAttempt = filteredAttempts.get(selectedIndex + 1);
                    }
                }

                // Configure the DatePicker based on the positions of previous and subsequent attempts
                LocalDate minDate;
                LocalDate maxDate;

                if (filteredAttempts.size() == 1) {
                    // Only one attempt
                    minDate = selectedEstrusDate;
                    maxDate = currentDate.isAfter(selectedEstrusDate.plusDays(23)) ? currentDate : selectedEstrusDate.plusDays(23);
                } else if (previousAttempt == null && subsequentAttempt != null) {
                    // First attempt
                    minDate = selectedEstrusDate;
                    maxDate = LocalDate.parse(subsequentAttempt.getAttemptDate()).minusDays(1);
                } else if (previousAttempt != null && subsequentAttempt != null) {
                    // Middle attempt
                    minDate = LocalDate.parse(previousAttempt.getAttemptDate()).plusDays(1);
                    maxDate = LocalDate.parse(subsequentAttempt.getAttemptDate()).minusDays(1);
                } else if (previousAttempt != null) {
                    // Last attempt
                    minDate = LocalDate.parse(previousAttempt.getAttemptDate()).plusDays(1);
                    maxDate = selectedEstrusDate.plusDays(23).isAfter(currentDate) ? currentDate : selectedEstrusDate.plusDays(23);
                } else {
                    // Fallback case
                    minDate = selectedEstrusDate;
                    maxDate = currentDate;
                }

                // Configure the DatePicker with the calculated min and max dates
                configureDatePicker(attemptDatePicker, minDate, maxDate);

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while initializing the Attempt Date Picker.");
            }

    }








}
