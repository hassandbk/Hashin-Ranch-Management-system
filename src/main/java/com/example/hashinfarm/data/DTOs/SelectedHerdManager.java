package com.example.hashinfarm.data.DTOs;

import com.example.hashinfarm.data.DTOs.records.Herd;
import javafx.beans.property.*;

public class SelectedHerdManager {
    private static SelectedHerdManager instance;

    // Define properties for herd details
    private final IntegerProperty selectedHerdId = new SimpleIntegerProperty();
    private final StringProperty selectedHerdName = new SimpleStringProperty();
    private final IntegerProperty selectedHerdTotalAnimals = new SimpleIntegerProperty();
    private final StringProperty selectedHerdAnimalClass = new SimpleStringProperty();
    private final StringProperty selectedHerdBreedType = new SimpleStringProperty();
    private final StringProperty selectedHerdAgeClass = new SimpleStringProperty();
    private final StringProperty selectedHerdBreedSystem = new SimpleStringProperty();
    private final StringProperty selectedHerdSolutionType = new SimpleStringProperty();
    private final StringProperty selectedHerdFeedBasis = new SimpleStringProperty();
    private final StringProperty selectedHerdLocation = new SimpleStringProperty();


    private SelectedHerdManager() {
        // Private constructor to prevent instantiation
    }

    public static SelectedHerdManager getInstance() {
        if (instance == null) {
            instance = new SelectedHerdManager();
        }
        return instance;
    }

    // Getter and setter methods for each property
    public int getSelectedHerdId() {
        return selectedHerdId.get();
    }

    public IntegerProperty selectedHerdIdProperty() {
        return selectedHerdId;
    }

    public void setSelectedHerdId(int selectedHerdId) {
        this.selectedHerdId.set(selectedHerdId);
    }

    public String getSelectedHerdName() {
        return selectedHerdName.get();
    }

    public StringProperty selectedHerdNameProperty() {
        return selectedHerdName;
    }

    public void setSelectedHerdName(String selectedHerdName) {
        this.selectedHerdName.set(selectedHerdName);
    }

    public int getSelectedHerdTotalAnimals() {
        return selectedHerdTotalAnimals.get();
    }

    public IntegerProperty selectedHerdTotalAnimalsProperty() {
        return selectedHerdTotalAnimals;
    }

    public void setSelectedHerdTotalAnimals(int selectedHerdTotalAnimals) {
        this.selectedHerdTotalAnimals.set(selectedHerdTotalAnimals);
    }

    public String getSelectedHerdAnimalClass() {
        return selectedHerdAnimalClass.get();
    }

    public StringProperty selectedHerdAnimalClassProperty() {
        return selectedHerdAnimalClass;
    }

    public void setSelectedHerdAnimalClass(String selectedHerdAnimalClass) {
        this.selectedHerdAnimalClass.set(selectedHerdAnimalClass);
    }

    public String getSelectedHerdBreedType() {
        return selectedHerdBreedType.get();
    }

    public StringProperty selectedHerdBreedTypeProperty() {
        return selectedHerdBreedType;
    }

    public void setSelectedHerdBreedType(String selectedHerdBreedType) {
        this.selectedHerdBreedType.set(selectedHerdBreedType);
    }

    public String getSelectedHerdAgeClass() {
        return selectedHerdAgeClass.get();
    }

    public StringProperty selectedHerdAgeClassProperty() {
        return selectedHerdAgeClass;
    }

    public void setSelectedHerdAgeClass(String selectedHerdAgeClass) {
        this.selectedHerdAgeClass.set(selectedHerdAgeClass);
    }

    public String getSelectedHerdBreedSystem() {
        return selectedHerdBreedSystem.get();
    }

    public StringProperty selectedHerdBreedSystemProperty() {
        return selectedHerdBreedSystem;
    }

    public void setSelectedHerdBreedSystem(String selectedHerdBreedSystem) {
        this.selectedHerdBreedSystem.set(selectedHerdBreedSystem);
    }

    public String getSelectedHerdSolutionType() {
        return selectedHerdSolutionType.get();
    }

    public StringProperty selectedHerdSolutionTypeProperty() {
        return selectedHerdSolutionType;
    }

    public void setSelectedHerdSolutionType(String selectedHerdSolutionType) {
        this.selectedHerdSolutionType.set(selectedHerdSolutionType);
    }

    public String getSelectedHerdFeedBasis() {
        return selectedHerdFeedBasis.get();
    }

    public StringProperty selectedHerdFeedBasisProperty() {
        return selectedHerdFeedBasis;
    }

    public void setSelectedHerdFeedBasis(String selectedHerdFeedBasis) {
        this.selectedHerdFeedBasis.set(selectedHerdFeedBasis);
    }

    public String getSelectedHerdLocation() {
        return selectedHerdLocation.get();
    }

    public StringProperty selectedHerdLocationProperty() {
        return selectedHerdLocation;
    }

    public void setSelectedHerdLocation(String selectedHerdLocation) {
        this.selectedHerdLocation.set(selectedHerdLocation);
    }





    // Update method to set all properties at once
    public void setSelectedHerd(Herd herd) {
        selectedHerdId.set(herd.id());
        selectedHerdName.set(herd.name());
        selectedHerdTotalAnimals.set(herd.totalAnimals());
        selectedHerdAnimalClass.set(herd.animalClass());
        selectedHerdBreedType.set(herd.breedType());
        selectedHerdAgeClass.set(herd.ageClass());
        selectedHerdBreedSystem.set(herd.breedSystem());
        selectedHerdSolutionType.set(herd.solutionType());
        selectedHerdFeedBasis.set(herd.feedBasis());
        selectedHerdLocation.set(herd.location());
    }

}
