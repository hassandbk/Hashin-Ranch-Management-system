// CLASS 2 - GenericHandler
package com.example.hashinfarm.controller.handlers.homeCenterPanelHandlers;

import com.example.hashinfarm.controller.interfaces.ActionHandler;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;

public class GenericHandler implements ActionHandler {
    private final String fxmlFileName;

    public GenericHandler(String fxmlFileName) {
        this.fxmlFileName = fxmlFileName;
    }

    @Override
    public void handle(ActionEvent event) {
        // You can choose to handle the event or not, depending on your requirements.
        // In this case, we'll just call the other handle method.
        handle((GridPane) event.getSource());
    }

    @Override
    public void handle(GridPane gridPane) {
        // Instantiate the specific handler based on the fxmlFileName
        ActionHandler specificHandler = getSpecificHandler();

        // Call the handle method of the specific handler
        specificHandler.handle(gridPane);
    }

    // Helper method to get the specific handler based on the fxmlFileName
    private ActionHandler getSpecificHandler() {
        return switch (fxmlFileName) {
            case "Overview.fxml" -> new OverviewHandler();
            case "IncomeExpensesScene.fxml" -> new IncomeExpensesHandler();
            case "ScheduleJournalsScene.fxml" -> new ScheduleJournalsHandler();
            case "OwnerClientScene.fxml" -> new OwnerClientHandler();
            case "FeedsLogisticsScene.fxml" -> new FeedsLogisticsHandler();
            case "CattleManagementScene.fxml" -> new CattleHandler();
            case "SheepManagementScene.fxml" -> new SheepHandler();
            case "GoatsManagementScene.fxml" -> new GoatsHandler();
            case "PoultryManagementScene.fxml" -> new PoultryHandler();
            case "FishManagementScene.fxml" -> new FishHandler();
            // Add cases for other fxml files and their respective handlers...
            default -> throw new IllegalArgumentException("Handler not found for fxmlFileName: " + fxmlFileName);
        };
    }
}
