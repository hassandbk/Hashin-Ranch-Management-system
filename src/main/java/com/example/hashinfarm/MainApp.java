package com.example.hashinfarm;

import com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.CattleController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private CattleController cattleController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for the home view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/home.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Get the controller instance
        CattleController cattleController = loader.getController();

        // Set up the primary stage to start in fullscreen
        configurePrimaryStage(primaryStage, scene);

        // Show the application
        primaryStage.show();

        // Add shutdown hook
        primaryStage.setOnCloseRequest(event -> {
            if (cattleController != null) {
                cattleController.shutdown();
            }
        });
    }


    private void configurePrimaryStage(Stage primaryStage, Scene scene) {
        // Set the scene
        primaryStage.setScene(scene);

        // Set the stage to be always maximized initially
        primaryStage.setMaximized(true);
    }
}
