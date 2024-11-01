package com.example.hashinfarm.data.DTOs.records;

import com.example.hashinfarm.data.DTOs.Cattle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public record Herd(
        int id,
        String name,
        int totalAnimals,
        String animalClass,
        String breedType,
        String breedSystem,
        String ageClass,
        String solutionType,
        String feedBasis,
        String location,
        String action,
        ObservableList<Cattle> animals
) {
    public Herd(int id, String name, int totalAnimals, String animalClass, String breedType, String ageClass,
                String breedSystem, String solutionType, String feedBasis, String location, String action) {
        this(id, name, totalAnimals, animalClass, breedType, breedSystem, ageClass, solutionType, feedBasis, location,
                action, FXCollections.observableArrayList());
    }

    // Add this method to return an unmodifiable view of animals
    public ObservableList<Cattle> getAnimals() {
        return FXCollections.unmodifiableObservableList(animals);
    }
}
