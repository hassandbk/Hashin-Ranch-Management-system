package com.example.hashinfarm.data.DTOs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class LactationPeriodWithSelection {
    private final LactationPeriod lactationPeriod;
    private final BooleanProperty selected;

    public LactationPeriodWithSelection(LactationPeriod lactationPeriod) {
        this.lactationPeriod = lactationPeriod;
        this.selected = new SimpleBooleanProperty(false);
    }

    public LactationPeriod getLactationPeriod() {
        return lactationPeriod;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
