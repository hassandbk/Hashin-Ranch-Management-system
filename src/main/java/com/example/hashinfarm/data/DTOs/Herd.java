package com.example.hashinfarm.data.DTOs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Herd {
    private int id;
    private String name;
    private int totalAnimals;
    private String animalClass;
    private String breedType;
    private String breedSystem;
    private String ageClass;
    private String solutionType;
    private String feedBasis;
    private String location;
    private String action;
    private ObservableList<Cattle> animals;

    public Herd(int id, String name, int totalAnimals, String animalClass, String breedType,String ageClass,String breedSystem,
                String solutionType, String feedBasis, String location, String action) {
        this.id = id;
        this.name = name;
        this.totalAnimals = totalAnimals;
        this.animalClass = animalClass;
        this.breedType = breedType;
        this.ageClass = ageClass;
        this.breedSystem = breedSystem;
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
    // Getters and setters for the new fields
    public String getBreedSystem() {
        return breedSystem;
    }

    public void setBreedSystem(String breedSystem) {
        this.breedSystem = breedSystem;
    }

    public String getAgeClass() {
        return ageClass;
    }

    public void setAgeClass(String ageClass) {
        this.ageClass = ageClass;
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
