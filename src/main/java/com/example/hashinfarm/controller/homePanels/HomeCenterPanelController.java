// CLASS 1 - HomeCenterPanelController
package com.example.hashinfarm.controller.homePanels;

import com.example.hashinfarm.controller.handlers.homeCenterPanelHandlers.GenericHandler;
import com.example.hashinfarm.controller.interfaces.ActionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class HomeCenterPanelController {

    @FXML
    private GridPane mainGridPane;

    @FXML
    private void initialize() {
        // Initialization logic
    }

    @FXML
    private void handleButtonClick(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button clickedButton) {
            String fxId = clickedButton.getId();
            String fxmlFileName = getFXMLFileName(fxId);
            handleButtonAction(fxmlFileName);
        }
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

    private void handleButtonAction(String fxmlFileName) {
        if (fxmlFileName != null) {
            ActionHandler handler = new GenericHandler(fxmlFileName);
            handler.handle(mainGridPane);
        }
    }
}
