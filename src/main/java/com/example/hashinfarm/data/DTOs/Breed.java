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

    public IntegerProperty breedIdProperty() {
        return breedId;
    }

    public StringProperty breedNameProperty() {
        return breedName;
    }

    public StringProperty originProperty() {
        return origin;
    }

    public BooleanProperty recognitionProperty() {
        return recognition;
    }

    public StringProperty commentsProperty() {
        return comments;
    }

    // Getters and setters for non-property fields
    public int getBreedId() {
        return breedId.get();
    }

    public void setBreedId(int breedId) {
        this.breedId.set(breedId);
    }

    public String getBreedName() {
        return breedName.get();
    }

    public void setBreedName(String breedName) {
        this.breedName.set(breedName);
    }

    public String getOrigin() {
        return origin.get();
    }

    public void setOrigin(String origin) {
        this.origin.set(origin);
    }

    public boolean isRecognition() {
        return recognition.get();
    }

    public void setRecognition(boolean recognition) {
        this.recognition.set(recognition);
    }

    public String getComments() {
        return comments.get();
    }

    public void setComments(String comments) {
        this.comments.set(comments);
    }
}
