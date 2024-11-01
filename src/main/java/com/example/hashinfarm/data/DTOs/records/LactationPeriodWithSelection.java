package com.example.hashinfarm.data.DTOs.records;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public record LactationPeriodWithSelection(LactationPeriod lactationPeriod, BooleanProperty selected) {
    public LactationPeriodWithSelection(LactationPeriod lactationPeriod) {
        this(lactationPeriod, new SimpleBooleanProperty(false));
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
