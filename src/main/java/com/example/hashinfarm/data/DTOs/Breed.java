package com.example.hashinfarm.data.DTOs;

import javafx.beans.property.*;

public class Breed {
    private final IntegerProperty breedId;
    private final StringProperty breedName;
    private final StringProperty origin;
    private final BooleanProperty recognition;
    private final StringProperty comments;

    public Breed(int breedId, String breedName, String origin, boolean recognition, String comments) {
        this.breedId = new SimpleIntegerProperty(breedId);
        this.breedName = new SimpleStringProperty(breedName);
        this.origin = new SimpleStringProperty(origin);
        this.recognition = new SimpleBooleanProperty(recognition);
        this.comments = new SimpleStringProperty(comments);
    }

    // Getters and setters for non-property fields
    public int getBreedId() {
        return breedId.get();
    }

    public String getBreedName() {
        return breedName.get();
    }

    public String getOrigin() {
        return origin.get();
    }

    public boolean isRecognition() {
        return recognition.get();
    }

    public String getComments() {
        return comments.get();
    }

}
