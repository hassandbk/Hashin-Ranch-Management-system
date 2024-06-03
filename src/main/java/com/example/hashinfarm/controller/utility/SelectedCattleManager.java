package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.model.Cattle;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.Period;

public class SelectedCattleManager {
    private static SelectedCattleManager instance;

    private final IntegerProperty selectedCattleID = new SimpleIntegerProperty();
    private final StringProperty selectedTagId = new SimpleStringProperty();
    private final IntegerProperty selectedHerdId = new SimpleIntegerProperty();
    private final StringProperty selectedColorMarkings = new SimpleStringProperty();
    private final StringProperty selectedName = new SimpleStringProperty();
    private final StringProperty selectedGender = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> selectedDateOfBirth = new SimpleObjectProperty<>();
    private final IntegerProperty selectedWeightId = new SimpleIntegerProperty();
    private final StringProperty selectedBcs = new SimpleStringProperty();
    private final IntegerProperty selectedBreedId = new SimpleIntegerProperty();
    private final StringProperty selectedBreedName = new SimpleStringProperty();
    private final IntegerProperty selectedSireId = new SimpleIntegerProperty();
    private final StringProperty selectedSireName = new SimpleStringProperty();
    private final IntegerProperty selectedDamId = new SimpleIntegerProperty();
    private final StringProperty selectedDamName = new SimpleStringProperty();
    private final IntegerProperty selectedDamsHerd = new SimpleIntegerProperty();
    private final IntegerProperty selectedSiresHerd = new SimpleIntegerProperty();
    private final StringProperty selectedSireHerdName = new SimpleStringProperty();
    private final StringProperty selectedDamHerdName = new SimpleStringProperty();
    private final StringProperty selectedSireBreedName = new SimpleStringProperty();
    private final StringProperty selectedDamBreedName = new SimpleStringProperty();

    // New property for age
    private final IntegerProperty selectedAge = new SimpleIntegerProperty();

    private SelectedCattleManager() {
        // Bind selectedAge to the selectedDateOfBirth property
        selectedDateOfBirth.addListener((observable, oldValue, newValue) -> updateAge(newValue));
    }

    public static SelectedCattleManager getInstance() {
        if (instance == null) {
            instance = new SelectedCattleManager();
        }
        return instance;
    }

    // Update the age based on the date of birth
    private void updateAge(LocalDate dateOfBirth) {
        if (dateOfBirth != null) {
            selectedAge.set(Period.between(dateOfBirth, LocalDate.now()).getYears());
        } else {
            selectedAge.set(0);
        }
    }

    public int getSelectedCattleID() {
        return selectedCattleID.get();
    }

    public IntegerProperty selectedCattleIDProperty() {
        return selectedCattleID;
    }

    public void setSelectedCattleID(int selectedCattleID) {
        this.selectedCattleID.set(selectedCattleID);
    }

    public String getSelectedTagId() {
        return selectedTagId.get();
    }

    public StringProperty selectedTagIdProperty() {
        return selectedTagId;
    }

    public void setSelectedTagId(String selectedTagId) {
        this.selectedTagId.set(selectedTagId);
    }

    public int getSelectedHerdId() {
        return selectedHerdId.get();
    }

    public IntegerProperty selectedHerdIdProperty() {
        return selectedHerdId;
    }

    public void setSelectedHerdId(int selectedHerdId) {
        this.selectedHerdId.set(selectedHerdId);
    }

    public String getSelectedColorMarkings() {
        return selectedColorMarkings.get();
    }

    public StringProperty selectedColorMarkingsProperty() {
        return selectedColorMarkings;
    }

    public void setSelectedColorMarkings(String selectedColorMarkings) {
        this.selectedColorMarkings.set(selectedColorMarkings);
    }

    public String getSelectedName() {
        return selectedName.get();
    }

    public StringProperty selectedNameProperty() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName.set(selectedName);
    }

    public String getSelectedGender() {
        return selectedGender.get();
    }

    public StringProperty selectedGenderProperty() {
        return selectedGender;
    }

    public void setSelectedGender(String selectedGender) {
        this.selectedGender.set(selectedGender);
    }

    public LocalDate getSelectedDateOfBirth() {
        return selectedDateOfBirth.get();
    }

    public void setSelectedDateOfBirth(LocalDate selectedDateOfBirth) {
        this.selectedDateOfBirth.set(selectedDateOfBirth);
    }

    public ObjectProperty<LocalDate> selectedDateOfBirthProperty() {
        return selectedDateOfBirth;
    }

    public int getComputedAge() {
        LocalDate dateOfBirth = selectedDateOfBirth.get();
        if (dateOfBirth != null) {
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
        }
        return 0;
    }

    // New method for age property
    public IntegerProperty selectedAgeProperty() {
        return selectedAge;
    }

    public int getSelectedWeightId() {
        return selectedWeightId.get();
    }

    public IntegerProperty selectedWeightIdProperty() {
        return selectedWeightId;
    }

    public void setSelectedWeightId(int selectedWeightId) {
        this.selectedWeightId.set(selectedWeightId);
    }

    public String getSelectedBcs() {
        return selectedBcs.get();
    }

    public StringProperty selectedBcsProperty() {
        return selectedBcs;
    }

    public void setSelectedBcs(String selectedBcs) {
        this.selectedBcs.set(selectedBcs);
    }

    public int getSelectedBreedId() {
        return selectedBreedId.get();
    }

    public IntegerProperty selectedBreedIdProperty() {
        return selectedBreedId;
    }

    public void setSelectedBreedId(int selectedBreedId) {
        this.selectedBreedId.set(selectedBreedId);
    }

    public String getSelectedBreedName() {
        return selectedBreedName.get();
    }

    public StringProperty selectedBreedNameProperty() {
        return selectedBreedName;
    }

    public void setSelectedBreedName(String selectedBreedName) {
        this.selectedBreedName.set(selectedBreedName);
    }

    public int getSelectedSireId() {
        return selectedSireId.get();
    }

    public IntegerProperty selectedSireIdProperty() {
        return selectedSireId;
    }

    public void setSelectedSireId(int selectedSireId) {
        this.selectedSireId.set(selectedSireId);
    }

    public String getSelectedSireName() {
        return selectedSireName.get();
    }

    public StringProperty selectedSireNameProperty() {
        return selectedSireName;
    }

    public void setSelectedSireName(String selectedSireName) {
        this.selectedSireName.set(selectedSireName);
    }

    public int getSelectedDamId() {
        return selectedDamId.get();
    }

    public IntegerProperty selectedDamIdProperty() {
        return selectedDamId;
    }

    public void setSelectedDamId(int selectedDamId) {
        this.selectedDamId.set(selectedDamId);
    }

    public String getSelectedDamName() {
        return selectedDamName.get();
    }

    public StringProperty selectedDamNameProperty() {
        return selectedDamName;
    }

    public void setSelectedDamName(String selectedDamName) {
        this.selectedDamName.set(selectedDamName);
    }

    public int getSelectedDamsHerd() {
        return selectedDamsHerd.get();
    }

    public IntegerProperty selectedDamsHerdProperty() {
        return selectedDamsHerd;
    }

    public void setSelectedDamsHerd(int selectedDamsHerd) {
        this.selectedDamsHerd.set(selectedDamsHerd);
    }

    public int getSelectedSiresHerd() {
        return selectedSiresHerd.get();
    }

    public IntegerProperty selectedSiresHerdProperty() {
        return selectedSiresHerd;
    }

    public void setSelectedSiresHerd(int selectedSiresHerd) {
        this.selectedSiresHerd.set(selectedSiresHerd);
    }

    public String getSelectedSireHerdName() {
        return selectedSireHerdName.get();
    }

    public StringProperty selectedSireHerdNameProperty() {
        return selectedSireHerdName;
    }

    public void setSelectedSireHerdName(String selectedSireHerdName) {
        this.selectedSireHerdName.set(selectedSireHerdName);
    }

    public String getSelectedDamHerdName() {
        return selectedDamHerdName.get();
    }

    public StringProperty selectedDamHerdNameProperty() {
        return selectedDamHerdName;
    }

    public void setSelectedDamHerdName(String selectedDamHerdName) {
        this.selectedDamHerdName.set(selectedDamHerdName);
    }

    public String getSelectedSireBreedName() {
        return selectedSireBreedName.get();
    }

    public StringProperty selectedSireBreedNameProperty() {
        return selectedSireBreedName;
    }

    public void setSelectedSireBreedName(String selectedSireBreedName) {
        this.selectedSireBreedName.set(selectedSireBreedName);
    }

    public String getSelectedDamBreedName() {
        return selectedDamBreedName.get();
    }

    public StringProperty selectedDamBreedNameProperty() {
        return selectedDamBreedName;
    }

    public void setSelectedDamBreedName(String selectedDamBreedName) {
        this.selectedDamBreedName.set(selectedDamBreedName);
    }

    // Update method to set all properties at once
    public void setSelectedCattle(Cattle cattle) {
        selectedCattleID.set(cattle.getCattleId());
        selectedTagId.set(cattle.getTagId());
        selectedHerdId.set(cattle.getHerdId());
        selectedColorMarkings.set(cattle.getColorMarkings());
        selectedName.set(cattle.getName());
        selectedGender.set(cattle.getGender());
        selectedDateOfBirth.set(cattle.getDateOfBirth());
        selectedWeightId.set(cattle.getWeightId());
        selectedBcs.set(cattle.getBcs());
        selectedBreedId.set(cattle.getBreedId());
        selectedBreedName.set(cattle.getBreedName());
        selectedSireId.set(cattle.getSireId());
        selectedSireName.set(cattle.getSireName());
        selectedDamId.set(cattle.getDamId());
        selectedDamName.set(cattle.getDamName());
        selectedDamsHerd.set(cattle.getDamsHerd());
        selectedSiresHerd.set(cattle.getSiresHerd());
        selectedSireHerdName.set(cattle.getSireHerdName());
        selectedDamHerdName.set(cattle.getDamHerdName());
        selectedSireBreedName.set(cattle.getSireBreedName());
        selectedDamBreedName.set(cattle.getDamBreedName());

        // Update age based on the new date of birth
        updateAge(cattle.getDateOfBirth());
    }

    public Cattle getSelectedCattle() {
        return new Cattle(
                selectedCattleID.get(),
                selectedTagId.get(),
                selectedHerdId.get(),
                selectedColorMarkings.get(),
                selectedName.get(),
                selectedGender.get(),
                selectedDateOfBirth.get(),
                selectedWeightId.get(),
                selectedBcs.get(),
                selectedBreedId.get(),
                selectedBreedName.get(),
                selectedSireId.get(),
                selectedSireName.get(),
                selectedDamId.get(),
                selectedDamName.get(),
                selectedDamsHerd.get(),
                selectedSiresHerd.get(),
                selectedDamHerdName.get(),
                selectedSireHerdName.get(),
                selectedSireBreedName.get(),
                selectedDamBreedName.get()
        );
    }
}
