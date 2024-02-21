// CLASS 6 - CattleHandler
package com.example.hashinfarm.controller.handlers.homeCenterPanelHandlers;

import com.example.hashinfarm.controller.interfaces.ActionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CattleHandler implements ActionHandler {

    @Override
    public void handle(ActionEvent event) {
        // You can choose to handle the event or not, depending on your requirements.
        // In this case, we'll just call the other handle method.
        handle((GridPane) event.getSource());
    }

    @Override
    public void handle(GridPane gridPane) {
        loadScene(gridPane, "CattleManagementScene.fxml");
    }

    private void loadScene(GridPane gridPane, String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/" + fxmlFileName));
            Node newContent = loader.load();

            // Create a VBox to hold the new content
            VBox contentHolder = new VBox();
            contentHolder.getChildren().add(newContent);

            // Assuming the GridPane is a direct child of the BorderPane in your XML
            Node gridPaneParent = gridPane.getParent();
            if (gridPaneParent instanceof BorderPane oldContent) {

                // Set the VBox as the center of the BorderPane
                oldContent.setCenter(contentHolder);
                oldContent.setLeft(null);  // Remove the left content
                oldContent.setBottom(null);  // Remove the bottom content

                // Set the VBox to expand and fill the available space
                VBox.setVgrow(contentHolder, Priority.ALWAYS);

                // Set the alignment and margin for the VBox
                BorderPane.setAlignment(contentHolder, Pos.TOP_CENTER);
                BorderPane.setMargin(contentHolder, new Insets(10, 10, 10, 10)); // Adjust the margin as needed
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
