package com.example.hashinfarm.presentationLayer.controllers.home;

import com.example.hashinfarm.presentationLayer.controllers.LoadingStage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HomeCenterPanelController {

    @FXML private Button handleOverviewButtonClick, handleIncomeExpensesButtonClick, handleScheduleJournalsButtonClick,
            handleOwnerClientButtonClick, handleFeedsLogisticsButtonClick, handleCattleButtonClick,
            handleSheepButtonClick, handleGoatsButtonClick, handlePoultryButtonClick, handleFishButtonClick;

    @FXML private GridPane mainGridPane;
    private final LoadingStage loadingStage = new LoadingStage();

    private Map<Button, String> viewMap;

    @FXML
    private void initialize() {
        initializeViewMap();
    }

    private void initializeViewMap() {
        viewMap = Map.of(
                handleOverviewButtonClick, "OverviewView.fxml",
                handleIncomeExpensesButtonClick, "IncomeExpensesView.fxml",
                handleScheduleJournalsButtonClick, "ScheduleJournalsView.fxml",
                handleOwnerClientButtonClick, "OwnerClientView.fxml",
                handleFeedsLogisticsButtonClick, "FeedsLogisticsView.fxml",
                handleCattleButtonClick, "CattleManagementView.fxml",
                handleSheepButtonClick, "SheepManagementView.fxml",
                handleGoatsButtonClick, "GoatsManagementView.fxml",
                handlePoultryButtonClick, "PoultryManagementView.fxml",
                handleFishButtonClick, "FishManagementView.fxml"
        );
    }

    @FXML
    private void handleButtonClick(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button clickedButton) {
            String fxmlFileName = viewMap.get(clickedButton);
            if (fxmlFileName != null) {
                showLoadingStage();
                CompletableFuture.supplyAsync(() -> loadNewView(fxmlFileName))
                        .thenAccept(success -> {
                            if (!success) {
                                System.err.println("Failed to load view: " + fxmlFileName);
                            }
                            Platform.runLater(this::closeLoadingStage);
                        });
            } else {
                System.err.println("No FXML file mapped for button: " + clickedButton.getId());
            }
        }
    }

    private void showLoadingStage() {
        Platform.runLater(loadingStage::show);
    }

    private boolean loadNewView(String fxmlFileName) {
        try {
            loadSceneInBackground(mainGridPane, fxmlFileName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadSceneInBackground(GridPane gridPane, String fxmlFileName) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    URL resourceUrl = getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/" + fxmlFileName);
                    if (resourceUrl == null) {
                        throw new IOException("FXML file not found: " + fxmlFileName);
                    }
                    FXMLLoader loader = new FXMLLoader(resourceUrl);
                    return (Node) loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }, executor).thenAccept(newContent -> {
                if (newContent != null) {
                    Platform.runLater(() -> showScene(gridPane, newContent));  // Ensure UI updates on FX thread
                } else {
                    System.err.println("Failed to load the new content for: " + fxmlFileName);
                }
            }).whenComplete((result, error) -> {
                if (error != null) {
                    error.printStackTrace();
                }
                executor.shutdown();  // Close the executor explicitly
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!executor.isShutdown()) {
                    executor.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showScene(GridPane gridPane, Node newContent) {
        VBox contentHolder = new VBox();
        contentHolder.getChildren().add(newContent);

        Node gridPaneParent = gridPane.getParent();
        if (gridPaneParent instanceof BorderPane oldContent) {
            oldContent.setCenter(contentHolder);
            oldContent.setLeft(null);
            oldContent.setBottom(null);

            VBox.setVgrow(contentHolder, Priority.ALWAYS);
            BorderPane.setAlignment(contentHolder, javafx.geometry.Pos.TOP_CENTER);
            BorderPane.setMargin(contentHolder, new javafx.geometry.Insets(10, 10, 10, 10));
        }
    }

    private void closeLoadingStage() {
        CompletableFuture.delayedExecutor(10000, TimeUnit.MILLISECONDS)
                .execute(() -> Platform.runLater(loadingStage::close));
    }
}
