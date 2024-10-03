package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.model.Cattle;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.Period;

public class SelectedCattleManager {
    private static SelectedCattleManager instance;

    // Properties for managing the Cattle object
    private final ObjectProperty<Cattle> selectedCattle = new SimpleObjectProperty<>();
    private final IntegerProperty selectedAge = new SimpleIntegerProperty();

    private SelectedCattleManager() {
        // Listen for changes in the selected Cattle object to update age automatically
        selectedCattle.addListener((observable, oldCattle, newCattle) -> updateAge(newCattle));
    }

    public static SelectedCattleManager getInstance() {
        if (instance == null) {
            instance = new SelectedCattleManager();
        }
        return instance;
    }

    // Update age based on the date of birth of the selected cattle
    private void updateAge(Cattle cattle) {
        if (cattle != null && cattle.getDateOfBirth() != null) {
            selectedAge.set(Period.between(cattle.getDateOfBirth(), LocalDate.now()).getYears());
        } else {
            selectedAge.set(0);  // Reset age if no cattle or date of birth is provided
        }
    }

    // Getter for the computed age
    public int getComputedAge() {
        Cattle cattle = selectedCattle.get();
        if (cattle != null && cattle.getDateOfBirth() != null) {
            return Period.between(cattle.getDateOfBirth(), LocalDate.now()).getYears();
        }
        return 0; // Return 0 if no cattle or date of birth is available
    }

    // Getter and setter for the selected Cattle object
    public Cattle getSelectedCattle() {
        return selectedCattle.get();
    }

    public void setSelectedCattle(Cattle cattle) {
        selectedCattle.set(cattle);
    }

    public ObjectProperty<Cattle> selectedCattleProperty() {
        return selectedCattle;
    }
}
