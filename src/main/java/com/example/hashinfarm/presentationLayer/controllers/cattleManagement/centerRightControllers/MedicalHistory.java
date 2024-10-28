package com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.businessLogic.services.SelectedCattleManager;
import com.example.hashinfarm.data.DAOs.*;
import com.example.hashinfarm.data.records.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.function.Consumer;

import com.example.hashinfarm.helpers.CountryCodePickerHelper;
import com.example.hashinfarm.helpers.HealthNoteItemListCell;
import com.example.hashinfarm.helpers.HealthRecommendationItemListCell;
import com.example.hashinfarm.helpers.MedicationListItem;
import com.example.hashinfarm.utils.date.DateUtil;
import com.example.hashinfarm.utils.exceptions.DatabaseException;
import com.example.hashinfarm.utils.exceptions.ValidationException;
import com.example.hashinfarm.data.DTOs.*;

import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.utils.logging.DialogHelper;
import com.example.hashinfarm.utils.validation.InputFieldsValidation;
import com.example.hashinfarm.utils.validation.MeasurementType;
import com.example.hashinfarm.utils.validation.UnitsTextField;
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



public class MedicalHistory {

    private int selectedCattleId = 0;
    private final Map<String, String> originalDewormingValuesMap = new HashMap<>();
    private final Map<String, String> originalMedicationValuesMap = new HashMap<>();
    private final Map<String, String> originalHealthCheckupValuesMap = new HashMap<>();
    private final Map<String, String> originalInjuryValuesMap = new HashMap<>();

    private boolean isModifiedDeworming = false;
    private boolean isModifiedMedication = false;
    private boolean isModifiedHealthCheckup =false;
    private boolean isModifiedInjury =false;

    private DewormingRecord selectedDewormingRecord;
    private MedicationRecord selectedMedicationRecord;
    private HealthCheckupRecord selectedHealthCheckupRecord;
    private InjuryRecord selectedInjuryRecord;

    private CountryCodePickerHelper dewormingCountryCodePickerHelper;
    private CountryCodePickerHelper medicationCountryCodePickerHelper;

    private final Map<TitledPane, List<CheckBox>> titledPaneCheckboxMap = new HashMap<>();
    private final Map<String, Boolean> originalRecommendationsMap = new HashMap<>();


    @FXML private ImageView imageViewOfDewormingHistory,imageViewOfMedication;
    @FXML private ComboBox<Country> countryComboBoxOfDewormingHistory, countryComboBoxOfMedication;
    @FXML private TextField restOfNumberOfDewormingHistoryTextField, countryCodeOfDewormingHistoryTextField,countryCodeTextFieldOfMedication, restOfNumberTextFieldOfMedication;
    @FXML private Button modifyDewormingRecordButton,clearAllDewormingDetailsButton,modifyMedicationRecordButton,clearAllMedicationRecordButton,modifyHealthCheckupRecordButton,clearHealthCheckupRecordButton,modifyInjuryRecordButton,clearInjuryRecordButton;

    @FXML private ListView<HealthRecommendationItem> healthRecommendationsListView;
    @FXML private ListView<HealthNoteItem> healthNotesListView;

    // DEWORMING PART
    @FXML private TableView<DewormingRecord> dewormingHistoryTableView;
    @FXML private TableColumn<DewormingRecord, LocalDate> dewormingHistoryDateColumn;
    @FXML private TableColumn<DewormingRecord, String> dewormerTypeColumn, dosageColumn, administeredByColumn, routeOfAdministrationColumn, weightAtTimeColumn, contactDetailsColumn,manufacturerDetailsColumn;

    @FXML private TextField cattleNameTextField, dosageOfDewormingTextField, weightAtTimeOfDewormingTextField, administerOfDewormingTextField;
    @FXML private TextArea dewormerTypeTextArea,manufacturerDetailsTextArea;
    @FXML private ComboBox<String>  routeOfAdministrationCombo;
    @FXML private DatePicker  dewormingDatePicker;


    // MEDICATION PART
    @FXML private TableView<MedicationRecord> medicationHistoryTableView;
    @FXML private TableColumn<MedicationRecord, LocalDate> dateTakenOfMedicationColumn,nextScheduleOfMedicationColumn;
    @FXML private TableColumn<MedicationRecord, String> dosageOfMedicationColumn, frequencyOfMedicationColumn,  administerOfMedicationColumn, telNoOfMedicationColumn, categoryOfMedicationColumn;

    @FXML private TextField dosageOfMedicationTextField, frequencyOfMedicationTextField,  administerOfMedicationTextField;
    @FXML private DatePicker dateTakenOfMedicationDatePicker, nextScheduleOfMedicationDatePicker;
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


    //INJURY
    @FXML  private TableView<InjuryRecord> injuryTableView;
    @FXML  private TableColumn<InjuryRecord, Integer> idColumn;
    @FXML  private TableColumn<InjuryRecord, LocalDate> dateOfOccurrenceColumn;
    @FXML  private TableColumn<InjuryRecord, BigDecimal> medicationCostColumn;
    @FXML  private TableColumn<InjuryRecord, String> typeOfInjuryColumn, specificBodyPartColumn, severityColumn, causeOfInjuryColumn, firstAidMeasuresColumn, followUpTreatmentTypeColumn, monitoringInstructionsColumn,
            scheduledProceduresColumn, followUpMedicationsColumn;



    @FXML private DatePicker dateOfOccurrenceDatePicker;
    @FXML private ComboBox<String> typeOfInjuryComboBox, specificBodyPartComboBox, severityComboBox, followUpTreatmentTypeComboBox;
    @FXML private TextArea causeOfInjuryTextArea, firstAidMeasuresTextArea, monitoringInstructionsTextArea, scheduledProceduresTextArea, followUpMedicationsTextArea;
    @FXML private TextField medicalAdminNameTextField, medicalAdminContactTextField, medicationCostTextField, medicalCategoryTextField;
    @FXML private ListView<MedicationListItem> medicationAdministeredListView;

    // Initialize the ToggleGroup for radio buttons
    ToggleGroup toggleGroup = new ToggleGroup();

    public void initialize() {
        try {
            initializeSelectedCattleManager();
            initializeDatePickers();
            initializeImageView();
            initializeCountryCodePicker();
            initializeTextFields();

            setupDewormingTableColumns();
            setupMedicationTableColumns();
            setupHealthCheckupTableColumns();
            setupInjuryTableColumns();

            setupDewormingChangeListeners();
            setupMedicationChangeListeners();
            setupHealthCheckupChangeListeners();
            setupInjuryChangeListeners();

            initializeSelectionListener();

            mapTitledPaneToCheckBoxes();
            addCheckboxListeners();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Initialization Error", "An error occurred during initialization: " + e.getMessage());
            e.printStackTrace(); // For logging purposes
        }
    }
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }


    private void initializeDatePickers() {
        try {
            // Format each DatePicker using your existing utility
            DateUtil.datePickerFormat(dewormingDatePicker);
            DateUtil.datePickerFormat(dateTakenOfMedicationDatePicker);
            DateUtil.datePickerFormat(nextScheduleOfMedicationDatePicker);
            DateUtil.datePickerFormat(checkupDatePicker);
            DateUtil.datePickerFormat(dateOfOccurrenceDatePicker);
            // Initialize each DatePicker with the appropriate date disabling
            setDatePickerRestrictions(dewormingDatePicker, false); // Disable future dates
            setDatePickerRestrictions(dateTakenOfMedicationDatePicker, false); // Disable future dates
            setDatePickerRestrictions(nextScheduleOfMedicationDatePicker, true); // Disable past dates
            setDatePickerRestrictions(checkupDatePicker, false); // Disable future dates
            setDatePickerRestrictions(dateOfOccurrenceDatePicker, false);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Date Picker Error", "Failed to initialize date pickers: " + e.getMessage());
        }
    }

    private void setDatePickerRestrictions(DatePicker datePicker, boolean disablePast) {
        datePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    // Disable future dates if disablePast is false; otherwise, disable past dates
                    if ((disablePast && item.isBefore(LocalDate.now())) ||
                            (!disablePast && item.isAfter(LocalDate.now()))) {
                        setDisable(true); // Disable the date
                        setStyle("-fx-background-color: #ffcccc;"); // Set background color for disabled dates
                        setTooltip(new Tooltip("This date is not selectable")); // Optional tooltip
                    }
                }
            }
        });
    }

    private void initializeSelectedCattleManager() {
        try {
            SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();

            // Listener for changes in the entire Cattle object
            selectedCattleManager.selectedCattleProperty().addListener((observable, oldCattle, newCattle) -> {
                if (newCattle != null) {
                    // Update the cattle name text field
                    cattleNameTextField.setText(newCattle.getName());

                    // Load health data only if the selected Cattle ID is valid
                    if (newCattle.getCattleId() > 0) {
                        selectedCattleId = newCattle.getCattleId();
                        loadHealthRecommendations(selectedCattleId);
                        loadHealthCheckupNotes(selectedCattleId);
                        loadDewormingDataIntoTableView();
                        loadMedicationDataIntoTableView();
                        loadHealthCheckupDataIntoTableView();
                        loadInjuryDataIntoTableView();
                        enableActionButtons(true); // Enable buttons for valid cattle
                    } else {
                        enableActionButtons(false); // Disable buttons if cattle ID is invalid
                    }
                } else {
                    // Handle case when no valid cattle is selected
                    cattleNameTextField.clear(); // Clear the name text field
                    enableActionButtons(false); // Disable buttons
                }
            });

            // Initially disable buttons until cattle is selected
            enableActionButtons(false);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Cattle Manager Error", "Failed to initialize cattle manager: " + e.getMessage());
        }
    }


    // Method to enable or disable action buttons
    private void enableActionButtons(boolean enable) {
        modifyDewormingRecordButton.setDisable(!enable);
        clearAllDewormingDetailsButton.setDisable(!enable);
        modifyMedicationRecordButton.setDisable(!enable);
        clearAllMedicationRecordButton.setDisable(!enable);
        modifyHealthCheckupRecordButton.setDisable(!enable);
        clearHealthCheckupRecordButton.setDisable(!enable);
        modifyInjuryRecordButton.setDisable(!enable);
        clearInjuryRecordButton.setDisable(!enable);
    }

    private void initializeImageView() {
        try {
            imageViewOfDewormingHistory.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png"))));
            imageViewOfMedication.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png"))));
        } catch (NullPointerException e) {
            showAlert(AlertType.ERROR, "Image Load Error", "Failed to load placeholder images: " + e.getMessage());
        }
    }

    private void initializeCountryCodePicker() {
        try {
            dewormingCountryCodePickerHelper = new CountryCodePickerHelper(
                    countryComboBoxOfDewormingHistory, countryCodeOfDewormingHistoryTextField, imageViewOfDewormingHistory, restOfNumberOfDewormingHistoryTextField);
            dewormingCountryCodePickerHelper.initialize();

            medicationCountryCodePickerHelper = new CountryCodePickerHelper(
                    countryComboBoxOfMedication, countryCodeTextFieldOfMedication, imageViewOfMedication, restOfNumberTextFieldOfMedication);
            medicationCountryCodePickerHelper.initialize();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Country Code Picker Error", "Failed to initialize country code pickers: " + e.getMessage());
        }
    }

    private void initializeTextFields() {
        try {
            UnitsTextField.initializeTextField(dosageOfDewormingTextField, MeasurementType.DOSAGE);
            UnitsTextField.initializeTextField(weightAtTimeOfDewormingTextField, MeasurementType.WEIGHT);
            UnitsTextField.initializeTextField(temperatureTextField, MeasurementType.TEMPERATURE);
            UnitsTextField.initializeTextField(heartRateTextField, MeasurementType.HEART_RATE);
            UnitsTextField.initializeTextField(respiratoryRateTextField, MeasurementType.RESPIRATORY_RATE);
            UnitsTextField.initializeTextField(bloodPressureTextField, MeasurementType.BLOOD_PRESSURE);

            UnitsTextField.addValidationListener(dosageOfDewormingTextField, MeasurementType.DOSAGE,
                    text -> UnitsTextField.isValidMeasurement(text, MeasurementType.DOSAGE));
            UnitsTextField.addValidationListener(weightAtTimeOfDewormingTextField, MeasurementType.WEIGHT,
                    text -> UnitsTextField.isValidMeasurement(text, MeasurementType.WEIGHT));
            UnitsTextField.addValidationListener(temperatureTextField, MeasurementType.TEMPERATURE,
                    text -> UnitsTextField.isValidMeasurement(text, MeasurementType.TEMPERATURE));
            UnitsTextField.addValidationListener(heartRateTextField, MeasurementType.HEART_RATE,
                    text -> UnitsTextField.isValidMeasurement(text, MeasurementType.HEART_RATE));
            UnitsTextField.addValidationListener(respiratoryRateTextField, MeasurementType.RESPIRATORY_RATE,
                    text -> UnitsTextField.isValidMeasurement(text, MeasurementType.RESPIRATORY_RATE));
            UnitsTextField.addValidationListener(bloodPressureTextField, MeasurementType.BLOOD_PRESSURE,
                    text -> UnitsTextField.isValidMeasurement(text, MeasurementType.BLOOD_PRESSURE));
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Text Field Error", "Failed to initialize text fields: " + e.getMessage());
        }
    }


    private void initializeSelectionListener() {
        try {
            dewormingHistoryTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    populateDewormingHistoryFields(newValue);
                    selectedDewormingRecord = newValue;
                } else {
                    clearAllDewormingFields();
                }
            });

            medicationHistoryTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    populateMedicationHistoryFields(newValue);
                    selectedMedicationRecord = newValue;
                } else {
                    clearAllMedicationFields();
                }
            });

            healthCheckupTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    populateHealthCheckupFields(newValue);
                    selectedHealthCheckupRecord = newValue;
                } else {
                    clearAllHealthCheckupFields();
                }
            });

            injuryTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    populateInjuryFields(newValue);
                    selectedInjuryRecord = newValue;
                } else {
                    clearAllHealthCheckupFields();
                }
            });
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Selection Listener Error", "Failed to initialize selection listeners: " + e.getMessage());
        }
    }


    private void loadHealthRecommendations(int cattleId) {
        // Use a Set to automatically handle duplicates
        Set<String> recommendationsToUpdate = new HashSet<>();

        // Fetch health checkup histories for the selected cattle
        List<HealthCheckupHistory> healthCheckupHistories;
        try {
            healthCheckupHistories = HealthCheckupHistoryDAO.getHealthCheckupHistoriesByCattleId(cattleId);
        } catch (SQLException e) {
            // Handle exception
            showAlert(AlertType.ERROR, "Database Error", "Failed to load health checkup histories: " + e.getMessage());
            return; // Exit the method if there's an error
        }

        // Iterate through health checkup histories and get follow-up recommendations
        for (HealthCheckupHistory checkup : healthCheckupHistories) {
            List<FollowUpRecommendation> recommendations;
            try {
                recommendations = FollowUpRecommendationDAO.getFollowUpRecommendationsByHealthCheckupId(checkup.getId());
            } catch (SQLException e) {
                // Handle exception
                showAlert(AlertType.ERROR, "Database Error", "Failed to load follow-up recommendations: " + e.getMessage());
                return; // Exit the method if there's an error
            }

            // Populate the recommendation set
            for (FollowUpRecommendation recommendation : recommendations) {
                recommendationsToUpdate.add(recommendation.getRecommendation());
            }
        }

        // Clear the current ListView items
        healthRecommendationsListView.getItems().clear();

        // Create observable list for health recommendations
        ObservableList<HealthRecommendationItem> observableRecommendations = FXCollections.observableArrayList();
        for (String recommendation : recommendationsToUpdate) {
            observableRecommendations.add(new HealthRecommendationItem(recommendation));
        }

        // Set the items in the ListView and apply a custom cell factory
        healthRecommendationsListView.setItems(observableRecommendations);
        healthRecommendationsListView.setCellFactory(listView -> new HealthRecommendationItemListCell());
    }


    public void loadHealthCheckupNotes(int cattleId) {
        try {
            // Fetch categorized health notes for the specific cattle
            Map<String, List<String>> categorizedHealthNotes = HealthCheckupHistoryDAO.getCategorizedHealthCheckupHistoriesByCattleId(cattleId);

            // Format health notes into a structured observable list
            ObservableList<HealthNoteItem> observableHealthNotes = formatHealthNotes(categorizedHealthNotes);
            setListViewItems(observableHealthNotes);
        } catch (SQLException e) {
            AppLogger.error("Error fetching health checkup histories: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private ObservableList<HealthNoteItem> formatHealthNotes(Map<String, List<String>> categorizedHealthNotes) {
        ObservableList<HealthNoteItem> observableHealthNotes = FXCollections.observableArrayList();

        for (Map.Entry<String, List<String>> entry : categorizedHealthNotes.entrySet()) {
            // Create category item
            HealthNoteItem categoryItem = new HealthNoteItem(entry.getKey(), true); // true indicates a category
            observableHealthNotes.add(categoryItem);

            // Create sublist items
            for (String detail : entry.getValue()) {
                HealthNoteItem sublistItem = new HealthNoteItem(detail, false); // false indicates a sublist item
                observableHealthNotes.add(sublistItem);
            }
        }

        return observableHealthNotes;
    }

    // Sets the ListView items with a custom cell factory
    private void setListViewItems(ObservableList<HealthNoteItem> observableHealthNotes) {
        healthNotesListView.setItems(observableHealthNotes);

        healthNotesListView.setCellFactory(listView -> new HealthNoteItemListCell());
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

        if (!records.isEmpty()) {
            dewormingHistoryTableView.getSelectionModel().selectFirst();
            populateDewormingHistoryFields(records.getFirst());
        } else {
            // Optionally, clear fields or provide feedback to the user
            clearAllHealthCheckupFields();
        }
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
        manufacturerDetailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().manufacturerDetails()));
    }

    // Populate input fields with selected DewormingRecord's values
    private void populateDewormingHistoryFields(DewormingRecord record) {

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
        InputFieldsValidation.validateField(selectedCattleId == 0, "Select a Cattle from the Herds table", null);
        InputFieldsValidation.validateField(dewormerTypeTextArea.getText().trim().isEmpty(), "Dewormer Type is required.", dewormerTypeTextArea);
        InputFieldsValidation.validateField(dosageOfDewormingTextField.getText().trim().isEmpty(), "Dosage is required.", dosageOfDewormingTextField);
        InputFieldsValidation.validateField(dewormingDatePicker.getValue() == null, "Date of Deworming is required.", dewormingDatePicker);
        InputFieldsValidation.validateField(routeOfAdministrationCombo.getValue() == null, "Route of Administration is required.", routeOfAdministrationCombo);

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
                v -> clearAllDewormingFields(), // This action will be triggered for New Record
                v -> {} // For clear fields action
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

        if (!records.isEmpty()) {
            medicationHistoryTableView.getSelectionModel().selectFirst();
            populateMedicationHistoryFields(records.getFirst());
        } else {
            // Optionally, clear fields or provide feedback to the user
            clearAllHealthCheckupFields();
        }
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
        telNoOfMedicationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().telNo()));
        categoryOfMedicationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().category()));
    }

    private void populateMedicationHistoryFields(MedicationRecord record) {

        // Populate the fields with values from the MedicationRecord
        dateTakenOfMedicationDatePicker.setValue(record.dateTaken()); // Set date taken
        nextScheduleOfMedicationDatePicker.setValue(record.nextSchedule()); // Set the next schedule date
        dosageOfMedicationTextField.setText(record.dosage()); // Set dosage
        frequencyOfMedicationTextField.setText(record.frequency()); // Set frequency
        administerOfMedicationTextField.setText(record.administeredBy()); // Set administered by

        String category = record.category() != null ? record.category().trim() : null;
        categoryOfMedicationComboBox.setValue(category);

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
        InputFieldsValidation.validateField(selectedCattleId == 0, "Select a Cattle from the Herds table", null);
        InputFieldsValidation.validateField(dosageOfMedicationTextField.getText().trim().isEmpty(), "Dosage is required.", dosageOfMedicationTextField);
        InputFieldsValidation.validateField(frequencyOfMedicationTextField.getText().trim().isEmpty(), "Frequency is required.", frequencyOfMedicationTextField);
        InputFieldsValidation.validateField(administerOfMedicationTextField.getText().trim().isEmpty(), "Administered By is required.", administerOfMedicationTextField);
        InputFieldsValidation.validateField(dateTakenOfMedicationDatePicker.getValue() == null, "Date Taken is required.", dateTakenOfMedicationDatePicker);
        InputFieldsValidation.validateField(nextScheduleOfMedicationDatePicker.getValue() == null, "Next Schedule is required.", nextScheduleOfMedicationDatePicker);
        InputFieldsValidation.validateField(administerOfMedicationTextField.getText().trim().isEmpty(), "Telephone Number is required.", administerOfMedicationTextField); // Assuming telNo is entered here
        InputFieldsValidation.validateField(categoryOfMedicationComboBox.getValue() == null || categoryOfMedicationComboBox.getValue().isEmpty(), "Category of Medication is required.", categoryOfMedicationComboBox);

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
        originalMedicationValuesMap.put("category", record.category()); // Store the category of medication
        originalMedicationValuesMap.put("callingCode", callingCode); // Store the calling code
        originalMedicationValuesMap.put("restOfNumber", restOfNumber); // Store the rest of the number
        originalMedicationValuesMap.put("responseType", getSelectedCheckBoxResponse()); // Store the response type

    }


    private void setupMedicationChangeListeners() {
        dateTakenOfMedicationDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("dateTaken", newValue != null ? newValue.toString() : null));
        nextScheduleOfMedicationDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInMedicationData("nextSchedule", newValue != null ? newValue.toString() : null));
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
                v -> clearAllMedicationFields(),
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
        LocalDate nextSchedule = nextScheduleOfMedicationDatePicker.getValue();
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
        administerOfMedicationTextField.clear();
        dateTakenOfMedicationDatePicker.setValue(null);
        nextScheduleOfMedicationDatePicker.setValue(null);
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

    //HEALTH CHECKUP
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

        if (!records.isEmpty()) {
            healthCheckupTableView.getSelectionModel().selectFirst();
            populateHealthCheckupFields(records.getFirst());
        } else {
            // Optionally, clear fields or provide feedback to the user
            clearAllHealthCheckupFields();
        }
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
        try {

            // Checkup date (null check)
            if (record.checkupDate() != null) {
                checkupDatePicker.setValue(record.checkupDate());
                createdATTextField.setText(record.checkupDate().toString());
            } else {
                checkupDatePicker.setValue(null);
                createdATTextField.setText(""); // Set a blank string if null
            }

            // Vital Signs (null checks for each field)
            temperatureTextField.setText(record.temperature() != null ? record.temperature() : "");
            heartRateTextField.setText(record.heartRate() != null ? record.heartRate() : "");
            respiratoryRateTextField.setText(record.respiratoryRate() != null ? record.respiratoryRate() : "");
            bloodPressureTextField.setText(record.bloodPressure() != null ? record.bloodPressure() : "");

            // Observations and Findings (null checks for each field)
            behavioralObservationsTextArea.setText(record.behavioralObservations() != null ? record.behavioralObservations() : "");
            physicalExaminationTextArea.setText(record.physicalExaminationFindings() != null ? record.physicalExaminationFindings() : "");
            healthIssuesTextArea.setText(record.healthIssues() != null ? record.healthIssues() : "");
            specificObservationsTextArea.setText(record.specificObservations() != null ? record.specificObservations() : "");
            checkupNotesTextArea.setText(record.checkupNotes() != null ? record.checkupNotes() : "");
            chronicConditionsTextArea.setText(record.chronicConditions() != null ? record.chronicConditions() : "");


            try {
                List<FollowUpRecommendation> recommendations = FollowUpRecommendationDAO.getFollowUpRecommendationsByHealthCheckupId(record.id());
                setCheckboxStates(recommendations);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error", "Failed to retrieve follow-up recommendations.");
            }


            // Store original checkbox states for later comparison
            storeOriginalRecommendations();

            // Populate the original values map for change tracking
            populateOriginalHealthCheckupValuesMap(record);

            // Reset the modification state if applicable
            isModifiedHealthCheckup = false;

        } catch (Exception e) {
            e.printStackTrace();
            // Handle any unexpected exceptions and alert the user
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred while populating health checkup fields.");
        }
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
        InputFieldsValidation.validateField(selectedCattleId == 0, "Select a Cattle from the Herds table", null);
        InputFieldsValidation.validateField(temperatureTextField.getText().trim().isEmpty(), "Temperature is required.", temperatureTextField);
        InputFieldsValidation.validateField(checkupDatePicker.getValue() == null, "Date of Checkup is required.", checkupDatePicker);

        // Validate behavioral observations
        InputFieldsValidation.validateField(behavioralObservationsTextArea.getText().trim().isEmpty(), "Behavioral observations are required.", behavioralObservationsTextArea);
        InputFieldsValidation.validateField(physicalExaminationTextArea.getText().trim().isEmpty(), "Physical examination findings are required.", physicalExaminationTextArea);
        InputFieldsValidation.validateField(healthIssuesTextArea.getText().trim().isEmpty(), "Health issues are required.", healthIssuesTextArea);
        InputFieldsValidation.validateField(specificObservationsTextArea.getText().trim().isEmpty(), "Specific observations are required.", specificObservationsTextArea);
        InputFieldsValidation.validateField(checkupNotesTextArea.getText().trim().isEmpty(), "Checkup notes are required.", checkupNotesTextArea);
        InputFieldsValidation.validateField(chronicConditionsTextArea.getText().trim().isEmpty(), "Chronic conditions are required.", chronicConditionsTextArea);

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
                v -> clearAllHealthCheckupFields(),
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

            // Update the record in the database, including follow-up recommendations
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






//INJURY


    private void loadInjuryDataIntoTableView() {
        clearAllInjuryFields();

        if (selectedCattleId == 0) {  // Ensure you have a valid selectedCattleId
            showAlert(AlertType.WARNING, "No Cattle Selected", "Please select a cattle before proceeding.");
            return;
        }

        List<InjuryReport> injuryReports;
        try {
            injuryReports = InjuryReportDAO.getInjuryReportsByCattleId(selectedCattleId); // Replace DAO method with the appropriate one
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to load injury reports. Please try again.");
            return;
        }

        ObservableList<InjuryRecord> records = getInjuryRecords(injuryReports);
        injuryTableView.setItems(records);

        if (!records.isEmpty()) {
            injuryTableView.getSelectionModel().selectFirst();
            populateInjuryFields(records.getFirst()); // Populate the first record's data into the fields
        } else {
            clearAllInjuryFields();
        }
    }

    private ObservableList<InjuryRecord> getInjuryRecords(List<InjuryReport> injuryReports) {
        ObservableList<InjuryRecord> records = FXCollections.observableArrayList();

        for (InjuryReport report : injuryReports) {
            records.add(new InjuryRecord(
                    report.getId(),
                    report.getCattleId(),
                    report.getDateOfOccurrence(),
                    report.getTypeOfInjury(),
                    report.getSpecificBodyPart(),
                    report.getSeverity(),
                    report.getCauseOfInjury(),
                    report.getFirstAidMeasures(),
                    report.getFollowUpTreatmentType(),
                    report.getMonitoringInstructions(),
                    report.getScheduledProcedures(),
                    report.getFollowUpMedications(),
                    report.getMedicationCost(),
                    report.getMedicationHistoryId()
            ));
        }

        return records;
    }
    private void setupInjuryTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));
        dateOfOccurrenceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().dateOfOccurrence()));
        medicationCostColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().medicationCost()));

        // Set up string-based columns
        typeOfInjuryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().typeOfInjury()));
        specificBodyPartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().specificBodyPart()));
        severityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().severity()));
        causeOfInjuryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().causeOfInjury()));
        firstAidMeasuresColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().firstAidMeasures()));
        followUpTreatmentTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().followUpTreatmentType()));
        monitoringInstructionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().monitoringInstructions()));
        scheduledProceduresColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().scheduledProcedures()));
        followUpMedicationsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().followUpMedications()));

    }

    private void populateInjuryFields(InjuryRecord record) {
        try {
            // Date of Occurrence (null check)
            dateOfOccurrenceDatePicker.setValue(record.dateOfOccurrence() != null ? record.dateOfOccurrence() : null);

            // Injury Information (null checks for each field)
            typeOfInjuryComboBox.setValue(record.typeOfInjury() != null ? record.typeOfInjury() : "");
            specificBodyPartComboBox.setValue(record.specificBodyPart() != null ? record.specificBodyPart() : "");
            severityComboBox.setValue(record.severity() != null ? record.severity() : "");

            // Injury Details (null checks for each field)
            causeOfInjuryTextArea.setText(record.causeOfInjury() != null ? record.causeOfInjury() : "");
            firstAidMeasuresTextArea.setText(record.firstAidMeasures() != null ? record.firstAidMeasures() : "");
            followUpTreatmentTypeComboBox.setValue(record.followUpTreatmentType() != null ? record.followUpTreatmentType() : "");
            monitoringInstructionsTextArea.setText(record.monitoringInstructions() != null ? record.monitoringInstructions() : "");
            scheduledProceduresTextArea.setText(record.scheduledProcedures() != null ? record.scheduledProcedures() : "");
            followUpMedicationsTextArea.setText(record.followUpMedications() != null ? record.followUpMedications() : "");
            medicationCostTextField.setText(record.medicationCost() != null ? record.medicationCost().toString() : "");

            // Load the medication history and select the matching item based on medicationHistoryId
            int medicationHistoryId = record.medicationHistoryId(); // Get the medication history ID
            loadMedicationHistory(medicationHistoryId); // Pass the ID to load and select

            // Load original injury values to the map
            populateOriginalInjuryValuesMap(record);

            // Reset modification state if necessary
            isModifiedInjury = false;

        } catch (Exception e) {
            e.printStackTrace();
            // Handle any unexpected exceptions and alert the user
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred while populating injury fields.");
        }
    }

    private void loadMedicationHistory(int selectedMedicationHistoryId) {
        try {
            List<MedicationHistory> medicationHistories = MedicationHistoryDAO.getMedicationHistoriesByCattleId(selectedCattleId);
            List<MedicationListItem> items = createMedicationItems(medicationHistories, selectedMedicationHistoryId);

            medicationAdministeredListView.setItems(FXCollections.observableArrayList(items));
            setupMedicationListViewListeners();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to retrieve medication history.");
        }
    }




    // Helper method to create medication list items
    private List<MedicationListItem> createMedicationItems(List<MedicationHistory> histories, int selectedId) {
        List<MedicationListItem> items = new ArrayList<>();
        for (MedicationHistory medication : histories) {
            MedicationListItem item = new MedicationListItem(medication);
            item.getRadioButton().setToggleGroup(toggleGroup);
            items.add(item);

            if (medication.getId() == selectedId) {
                toggleGroup.selectToggle(item.getRadioButton());
            }
        }
        return items;
    }

    // Setup listeners for the medication ListView and ToggleGroup
    private void setupMedicationListViewListeners() {
        medicationAdministeredListView.setOnMouseClicked(event -> {
            MedicationListItem selectedItem = medicationAdministeredListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                toggleGroup.selectToggle(selectedItem.getRadioButton());
            }
        });

        toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                MedicationListItem selectedItem = (MedicationListItem) ((RadioButton) newToggle).getParent();
                medicalAdminNameTextField.setText(selectedItem.getMedicationHistory().getAdministeredBy());
                medicalAdminContactTextField.setText(selectedItem.getMedicationHistory().getTelNo());
                medicalCategoryTextField.setText(selectedItem.getMedicationHistory().getCategory());
            }
        });
    }


    private void validateInjuryInputFields() throws ValidationException {
        InputFieldsValidation.validateField(selectedCattleId == 0, "Select a Cattle from the Herds table", null);
        InputFieldsValidation.validateField(dateOfOccurrenceDatePicker.getValue() == null, "Date of Occurrence is required.", dateOfOccurrenceDatePicker);
        InputFieldsValidation.validateField(typeOfInjuryComboBox.getValue() == null || typeOfInjuryComboBox.getValue().trim().isEmpty(), "Type of Injury is required.", typeOfInjuryComboBox);
        InputFieldsValidation.validateField(specificBodyPartComboBox.getValue() == null || specificBodyPartComboBox.getValue().trim().isEmpty(), "Specific Body Part is required.", specificBodyPartComboBox);
        InputFieldsValidation.validateField(severityComboBox.getValue() == null || severityComboBox.getValue().trim().isEmpty(), "Severity is required.", severityComboBox);
        InputFieldsValidation.validateField(causeOfInjuryTextArea.getText().trim().isEmpty(), "Cause of Injury is required.", causeOfInjuryTextArea);
        InputFieldsValidation.validateField(firstAidMeasuresTextArea.getText().trim().isEmpty(), "First Aid Measures are required.", firstAidMeasuresTextArea);
        InputFieldsValidation.validateField(followUpTreatmentTypeComboBox.getValue() == null || followUpTreatmentTypeComboBox.getValue().trim().isEmpty(), "Follow-Up Treatment Type is required.", followUpTreatmentTypeComboBox);
        InputFieldsValidation.validateField(monitoringInstructionsTextArea.getText().trim().isEmpty(), "Monitoring Instructions are required.", monitoringInstructionsTextArea);
        InputFieldsValidation.validateField(scheduledProceduresTextArea.getText().trim().isEmpty(), "Scheduled Procedures are required.", scheduledProceduresTextArea);
        InputFieldsValidation.validateField(followUpMedicationsTextArea.getText().trim().isEmpty(), "Follow-Up Medications are required.", followUpMedicationsTextArea);
        InputFieldsValidation.validateField(medicalAdminNameTextField.getText().trim().isEmpty(), "Medical Administrator Name is required.", medicalAdminNameTextField);
        InputFieldsValidation.validateField(medicalCategoryTextField.getText().trim().isEmpty(), "Medical Administrator Address is required.", medicalCategoryTextField);
        InputFieldsValidation.validateField(medicalAdminContactTextField.getText().trim().isEmpty(), "Medical Administrator Contact is required.", medicalAdminContactTextField);
        InputFieldsValidation.validateField(medicationCostTextField.getText().trim().isEmpty(), "Medication Cost is required.", medicationCostTextField);
    }


    private void populateOriginalInjuryValuesMap(InjuryRecord record) {
        // Store original values in the map for injury-related fields
        originalInjuryValuesMap.put("id", String.valueOf(record.id())); // Store the ID
        originalInjuryValuesMap.put("dateOfOccurrence", record.dateOfOccurrence().toString()); // Store the date of occurrence
        originalInjuryValuesMap.put("typeOfInjury", record.typeOfInjury()); // Store the type of injury
        originalInjuryValuesMap.put("specificBodyPart", record.specificBodyPart()); // Store the specific body part
        originalInjuryValuesMap.put("severity", record.severity()); // Store the severity
        originalInjuryValuesMap.put("causeOfInjury", record.causeOfInjury()); // Store the cause of injury
        originalInjuryValuesMap.put("firstAidMeasures", record.firstAidMeasures()); // Store the first aid measures
        originalInjuryValuesMap.put("followUpTreatmentType", record.followUpTreatmentType()); // Store follow-up treatment type
        originalInjuryValuesMap.put("monitoringInstructions", record.monitoringInstructions()); // Store monitoring instructions
        originalInjuryValuesMap.put("scheduledProcedures", record.scheduledProcedures()); // Store scheduled procedures
        originalInjuryValuesMap.put("followUpMedications", record.followUpMedications()); // Store follow-up medications
        originalInjuryValuesMap.put("medicationCost", record.medicationCost().toString()); // Store the medication cost
        originalInjuryValuesMap.put("medicationHistoryId", String.valueOf(record.medicationHistoryId())); // Store the medication history ID
    }

    private void setupInjuryChangeListeners() {
        // Listeners to track changes in injury-related fields
        dateOfOccurrenceDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("dateOfOccurrence", newValue != null ? newValue.toString() : null));
        typeOfInjuryComboBox.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("typeOfInjury", newValue != null ? newValue : ""));
        specificBodyPartComboBox.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("specificBodyPart", newValue != null ? newValue : ""));
        severityComboBox.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("severity", newValue != null ? newValue : ""));
        causeOfInjuryTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("causeOfInjury", newValue != null ? newValue : ""));
        firstAidMeasuresTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("firstAidMeasures", newValue != null ? newValue : ""));
        followUpTreatmentTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("followUpTreatmentType", newValue != null ? newValue : ""));
        monitoringInstructionsTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("monitoringInstructions", newValue != null ? newValue : ""));
        scheduledProceduresTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("scheduledProcedures", newValue != null ? newValue : ""));
        followUpMedicationsTextArea.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("followUpMedications", newValue != null ? newValue : ""));
        medicalAdminNameTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("medicalAdminName", newValue != null ? newValue : ""));
        medicalCategoryTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("medicalType", newValue != null ? newValue : ""));
        medicalAdminContactTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("medicalAdminContact", newValue != null ? newValue : ""));
        medicationCostTextField.textProperty().addListener((obs, oldValue, newValue) -> checkForChangesInInjuryData("medicationCost", newValue != null ? newValue : ""));

        toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                // Get the newly selected Medication History ID
                MedicationListItem selectedItem = (MedicationListItem) ((RadioButton) newToggle).getParent();
                int newMedicationHistoryId = selectedItem.getMedicationHistory().getId();

                // Check if the Medication History ID has changed
                checkForChangesInInjuryData("medicationHistoryId", String.valueOf(newMedicationHistoryId));
            }
        });
    }

    private void checkForChangesInInjuryData(String fieldName, String newValue) {
        // Get the original value from the map
        String originalValue = originalInjuryValuesMap.get(fieldName);

        // Compare the new value to the original value
        if (originalValue != null && !originalValue.equals(newValue)) {
            // Mark that a change has occurred only if the value differs from the original
            isModifiedInjury = true;
        } else if (originalValue != null) {
            // Reset the modified state if the current value matches the original
            isModifiedInjury = false;
        }
    }


    @FXML
    public void modifyInjuryRecord() {
        // Get the selected injury record from the table view
        selectedInjuryRecord = injuryTableView.getSelectionModel().getSelectedItem();

        if (selectedInjuryRecord == null) {
            // No record is selected, so show the save dialog
            showInjurySaveDialog();
        } else {
            // A record is selected, so show the update dialog
            showInjuryUpdateDialog();
        }
    }

    private void showInjurySaveDialog() {
        // Define the buttons for the dialog
        List<ButtonType> buttonTypes = Arrays.asList(
                new ButtonType("Save"),            // Option to save a new injury record
                new ButtonType("Clear Fields"),    // Option to clear all input fields
                new ButtonType("Cancel")           // Option to cancel the operation
        );

        // Define the actions associated with each button
        List<Consumer<Void>> actions = Arrays.asList(
                v -> saveNewInjuryReport(),        // Save the new injury record
                v -> clearAllInjuryFields(),       // Clear all injury-related input fields
                v -> {}                              // Cancel button does nothing (handled automatically)
        );

        // Show the confirmation dialog for saving a new injury record
        DialogHelper.showConfirmationDialog("New Injury Record", "Choose an Action?", buttonTypes, actions);
    }

    private void showInjuryUpdateDialog() {
        // Show the update dialog for modifying an existing injury record
        DialogHelper.showUpdateDialog(
                "Modify Injury Record",            // Title of the dialog
                isModifiedInjury,                  // Boolean indicating whether there are changes to save
                v -> updateInjuryReport(),         // Update the existing injury record
                v -> restoreOriginalInjuryData(),  // Restore the original data (undo changes)
                v -> deleteInjuryRecord(),         // Delete the injury record
                v -> clearAllInjuryFields(),       // Clear all injury-related input fields
                v -> {}                             // Cancel button does nothing (handled automatically)
        );
    }


    private int getSelectedMedicationHistoryId() {
        try {
            if (toggleGroup == null || toggleGroup.getSelectedToggle() == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "No Medication History selected.");
                return 0;
            }

            RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
            if (selectedRadioButton.getParent() == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "Invalid selection.");
                return 0;
            }

            MedicationListItem selectedItem = (MedicationListItem) selectedRadioButton.getParent();
            if (selectedItem == null || selectedItem.getMedicationHistory() == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "Invalid Medication History selected.");
                return 0;
            }

            MedicationHistory selectedMedication = selectedItem.getMedicationHistory();
            return selectedMedication.getId(); // Return the valid medication history ID
        } catch (Exception e) {
            AppLogger.error("Error retrieving selected medication history.", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve the selected medication history.");
            return 0;
        }
    }

    private void saveNewInjuryReport() {
        try {
            // Validate input fields (throws ValidationException if invalid)
            validateInjuryInputFields();

            // Null-safe creation of the InjuryReport object
            InjuryReport injuryReport = new InjuryReport(
                    0, // ID will be generated
                    selectedCattleId,
                    dateOfOccurrenceDatePicker.getValue() != null ? dateOfOccurrenceDatePicker.getValue() : LocalDate.now(),
                    typeOfInjuryComboBox.getValue() != null ? typeOfInjuryComboBox.getValue() : "",
                    specificBodyPartComboBox.getValue() != null ? specificBodyPartComboBox.getValue() : "",
                    severityComboBox.getValue() != null ? severityComboBox.getValue() : "",
                    causeOfInjuryTextArea.getText() != null ? causeOfInjuryTextArea.getText().trim() : "",
                    firstAidMeasuresTextArea.getText() != null ? firstAidMeasuresTextArea.getText().trim() : "",
                    followUpTreatmentTypeComboBox.getValue() != null ? followUpTreatmentTypeComboBox.getValue() : "",
                    monitoringInstructionsTextArea.getText() != null ? monitoringInstructionsTextArea.getText().trim() : "",
                    scheduledProceduresTextArea.getText() != null ? scheduledProceduresTextArea.getText().trim() : "",
                    followUpMedicationsTextArea.getText() != null ? followUpMedicationsTextArea.getText().trim() : "",
                    new BigDecimal(medicationCostTextField.getText().trim().isEmpty() ? "0" : medicationCostTextField.getText().trim()),
                    getSelectedMedicationHistoryId()
            );

            // Save the InjuryReport object to the database
            InjuryReportDAO.insertInjuryReport(injuryReport);

            // Reload the injury data into the table view
            loadInjuryDataIntoTableView();

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Injury details saved successfully.");
        } catch (ValidationException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (SQLException e) {
            AppLogger.error("Error saving injury report.", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save injury report. Please try again.");
        } catch (Exception e) {
            AppLogger.error("Unexpected error occurred while saving injury report.", e);
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred. Please try again.");
        }
    }


    private void updateInjuryReport() {
        try {
            // Validate input fields (throws ValidationException if invalid)
            validateInjuryInputFields();

            InjuryRecord selectedInjuryRecord = injuryTableView.getSelectionModel().getSelectedItem();
            if (selectedInjuryRecord == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "No Injury Report selected.");
                return;
            }

            InjuryReport reportToUpdate = convertToInjuryReport(selectedInjuryRecord);

            // Update the InjuryReport object fields with null-safe values
            reportToUpdate.setDateOfOccurrence(dateOfOccurrenceDatePicker.getValue() != null ? dateOfOccurrenceDatePicker.getValue() : reportToUpdate.getDateOfOccurrence());
            reportToUpdate.setTypeOfInjury(typeOfInjuryComboBox.getValue() != null ? typeOfInjuryComboBox.getValue() : reportToUpdate.getTypeOfInjury());
            reportToUpdate.setSpecificBodyPart(specificBodyPartComboBox.getValue() != null ? specificBodyPartComboBox.getValue() : reportToUpdate.getSpecificBodyPart());
            reportToUpdate.setSeverity(severityComboBox.getValue() != null ? severityComboBox.getValue() : reportToUpdate.getSeverity());
            reportToUpdate.setCauseOfInjury(causeOfInjuryTextArea.getText() != null ? causeOfInjuryTextArea.getText().trim() : reportToUpdate.getCauseOfInjury());
            reportToUpdate.setFirstAidMeasures(firstAidMeasuresTextArea.getText() != null ? firstAidMeasuresTextArea.getText().trim() : reportToUpdate.getFirstAidMeasures());
            reportToUpdate.setFollowUpTreatmentType(followUpTreatmentTypeComboBox.getValue() != null ? followUpTreatmentTypeComboBox.getValue() : reportToUpdate.getFollowUpTreatmentType());
            reportToUpdate.setMonitoringInstructions(monitoringInstructionsTextArea.getText() != null ? monitoringInstructionsTextArea.getText().trim() : reportToUpdate.getMonitoringInstructions());
            reportToUpdate.setScheduledProcedures(scheduledProceduresTextArea.getText() != null ? scheduledProceduresTextArea.getText().trim() : reportToUpdate.getScheduledProcedures());
            reportToUpdate.setFollowUpMedications(followUpMedicationsTextArea.getText() != null ? followUpMedicationsTextArea.getText().trim() : reportToUpdate.getFollowUpMedications());
            reportToUpdate.setMedicationCost(new BigDecimal(medicationCostTextField.getText().trim().isEmpty() ? "0" : medicationCostTextField.getText().trim()));
            reportToUpdate.setMedicationHistoryId(getSelectedMedicationHistoryId());

            InjuryReportDAO.updateInjuryReport(reportToUpdate);

            loadInjuryDataIntoTableView();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Injury details updated successfully.");
        } catch (ValidationException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (SQLException e) {
            AppLogger.error("Error updating injury report.", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update injury report. Please try again.");
        } catch (Exception e) {
            AppLogger.error("Unexpected error occurred while updating injury report.", e);
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred. Please try again.");
        }
    }


    private void deleteInjuryRecord() {
        try {
            InjuryRecord selectedInjuryRecord = injuryTableView.getSelectionModel().getSelectedItem();
            if (selectedInjuryRecord == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "No Injury Report selected.");
                return;
            }

            InjuryReport reportToDelete = convertToInjuryReport(selectedInjuryRecord);

            if (!confirmInjuryRecordDeletion("Record ID: " + reportToDelete.getId())) {
                return;
            }

            // Try deleting the record
            deleteInjuryRecord(reportToDelete);

            showAlert(Alert.AlertType.INFORMATION, "Record Deleted", "The selected injury record has been successfully deleted.");
        } catch (SQLException e) {
            AppLogger.error("Error deleting injury report.", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete injury report. Please try again.");
        } catch (Exception e) {
            AppLogger.error("Unexpected error occurred while deleting injury report.", e);
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred. Please try again.");
        } finally {
            // Reload data regardless of success/failure
            loadInjuryDataIntoTableView();
        }
    }

    private InjuryReport convertToInjuryReport(InjuryRecord record) {
        return new InjuryReport(
                record.id(),
                record.cattleId(),
                record.dateOfOccurrence(),
                record.typeOfInjury(),
                record.specificBodyPart(),
                record.severity(),
                record.causeOfInjury(),
                record.firstAidMeasures(),
                record.followUpTreatmentType(),
                record.monitoringInstructions(),
                record.scheduledProcedures(),
                record.followUpMedications(),
                record.medicationCost(),
                record.medicationHistoryId()
        );
    }

    private void deleteInjuryRecord(InjuryReport report) throws SQLException {
        int reportId = report.getId();
        try {
            InjuryReportDAO.deleteInjuryReportById(reportId);
        } catch (SQLException e) {
            AppLogger.error("Failed to delete injury report with ID: " + reportId, e);
            throw e;
        }
    }

    private boolean confirmInjuryRecordDeletion(String contentText) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this injury record?");
        confirmationAlert.setContentText(contentText + "\nThis action cannot be undone.");

        Optional<ButtonType> confirmation = confirmationAlert.showAndWait();
        return confirmation.isPresent() && confirmation.get() == ButtonType.OK;
    }


    private void restoreOriginalInjuryData() {
        dateOfOccurrenceDatePicker.setValue(LocalDate.parse(originalInjuryValuesMap.get("dateOfOccurrence")));
        typeOfInjuryComboBox.setValue(originalInjuryValuesMap.get("typeOfInjury"));
        specificBodyPartComboBox.setValue(originalInjuryValuesMap.get("specificBodyPart"));
        severityComboBox.setValue(originalInjuryValuesMap.get("severity"));
        causeOfInjuryTextArea.setText(originalInjuryValuesMap.get("causeOfInjury"));
        firstAidMeasuresTextArea.setText(originalInjuryValuesMap.get("firstAidMeasures"));
        followUpTreatmentTypeComboBox.setValue(originalInjuryValuesMap.get("followUpTreatmentType"));
        monitoringInstructionsTextArea.setText(originalInjuryValuesMap.get("monitoringInstructions"));
        scheduledProceduresTextArea.setText(originalInjuryValuesMap.get("scheduledProcedures"));
        followUpMedicationsTextArea.setText(originalInjuryValuesMap.get("followUpMedications"));
        medicationCostTextField.setText(originalInjuryValuesMap.get("medicationCost"));

        // Restore medication history selection using the stored medication history ID
        int medicationHistoryId = Integer.parseInt(originalInjuryValuesMap.get("medicationHistoryId"));
        loadMedicationHistory(medicationHistoryId); // Load and select the matching medication history

        // Reset the modification state
        isModifiedInjury = false;
    }

    // Method to clear all fields related to injury records
    private void clearAllInjuryFields() {
        // Clear selections in the injury table view
        injuryTableView.getSelectionModel().clearSelection();

        // Reset the selected injury record
        selectedInjuryRecord = null;

        // Clear input fields related to injury
        dateOfOccurrenceDatePicker.setValue(null);

        // Reset injury-related combo boxes
        typeOfInjuryComboBox.setValue(null);
        specificBodyPartComboBox.setValue(null);
        severityComboBox.setValue(null);
        followUpTreatmentTypeComboBox.setValue(null);

        // Clear text areas for injury details
        causeOfInjuryTextArea.clear();
        firstAidMeasuresTextArea.clear();
        monitoringInstructionsTextArea.clear();
        scheduledProceduresTextArea.clear();
        followUpMedicationsTextArea.clear();

        // Clear medical admin info fields
        medicalAdminNameTextField.clear();
        medicalCategoryTextField.clear();
        medicalAdminContactTextField.clear();

        // Clear the medication cost field
        medicationCostTextField.clear();

        // Clear the medication administered list view selection
        medicationAdministeredListView.getSelectionModel().clearSelection(); // Deselect any selected item

        // Clear the toggle group selection
        toggleGroup.selectToggle(null); // Deselect any selected toggle

        // Clear any maps or flags related to the injury record
        originalInjuryValuesMap.clear(); // Clear the map holding original values (if it exists)
        isModifiedInjury = false; // Reset the modification flag
    }


    @FXML private void clearInjuryRecord() {
        clearAllInjuryFields();
    }



}
