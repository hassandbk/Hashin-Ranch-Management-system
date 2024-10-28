package com.example.hashinfarm.helpers;

import com.example.hashinfarm.utils.DataLoader;
import com.example.hashinfarm.utils.FlagImageLoader;
import com.example.hashinfarm.utils.exceptions.ValidationException;
import com.example.hashinfarm.data.DTOs.Country;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static javafx.event.EventType.ROOT;

public class CountryCodePickerHelper {

    private final ObservableList<Country> countries = FXCollections.observableArrayList();
    private final ObservableList<Country> filteredCountries = FXCollections.observableArrayList();
    private final HashMap<String, Country> countryMap = new HashMap<>();
    private final ConcurrentHashMap<String, Image> flagImageCache = new ConcurrentHashMap<>();
    private final FlagImageLoader flagImageLoader = new FlagImageLoader();

    private final ComboBox<Country> countryComboBox;
    private final TextField countryTextField;
    private final ImageView imageView;
    private final TextField restOfNumber;
    private static Country selectedCountry = null;
    private boolean isProgrammaticUpdate = false;
    public CountryCodePickerHelper(ComboBox<Country> countryComboBox, TextField countryTextField, ImageView imageView, TextField restOfNumber) {
        this.countryComboBox = countryComboBox;
        this.countryTextField = countryTextField;
        this.imageView = imageView;
        this.restOfNumber = restOfNumber;
    }

    public static Country getSelectedCountry() {
        if (selectedCountry != null) {
            return selectedCountry;
        }
        return null;
    }

    // Initialize the Country Picker
    public void initialize() {
        loadCountryDataInBackground(() -> preloadFlagImages(this::setupUI));
    }

    // Load country data asynchronously
    private void loadCountryDataInBackground(Runnable onSuccess) {
        Task<List<Country>> loadCountriesTask = new Task<>() {
            @Override
            protected List<Country> call() throws Exception {
                return DataLoader.loadCountries();
            }
        };

        loadCountriesTask.setOnSucceeded(event -> {
            List<Country> countryList = loadCountriesTask.getValue();
            Platform.runLater(() -> {
                countries.addAll(countryList);
                for (Country country : countryList) {
                    countryMap.put(country.getName().toLowerCase(), country);
                    countryMap.put(country.getCallingCode().toLowerCase(), country);
                }
                filteredCountries.setAll(countries);
                countryComboBox.setItems(filteredCountries);
                onSuccess.run();
            });
        });

        loadCountriesTask.setOnFailed(event -> {
            Throwable exception = loadCountriesTask.getException();
            if (exception != null) {
                exception.printStackTrace();
                throw new RuntimeException("Failed to load countries: " + exception.getMessage(), exception);
            }
        });

        new Thread(loadCountriesTask).start();
    }

    // Preload flag images for all countries
    private void preloadFlagImages(Runnable onSuccess) {
        Task<Void> preloadFlagsTask = new Task<>() {
            @Override
            protected Void call() throws IOException {
                for (Country country : countries) {
                    String flagName = Optional.ofNullable(country.getFlagName()).orElse("placeholder.png");
                    if (!flagImageCache.containsKey(flagName)) {
                        flagImageCache.put(flagName, flagImageLoader.loadFlagImage(flagName));
                    }
                }
                return null;
            }
        };

        preloadFlagsTask.setOnSucceeded(event -> Platform.runLater(onSuccess));
        preloadFlagsTask.setOnFailed(event -> {
            Throwable exception = preloadFlagsTask.getException();
            if (exception != null) {
                exception.printStackTrace();
                throw new RuntimeException("Failed to preload flag images: " + exception.getMessage(), exception);
            }
        });

        new Thread(preloadFlagsTask).start();
    }

    // Set up ComboBox and listeners
    private void setupUI() {
        setupComboBox();
        setupCountryTextFieldListener();
        setupCountryComboBoxListener();
    }

    // Set up the ComboBox cell factory to display country names and flags
    private void setupComboBox() {
        countryComboBox.setCellFactory(listView -> new CountryListCell(flagImageCache));
    }

    // Add listener to the country TextField to filter countries based on input
    private void setupCountryTextFieldListener() {
        countryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Only react to changes if it's not a programmatic update
            if (!isProgrammaticUpdate) {
                filterCountries(newValue);
                countryComboBox.show();  // Show dropdown only for user-initiated input
            }
        });
    }

    // Add listener to the ComboBox for when a country is selected
    private void setupCountryComboBoxListener() {
        countryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedCountry = newValue;
                updateCountryDetails(selectedCountry);
                // Stop event propagation to prevent triggering countryTextFieldListener
                Event.fireEvent(countryTextField, new Event( ROOT ));
                restOfNumber.requestFocus();
            } else {
                selectedCountry = null;
                imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png"))));
            }
        });
    }

    // Filter the countries based on input
    private void filterCountries(String input) {
        if (input == null || input.trim().isEmpty()) {
            filteredCountries.setAll(countries);
        } else {
            String lowerInput = input.toLowerCase();
            ObservableList<Country> matchingCountries = FXCollections.observableArrayList();
            HashSet<String> addedCountries = new HashSet<>();

            for (String key : countryMap.keySet()) {
                if (key.contains(lowerInput)) {
                    Country matchedCountry = countryMap.get(key);
                    if (matchedCountry != null && !addedCountries.contains(matchedCountry.getName())) {
                        matchingCountries.add(matchedCountry);
                        addedCountries.add(matchedCountry.getName());
                    }
                }
            }
            filteredCountries.setAll(matchingCountries);
        }
    }

    // Update country details when a country is selected
    public void updateCountryDetails(Country country) {
        if (country != null) {
            String flagName = Optional.ofNullable(country.getFlagName()).orElse("placeholder.png");
            Image flagImage = flagImageCache.get(flagName);
            imageView.setImage(Objects.requireNonNullElseGet(flagImage, () -> new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png")))));

            Country mappedCountry = countryMap.get(country.getCallingCode().toLowerCase());
            if (mappedCountry != null) {
                countryTextField.setText(mappedCountry.getCallingCode());
            }
        }
    }
    // Method to select a country based on the calling code programmatically
    public void selectCountryByCallingCode(String callingCode) {
        isProgrammaticUpdate = true;  // Disable listener reaction

        for (Country country : countries) {
            if (country.getCallingCode().equals(callingCode)) {
                countryComboBox.getSelectionModel().select(country);  // Select the country in the ComboBox
                updateCountryDetails(country);  // Update flag and other details
                break;
            }
        }

        isProgrammaticUpdate = false;  // Re-enable listener reaction
    }
    public static String validateAndFormatContact(String restOfNumber) throws ValidationException {
        Country selectedCountry = getSelectedCountry();
        String selectedCallingCode = Objects.requireNonNull(selectedCountry).getCallingCode(); // Example: +256
        String countryAlpha2Code = selectedCountry.getAlpha2(); // Example: "UG" for Uganda
        String phoneNumberString = buildPhoneNumberString(selectedCallingCode,restOfNumber);

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phoneNumberString, countryAlpha2Code);

            // Check if the number is valid for the selected region
            if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
                throw new ValidationException("Invalid phone number for the selected country.");
            }

            // Return formatted phone number in the desired format
            return String.format("(%s)%s", selectedCallingCode, restOfNumber);
        } catch (NumberParseException e) {
            throw new ValidationException("Failed to parse phone number: " + e.getMessage());
        }
    }


    public static String buildPhoneNumberString(String selectedCallingCode, String restOfNumber) throws ValidationException {

        // Validate that country code is selected and the rest of the number is not empty
        if (selectedCallingCode == null || selectedCallingCode.isEmpty()) {
            throw new ValidationException("Please select a valid country code.");
        }

        if (restOfNumber.isEmpty()) {
            throw new ValidationException("Phone number cannot be empty.");
        }

        // Combine the calling code and the rest of the number
        return selectedCallingCode + restOfNumber;
    }

    public static double parseNumericValue(String text, String suffix) throws ValidationException {
        if (!text.endsWith(suffix)) {
            throw new ValidationException("Input does not contain the correct unit: " + suffix);
        }

        // Remove the suffix and parse the numeric part
        String numericPart = text.substring(0, text.length() - suffix.length()).trim();
        try {
            return Double.parseDouble(numericPart); // Convert to double
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid numeric value: " + text);
        }
    }

    public  String[] processContactDetails(String contactDetails,
                                                 TextField countryCodeTextField,
                                                 TextField restOfNumberTextField,
                                                 ComboBox<?> countryComboBox) {
        // Initialize country code and rest of number variables
        String callingCode = "";
        String restOfNumber = "";

        if (contactDetails != null && !contactDetails.isEmpty()) {
            try {
                // Extract the country calling code and the rest of the phone number
                callingCode = contactDetails.substring(contactDetails.indexOf('(') + 1, contactDetails.indexOf(')')).trim(); // e.g., "+256"
                restOfNumber = contactDetails.substring(contactDetails.indexOf(')') + 1).trim();  // e.g., "705068168"

                // Select the country by calling code
                selectCountryByCallingCode(callingCode);
                restOfNumberTextField.setText(restOfNumber); // Set the phone number in the appropriate TextField

            } catch (Exception e) {
                // If an error occurs, clear the text fields
                countryCodeTextField.clear();
                restOfNumberTextField.clear();
                countryComboBox.setValue(null); // Clear the ComboBox selection if there's an error
            }
        } else {
            // If no contact details are provided, clear the fields
            countryCodeTextField.clear();
            restOfNumberTextField.clear();
            countryComboBox.setValue(null);
        }

        // Return the calling code and rest of the number
        return new String[]{callingCode, restOfNumber};
    }


    public void clearPhoneNumberFields() {
        isProgrammaticUpdate = true; // Disable listener reaction
        countryTextField.clear(); // Clear the country TextField
        countryComboBox.getSelectionModel().clearSelection(); // Clear the ComboBox selection
        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png")))); // Reset flag image
        restOfNumber.clear(); // Clear the restOfNumber TextField
        selectedCountry = null; // Reset selected country
        isProgrammaticUpdate = false; // Re-enable listener reaction
    }
}
