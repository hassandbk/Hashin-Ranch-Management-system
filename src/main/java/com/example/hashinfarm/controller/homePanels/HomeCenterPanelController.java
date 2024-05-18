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
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeCenterPanelController {

    private final Map<String, String> viewMap = new HashMap<>();

    @FXML
    private GridPane mainGridPane;
    private final LoadingStage loadingStage = new LoadingStage(); // Initialize loading stage
    private final Logger logger = Logger.getLogger(HomeCenterPanelController.class.getName());

    @FXML
    private void initialize() {
        initializeViewMap();
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
                showLoadingStage();
                CompletableFuture.supplyAsync(() -> loadNewView(fxmlFileName))
                        .thenAcceptAsync(success -> {
                            if (!success) {
                                logger.log(Level.SEVERE, "Failed to load view: " + fxmlFileName);
                            }
                            closeLoadingStage();
                        }, Platform::runLater);
            }
        }
    }

    private void showLoadingStage() {
        loadingStage.show(); // Show loading stage
    }

    private boolean loadNewView(String fxmlFileName) {
        try {
            ActionHandler handler = new GenericHandler(fxmlFileName);
            Platform.runLater(() -> handler.handle(mainGridPane));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error loading view: " + fxmlFileName, e);
            return false;
        }
    }

    private void closeLoadingStage() {
        CompletableFuture.delayedExecutor(10000, TimeUnit.MILLISECONDS)
                .execute(() -> Platform.runLater(loadingStage::close));
    }
}
