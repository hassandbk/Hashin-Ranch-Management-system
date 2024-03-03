package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers;

import com.example.hashinfarm.controller.utility.SplitPaneDividerEnforcer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CattleController {

    @FXML
    private SplitPane splitPane;

    @FXML
    private Button leftArrowButton;

    @FXML
    private Button rightArrowButton;

    @FXML
    private VBox leftPanePlaceholder;

    @FXML
    private VBox rightPanePlaceholder;

    private final double minPosition = 0.1;
    private final double maxPosition = 0.9;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML
    private void initialize() {
        SplitPaneDividerEnforcer dividerEnforcer = new SplitPaneDividerEnforcer(minPosition, maxPosition);
        dividerEnforcer.enforceConstraints(splitPane);

        // Add button actions
        leftArrowButton.setOnAction(event -> animateSplitPane(minPosition));
        rightArrowButton.setOnAction(event -> animateSplitPane(maxPosition));

        // Add listener to the positionProperty of the divider
        splitPane.getDividers().getFirst().positionProperty().addListener((obs, oldPos, newPos) ->
                updateButtonsPosition(newPos.doubleValue()));

        loadFXMLAsync("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterLeft.fxml", leftPanePlaceholder);
        loadFXMLAsync("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterRight.fxml", rightPanePlaceholder);
    }

    private void loadFXMLAsync(String fxmlPath, VBox placeholder) {
        Task<VBox> task = new Task<>() {
            @Override
            protected VBox call() throws IOException {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                return fxmlLoader.load();
            }
        };

        task.setOnSucceeded(event -> {
            try {
                VBox content = task.getValue();
                placeholder.getChildren().setAll(content.getChildren());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            if (exception != null) {
                exception.printStackTrace();
            }
        });

        executorService.execute(task);
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
        double finalTargetPosition = targetPosition;
        Platform.runLater(() -> {
            KeyValue dividerKeyValue = new KeyValue(splitPane.getDividers().getFirst().positionProperty(), finalTargetPosition);

            // Animate the left arrow button
            KeyValue leftButtonKeyValue = new KeyValue(leftArrowButton.layoutXProperty(), leftButtonTarget);

            // Animate the right arrow button
            KeyValue rightButtonKeyValue = new KeyValue(rightArrowButton.layoutXProperty(), rightButtonTarget);

            KeyFrame keyFrame = new KeyFrame(Duration.millis(animationDurationMillis), dividerKeyValue, leftButtonKeyValue, rightButtonKeyValue);

            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        });
    }

    private void updateButtonsPosition(double dividerPosition) {
        double buttonSpace = 30.0; // Adjust this value as needed
        double leftButtonPosition = dividerPosition * splitPane.getWidth() - buttonSpace;
        double rightButtonPosition = dividerPosition * splitPane.getWidth();

        Platform.runLater(() -> {
            leftArrowButton.setLayoutX(leftButtonPosition);
            rightArrowButton.setLayoutX(rightButtonPosition);
        });
    }

    // Shutdown executorService when application exits
    public void shutdown() {
        executorService.shutdown();
    }
}
