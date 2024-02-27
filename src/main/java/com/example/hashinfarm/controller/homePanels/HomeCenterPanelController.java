package com.example.hashinfarm.controller.homePanels;

import com.example.hashinfarm.controller.handlers.homeCenterPanelHandlers.GenericHandler;
import com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.LoadingStage;
import com.example.hashinfarm.controller.interfaces.ActionHandler;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HomeCenterPanelController {

    @FXML
    private GridPane mainGridPane;

    private LoadingStage loadingStage;

    @FXML
    private void initialize() {
        loadingStage = new LoadingStage(); // Initialize loading stage
        // Initialization logic
    }

    @FXML
    private void handleButtonClick(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button clickedButton) {
            String fxId = clickedButton.getId();
            String fxmlFileName = getFXMLFileName(fxId);
            if (fxmlFileName != null) {
                // Show loading stage
                showLoadingStage();

                CompletableFuture<Void> viewLoadingFuture = CompletableFuture.runAsync(() -> {
                    // Load the new view asynchronously
                    loadNewView(fxmlFileName);
                });

                // Combine both futures to execute closing the loading stage when the view is loaded
                viewLoadingFuture.thenRun(() -> closeLoadingStageWithDelay(2000)); // Close loading stage after 2 seconds
            }
        }
    }


    private void showLoadingStage() {
        loadingStage.show(); // Show loading stage
    }

    private String getFXMLFileName(String fxid) {
        return switch (fxid) {
            case "handleOverviewButtonClick" -> "Overview.fxml";
            case "handleIncomeExpensesButtonClick" -> "IncomeExpensesScene.fxml";
            case "handleScheduleJournalsButtonClick" -> "ScheduleJournalsScene.fxml";
            case "handleOwnerClientButtonClick" -> "OwnerClientScene.fxml";
            case "handleFeedsLogisticsButtonClick" -> "FeedsLogisticsScene.fxml";
            case "handleCattleButtonClick" -> "CattleManagementScene.fxml";
            case "handleSheepButtonClick" -> "SheepManagementScene.fxml";
            case "handleGoatsButtonClick" -> "GoatsManagementScene.fxml";
            case "handlePoultryButtonClick" -> "PoultryManagementScene.fxml";
            case "handleFishButtonClick" -> "FishManagementScene.fxml";
            // Add cases for other buttons...
            default -> null; // Handle unknown case gracefully
        };
    }

    private void loadNewView(String fxmlFileName) {
        if (fxmlFileName != null) {
            ActionHandler handler = new GenericHandler(fxmlFileName);
            // Ensure UI updates happen on the JavaFX application thread
            Platform.runLater(() -> handler.handle(mainGridPane));
        }
    }



    private void closeLoadingStageWithDelay(long delayInMillis) {
        CompletableFuture.delayedExecutor(delayInMillis, TimeUnit.MILLISECONDS)
                .execute(() -> Platform.runLater(() -> loadingStage.close()));
    }
}
