package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.model.Country;
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
    private Country selectedCountry = null;

    public CountryCodePickerHelper(ComboBox<Country> countryComboBox, TextField countryTextField, ImageView imageView, TextField restOfNumber) {
        this.countryComboBox = countryComboBox;
        this.countryTextField = countryTextField;
        this.imageView = imageView;
        this.restOfNumber = restOfNumber;
    }

    public Country getSelectedCountry() {
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
            filterCountries(newValue);
            countryComboBox.show();
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
    private void updateCountryDetails(Country country) {
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
}
