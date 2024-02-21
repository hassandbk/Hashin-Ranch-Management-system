package com.example.hashinfarm.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Cattle {
    private int id;
    private String name;
    private BooleanProperty selected;

    public Cattle(int id, String name) {
        this.id = id;
        this.name = name;
        this.selected = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
