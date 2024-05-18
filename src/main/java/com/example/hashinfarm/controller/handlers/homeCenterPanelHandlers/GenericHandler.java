package com.example.hashinfarm.controller.handlers.homeCenterPanelHandlers;

import com.example.hashinfarm.controller.interfaces.ActionHandler;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenericHandler implements ActionHandler {
    private final String fxmlFileName;

    public GenericHandler(String fxmlFileName) {
        this.fxmlFileName = fxmlFileName;
    }

    @Override
    public void handle(ActionEvent event) {
        handle((GridPane) event.getSource());
    }

    @Override
    public void handle(GridPane gridPane) {
        loadSceneInBackground(gridPane, fxmlFileName);
    }

    private void loadSceneInBackground(GridPane gridPane, String fxmlFileName) {
    try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
        Task<Node> loadTask = new Task<>() {
            @Override
            protected Node call() throws Exception {
                URL resourceUrl = getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/" + fxmlFileName);
                if (resourceUrl == null) {
                    throw new IOException("FXML file not found: " + fxmlFileName);
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                return loader.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            Node newContent = loadTask.getValue();
            showScene(gridPane, newContent);
        });

        loadTask.setOnFailed(event -> {
            Throwable exception = loadTask.getException();
            showErrorDialog(exception);
        });

        executor.submit(loadTask);
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
            BorderPane.setAlignment(contentHolder, Pos.TOP_CENTER);
            BorderPane.setMargin(contentHolder, new Insets(10, 10, 10, 10));
        }
    }

    private void showErrorDialog(Throwable exception) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        });
    }
}
