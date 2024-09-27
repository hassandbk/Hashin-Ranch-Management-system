package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.DewormingHistoryDAO;
import com.example.hashinfarm.controller.dao.MedicationHistoryDAO;
import com.example.hashinfarm.controller.records.DewormingRecord;
import com.example.hashinfarm.controller.records.MedicationRecord;
import com.example.hashinfarm.controller.utility.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;


import com.example.hashinfarm.exceptions.DatabaseException;
import com.example.hashinfarm.exceptions.ValidationException;
import com.example.hashinfarm.model.Country;
import com.example.hashinfarm.model.DewormingHistory;

import com.example.hashinfarm.model.MedicationHistory;
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

    private boolean isModifiedDeworming = false;
    private boolean isModifiedMedication = false;
    private DewormingRecord selectedDewormingRecord;
    private MedicationRecord selectedMedicationRecord;

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


    public void initialize() {
        initializeSelectedCattleManager();
        DateUtil.datePickerFormat(dewormingDatePicker);
        DateUtil.datePickerFormat(dateTakenOfMedicationDatePicker);
        DateUtil.datePickerFormat(nextScheduleOfMedicationTextField);
        setupDewormingTableColumns();
        initializeImageView();
        initializeCountryCodePicker();
        initializeTextFields();
        initializeSelectionListener();
        setupDewormingChangeListeners();

        setupMedicationTableColumns();
        setupMedicationChangeListeners();
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
        UnitsTextField.initializeTextField(dosageOfDewormingTextField, true);
        UnitsTextField.initializeTextField(weightAtTimeOfDewormingTextField, false);
        UnitsTextField.initializeTextField(dosageOfMedicationTextField, true);

        UnitsTextField.addValidationListener(dosageOfDewormingTextField, UnitsTextField::isValidDosage);
        UnitsTextField.addValidationListener(weightAtTimeOfDewormingTextField, UnitsTextField::isValidWeight);
        UnitsTextField.addValidationListener(dosageOfMedicationTextField, UnitsTextField::isValidDosage);
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

    // Populate original values map for change tracking
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
            isModifiedDeworming = true; // Mark changes if value differs
        } else if (originalValue != null) {
            isModifiedDeworming = false; // Reset modification flag if values match
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
        double dosage = CountryCodePickerHelper.parseNumericValue(dosageOfDewormingTextField.getText().trim(), UnitsTextField.ML_SUFFIX);
        double weightAtTime = CountryCodePickerHelper.parseNumericValue(weightAtTimeOfDewormingTextField.getText().trim(), UnitsTextField.KG_SUFFIX);
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

            // Show success message
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

        // Reset modification flag
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
        nextScheduleOfMedicationTextField.setValue(record.nextSchedule()); // Set next schedule date
        dosageOfMedicationTextField.setText(record.dosage()); // Set dosage
        frequencyOfMedicationTextField.setText(record.frequency()); // Set frequency
        typeOfMedicationTextField.setText(record.type()); // Set medication type
        administerOfMedicationTextField.setText(record.administeredBy()); // Set administered by
        categoryOfMedicationComboBox.setValue(record.category()); // Set category of medication

        negativeCheckBox.setSelected(false);  // Set default value for negative check (adjust as necessary)
        positiveCheckBox.setSelected(false);  // Set default value for positive check (adjust as necessary)


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

            // Show success message
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




}
