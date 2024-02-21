// CLASS 9 - PoultryHandler
package com.example.hashinfarm.controller.handlers.homeCenterPanelHandlers;

import com.example.hashinfarm.controller.interfaces.ActionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class PoultryHandler implements ActionHandler {

    @Override
    public void handle(ActionEvent event) {
        // You can choose to handle the event or not, depending on your requirements.
        // In this case, we'll just call the other handle method.
        handle((GridPane) event.getSource());
    }

    @Override
    public void handle(GridPane gridPane) {
        loadScene(gridPane, "PoultryManagementScene.fxml");
    }

    private void loadScene(GridPane gridPane, String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/" + fxmlFileName));
            Node node = loader.load();
            gridPane.getChildren().clear();
            gridPane.getChildren().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
