package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.DewormingHistoryDAO;
import com.example.hashinfarm.controller.dao.FollowUpRecommendationDAO;
import com.example.hashinfarm.controller.dao.HealthCheckupHistoryDAO;
import com.example.hashinfarm.controller.dao.MedicationHistoryDAO;
import com.example.hashinfarm.controller.records.DewormingRecord;
import com.example.hashinfarm.controller.records.HealthCheckupRecord;
import com.example.hashinfarm.controller.records.MedicationRecord;
import com.example.hashinfarm.controller.utility.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import com.example.hashinfarm.exceptions.DatabaseException;
import com.example.hashinfarm.exceptions.ValidationException;
import com.example.hashinfarm.model.*;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.example.hashinfarm.controller.utility.CountryCodePickerHelper;



public class MedicalHistory {

    private int selectedCattleId = 0;
    private final Map<String, String> originalDewormingValuesMap = new HashMap<>();
    private final Map<String, String> originalMedicationValuesMap = new HashMap<>();
    private final Map<String, String> originalHealthCheckupValuesMap = new HashMap<>();

    private boolean isModifiedDeworming = false;
    private boolean isModifiedMedication = false;
    private boolean isModifiedHealthCheckup =false;

    private DewormingRecord selectedDewormingRecord;
    private MedicationRecord selectedMedicationRecord;
    private HealthCheckupRecord selectedHealthCheckupRecord;

    private CountryCodePickerHelper dewormingCountryCodePickerHelper;
    private CountryCodePickerHelper medicationCountryCodePickerHelper;



    @FXML private ImageView imageViewOfDewormingHistory,imageViewOfMedication;
    @FXML private ComboBox<Country> countryComboBoxOfDewormingHistory, countryComboBoxOfMedication;
    @FXML private TextField restOfNumberOfDewormingHistoryTextField, countryCodeOfDewormingHistoryTextField,countryCodeTextFieldOfMedication, restOfNumberTextFieldOfMedication;

    // DEWORMING PART
    @FXML private TableView<DewormingRecord> dewormingHistoryTableView;
    @FXML private TableColumn<DewormingRecord, LocalDate> dewormingHistoryDateColumn;
    @FXML private TableColumn<DewormingRecord, String> dewormerTypeColumn, dosageColumn, administeredByColumn, routeOfAdministrationColumn, weightAtTimeColumn, contactDetailsColumn;

    @FXML private TextField cattleNameTextField, dosageOfDewormingTextField, weightAtTimeOfDewormingTextField, administerOfDewormingTextField;
    @FXML private TextArea dewormerTypeTextArea,manufacturerDetailsTextArea;
    @FXML private ComboBox<String>  routeOfAdministrationCombo;
    @FXML private DatePicker  dewormingDatePicker;


    // MEDICATION PART
    @FXML private TableView<MedicationRecord> medicationHistoryTableView;
    @FXML private TableColumn<MedicationRecord, LocalDate> dateTakenOfMedicationColumn,nextScheduleOfMedicationColumn;
    @FXML private TableColumn<MedicationRecord, String> dosageOfMedicationColumn, frequencyOfMedicationColumn, typeOfMedicationColumn, administerOfMedicationColumn, telNoOfMedicationColumn, categoryOfMedicationColumn;

    @FXML private TextField dosageOfMedicationTextField, frequencyOfMedicationTextField, typeOfMedicationTextField, administerOfMedicationTextField;
    @FXML private DatePicker dateTakenOfMedicationDatePicker, nextScheduleOfMedicationTextField;
    @FXML private ComboBox<String> categoryOfMedicationComboBox;
    @FXML private CheckBox negativeCheckBox, positiveCheckBox;

    // HEALTH CHECKUP
    @FXML private TableView<HealthCheckupRecord> healthCheckupTableView;
    @FXML private TableColumn<HealthCheckupRecord, Integer> idHealthCheckupColumn;
    @FXML private TableColumn<HealthCheckupRecord, LocalDate> checkupDateColumn;
    @FXML private TableColumn<HealthCheckupRecord, String> createdATColumn, temperatureColumn, heartRateColumn, respiratoryRateColumn, bloodPressureColumn, behavioralObservationsColumn, physicalExaminationColumn, healthIssuesColumn, specificObservationsColumn, checkupNotesColumn, chronicConditionsColumn;

    @FXML private DatePicker checkupDatePicker;
    @FXML private TextField createdATTextField, temperatureTextField, heartRateTextField, respiratoryRateTextField, bloodPressureTextField;
    @FXML private TextArea behavioralObservationsTextArea, physicalExaminationTextArea, healthIssuesTextArea, specificObservationsTextArea, checkupNotesTextArea, chronicConditionsTextArea;
    @FXML private TitledPane healthAssessmentsTitledPane, preventiveCareTitledPane, nutritionalManagementTitledPane, physicalHealthMaintenanceTitledPane, managementPracticesTitledPane, environmentalHousingAssessmentsTitledPane, monitoringReviewTitledPane, additionalRecommendationsTitledPane;

    @FXML private CheckBox regularHealthAssessmentsCheckBox, monitoringTemperatureCheckBox, monitoringDiseasesCheckBox, routineBloodTestsCheckBox, fecalTestingCheckBox, skinCoatInspectionsCheckBox, monitoringBloatCheckBox, monitoringAnaplasmosisCheckBox, monitoringJohnesDiseaseCheckBox, behavioralAssessmentsCheckBox;
    @FXML private CheckBox vaccinationProtocolsCheckBox, deWormingTreatmentsCheckBox, parasiteControlMeasuresCheckBox, bioSecurityPracticesCheckBox, breedingSoundnessExamsCheckBox, colostrumManagementCheckBox, stressReductionTechniquesCheckBox, bodyConditionScoringCheckBox, nutritionalSupplementationCheckBox;
    @FXML private CheckBox nutritionalEvaluationCheckBox, monitoringWaterQualityCheckBox, monitoringMilkProductionCheckBox, regularWeightMonitoringCheckBox, consultationNutritionalRequirementsCheckBox;
    @FXML private CheckBox hoofHealthAssessmentsCheckBox, footTrimmingScheduleCheckBox, dentalCheckUpsCheckBox, monitoringLamenessIssuesCheckBox, overallBodyConditionAssessmentCheckBox;
    @FXML private CheckBox calvingManagementProtocolsCheckBox, cullingDecisionsCheckBox, herdImmunityAssessmentCheckBox, trainingStaffHandlingTechniquesCheckBox, implementingQuarantineProtocolsCheckBox, recordKeepingHealthInterventionsCheckBox;
    @FXML private CheckBox livingConditionsAssessmentCheckBox, flyInsectManagementCheckBox, adequateExerciseCheckBox, environmentalToxinsMonitoringCheckBox;
    @FXML private CheckBox vaccinationScheduleReviewCheckBox, regularWeightChecksCheckBox, educationZoonoticDiseasesCheckBox, postMortemExaminationsCheckBox, recordKeepingVeterinaryInterventionsCheckBox;
    @FXML private CheckBox monitoringCalfHealthCheckBox, limitingStressFactorsCheckBox, evaluationBreedingPracticesCheckBox, implementationHerdHealthPlansCheckBox, observationSocialBehaviorCheckBox, regularReproductivePerformanceAssessmentCheckBox;

    private final Map<TitledPane, List<CheckBox>> titledPaneCheckboxMap = new HashMap<>();
    private final Map<String, Boolean> originalRecommendationsMap = new HashMap<>();





    public void initialize() {
        initializeSelectedCattleManager();
        DateUtil.datePickerFormat(dewormingDatePicker);
        DateUtil.datePickerFormat(dateTakenOfMedicationDatePicker);
        DateUtil.datePickerFormat(nextScheduleOfMedicationTextField);
        initializeImageView();
        initializeCountryCodePicker();
        initializeTextFields();
        initializeSelectionListener();

        setupDewormingTableColumns();
        setupDewormingChangeListeners();

        setupMedicationTableColumns();
        setupMedicationChangeListeners();

        setupHealthCheckupTableColumns();
        setupHealthCheckupChangeListeners();


        // Initialize the map


        mapTitledPaneToCheckBoxes();
        addCheckboxListeners();
    }
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }


    private void initializeSelectedCattleManager() {
        SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();
        selectedCattleManager.selectedNameProperty().addListener((observable, oldValue, newValue) ->
                cattleNameTextField.setText(newValue)
        );

        selectedCattleManager.selectedCattleIDProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.intValue() == 0) return;
            selectedCattleId = newValue.intValue();
            loadDewormingDataIntoTableView();
            loadMedicationDataIntoTableView();
            loadHealthCheckupDataIntoTableView();
        });
    }


    private void initializeImageView() {
        imageViewOfDewormingHistory.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png"))));
        imageViewOfMedication.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png"))));
    }



    private void initializeCountryCodePicker() {
        dewormingCountryCodePickerHelper = new CountryCodePickerHelper(countryComboBoxOfDewormingHistory, countryCodeOfDewormingHistoryTextField, imageViewOfDewormingHistory, restOfNumberOfDewormingHistoryTextField);
        dewormingCountryCodePickerHelper.initialize();

        medicationCountryCodePickerHelper = new CountryCodePickerHelper(countryComboBoxOfMedication, countryCodeTextFieldOfMedication, imageViewOfMedication, restOfNumberTextFieldOfMedication);
        medicationCountryCodePickerHelper.initialize();
    }


    private void initializeTextFields() {
        UnitsTextField.initializeTextField(dosageOfDewormingTextField, MeasurementType.DOSAGE);
        UnitsTextField.initializeTextField(weightAtTimeOfDewormingTextField, MeasurementType.WEIGHT);
        UnitsTextField.initializeTextField(temperatureTextField, MeasurementType.TEMPERATURE);
        UnitsTextField.initializeTextField(heartRateTextField, MeasurementType.HEART_RATE);
        UnitsTextField.initializeTextField(respiratoryRateTextField, MeasurementType.RESPIRATORY_RATE);
        UnitsTextField.initializeTextField(bloodPressureTextField, MeasurementType.BLOOD_PRESSURE);

        UnitsTextField.addValidationListener(dosageOfDewormingTextField, MeasurementType.DOSAGE,
                text -> UnitsTextField.isValidMeasurement(text, MeasurementType.DOSAGE)
        );
        UnitsTextField.addValidationListener(weightAtTimeOfDewormingTextField, MeasurementType.WEIGHT,
                text -> UnitsTextField.isValidMeasurement(text, MeasurementType.WEIGHT)
        );
        UnitsTextField.addValidationListener(temperatureTextField, MeasurementType.TEMPERATURE,
                text -> UnitsTextField.isValidMeasurement(text, MeasurementType.TEMPERATURE)
        );
        UnitsTextField.addValidationListener(heartRateTextField, MeasurementType.HEART_RATE,
                text -> UnitsTextField.isValidMeasurement(text, MeasurementType.HEART_RATE)
        );
        UnitsTextField.addValidationListener(respiratoryRateTextField, MeasurementType.RESPIRATORY_RATE,
                text -> UnitsTextField.isValidMeasurement(text, MeasurementType.RESPIRATORY_RATE)
        );
        UnitsTextField.addValidationListener(bloodPressureTextField, MeasurementType.BLOOD_PRESSURE,
                text -> UnitsTextField.isValidMeasurement(text, MeasurementType.BLOOD_PRESSURE)
        );
    }





    private void initializeSelectionListener() {
        dewormingHistoryTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateDewormingHistoryFields(newValue);
                selectedDewormingRecord =newValue;
            }
        });

        medicationHistoryTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateMedicationHistoryFields(newValue);
                selectedMedicationRecord =newValue;
            }
        });






        // Listener for health checkups table
        healthCheckupTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateHealthCheckupFields(newValue);
                selectedHealthCheckupRecord = newValue;
            }
        });

    }






    //DEWORMING HISTORY
    private void loadDewormingDataIntoTableView() {
        clearAllDewormingFields();

        if (selectedCattleId == 0) {  // Make sure you have a valid selectedCattleId
            // Show an alert if no cattle is selected
            showAlert(AlertType.WARNING, "No Cattle Selected", "Please select a cattle before proceeding.");
            return;
        }

        List<DewormingHistory> dewormingHistories;
        try {
            dewormingHistories = DewormingHistoryDAO.getDewormingHistoriesByCattleId(selectedCattleId);
        } catch (SQLException e) {

            showAlert(AlertType.ERROR, "Database Error", "Failed to load deworming history. Please try again.");
            return;
        }

        ObservableList<DewormingRecord> records = getDewormingRecords(dewormingHistories);
        dewormingHistoryTableView.setItems(records);
    }

    private ObservableList<DewormingRecord> getDewormingRecords(List<DewormingHistory> dewormingHistories) {
        ObservableList<DewormingRecord> records = FXCollections.observableArrayList();

        for (DewormingHistory history : dewormingHistories) {

            records.add(new DewormingRecord(
                    history.getId(),
                    history.getDateOfDeworming(),
                    history.getDewormerType(),
                    String.valueOf(history.getDosage()),
                    history.getAdministeredBy(),
                    history.getRouteOfAdministration(),
                    history.getWeightAtTime() != null ? String.valueOf(history.getWeightAtTime()) : "", // Handle potential null
                    history.getContactDetails(),
                    history.getManufacturerDetails()
            ));
        }

        return records;
    }



    private void setupDewormingTableColumns() {
        dewormingHistoryDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().dewormingDate()));
        dewormerTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().dewormerType()));
        dosageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().dosage()));
        administeredByColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().administeredBy()));
        routeOfAdministrationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().routeOfAdministration()));
        weightAtTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().weightAtTime()));
        contactDetailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().contactDetails()));
    }

    // Populate input fields with selected DewormingRecord's values
    private void populateDewormingHistoryFields(DewormingRecord record) {
        clearAllDewormingFields();

        // Populate fields with record's values
        dewormingDatePicker.setValue(record.dewormingDate());
        dewormerTypeTextArea.setText(record.dewormerType());
        dosageOfDewormingTextField.setText(record.dosage());
        administerOfDewormingTextField.setText(record.administeredBy());
        routeOfAdministrationCombo.setValue(record.routeOfAdministration());
        weightAtTimeOfDewormingTextField.setText(record.weightAtTime());
        manufacturerDetailsTextArea.setText(record.manufacturerDetails());

        // Process contact details
        String contactDetails = record.contactDetails();
        String[] contactInfo = processDewormingContactDetails(contactDetails);

        // Populate the original values map with record and contact details
        populateOriginalDewormingValuesMap(record, contactInfo[0], contactInfo[1]);

        // Reset the modification state
        isModifiedDeworming = false;
    }

    // Extract and process contact details from the record
    private String[] processDewormingContactDetails(String contactDetails) {
        return dewormingCountryCodePickerHelper.processContactDetails(contactDetails,
                countryCodeOfDewormingHistoryTextField,
                restOfNumberOfDewormingHistoryTextField,
                countryComboBoxOfDewormingHistory);
    }


    private boolean validateDewormingInputFields() throws ValidationException {
        // Validate all fields and apply error effects as needed
        InputFieldsValidationHelper.validateField(selectedCattleId == 0, "Select a Cattle from the Herds table", null);
        InputFieldsValidationHelper.validateField(dewormerTypeTextArea.getText().trim().isEmpty(), "Dewormer Type is required.", dewormerTypeTextArea);
        InputFieldsValidationHelper.validateField(dosageOfDewormingTextField.getText().trim().isEmpty(), "Dosage is required.", dosageOfDewormingTextField);
        InputFieldsValidationHelper.validateField(dewormingDatePicker.getValue() == null, "Date of Deworming is required.", dewormingDatePicker);
        InputFieldsValidationHelper.validateField(routeOfAdministrationCombo.getValue() == null, "Route of Administration is required.", routeOfAdministrationCombo);

        // Validate and format phone number (assuming it's implemented)
        CountryCodePickerHelper.validateAndFormatContact(restOfNumberOfDewormingHistoryTextField.getText().trim());

        // If no exception was thrown, return true (indicating all fields are valid)
        return true;
    }

    // Populate the original values map for change tracking
    private void populateOriginalDewormingValuesMap(DewormingRecord record, String callingCode, String restOfNumber) {
        originalDewormingValuesMap.put("id", String.valueOf(record.id()));
        originalDewormingValuesMap.put("dewormingDate", record.dewormingDate().toString());
        originalDewormingValuesMap.put("dewormerType", record.dewormerType());
        originalDewormingValuesMap.put("dosage", record.dosage()); // Store the dosage
        originalDewormingValuesMap.put("administeredBy", record.administeredBy());
        originalDewormingValuesMap.put("routeOfAdministration", record.routeOfAdministration());
        originalDewormingValuesMap.put("weightAtTime", record.weightAtTime());
        originalDewormingValuesMap.put("manufacturerDetails", record.manufacturerDetails());
        originalDewormingValuesMap.put("callingCode", callingCode);
        originalDewormingValuesMap.put("restOfNumber", restOfNumber);
    }

    // Set up listeners for changes in input fields
    private void setupDewormingChangeListeners() {
        dewormingDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInDewormingData("dewormingDate", newValue != null ? newValue.toString() : null));
        dewormerTypeTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInDewormingData("dewormerType", newValue != null ? newValue : ""));
        dosageOfDewormingTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInDewormingData("dosage", newValue != null ? newValue : ""));
        administerOfDewormingTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInDewormingData("administeredBy", newValue != null ? newValue : ""));
        routeOfAdministrationCombo.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInDewormingData("routeOfAdministration", newValue != null ? newValue : ""));
        weightAtTimeOfDewormingTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInDewormingData("weightAtTime", newValue != null ? newValue : ""));
        manufacturerDetailsTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInDewormingData("manufacturerDetails", newValue != null ? newValue : ""));

        countryCodeOfDewormingHistoryTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInDewormingData("callingCode", newValue != null ? newValue : ""));
        restOfNumberOfDewormingHistoryTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInDewormingData("restOfNumber", newValue != null ? newValue : ""));
    }

    // Check for changes in deworming data and update modification state
    private void checkForChangesInDewormingData(String fieldName, String newValue) {
        String originalValue = originalDewormingValuesMap.get(fieldName);

        if (originalValue != null && !originalValue.equals(newValue)) {
            isModifiedDeworming = true; // Mark changes if the value differs
        } else if (originalValue != null) {
            isModifiedDeworming = false; // Reset the modification flag if values match
        }
    }

    //MODIFY DETAILS BUTTON
    @FXML
    public void modifyDewormingRecord() {
        selectedDewormingRecord = dewormingHistoryTableView.getSelectionModel().getSelectedItem();
        if (selectedDewormingRecord == null) {
            // No record selected, show saving options
            showDewormingSaveDialog();
        } else {
            showDewormingUpdateDialog();
        }
    }

    private void showDewormingSaveDialog() {
        List<ButtonType> buttonTypes = Arrays.asList(
                new ButtonType("Save"),
                new ButtonType("Clear Fields"),
                new ButtonType("Cancel")
        );

        List<Consumer<Void>> actions = Arrays.asList(
                v -> saveNewDewormingRecord(),
                v -> clearAllDewormingFields(),
                v -> {} // Cancel does nothing as it's handled automatically
        );

        DialogHelper.showConfirmationDialog("New Record", "Choose an Action ?", buttonTypes, actions);
    }

    private void showDewormingUpdateDialog() {
        DialogHelper.showUpdateDialog(
                "Modify Deworming Record",
                isModifiedDeworming,
                v -> updateDewormingRecord(),
                v -> restoreOriginalDewormingData(),
                v -> deleteDewormingRecord(),
                v -> {}
        );
    }


    // SAVE DEWORMING RECORD
    private void saveNewDewormingRecord() {
        try {
            if (!validateDewormingInputFields()) return;

            DewormingHistory dewormingHistory = createDewormingHistory();
            saveDewormingHistoryToDatabase(dewormingHistory);
            loadDewormingDataIntoTableView();
            showAlert(AlertType.INFORMATION, "Success", "Deworming details saved successfully.");
        } catch (ValidationException e) {
            showAlert(AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            showAlert(AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    // Create DewormingHistory object from input fields
    private DewormingHistory createDewormingHistory() throws ValidationException {
        String dewormerType = dewormerTypeTextArea.getText().trim();
        double dosage = CountryCodePickerHelper.parseNumericValue(
                dosageOfDewormingTextField.getText().trim(), MeasurementType.DOSAGE.getSuffix()
        );
        double weightAtTime = CountryCodePickerHelper.parseNumericValue(
                weightAtTimeOfDewormingTextField.getText().trim(), MeasurementType.WEIGHT.getSuffix()
        );

        String administeredBy = administerOfDewormingTextField.getText().trim();
        LocalDate dateOfDeworming = dewormingDatePicker.getValue();
        String manufacturerDetails = manufacturerDetailsTextArea.getText().trim();
        String contactDetails = CountryCodePickerHelper.validateAndFormatContact(restOfNumberOfDewormingHistoryTextField.getText().trim());
        String routeOfAdministration = routeOfAdministrationCombo.getValue();

        return new DewormingHistory(
                0, // ID will be generated by the database
                selectedCattleId,
                dewormerType,
                dosage,
                weightAtTime,
                administeredBy,
                routeOfAdministration,
                dateOfDeworming,
                manufacturerDetails,
                contactDetails
        );
    }

    // Save DewormingHistory to the database
    private void saveDewormingHistoryToDatabase(DewormingHistory dewormingHistory) throws DatabaseException {
        try {
            // Insert details into the database
            DewormingHistoryDAO.insertDewormingHistory(dewormingHistory);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save deworming details: " + e.getMessage(), e);
        }
    }



    // UPDATE DEWORMING RECORD
    private void updateDewormingRecord() {
        try {
            // Validate the input fields; exception will be thrown if validation fails
            validateDewormingInputFields();

            // Proceed with creating the updated history object
            DewormingHistory updatedHistory = createDewormingHistory();

            // Retrieve and set the original ID from the map
            String originalIdString = originalDewormingValuesMap.get("id");
            if (originalIdString != null) {
                int originalId = Integer.parseInt(originalIdString);
                updatedHistory.setId(originalId);
            } else {
                throw new IllegalStateException("Original ID not found in the values map.");
            }

            // Update the record in the database
            DewormingHistoryDAO.updateDewormingHistory(updatedHistory);
            loadDewormingDataIntoTableView();

            // Show the success message
            showAlert(AlertType.INFORMATION, "Success", "Deworming record updated successfully.");
        } catch (ValidationException e) {
            // Handle validation errors
            showAlert(AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (SQLException e) {
            // Handle database errors
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            // Handle other errors like missing ID
            showAlert(AlertType.ERROR, "Error", e.getMessage());
        }
    }



    //DELETE DEWORMING RECORD

    // Action for deleting a selected deworming record
    private void deleteDewormingRecord() {
        selectedDewormingRecord = dewormingHistoryTableView.getSelectionModel().getSelectedItem();
        if (selectedDewormingRecord == null) {
            return; // No record selected
        }

        if (!confirmDewormingRecordDeletion("Record ID: " + selectedDewormingRecord.id())) {
            return; // User canceled deletion
        }

        try {
            deleteDewormingRecord(selectedDewormingRecord);
            showAlert(Alert.AlertType.INFORMATION, "Record Deleted", "The selected deworming record has been successfully deleted.");
        } finally {
            loadDewormingDataIntoTableView(); // Refresh table after deletion attempt
        }
    }

    // Show a confirmation dialog before deleting a record
    private boolean confirmDewormingRecordDeletion(String contentText) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this record?");
        confirmationAlert.setContentText(contentText + "\nThis action cannot be undone.");
        Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();
        return confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK;
    }

    // Delete the deworming record from the database
    private void deleteDewormingRecord(DewormingRecord record) {
        int cattleId = selectedCattleId;
        int recordId = record.id();
        try {
            DewormingHistoryDAO.deleteDewormingHistoryByCattleIdAndId(cattleId, recordId);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete the deworming record. Please try again.");
        }
    }


    //RESTORE DEWORMING DETAILS
    private void restoreOriginalDewormingData() {
        // Restore original data to fields
        dewormingDatePicker.setValue(LocalDate.parse(originalDewormingValuesMap.get("dewormingDate")));
        dewormerTypeTextArea.setText(originalDewormingValuesMap.get("dewormerType"));
        dosageOfDewormingTextField.setText(originalDewormingValuesMap.get("dosage"));
        administerOfDewormingTextField.setText(originalDewormingValuesMap.get("administeredBy"));
        routeOfAdministrationCombo.setValue(originalDewormingValuesMap.get("routeOfAdministration"));
        weightAtTimeOfDewormingTextField.setText(originalDewormingValuesMap.get("weightAtTime"));
        manufacturerDetailsTextArea.setText(originalDewormingValuesMap.get("manufacturerDetails"));

        // Construct contact details from the original values map
        String callingCode = originalDewormingValuesMap.get("callingCode");
        String restOfNumber = originalDewormingValuesMap.get("restOfNumber");
        String contactDetails = String.format("(%s) %s", callingCode, restOfNumber);

        // Restore contact details using the processContactDetails method
        processDewormingContactDetails(contactDetails);
        // Reset the modification state
        isModifiedDeworming = false; // Resetting the modification state since a new record is loaded
    }



    // CLEAR DEWORMING DETAILS
    @FXML
    private void clearAllDewormingDetails() {
        clearAllDewormingFields();
    }

    // Clear all deworming fields, deselect table view, and reset flags
    private void clearAllDewormingFields() {
        dewormingHistoryTableView.getSelectionModel().clearSelection();
        selectedDewormingRecord = null; // Deselect currently selected record

        // Clear the map if it's not empty
        if (!originalDewormingValuesMap.isEmpty()) {
            originalDewormingValuesMap.clear();
        }

        // Clear all input fields
        dewormingDatePicker.setValue(null);
        dewormerTypeTextArea.clear();
        dosageOfDewormingTextField.clear();
        administerOfDewormingTextField.clear();
        routeOfAdministrationCombo.setValue(null);
        weightAtTimeOfDewormingTextField.clear();
        manufacturerDetailsTextArea.clear();
        dewormingCountryCodePickerHelper.clearPhoneNumberFields();

        // Reset the modification flag
        isModifiedDeworming = false;
    }



//MEDICATION HISTORY

    private void loadMedicationDataIntoTableView() {

        clearAllMedicationFields();

        if (selectedCattleId == 0) {  // Ensure a valid selectedCattleId
            // Show an alert if no cattle is selected
            showAlert(AlertType.WARNING, "No Cattle Selected", "Please select a cattle before proceeding.");
            return;
        }

        List<MedicationHistory> medicationHistories;
        try {
            medicationHistories = MedicationHistoryDAO.getMedicationHistoriesByCattleId(selectedCattleId);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to load medication history. Please try again.");
            return;
        }

        ObservableList<MedicationRecord> records = getMedicationRecords(medicationHistories);
        medicationHistoryTableView.setItems(records);
    }


    private ObservableList<MedicationRecord> getMedicationRecords(List<MedicationHistory> medicationHistories) {
        ObservableList<MedicationRecord> records = FXCollections.observableArrayList();

        for (MedicationHistory history : medicationHistories) {
            records.add(new MedicationRecord(
                    history.getId(),
                    history.getDosage(),
                    history.getFrequency(),
                    history.getDateTaken(),
                    history.getNextSchedule(),
                    history.getType(),
                    history.getAdministeredBy(),
                    history.getTelNo(),
                    history.getCategory()
            ));
        }

        return records;
    }

    private void setupMedicationTableColumns() {
        dateTakenOfMedicationColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().dateTaken()));
        dosageOfMedicationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().dosage()));
        frequencyOfMedicationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().frequency()));
        nextScheduleOfMedicationColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().nextSchedule()));
        administerOfMedicationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().administeredBy()));
        typeOfMedicationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().type()));
        telNoOfMedicationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().telNo()));
        categoryOfMedicationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().category()));
    }

    private void populateMedicationHistoryFields(MedicationRecord record) {

        clearAllMedicationFields();

        // Populate the fields with values from the MedicationRecord
        dateTakenOfMedicationDatePicker.setValue(record.dateTaken()); // Set date taken
        nextScheduleOfMedicationTextField.setValue(record.nextSchedule()); // Set the next schedule date
        dosageOfMedicationTextField.setText(record.dosage()); // Set dosage
        frequencyOfMedicationTextField.setText(record.frequency()); // Set frequency
        typeOfMedicationTextField.setText(record.type()); // Set medication type
        administerOfMedicationTextField.setText(record.administeredBy()); // Set administered by
        categoryOfMedicationComboBox.setValue(record.category()); // Set category of medication

        negativeCheckBox.setSelected(false);  // Set default value for negative check (adjust as necessary)
        positiveCheckBox.setSelected(false);  // Set the default value for positive check (adjust as necessary)


        // Extract and process contact details
        String contactDetails = record.telNo();
        String[] contactInfo = processMedicationContactDetails(contactDetails);

        // Call the method to populate the original values map with record data if needed
        populateOriginalMedicationValuesMap(record, contactInfo[0], contactInfo[1]);

        // Reset the modification state, since a new record is being loaded
        isModifiedMedication = false;
    }


    private String[] processMedicationContactDetails(String contactDetails) {
        return medicationCountryCodePickerHelper.processContactDetails(contactDetails,
                countryCodeTextFieldOfMedication,
                restOfNumberTextFieldOfMedication,
                countryComboBoxOfMedication);
    }


    private boolean validateMedicationInputFields() throws ValidationException {
        // Validate all fields directly using the input field IDs
        InputFieldsValidationHelper.validateField(selectedCattleId == 0, "Select a Cattle from the Herds table", null);
        InputFieldsValidationHelper.validateField(dosageOfMedicationTextField.getText().trim().isEmpty(), "Dosage is required.", dosageOfMedicationTextField);
        InputFieldsValidationHelper.validateField(frequencyOfMedicationTextField.getText().trim().isEmpty(), "Frequency is required.", frequencyOfMedicationTextField);
        InputFieldsValidationHelper.validateField(administerOfMedicationTextField.getText().trim().isEmpty(), "Administered By is required.", administerOfMedicationTextField);
        InputFieldsValidationHelper.validateField(dateTakenOfMedicationDatePicker.getValue() == null, "Date Taken is required.", dateTakenOfMedicationDatePicker);
        InputFieldsValidationHelper.validateField(nextScheduleOfMedicationTextField.getValue() == null, "Next Schedule is required.", nextScheduleOfMedicationTextField);
        InputFieldsValidationHelper.validateField(typeOfMedicationTextField.getText().trim().isEmpty(), "Type of Medication is required.", typeOfMedicationTextField);
        InputFieldsValidationHelper.validateField(administerOfMedicationTextField.getText().trim().isEmpty(), "Telephone Number is required.", administerOfMedicationTextField); // Assuming telNo is entered here
        InputFieldsValidationHelper.validateField(categoryOfMedicationComboBox.getValue() == null || categoryOfMedicationComboBox.getValue().isEmpty(), "Category of Medication is required.", categoryOfMedicationComboBox);

        // Validate and format phone number (assuming it's implemented in another method)
        CountryCodePickerHelper.validateAndFormatContact(restOfNumberTextFieldOfMedication.getText().trim());


        // If no exception was thrown, return true (indicating all fields are valid)
        return true;
    }



    private void populateOriginalMedicationValuesMap(MedicationRecord record, String callingCode, String restOfNumber) {
        // Store original values in the map
        originalMedicationValuesMap.put("id", String.valueOf(record.id())); // Store the ID
        originalMedicationValuesMap.put("dateTaken", record.dateTaken().toString()); // Store the date taken
        originalMedicationValuesMap.put("nextSchedule", record.nextSchedule().toString()); // Store the next schedule
        originalMedicationValuesMap.put("dosage", record.dosage()); // Store the dosage
        originalMedicationValuesMap.put("frequency", record.frequency()); // Store the frequency
        originalMedicationValuesMap.put("administeredBy", record.administeredBy()); // Store the name of the administrator
        originalMedicationValuesMap.put("type", record.type()); // Store the type of medication
        originalMedicationValuesMap.put("category", record.category()); // Store the category of medication
        originalMedicationValuesMap.put("callingCode", callingCode); // Store the calling code
        originalMedicationValuesMap.put("restOfNumber", restOfNumber); // Store the rest of the number
        originalMedicationValuesMap.put("responseType", getSelectedCheckBoxResponse()); // Store the response type

    }


    private void setupMedicationChangeListeners() {
        dateTakenOfMedicationDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("dateTaken", newValue != null ? newValue.toString() : null));
        nextScheduleOfMedicationTextField.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("nextSchedule", newValue != null ? newValue.toString() : null));
        typeOfMedicationTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("type", newValue != null ? newValue : ""));
        dosageOfMedicationTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("dosage", newValue != null ? newValue : ""));
        administerOfMedicationTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("administeredBy", newValue != null ? newValue : ""));
        frequencyOfMedicationTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("frequency", newValue != null ? newValue : ""));
        categoryOfMedicationComboBox.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("category", newValue));
        countryCodeTextFieldOfMedication.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("callingCode", newValue != null ? newValue : ""));
        restOfNumberTextFieldOfMedication.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("restOfNumber", newValue != null ? newValue : ""));
        countryComboBoxOfMedication.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("countryCode", newValue != null ? newValue.toString() : ""));

        // Unified listener for negative and positive checkboxes
        ChangeListener<Boolean> mutualExclusiveListener = (observable, oldValue, newValue) -> {
            if (newValue) {
                if (observable == negativeCheckBox.selectedProperty()) {
                    positiveCheckBox.setSelected(false);
                    checkForChangesInMedicationData("responseType", "Negative");
                } else {
                    negativeCheckBox.setSelected(false);
                    checkForChangesInMedicationData("responseType", "Positive");
                }
            }
        };

        // Add the listener to both checkboxes
        negativeCheckBox.selectedProperty().addListener(mutualExclusiveListener);
        positiveCheckBox.selectedProperty().addListener(mutualExclusiveListener);
    }


    private void checkForChangesInMedicationData(String fieldName, String newValue) {
        // Get the original value from the map
        String originalValue = originalMedicationValuesMap.get(fieldName);

        // Compare the new value to the original value
        if (originalValue != null && !originalValue.equals(newValue)) {
            // Mark that a change has occurred only if the value differs from the original
            isModifiedMedication = true;
        } else if (originalValue != null) {
            // Reset the modified state if the current value matches the original
            isModifiedMedication = false;
        }
    }


    //MODIFY MEDICATION RECORD BUTTON
    @FXML
    public void modifyMedicationRecord() {
        selectedMedicationRecord = medicationHistoryTableView.getSelectionModel().getSelectedItem();
        if (selectedMedicationRecord == null) {
            // No record selected, show saving options
            showMedicationSaveDialog();
        } else {
            showMedicationUpdateDialog();
        }
    }

    private void showMedicationSaveDialog() {
        List<ButtonType> buttonTypes = Arrays.asList(
                new ButtonType("Save"),
                new ButtonType("Clear Fields"),
                new ButtonType("Cancel")
        );

        List<Consumer<Void>> actions = Arrays.asList(
                v -> saveNewMedicationRecord(),
                v -> clearAllMedicationFields(),
                v -> {} // Cancel does nothing as it's handled automatically
        );

        DialogHelper.showConfirmationDialog("New Record", "Choose an Action ?", buttonTypes, actions);
    }
    private void showMedicationUpdateDialog() {
        DialogHelper.showUpdateDialog(
                "Modify Medication Record",
                isModifiedMedication,
                v -> updateMedicationRecord(),
                v -> restoreOriginalMedicationData(),
                v -> deleteMedicationRecord(),
                v -> {}
        );
    }



    // SAVE MEDICATION DETAILS
    private void saveNewMedicationRecord() {
        try {
            if (!validateMedicationInputFields()) return;

            MedicationHistory medicationHistory = createMedicationHistory();
            saveMedicationHistoryToDatabase(medicationHistory);
            loadMedicationDataIntoTableView();
            showAlert(AlertType.INFORMATION, "Success", "Medication details saved successfully.");
        } catch (ValidationException e) {
            showAlert(AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            showAlert(AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private MedicationHistory createMedicationHistory() throws ValidationException {
        String dosage = dosageOfMedicationTextField.getText().trim();
        String frequency = frequencyOfMedicationTextField.getText().trim();
        String administeredBy = administerOfMedicationTextField.getText().trim();
        LocalDate dateTaken = dateTakenOfMedicationDatePicker.getValue();
        LocalDate nextSchedule = nextScheduleOfMedicationTextField.getValue();
        String type = typeOfMedicationTextField.getText().trim();
        String telNo = CountryCodePickerHelper.validateAndFormatContact(restOfNumberTextFieldOfMedication.getText().trim());
        String category = categoryOfMedicationComboBox.getValue(); // Assuming it's a ComboBox

        // Get the selected response from checkboxes directly
        String responseType = getSelectedCheckBoxResponse();

        return new MedicationHistory(
                0, // ID will be generated by the database
                selectedCattleId,
                dosage,
                frequency,
                dateTaken,
                nextSchedule,
                type,
                administeredBy,
                telNo,
                category,
                responseType
        );
    }

    private String getSelectedCheckBoxResponse() {
        boolean isNegativeResponse = negativeCheckBox.isSelected();
        boolean isPositiveResponse = positiveCheckBox.isSelected();

        if (isNegativeResponse) {
            return "Negative";
        } else if (isPositiveResponse) {
            return "Positive";
        } else {
            return null; // Return null if none is selected
        }
    }



    private void saveMedicationHistoryToDatabase(MedicationHistory medicationHistory) throws DatabaseException {
        try {
            // Insert details into the database
            MedicationHistoryDAO.insertMedicationHistory(medicationHistory);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save medication details: " + e.getMessage(), e);
        }
    }


    // UPDATE MEDICATION RECORD DETAILS
    private void updateMedicationRecord() {
        try {
            // Validate the input fields; exception will be thrown if validation fails
            validateMedicationInputFields();

            // Proceed with creating the updated medication object
            MedicationHistory updatedHistory = createMedicationHistory();

            // Retrieve and set the original ID from the map
            String originalIdString = originalMedicationValuesMap.get("id");
            if (originalIdString != null) {
                int originalId = Integer.parseInt(originalIdString);
                updatedHistory.setId(originalId);
            } else {
                throw new IllegalStateException("Original ID not found in the values map.");
            }

            // Update the record in the database
            MedicationHistoryDAO.updateMedicationHistory(updatedHistory);
            loadMedicationDataIntoTableView();

            // Show the success message
            showAlert(AlertType.INFORMATION, "Success", "Medication record updated successfully.");
        } catch (ValidationException e) {
            // Handle validation errors
            showAlert(AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (SQLException e) {
            // Handle database errors
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            // Handle other errors like missing ID
            showAlert(AlertType.ERROR, "Error", e.getMessage());
        }
    }


    //DELETE MEDICATION RECORD

    private void deleteMedicationRecord() {
        // Get the selected medication record from the TableView
        selectedMedicationRecord = medicationHistoryTableView.getSelectionModel().getSelectedItem();

        if (selectedMedicationRecord == null) {
            return; // No record selected, nothing to do
        }

        // Confirm the deletion from the user before proceeding
        if (!confirmMedicationRecordDeletion("Record ID: " + selectedMedicationRecord.id())) {
            return; // User canceled the deletion
        }

        try {
            // Delete the selected medication record
            deleteMedicationRecord(selectedMedicationRecord);
            // Show confirmation that the record was deleted
            showAlert(Alert.AlertType.INFORMATION, "Record Deleted", "The selected medication record has been successfully deleted.");
        } finally {
            // Reload and refresh the data in the TableView after deletion
            loadMedicationDataIntoTableView();
        }
    }
    private void deleteMedicationRecord(MedicationRecord record) {
        int cattleId = selectedCattleId; // Assuming selectedCattleId is set elsewhere in your controller
        int recordId = record.id();

        try {
            // Call the DAO to delete the medication record by cattleId and recordId
            MedicationHistoryDAO.deleteMedicationHistoryByCattleIdAndId(cattleId, recordId);
        } catch (SQLException e) {
            // Handle any SQL exception by showing an alert
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete the medication record. Please try again.");
        }
    }


    private boolean confirmMedicationRecordDeletion(String contentText) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this Medication record?");
        confirmationAlert.setContentText(contentText + "\nThis action cannot be undone.");

        Optional<ButtonType> confirmation = confirmationAlert.showAndWait();
        return confirmation.isPresent() && confirmation.get() == ButtonType.OK;
    }


    //RESTORE MEDICATION DETAILS
    private void restoreOriginalMedicationData() {
        // Restore original data to fields
        dateTakenOfMedicationDatePicker.setValue(LocalDate.parse(originalMedicationValuesMap.get("dateTaken")));
        dosageOfMedicationTextField.setText(originalMedicationValuesMap.get("dosage"));
        frequencyOfMedicationTextField.setText(originalMedicationValuesMap.get("frequency"));
        administerOfMedicationTextField.setText(originalMedicationValuesMap.get("administeredBy"));
        typeOfMedicationTextField.setText(originalMedicationValuesMap.get("type"));
        categoryOfMedicationComboBox.setValue(originalMedicationValuesMap.get("category"));

        // Construct contact details from the original values map
        String callingCode = originalMedicationValuesMap.get("callingCode");
        String restOfNumber = originalMedicationValuesMap.get("restOfNumber");
        String contactDetails = String.format("(%s) %s", callingCode, restOfNumber);

        // Restore contact details using the processMedicationContactDetails method
        processMedicationContactDetails(contactDetails);

        // Restore the response type based on original values
        String responseType = originalMedicationValuesMap.get("responseType");
        if ("Positive".equals(responseType)) {
            positiveCheckBox.setSelected(true);
            negativeCheckBox.setSelected(false);
        } else if ("Negative".equals(responseType)) {
            positiveCheckBox.setSelected(false);
            negativeCheckBox.setSelected(true);
        } else {
            positiveCheckBox.setSelected(false);
            negativeCheckBox.setSelected(false);
        }

        // Reset the modification state
        isModifiedMedication = false; // Resetting the modification state since a new record is loaded
    }



    //CLEAR MEDICATION DETAILS
    @FXML
    private void clearAllMedicationRecord() {
        clearAllMedicationFields();
    }
    // Clear all input fields
    private void clearAllMedicationFields() {
        medicationHistoryTableView.getSelectionModel().clearSelection();

        selectedMedicationRecord = null;

        // Check if the originalMedicationValuesMap is not empty
        if (! originalMedicationValuesMap.isEmpty()) {
            // Clear the entire map
            originalMedicationValuesMap.clear();
        }
        // Clear all input fields
        dosageOfMedicationTextField.clear();
        frequencyOfMedicationTextField.clear();
        typeOfMedicationTextField.clear();
        administerOfMedicationTextField.clear();
        dateTakenOfMedicationDatePicker.setValue(null);
        nextScheduleOfMedicationTextField.setValue(null);
        categoryOfMedicationComboBox.setValue(null);
        countryCodeTextFieldOfMedication.clear();
        restOfNumberTextFieldOfMedication.clear();
        countryComboBoxOfMedication.setValue(null);

        // Reset checkboxes
        negativeCheckBox.setSelected(false);
        positiveCheckBox.setSelected(false);

        // Clear any phone number fields if applicable
        medicationCountryCodePickerHelper.clearPhoneNumberFields();

        // Reset the modified flag
        isModifiedMedication = false; // Assuming isModified tracks if there were changes to the input fields
    }























    // HEALTH CHECKUP HISTORY
    private void loadHealthCheckupDataIntoTableView() {
        clearAllHealthCheckupFields();

        if (selectedCattleId == 0) {  // Ensure you have a valid selectedCattleId
            // Show an alert if no cattle is selected
            showAlert(AlertType.WARNING, "No Cattle Selected", "Please select a cattle before proceeding.");
            return;
        }

        List<HealthCheckupHistory> healthCheckupHistories;
        try {
            healthCheckupHistories = HealthCheckupHistoryDAO.getHealthCheckupHistoriesByCattleId(selectedCattleId);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to load health checkup history. Please try again.");
            return;
        }

        ObservableList<HealthCheckupRecord> records = getHealthCheckupRecords(healthCheckupHistories);
        healthCheckupTableView.setItems(records);
    }

    private ObservableList<HealthCheckupRecord> getHealthCheckupRecords(List<HealthCheckupHistory> healthCheckupHistories) {
        ObservableList<HealthCheckupRecord> records = FXCollections.observableArrayList();

        for (HealthCheckupHistory history : healthCheckupHistories) {
            records.add(new HealthCheckupRecord(
                    history.getId(),
                    history.getCheckupDate(),
                    history.getTemperature(),
                    history.getHeartRate(),
                    history.getRespiratoryRate(),
                    history.getBloodPressure(),
                    history.getBehavioralObservations(),
                    history.getPhysicalExaminationFindings(),
                    history.getHealthIssues(),
                    history.getSpecificObservations(),
                    history.getCheckupNotes(),
                    history.getChronicConditions(),
                    history.getCreatedAt()
            ));
        }

        return records;
    }


    private void setupHealthCheckupTableColumns() {
        idHealthCheckupColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));
        checkupDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().checkupDate()));
        // Convert LocalDateTime to String for the createdAt column
        createdATColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))); // Format as desired


        temperatureColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().temperature()));
        heartRateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().heartRate()));
        bloodPressureColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().bloodPressure()));
        respiratoryRateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().respiratoryRate()));
        behavioralObservationsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().behavioralObservations()));
        physicalExaminationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().physicalExaminationFindings()));
        healthIssuesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().healthIssues()));
        specificObservationsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().specificObservations()));
        checkupNotesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().checkupNotes()));
        chronicConditionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().chronicConditions()));
    }



    private void populateHealthCheckupFields(HealthCheckupRecord record) {
        // Clear all fields before populating with new data
        clearAllHealthCheckupFields();

        // Populate the fields with values from the HealthCheckupRecord
        checkupDatePicker.setValue(record.checkupDate());
        createdATTextField.setText(record.checkupDate().toString());

        // Vital Signs
        temperatureTextField.setText(record.temperature());
        heartRateTextField.setText(record.heartRate());
        respiratoryRateTextField.setText(record.respiratoryRate());
        bloodPressureTextField.setText(record.bloodPressure());

        // Observations and Findings
        behavioralObservationsTextArea.setText(record.behavioralObservations());
        physicalExaminationTextArea.setText(record.physicalExaminationFindings());
        healthIssuesTextArea.setText(record.healthIssues());
        specificObservationsTextArea.setText(record.specificObservations());
        checkupNotesTextArea.setText(record.checkupNotes());
        chronicConditionsTextArea.setText(record.chronicConditions());

        // Retrieve follow-up recommendations using the health checkup ID
        try {
            List<FollowUpRecommendation> recommendations = FollowUpRecommendationDAO.getFollowUpRecommendationsByHealthCheckupId(record.id());
            setCheckboxStates(recommendations);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception appropriately (e.g., show error message)
        }
        // Store original checkbox states for later comparison
        storeOriginalRecommendations();

        // Populate the original values map for change tracking
        populateOriginalHealthCheckupValuesMap(record);

        // Reset the modification state if applicable
        isModifiedHealthCheckup = false;
    }

    private void setCheckboxStates(List<FollowUpRecommendation> recommendations) {
        // Clear previous checkbox states
        titledPaneCheckboxMap.forEach((titledPane, checkboxes) -> {
            for (CheckBox checkBox : checkboxes) {
                checkBox.setSelected(false); // Reset all to uncheck first
            }
        });

        // Set checkbox states based on recommendations
        for (FollowUpRecommendation recommendation : recommendations) {
            String recommendationText = recommendation.getRecommendation().trim();

            // Check each checkbox in the map and set it if it matches the recommendation text
            titledPaneCheckboxMap.forEach((titledPane, checkboxes) -> {
                for (CheckBox checkBox : checkboxes) {
                    if (checkBox.getText().trim().equals(recommendationText)) {
                        checkBox.setSelected(true); // Set to check if it matches
                        break; // No need to continue if we found the match
                    }
                }
            });
        }
    }

    private void storeOriginalRecommendations() {
        originalRecommendationsMap.clear();
        titledPaneCheckboxMap.forEach((titledPane, checkboxes) ->
                checkboxes.forEach(checkBox ->
                        originalRecommendationsMap.put(checkBox.getText().trim(), checkBox.isSelected())
                )
        );
    }

    private void mapTitledPaneToCheckBoxes() {
        titledPaneCheckboxMap.put(healthAssessmentsTitledPane, List.of(
                regularHealthAssessmentsCheckBox,
                monitoringTemperatureCheckBox,
                monitoringDiseasesCheckBox,
                routineBloodTestsCheckBox,
                fecalTestingCheckBox,
                skinCoatInspectionsCheckBox,
                monitoringBloatCheckBox,
                monitoringAnaplasmosisCheckBox,
                monitoringJohnesDiseaseCheckBox,
                behavioralAssessmentsCheckBox
        ));

        titledPaneCheckboxMap.put(preventiveCareTitledPane, List.of(
                vaccinationProtocolsCheckBox,
                deWormingTreatmentsCheckBox,
                parasiteControlMeasuresCheckBox,
                bioSecurityPracticesCheckBox,
                breedingSoundnessExamsCheckBox,
                colostrumManagementCheckBox,
                stressReductionTechniquesCheckBox,
                bodyConditionScoringCheckBox,
                nutritionalSupplementationCheckBox
        ));

        titledPaneCheckboxMap.put(nutritionalManagementTitledPane, List.of(
                nutritionalEvaluationCheckBox,
                monitoringWaterQualityCheckBox,
                monitoringMilkProductionCheckBox,
                regularWeightMonitoringCheckBox,
                consultationNutritionalRequirementsCheckBox
        ));

        titledPaneCheckboxMap.put(physicalHealthMaintenanceTitledPane, List.of(
                hoofHealthAssessmentsCheckBox,
                footTrimmingScheduleCheckBox,
                dentalCheckUpsCheckBox,
                monitoringLamenessIssuesCheckBox,
                overallBodyConditionAssessmentCheckBox
        ));

        titledPaneCheckboxMap.put(managementPracticesTitledPane, List.of(
                calvingManagementProtocolsCheckBox,
                cullingDecisionsCheckBox,
                herdImmunityAssessmentCheckBox,
                trainingStaffHandlingTechniquesCheckBox,
                implementingQuarantineProtocolsCheckBox,
                recordKeepingHealthInterventionsCheckBox
        ));

        titledPaneCheckboxMap.put(environmentalHousingAssessmentsTitledPane, List.of(
                livingConditionsAssessmentCheckBox,
                flyInsectManagementCheckBox,
                adequateExerciseCheckBox,
                environmentalToxinsMonitoringCheckBox
        ));

        titledPaneCheckboxMap.put(monitoringReviewTitledPane, List.of(
                vaccinationScheduleReviewCheckBox,
                regularWeightChecksCheckBox,
                educationZoonoticDiseasesCheckBox,
                postMortemExaminationsCheckBox,
                recordKeepingVeterinaryInterventionsCheckBox
        ));

        titledPaneCheckboxMap.put(additionalRecommendationsTitledPane, List.of(
                monitoringCalfHealthCheckBox,
                limitingStressFactorsCheckBox,
                evaluationBreedingPracticesCheckBox,
                implementationHerdHealthPlansCheckBox,
                observationSocialBehaviorCheckBox,
                regularReproductivePerformanceAssessmentCheckBox
        ));
    }

    private void addCheckboxListeners() {
        for (Map.Entry<TitledPane, List<CheckBox>> entry : titledPaneCheckboxMap.entrySet()) {
            for (CheckBox checkBox : entry.getValue()) {
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> checkForCheckboxChanges(checkBox.getText().trim(), newValue));
            }
        }
    }

    // Check for changes in checkbox selections
    private void checkForCheckboxChanges(String recommendationText, boolean isSelected) {
        Boolean originalSelected = originalRecommendationsMap.get(recommendationText);

        // If the original state exists, and it's different from the new state, mark as modified
        if (originalSelected != null && originalSelected != isSelected) {
            isModifiedHealthCheckup = true; // Mark as modified if the selection has changed
        } else {
            // If the original state is the same as the new state, check if all checkboxes match the original states
            isModifiedHealthCheckup = checkIfAnyCheckboxModified();
        }
    }

    // Helper method to check if any checkbox state differs from its original state
    private boolean checkIfAnyCheckboxModified() {
        for (Map.Entry<TitledPane, List<CheckBox>> entry : titledPaneCheckboxMap.entrySet()) {
            for (CheckBox checkBox : entry.getValue()) {
                Boolean originalSelected = originalRecommendationsMap.get(checkBox.getText().trim());
                if (originalSelected != null && originalSelected != checkBox.isSelected()) {
                    return true; // At least one checkbox has been modified
                }
            }
        }
        return false; // No modifications found
    }


    private List<FollowUpRecommendation> getSelectedFollowUpRecommendations() {
        List<FollowUpRecommendation> recommendations = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now(); // Get current date and time

        // Iterate through TitledPane entries
        for (Map.Entry<TitledPane, List<CheckBox>> entry : titledPaneCheckboxMap.entrySet()) {
            List<CheckBox> checkboxes = entry.getValue();

            // Check each checkbox in the TitledPane
            for (CheckBox checkbox : checkboxes) {
                if (checkbox.isSelected()) {
                    String recommendationText = checkbox.getText().trim();
                    FollowUpRecommendation recommendation = new FollowUpRecommendation(
                            0, // ID (this will be generated by the database)
                            0, // This will be updated later when saved
                            recommendationText,
                            createdAt
                    );

                    recommendations.add(recommendation);
                }
            }
        }

        return recommendations;
    }

    // Method to clear all fields related to health checkup
    private void clearAllHealthCheckupFields() {
        // Clear selections in the health checkup table view
        healthCheckupTableView.getSelectionModel().clearSelection();

        selectedHealthCheckupRecord = null;

        // Clear input fields related to health checkup
        checkupDatePicker.setValue(null);
        createdATTextField.clear();

        // Vital Signs
        temperatureTextField.clear();
        heartRateTextField.clear();
        respiratoryRateTextField.clear();
        bloodPressureTextField.clear();

        // Observations and Findings
        behavioralObservationsTextArea.clear();
        physicalExaminationTextArea.clear();
        healthIssuesTextArea.clear();
        specificObservationsTextArea.clear();
        checkupNotesTextArea.clear();
        chronicConditionsTextArea.clear();

        // Reset all checkboxes to unchecked state
        resetAllCheckboxes();

        // Clear any maps or flags related to the health checkup
        originalHealthCheckupValuesMap.clear(); // Clear the original values map if it exists
        isModifiedHealthCheckup = false; // Reset the modification flag
    }

    // Method to reset all checkboxes to an unchecked state
    private void resetAllCheckboxes() {
        titledPaneCheckboxMap.forEach((titledPane, checkboxes) -> {
            for (CheckBox checkBox : checkboxes) {
                checkBox.setSelected(false); // Reset all checkboxes to uncheck
            }
        });
    }

    private boolean validateHealthCheckupInputFields() throws ValidationException {
        // Validate all fields and apply error effects as needed
        InputFieldsValidationHelper.validateField(selectedCattleId == 0, "Select a Cattle from the Herds table", null);
        InputFieldsValidationHelper.validateField(temperatureTextField.getText().trim().isEmpty(), "Temperature is required.", temperatureTextField);
        InputFieldsValidationHelper.validateField(checkupDatePicker.getValue() == null, "Date of Checkup is required.", checkupDatePicker);

        // Validate behavioral observations
        InputFieldsValidationHelper.validateField(behavioralObservationsTextArea.getText().trim().isEmpty(), "Behavioral observations are required.", behavioralObservationsTextArea);
        InputFieldsValidationHelper.validateField(physicalExaminationTextArea.getText().trim().isEmpty(), "Physical examination findings are required.", physicalExaminationTextArea);
        InputFieldsValidationHelper.validateField(healthIssuesTextArea.getText().trim().isEmpty(), "Health issues are required.", healthIssuesTextArea);
        InputFieldsValidationHelper.validateField(specificObservationsTextArea.getText().trim().isEmpty(), "Specific observations are required.", specificObservationsTextArea);
        InputFieldsValidationHelper.validateField(checkupNotesTextArea.getText().trim().isEmpty(), "Checkup notes are required.", checkupNotesTextArea);
        InputFieldsValidationHelper.validateField(chronicConditionsTextArea.getText().trim().isEmpty(), "Chronic conditions are required.", chronicConditionsTextArea);

        // If no exception was thrown, return true (indicating all fields are valid)
        return true;
    }


    // Populate the original values map for change tracking
    private void populateOriginalHealthCheckupValuesMap(HealthCheckupRecord record) {
        originalHealthCheckupValuesMap.put("id", String.valueOf(record.id()));
        originalHealthCheckupValuesMap.put("checkupDate", record.checkupDate().toString());
        originalHealthCheckupValuesMap.put("temperature", record.temperature());
        originalHealthCheckupValuesMap.put("heartRate", record.heartRate());
        originalHealthCheckupValuesMap.put("respiratoryRate", record.respiratoryRate());
        originalHealthCheckupValuesMap.put("bloodPressure", record.bloodPressure());
        originalHealthCheckupValuesMap.put("behavioralObservations", record.behavioralObservations());
        originalHealthCheckupValuesMap.put("physicalExaminationFindings", record.physicalExaminationFindings());
        originalHealthCheckupValuesMap.put("healthIssues", record.healthIssues());
        originalHealthCheckupValuesMap.put("specificObservations", record.specificObservations());
        originalHealthCheckupValuesMap.put("checkupNotes", record.checkupNotes());
        originalHealthCheckupValuesMap.put("chronicConditions", record.chronicConditions());
    }

    // Set up listeners for changes in input fields related to health checkup
    private void setupHealthCheckupChangeListeners() {
        checkupDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("checkupDate", newValue != null ? newValue.toString() : null));
        temperatureTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("temperature", newValue != null ? newValue : ""));
        heartRateTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("heartRate", newValue != null ? newValue : ""));
        respiratoryRateTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("respiratoryRate", newValue != null ? newValue : ""));
        bloodPressureTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("bloodPressure", newValue != null ? newValue : ""));

        behavioralObservationsTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("behavioralObservations", newValue != null ? newValue : ""));
        physicalExaminationTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("physicalExaminationFindings", newValue != null ? newValue : ""));
        healthIssuesTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("healthIssues", newValue != null ? newValue : ""));
        specificObservationsTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("specificObservations", newValue != null ? newValue : ""));
        checkupNotesTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("checkupNotes", newValue != null ? newValue : ""));
        chronicConditionsTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInHealthCheckupData("chronicConditions", newValue != null ? newValue : ""));
    }

    // Check for changes in health checkup data and update modification state
    private void checkForChangesInHealthCheckupData(String fieldName, String newValue) {
        String originalValue = originalHealthCheckupValuesMap.get(fieldName);

        if (originalValue != null && !originalValue.equals(newValue)) {
            isModifiedHealthCheckup = true; // Mark changes if the value differs
        } else if (originalValue != null) {
            isModifiedHealthCheckup = false; // Reset the modification flag if values match
        }
    }




    // MODIFY DETAILS BUTTON
    @FXML
    public void modifyHealthCheckupRecord() {
        selectedHealthCheckupRecord = healthCheckupTableView.getSelectionModel().getSelectedItem();
        if (selectedHealthCheckupRecord == null) {
            // No record selected, show saving options
            showHealthCheckupSaveDialog();
        } else {
            showHealthCheckupUpdateDialog();
        }
    }

    private void showHealthCheckupSaveDialog() {
        List<ButtonType> buttonTypes = Arrays.asList(
                new ButtonType("Save"),
                new ButtonType("Clear Fields"),
                new ButtonType("Cancel")
        );

        List<Consumer<Void>> actions = Arrays.asList(
                v -> saveNewHealthCheckupRecord(),
                v -> clearAllHealthCheckupFields(),
                v -> {} // Cancel does nothing as it's handled automatically
        );

        DialogHelper.showConfirmationDialog("New Record", "Choose an Action?", buttonTypes, actions);
    }

    private void showHealthCheckupUpdateDialog() {
        DialogHelper.showUpdateDialog(
                "Modify Health Checkup Record",
                isModifiedHealthCheckup,
                v -> updateHealthCheckupRecord(),
                v -> restoreOriginalHealthCheckupData(),
                v -> deleteHealthCheckupRecord(),
                v -> {}
        );
    }

    // SAVE HEALTH CHECKUP RECORD
    private void saveNewHealthCheckupRecord() {
        try {
            if (!validateHealthCheckupInputFields()) return;

            HealthCheckupHistory healthCheckupHistory = createHealthCheckupHistory();
            saveHealthCheckupHistoryToDatabase(healthCheckupHistory);
            loadHealthCheckupDataIntoTableView();
            showAlert(AlertType.INFORMATION, "Success", "Health checkup details saved successfully.");
        } catch (ValidationException e) {
            showAlert(AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            showAlert(AlertType.ERROR, "Database Error", e.getMessage());
        }
    }
    // Create HealthCheckupHistory object from input fields
    private HealthCheckupHistory createHealthCheckupHistory() throws ValidationException {
        LocalDate checkupDate = checkupDatePicker.getValue();
        String temperature = temperatureTextField.getText().trim();
        String heartRate = heartRateTextField.getText().trim();
        String respiratoryRate = respiratoryRateTextField.getText().trim();
        String bloodPressure = bloodPressureTextField.getText().trim();

        String behavioralObservations = behavioralObservationsTextArea.getText().trim();
        String physicalExaminationFindings = physicalExaminationTextArea.getText().trim();
        String healthIssues = healthIssuesTextArea.getText().trim();
        String specificObservations = specificObservationsTextArea.getText().trim();
        String checkupNotes = checkupNotesTextArea.getText().trim();
        String chronicConditions = chronicConditionsTextArea.getText().trim();

        LocalDateTime createdAt = LocalDateTime.now(); // Get the current date and time

        List<FollowUpRecommendation> selectedRecommendations = getSelectedFollowUpRecommendations();

        // Create HealthCheckupHistory object
        return new HealthCheckupHistory(
                0, // ID will be generated by the database
                selectedCattleId, // Assuming you have selected cattle ID accessible here
                checkupDate,
                temperature,
                heartRate,
                respiratoryRate,
                bloodPressure,
                behavioralObservations,
                physicalExaminationFindings,
                healthIssues,
                specificObservations,
                checkupNotes,
                chronicConditions,
                createdAt,
                selectedRecommendations // Pass the selected recommendations
        );
    }



    // Save HealthCheckupHistory to the database
    private void saveHealthCheckupHistoryToDatabase(HealthCheckupHistory healthCheckupHistory) throws DatabaseException {
        try {
            // Insert details into the database
            HealthCheckupHistoryDAO.insertHealthCheckupHistory(healthCheckupHistory);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save health checkup details: " + e.getMessage(), e);
        }
    }




    // UPDATE HEALTH CHECKUP RECORD DETAILS
    private void updateHealthCheckupRecord() {
        try {
            // Validate the input fields; exception will be thrown if validation fails
            validateHealthCheckupInputFields();

            // Proceed with creating the updated health checkup object
            HealthCheckupHistory updatedHistory = createHealthCheckupHistory();

            // Retrieve and set the original ID from the map
            String originalIdString = originalHealthCheckupValuesMap.get("id");
            if (originalIdString != null) {
                int originalId = Integer.parseInt(originalIdString);
                updatedHistory.setId(originalId);
            } else {
                throw new IllegalStateException("Original ID not found in the values map.");
            }

            // Update the record in the database
            HealthCheckupHistoryDAO.updateHealthCheckupHistory(updatedHistory);
            loadHealthCheckupDataIntoTableView();

            // Show the success message
            showAlert(AlertType.INFORMATION, "Success", "Health checkup record updated successfully.");
        } catch (ValidationException e) {
            // Handle validation errors
            showAlert(AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (SQLException e) {
            // Handle database errors
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            // Handle other errors like missing ID
            showAlert(AlertType.ERROR, "Error", e.getMessage());
        }
    }






    // DELETE HEALTH CHECKUP RECORD
    private void deleteHealthCheckupRecord() {
        // Get the selected health checkup record from the TableView
        selectedHealthCheckupRecord = healthCheckupTableView.getSelectionModel().getSelectedItem();

        if (selectedHealthCheckupRecord == null) {
            return; // No record selected, nothing to do
        }

        // Confirm the deletion from the user before proceeding
        if (!confirmHealthCheckupRecordDeletion("Record ID: " + selectedHealthCheckupRecord.id())) {
            return; // User canceled the deletion
        }

        try {
            // Delete the selected health checkup record
            deleteHealthCheckupRecord(selectedHealthCheckupRecord);
            // Show confirmation that the record was deleted
            showAlert(Alert.AlertType.INFORMATION, "Record Deleted", "The selected health checkup record has been successfully deleted.");
        } finally {
            // Reload and refresh the data in the TableView after deletion
            loadHealthCheckupDataIntoTableView();
        }
    }

    private void deleteHealthCheckupRecord(HealthCheckupRecord record) {
        int cattleId = selectedCattleId; // Assuming selectedCattleId is set elsewhere in your controller
        int recordId = record.id();

        try {
            // Call the DAO to delete the health checkup record by cattleId and recordId
            HealthCheckupHistoryDAO.deleteHealthCheckupHistoryByCattleIdAndId(cattleId, recordId);
        } catch (SQLException e) {
            // Handle any SQL exception by showing an alert
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete the health checkup record. Please try again.");
        }
    }

    private boolean confirmHealthCheckupRecordDeletion(String contentText) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this health checkup record?");
        confirmationAlert.setContentText(contentText + "\nThis action cannot be undone.");

        Optional<ButtonType> confirmation = confirmationAlert.showAndWait();
        return confirmation.isPresent() && confirmation.get() == ButtonType.OK;
    }






    // RESTORE HEALTH CHECKUP DETAILS
    private void restoreOriginalHealthCheckupData() {
        // Restore original data to fields
        checkupDatePicker.setValue(LocalDate.parse(originalHealthCheckupValuesMap.get("checkupDate")));
        temperatureTextField.setText(originalHealthCheckupValuesMap.get("temperature"));
        heartRateTextField.setText(originalHealthCheckupValuesMap.get("heartRate"));
        respiratoryRateTextField.setText(originalHealthCheckupValuesMap.get("respiratoryRate"));
        bloodPressureTextField.setText(originalHealthCheckupValuesMap.get("bloodPressure"));
        behavioralObservationsTextArea.setText(originalHealthCheckupValuesMap.get("behavioralObservations"));
        physicalExaminationTextArea.setText(originalHealthCheckupValuesMap.get("physicalExaminationFindings"));
        healthIssuesTextArea.setText(originalHealthCheckupValuesMap.get("healthIssues"));
        specificObservationsTextArea.setText(originalHealthCheckupValuesMap.get("specificObservations"));
        checkupNotesTextArea.setText(originalHealthCheckupValuesMap.get("checkupNotes"));
        chronicConditionsTextArea.setText(originalHealthCheckupValuesMap.get("chronicConditions"));


// Restore checkbox states from the originalRecommendationsMap
        titledPaneCheckboxMap.forEach((titledPane, checkboxes) -> {
            for (CheckBox checkBox : checkboxes) {
                String recommendationText = checkBox.getText().trim();
                Boolean originalState = originalRecommendationsMap.get(recommendationText);

                // Restore the checkbox state to the original state if present in the map
                if (originalState != null) {
                    checkBox.setSelected(originalState);
                }
            }
        });

        // Reset the modification state
        isModifiedHealthCheckup = false; // Resetting the modification state since a new record is loaded
    }


    @FXML private void clearHealthCheckupRecord() {
        clearAllHealthCheckupFields();
    }
}
