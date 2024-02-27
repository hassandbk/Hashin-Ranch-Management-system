package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.model.Cattle;
import javafx.beans.property.*;

public class SelectedCowManager {
    private static SelectedCowManager instance;

    private final IntegerProperty selectedCowId = new SimpleIntegerProperty();
    private final StringProperty selectedTagId = new SimpleStringProperty();
    private final IntegerProperty selectedHerdId = new SimpleIntegerProperty();
    private final StringProperty selectedColorMarkings = new SimpleStringProperty();
    private final StringProperty selectedName = new SimpleStringProperty();
    private final StringProperty selectedGender = new SimpleStringProperty();
    private final ObjectProperty<java.sql.Date> selectedDateOfBirth = new SimpleObjectProperty<>();
    private final IntegerProperty selectedAge = new SimpleIntegerProperty();
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




    private SelectedCowManager() {
        // Private constructor to prevent instantiation
    }

    public static SelectedCowManager getInstance() {
        if (instance == null) {
            instance = new SelectedCowManager();
        }
        return instance;
    }




    public int getSelectedCowId() {
        return selectedCowId.get();
    }

    public IntegerProperty selectedCowIdProperty() {
        return selectedCowId;
    }

    public void setSelectedCowId(int selectedCowId) {
        this.selectedCowId.set(selectedCowId);
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






    public java.sql.Date getSelectedDateOfBirth() {
        return selectedDateOfBirth.get();
    }

    public ObjectProperty<java.sql.Date> selectedDateOfBirthProperty() {
        return selectedDateOfBirth;
    }

    public void setSelectedDateOfBirth(java.sql.Date selectedDateOfBirth) {
        this.selectedDateOfBirth.set(selectedDateOfBirth);
    }






    public int getSelectedAge() {
        return selectedAge.get();
    }

    public IntegerProperty selectedAgeProperty() {
        return selectedAge;
    }

    public void setSelectedAge(int selectedAge) {
        this.selectedAge.set(selectedAge);
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
    public void setSelectedCow(Cattle cattle) {
        selectedCowId.set(cattle.getCattleId());
        selectedTagId.set(cattle.getTagId());
        selectedHerdId.set(cattle.getHerdId());
        selectedColorMarkings.set(cattle.getColorMarkings());
        selectedName.set(cattle.getName());
        selectedGender.set(cattle.getGender());
        selectedDateOfBirth.set(cattle.getDateOfBirth());
        selectedAge.set(cattle.getAge());
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
    }
}
