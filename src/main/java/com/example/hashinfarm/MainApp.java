package com.example.hashinfarm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for the home view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/home.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Set up the primary stage to start in fullscreen
        configurePrimaryStage(primaryStage, scene);

        // Show the application
        primaryStage.show();
    }

    private void configurePrimaryStage(Stage primaryStage, Scene scene) {
        // Set the scene
        primaryStage.setScene(scene);

        // Set the stage to be always maximized initially
        primaryStage.setMaximized(true);


    }
}
