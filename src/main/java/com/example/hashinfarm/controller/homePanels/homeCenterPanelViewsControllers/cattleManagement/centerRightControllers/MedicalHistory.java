package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.DewormingHistoryDAO;
import com.example.hashinfarm.controller.handlers.ActionHandlerFactory;
import com.example.hashinfarm.controller.utility.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


import com.example.hashinfarm.exceptions.DatabaseException;
import com.example.hashinfarm.exceptions.ValidationException;
import com.example.hashinfarm.model.Country;
import com.example.hashinfarm.model.DewormingHistory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;


public class MedicalHistory {
    // Map to associate buttons with their respective handlers
    private final Map<Button, String> buttonHandlersMap = new HashMap<>();

    private int selectedCattleId = 0;

    @FXML private TextField cattleNameTextField, dosageTextField,frequencyTextField,
                            scheduledProceduresTextField,weightAtTimeTextField,administeredByTextField,postSurgeryMedicationFrequencyTextField,
                            medicationDosageTextField, medicationFrequencyTextField, surgeonNameTextField, surgeonContactTextField, anesthesiaDosageTextField, postSurgeryMedicationDosageTextField;
    @FXML private TextArea healthNotesTextArea,dewormerTypeTextArea,manufacturerDetailsTextArea, monitoringInstructionsTextArea, followUpMedicationsTextArea, surgicalProcedureDetailsTextArea, woundCareInstructionsTextArea, rehabilitationPlanTextArea;
    @FXML private Button vaccinationHistory, viewDewormingHistory, healthInfoHistory,viewInjuriesHistory,viewSurgicalHistory;
    @FXML private ComboBox<String>   physicalActivityRestrictionsComboBox, typeOfSurgeryComboBox,routeOfAdministrationCombo, followUpTreatmentTypeComboBox, postSurgeryMedicationTypeComboBox, anesthesiaTypeComboBox;
    @FXML private DatePicker  dateOfSurgeryDatePicker, followUpAppointmentsDatePicker,dewormingDatePicker;



    @FXML
    private ImageView imageView;
    @FXML
    private ComboBox<Country> countryComboBox;
    @FXML
    private TextField restOfNumber, countryTextField;
    @FXML
    private HBox inputContainer;
    private CountryCodePickerHelper countryCodePickerHelper;

    public void initialize() {
        SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();
        // Initialize button handlers with respective fx:id using a loop
        initializeButtonHandlers( vaccinationHistory,viewDewormingHistory,  healthInfoHistory, viewInjuriesHistory,viewSurgicalHistory );
        setDatesAndDateFormats();
        selectedCattleManager.selectedNameProperty().addListener((observable, oldValue, newValue) -> cattleNameTextField.setText(newValue));
        selectedCattleManager.selectedCattleIDProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue == null || newValue.intValue() == 0) return;
                            selectedCattleId = newValue.intValue();
                        });

        // Load the placeholder image initially
        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png"))));

        // Initialize the country code picker helper
        countryCodePickerHelper = new CountryCodePickerHelper(countryComboBox, countryTextField, imageView, restOfNumber);
        countryCodePickerHelper.initialize();
    }

    private void initializeButtonHandlers(Button... buttons) {
        for (Button button : buttons) {
            String buttonId = button.getId();
            buttonHandlersMap.put(button, buttonId);
            button.setOnAction(this::handleButtonAction);
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonId = buttonHandlersMap.get(button);
        Objects.requireNonNull(ActionHandlerFactory.createActionHandler(buttonId)).handle(event);
    }
    private void setDatePickerFormat(DatePicker datePicker) {
        DateUtil.datePickerFormat(datePicker);
    }

    private void setDatesAndDateFormats() {
        setDatePickerFormat(dewormingDatePicker);

    }
    @FXML
    private void saveDewormingDetails() {
        try {
            // Validate input fields
            if (!validateInputFields()) {
                return;
            }

            // Create DewormingHistory object
            DewormingHistory dewormingHistory = createDewormingHistory();

            // Save to database
            saveDewormingHistoryToDatabase(dewormingHistory);

            // Show success message
            showAlert(AlertType.INFORMATION, "Success", "Deworming details saved successfully.");
        } catch (ValidationException e) {
            showAlert(AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (DatabaseException e) {
            showAlert(AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private boolean validateInputFields() throws ValidationException {
        validateField(selectedCattleId == 0, "Select a Cattle from the Herds table");
        validateField(dewormerTypeTextArea.getText().trim().isEmpty(), "Dewormer Type is required.");
        validateField(dosageTextField.getText().trim().isEmpty(), "Dosage is required.");
        validateField(dewormingDatePicker.getValue() == null, "Date of Deworming is required.");
        validateField(routeOfAdministrationCombo.getValue() == null, "Route of Administration is required.");
        return true;
    }

    private void validateField(boolean condition, String errorMessage) throws ValidationException {
        if (condition) {
            throw new ValidationException(errorMessage);
        }
    }


    private DewormingHistory createDewormingHistory() throws ValidationException {
        String dewormerType = dewormerTypeTextArea.getText().trim();
        double dosage = Double.parseDouble(dosageTextField.getText().trim());
        Double weightAtTime = parseOptionalDouble(weightAtTimeTextField.getText().trim());
        String administeredBy = administeredByTextField.getText().trim();
        LocalDate dateOfDeworming = dewormingDatePicker.getValue();
        String manufacturerDetails = manufacturerDetailsTextArea.getText().trim();

        String contactDetails = validateAndFormatPhoneNumber();
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




    // Method to validate and format the phone number using CountryCodePickerHelper
    public String validateAndFormatPhoneNumber() throws ValidationException {

        Country selectedCountry = countryCodePickerHelper.getSelectedCountry();
        String selectedCallingCode = selectedCountry.getCallingCode(); // Example: +256
        String countryAlpha2Code = selectedCountry.getAlpha2(); // Example: "UG" for Uganda
        String phoneNumberString = phoneNumberString(selectedCallingCode);

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phoneNumberString, countryAlpha2Code);

            // Check if the number is valid for the selected region
            if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
                throw new ValidationException("Invalid phone number for the selected country.");
            }

            //  Return formatted phone number in E.164 standard
            return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new ValidationException("Failed to parse phone number: " + e.getMessage());
        }
    }

    private String phoneNumberString(String selectedCallingCode) throws ValidationException {
        String restOfNumberValue = restOfNumber.getText().trim(); // User-inputted number 705068168

        //  Validate that country code is selected and rest of the number is not empty
        if (selectedCallingCode == null || selectedCallingCode.isEmpty()) {
            throw new ValidationException("Please select a valid country code.");
        }

        if (restOfNumberValue.isEmpty()) {
            throw new ValidationException("Phone number cannot be empty.");
        }

        // Step 2: Combine the calling code and rest of the number
        return selectedCallingCode + restOfNumberValue;
    }

    private void saveDewormingHistoryToDatabase(DewormingHistory dewormingHistory) throws DatabaseException {
        try {
            // Insert details into the database
            DewormingHistoryDAO.insertDewormingHistory(dewormingHistory);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save deworming details: " + e.getMessage(), e);
        }
    }
    private Double parseOptionalDouble(String text) {
        if (text.isEmpty()) {
            return null;
        }
        return Double.parseDouble(text);
    }
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }






}
