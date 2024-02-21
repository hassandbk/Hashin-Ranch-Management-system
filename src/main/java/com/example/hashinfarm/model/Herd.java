package com.example.hashinfarm.model;
import com.example.hashinfarm.model.Cattle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Herd {
    private int id;
    private String name;
    private int totalAnimals;
    private String animalClass;
    private String breedType;
    private String solutionType;
    private String feedBasis;
    private String location;
    private String action;
    private ObservableList<Cattle> animals;

    public Herd(int id, String name, int totalAnimals, String animalClass, String breedType,
                String solutionType, String feedBasis, String location, String action) {
        this.id = id;
        this.name = name;
        this.totalAnimals = totalAnimals;
        this.animalClass = animalClass;
        this.breedType = breedType;
        this.solutionType = solutionType;
        this.feedBasis = feedBasis;
        this.location = location;
        this.action = action;
        this.animals = FXCollections.observableArrayList();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalAnimals() {
        return totalAnimals;
    }

    public void setTotalAnimals(int totalAnimals) {
        this.totalAnimals = totalAnimals;
    }

    public String getAnimalClass() {
        return animalClass;
    }

    public void setAnimalClass(String animalClass) {
        this.animalClass = animalClass;
    }

    public String getBreedType() {
        return breedType;
    }

    public void setBreedType(String breedType) {
        this.breedType = breedType;
    }

    public String getSolutionType() {
        return solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }

    public String getFeedBasis() {
        return feedBasis;
    }

    public void setFeedBasis(String feedBasis) {
        this.feedBasis = feedBasis;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ObservableList<Cattle> getAnimals() {
        return animals;
    }

    public void setAnimals(ObservableList<Cattle> animals) {
        this.animals = animals;
    }
}
