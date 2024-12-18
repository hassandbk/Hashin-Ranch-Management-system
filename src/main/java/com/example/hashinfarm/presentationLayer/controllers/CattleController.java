package com.example.hashinfarm.presentationLayer.controllers;
import com.example.hashinfarm.utils.SplitPaneDividerEnforcer;
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
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CattleController {

    private static final int THREAD_POOL_SIZE = 4; // Adjust pool size if needed
    private final double minPosition = 0.1;
    private final double maxPosition = 0.9;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

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
        SplitPaneDividerEnforcer dividerEnforcer = new SplitPaneDividerEnforcer(minPosition, maxPosition);
        dividerEnforcer.enforceConstraints(splitPane);

        leftArrowButton.setOnAction(event -> animateSplitPane(minPosition, splitPane, minPosition, maxPosition, leftArrowButton, rightArrowButton));
        rightArrowButton.setOnAction(event -> animateSplitPane(maxPosition, splitPane, minPosition, maxPosition, leftArrowButton, rightArrowButton));

        splitPane.getDividers().getFirst().positionProperty().addListener((obs, oldPos, newPos) -> updateButtonsPosition(newPos.doubleValue(), splitPane, leftArrowButton, rightArrowButton));

        loadLeftPanel();

        // Add shutdown hook to gracefully shutdown executorService
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownExecutorService));
    }

    private void loadLeftPanel() {
        Task<VBox> leftTask = new Task<>() {
            @Override
            protected VBox call() throws IOException {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterLeft.fxml"));
                return fxmlLoader.load();
            }
        };

        leftTask.setOnSucceeded(event -> {
            try {
                VBox leftContent = leftTask.getValue();
                leftPanePlaceholder.getChildren().setAll(leftContent.getChildren());
                loadRightPanel(); // Load the right panel only after the left panel has successfully loaded
            } catch (Exception e) {
                handleLoadFailure(e, "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterLeft.fxml");
            }
        });

        leftTask.setOnFailed(event -> {
            handleLoadFailure(leftTask.getException(), "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterLeft.fxml");
        });

        executorService.execute(leftTask);
    }

    private void loadRightPanel() {
        Task<VBox> rightTask = new Task<>() {
            @Override
            protected VBox call() throws IOException {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterRight.fxml"));
                return fxmlLoader.load();
            }
        };

        rightTask.setOnSucceeded(event -> {
            try {
                VBox rightContent = rightTask.getValue();
                rightPanePlaceholder.getChildren().setAll(rightContent.getChildren());
            } catch (Exception e) {
                handleLoadFailure(e, "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterRight.fxml");
            }
        });

        rightTask.setOnFailed(event -> {
            handleLoadFailure(rightTask.getException(), "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/CattleManagerCenterRight.fxml");
        });

        executorService.execute(rightTask);
    }

    private void handleLoadFailure(Throwable exception, String fxmlPath) {
        System.err.println("Error during asynchronous FXML loading: " + fxmlPath);
        if (exception != null) {
            exception.printStackTrace();
        }
    }

    public void animateSplitPane(double targetPosition, SplitPane splitPane, double minPosition, double maxPosition, Button leftArrowButton, Button rightArrowButton) {
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

            KeyFrame keyFrame = new KeyFrame(javafx.util.Duration.millis(animationDurationMillis), dividerKeyValue, leftButtonKeyValue, rightButtonKeyValue);

            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        });
    }

    public void updateButtonsPosition(double dividerPosition, SplitPane splitPane, Button leftArrowButton, Button rightArrowButton) {
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
                e.printStackTrace();
            }
        }
    }
}
