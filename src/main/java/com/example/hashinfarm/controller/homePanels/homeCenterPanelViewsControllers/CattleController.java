package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers;

import com.example.hashinfarm.controller.utility.AppLogger;
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
import java.util.concurrent.TimeUnit;

public class CattleController {

    private static final int THREAD_POOL_SIZE = 2;
    private final double minPosition = 0.1;
    private final double maxPosition = 0.9;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private FXMLLoader fxmlLoader;

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

    @FXML
    private void initialize() {
        SplitPaneDividerEnforcer dividerEnforcer =
                new SplitPaneDividerEnforcer(minPosition, maxPosition);
        dividerEnforcer.enforceConstraints(splitPane);

        leftArrowButton.setOnAction(event -> animateSplitPane(minPosition));
        rightArrowButton.setOnAction(event -> animateSplitPane(maxPosition));

        splitPane.getDividers().getFirst().positionProperty().addListener((obs, oldPos, newPos) ->
                updateButtonsPosition(newPos.doubleValue()));

        loadFXMLAsync("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterLeft.fxml",
                leftPanePlaceholder);
        loadFXMLAsync("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterRight.fxml",
                rightPanePlaceholder);

        // Add shutdown hook to gracefully shutdown executorService
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdownExecutorService();
        }));
    }

    private void loadFXMLAsync(String fxmlPath, VBox placeholder) {
        Task<VBox> task = new Task<>() {
            @Override
            protected VBox call() throws IOException {
                fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                return fxmlLoader.load();
            }
        };

        task.setOnSucceeded(event -> {
            try {
                VBox content = task.getValue();
                placeholder.getChildren().setAll(content.getChildren());
            } catch (Exception e) {
                handleLoadFailure(e, fxmlPath);
            }
        });

        task.setOnFailed(event -> {
            handleLoadFailure(task.getException(), fxmlPath);
        });

        executorService.execute(task);
    }

    private void handleLoadFailure(Throwable exception, String fxmlPath) {
        AppLogger.error("Error during asynchronous FXML loading: " + fxmlPath, exception);
        // Print stack trace to console
        if (exception != null) {
            exception.printStackTrace();
        }
    }

    private void animateSplitPane(double targetPosition) {
        targetPosition = Math.max(minPosition, Math.min(maxPosition, targetPosition));

        double animationDurationMillis = 800;
        double buttonSpace = 30.0;

        double leftButtonTarget = targetPosition * splitPane.getWidth() - buttonSpace;
        double rightButtonTarget = targetPosition * splitPane.getWidth();

        double finalTargetPosition = targetPosition;
        Platform.runLater(() -> {
            KeyValue dividerKeyValue = new KeyValue(splitPane.getDividers().getFirst().positionProperty(), finalTargetPosition);
            KeyValue leftButtonKeyValue = new KeyValue(leftArrowButton.layoutXProperty(), leftButtonTarget);
            KeyValue rightButtonKeyValue = new KeyValue(rightArrowButton.layoutXProperty(), rightButtonTarget);

            KeyFrame keyFrame = new KeyFrame(Duration.millis(animationDurationMillis),
                    dividerKeyValue, leftButtonKeyValue, rightButtonKeyValue);

            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        });
    }

    private void updateButtonsPosition(double dividerPosition) {
        double buttonSpace = 30.0;
        double leftButtonPosition = dividerPosition * splitPane.getWidth() - buttonSpace;
        double rightButtonPosition = dividerPosition * splitPane.getWidth();

        Platform.runLater(() -> {
            leftArrowButton.setLayoutX(leftButtonPosition);
            rightArrowButton.setLayoutX(rightButtonPosition);
        });
    }

    public void shutdownExecutorService() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}
