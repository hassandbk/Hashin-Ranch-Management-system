package com.example.hashinfarm.controller.homePanels;

import com.example.hashinfarm.controller.handlers.homeCenterPanelHandlers.GenericHandler;
import com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.LoadingStage;
import com.example.hashinfarm.controller.interfaces.ActionHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HomeCenterPanelController {

    @FXML
    private GridPane mainGridPane;

    private final Map<String, String> viewMap = new HashMap<>();
    private LoadingStage loadingStage;

    @FXML
    private void initialize() {
        initializeViewMap();
        loadingStage = new LoadingStage(); // Initialize loading stage
        // Initialization logic
    }

    private void initializeViewMap() {
        viewMap.put("handleOverviewButtonClick", "Overview.fxml");
        viewMap.put("handleIncomeExpensesButtonClick", "IncomeExpensesScene.fxml");
        viewMap.put("handleScheduleJournalsButtonClick", "ScheduleJournalsScene.fxml");
        viewMap.put("handleOwnerClientButtonClick", "OwnerClientScene.fxml");
        viewMap.put("handleFeedsLogisticsButtonClick", "FeedsLogisticsScene.fxml");
        viewMap.put("handleCattleButtonClick", "CattleManagementScene.fxml");
        viewMap.put("handleSheepButtonClick", "SheepManagementScene.fxml");
        viewMap.put("handleGoatsButtonClick", "GoatsManagementScene.fxml");
        viewMap.put("handlePoultryButtonClick", "PoultryManagementScene.fxml");
        viewMap.put("handleFishButtonClick", "FishManagementScene.fxml");
        // Add mappings for other buttons...
    }

    @FXML
    private void handleButtonClick(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button clickedButton) {
            String fxId = clickedButton.getId();
            String fxmlFileName = viewMap.get(fxId);
            if (fxmlFileName != null) {
                // Show loading stage
                showLoadingStage();

                CompletableFuture.runAsync(() -> loadNewView(fxmlFileName))
                        .thenRun(this::closeLoadingStageWithDelay); // Close loading stage after 2 seconds
            }
        }
    }

    private void showLoadingStage() {
        loadingStage.show(); // Show loading stage
    }

    private void loadNewView(String fxmlFileName) {
        ActionHandler handler = new GenericHandler(fxmlFileName);
        // Ensure UI updates happen on the JavaFX application thread
        Platform.runLater(() -> handler.handle(mainGridPane));
    }

    private void closeLoadingStageWithDelay() {
        CompletableFuture.delayedExecutor(2000, TimeUnit.MILLISECONDS)
                .execute(() -> Platform.runLater(() -> loadingStage.close()));
    }
}
