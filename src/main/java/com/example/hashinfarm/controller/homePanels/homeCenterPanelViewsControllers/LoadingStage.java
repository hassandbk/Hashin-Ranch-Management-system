package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class LoadingStage {
    private Stage stage;
    private Timeline timeline;
    private ProgressBar loadingProgressBar;

    public LoadingStage() {
        initialize();
    }

    private void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/LoadingScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED); // Remove window decorations
            stage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
            stage.setTitle("Loading...");
            stage.setResizable(false);

            // Get the progress bar from the loaded FXML
            loadingProgressBar = (ProgressBar) loader.getNamespace().get("loadingProgressBar");

            // Initialize the timeline for the progress bar animation
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(loadingProgressBar.progressProperty(), 0)),
                    new KeyFrame(Duration.seconds(3), new KeyValue(loadingProgressBar.progressProperty(), 1))
            );
            timeline.setCycleCount(1); // Only one cycle for this animation

            // Set color change handler for the progress bar
            loadingProgressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
                updateProgressBarColor(newValue.doubleValue());
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProgressBarColor(double progressValue) {
        // You can set the color to green gradually as progress increases
        String colorStyle = String.format("-fx-accent: rgb(%d,%d,0);",
                (int)(progressValue * 255), (int)(progressValue * 255));
        loadingProgressBar.setStyle(colorStyle);
    }

    public void show() {
        stage.show();
        timeline.play(); // Start the progress bar animation
    }

    public void close() {
        stage.close();
        timeline.stop(); // Stop the animation if the stage is closed
    }
}
