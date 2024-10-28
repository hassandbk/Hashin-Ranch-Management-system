package com.example.hashinfarm.presentationLayer.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class LoadingStage {
  private Stage stage;
  private Timeline timeline;
  private ProgressBar loadingProgressBar;
  private Label loadingLabel;

  public LoadingStage() {
    initialize();
  }

  private void initialize() {
    // Create the progress bar
    loadingProgressBar = new ProgressBar(0);
    loadingProgressBar.setPrefWidth(600);  // Decrease width to match the progress bar

    // Create the label for loading text
    loadingLabel = new Label("LOADING... 0%");
    loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #006400;");

    loadingLabel.setMaxWidth(600);
    loadingLabel.setMinWidth(600);
    loadingLabel.setAlignment(Pos.CENTER_LEFT);

    // Create a VBox to hold the progress bar and label
    VBox vbox = new VBox(10);  // 10 is the spacing between elements
    vbox.setAlignment(Pos.CENTER);
    vbox.getChildren().addAll(loadingProgressBar, loadingLabel);

    // Create a StackPane as the root layout
    StackPane root = new StackPane();
    root.getChildren().add(vbox);
    root.setPrefWidth(500);  // Set the preferred width of the root
    root.setPrefHeight(50); // Set the preferred height of the root to thrice its original
    root.setAlignment(Pos.CENTER);

    // Create the scene with the root layout
    Scene scene = new Scene(root);

    // Set up the stage
    stage = new Stage();
    stage.setScene(scene);
    stage.initStyle(StageStyle.UNDECORATED); // Remove window decorations
    stage.initModality(Modality.APPLICATION_MODAL); // Prevent interaction with other windows
    stage.setTitle("Loading...");
    stage.setResizable(false);

    // Initialize the timeline for the progress bar animation
    timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(loadingProgressBar.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(3), new KeyValue(loadingProgressBar.progressProperty(), 1))
    );
    timeline.setCycleCount(1); // Only one cycle for this animation

    // Add a listener to update the label text dynamically
    loadingProgressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
      updateProgressBarColor(newValue.doubleValue());
      updateLoadingLabel(newValue.doubleValue());
    });
  }

  private void updateProgressBarColor(double progressValue) {
    // Color transition from dark blue (0,0,139) to light blue (173,216,230)
    int red = (int) (173 * progressValue);
    int green = (int) (216 * progressValue);
    int blue = 139 + (int) ((230 - 139) * progressValue);
    String colorStyle = String.format("-fx-accent: rgb(%d,%d,%d);", red, green, blue);
    loadingProgressBar.setStyle(colorStyle);
  }

  private void updateLoadingLabel(double progressValue) {
    int percentage = (int) (progressValue * 100);
    int dotCount = (int) (progressValue * 60); // Adjust to change the number of dots
    String dots = "..".repeat(dotCount);
    Platform.runLater(() -> loadingLabel.setText(String.format("LOADING%s %d%%", dots, percentage)));
  }

  public void show() {
    stage.centerOnScreen();
    stage.show();
    timeline.play(); // Start the progress bar animation
  }

  public void close() {
    stage.close();
    timeline.stop(); // Stop the animation if the stage is closed
  }
}
