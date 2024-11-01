// SelectedHerdManager.java
package com.example.hashinfarm.businessLogic.services;

import com.example.hashinfarm.data.DTOs.records.Herd;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SelectedHerdManager {
    private static SelectedHerdManager instance;

    // Property for the selected herd
    private final ObjectProperty<Herd> selectedHerd = new SimpleObjectProperty<>();

    // Private constructor to prevent external instantiation
    private SelectedHerdManager() {}

    public static SelectedHerdManager getInstance() {
        if (instance == null) {
            instance = new SelectedHerdManager();
        }
        return instance;
    }

    // Getter and Setter for selected herd property
    public Herd getSelectedHerd() {
        return selectedHerd.get();
    }

    public void setSelectedHerd(Herd herd) {
        this.selectedHerd.set(herd);
    }

    public ObjectProperty<Herd> selectedHerdProperty() {
        return selectedHerd;
    }
}
