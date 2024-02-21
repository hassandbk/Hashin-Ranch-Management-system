package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers;

import com.example.hashinfarm.controller.utility.SplitPaneDividerEnforcer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class CattleController {

    @FXML
    private SplitPane splitPane;

    @FXML
    private Button leftArrowButton;

    @FXML
    private Button rightArrowButton;

    private final double minPosition = 0.1;
    private final double maxPosition = 0.9;

    @FXML
    private void initialize() {
        SplitPaneDividerEnforcer dividerEnforcer = new SplitPaneDividerEnforcer(minPosition, maxPosition);
        dividerEnforcer.enforceConstraints(splitPane);

        // Add button actions
        leftArrowButton.setOnAction(event -> animateSplitPane(minPosition));
        rightArrowButton.setOnAction(event -> animateSplitPane(maxPosition));

        // Add listener to the positionProperty of the divider
        splitPane.getDividers().get(0).positionProperty().addListener((obs, oldPos, newPos) ->
                updateButtonsPosition(newPos.doubleValue()));
    }

    private void animateSplitPane(double targetPosition) {
        targetPosition = Math.max(minPosition, Math.min(maxPosition, targetPosition));

        // Adjust the duration as needed
        double animationDurationMillis = 800;

        // Calculate the positions for the left and right arrow buttons
        double buttonSpace = 30.0; // Adjust this value as needed
        double leftButtonTarget = targetPosition * splitPane.getWidth() - buttonSpace;
        double rightButtonTarget = targetPosition * splitPane.getWidth();

        // Animate the specific divider to the target position
        KeyValue dividerKeyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), targetPosition);

        // Animate the left arrow button
        KeyValue leftButtonKeyValue = new KeyValue(leftArrowButton.layoutXProperty(), leftButtonTarget);

        // Animate the right arrow button
        KeyValue rightButtonKeyValue = new KeyValue(rightArrowButton.layoutXProperty(), rightButtonTarget);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(animationDurationMillis), dividerKeyValue, leftButtonKeyValue, rightButtonKeyValue);

        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    private void updateButtonsPosition(double dividerPosition) {
        double buttonSpace = 30.0; // Adjust this value as needed
        double leftButtonPosition = dividerPosition * splitPane.getWidth() - buttonSpace;
        double rightButtonPosition = dividerPosition * splitPane.getWidth();

        leftArrowButton.setLayoutX(leftButtonPosition);
        rightArrowButton.setLayoutX(rightButtonPosition);
    }
}
