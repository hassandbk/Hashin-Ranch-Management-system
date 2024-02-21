package com.example.hashinfarm.controller.utility;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SelectedCowManager {
    private static SelectedCowManager instance;
    private final StringProperty selectedCowName = new SimpleStringProperty(); // Property for selected cow's name
    private final StringProperty selectedCowId = new SimpleStringProperty(); // Property for selected cow's name

    private SelectedCowManager() {
        // Private constructor to prevent instantiation
    }

    public static SelectedCowManager getInstance() {
        if (instance == null) {
            instance = new SelectedCowManager();
        }
        return instance;
    }

    public String getSelectedCowName() {
        return selectedCowName.get();
    }

    public void setSelectedCowName(String selectedCowName) {
        this.selectedCowName.set(selectedCowName);
    }

    public String getSelectedCowId() {
        return selectedCowId.get();
    }

    public void setSelectedCowId(int selectedCowId) {
        this.selectedCowId.set(String.valueOf(selectedCowId));
    }

    // Property getter for selected cow's name
    public StringProperty selectedCowNameProperty() {
        return selectedCowName;
    }

    public StringProperty selectedCowIdProperty() {
        return selectedCowId;
    }
}
