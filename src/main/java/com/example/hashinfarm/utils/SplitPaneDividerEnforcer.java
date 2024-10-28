package com.example.hashinfarm.utils;

import javafx.scene.control.SplitPane;

public class SplitPaneDividerEnforcer {

    private final double minPosition;
    private final double maxPosition;

    public SplitPaneDividerEnforcer(double minPosition, double maxPosition) {
        this.minPosition = minPosition;
        this.maxPosition = maxPosition;
    }

    public void enforceConstraints(SplitPane splitPane) {
        splitPane.getDividers().forEach(divider -> {
            divider.positionProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() < minPosition) {
                    divider.setPosition(minPosition);
                } else if (newVal.doubleValue() > maxPosition) {
                    divider.setPosition(maxPosition);
                }
            });
        });
    }
}
