package com.example.hashinfarm.app;


import com.example.hashinfarm.presentationLayer.controllers.CattleController;
import com.example.hashinfarm.utils.logging.AppLogger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainApp extends Application {
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final CattleController cattleController;

    public MainApp() {
        this.cattleController = new CattleController();
    }

    public static void main(String[] args) {
        launch(MainApp.class);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the home view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/Home.fxml"));
            Parent root = loader.load();

            // Configure primary stage
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("HashFarm");
            primaryStage.setMaximized(true);
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                shutdownApplication();
            });

            primaryStage.show();
        } catch (IOException e) {
            // Catch and log the IOException
            AppLogger.error("IOException while loading FXML file: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        } catch (Exception e) {
            // Catch any other exceptions
            AppLogger.error("Exception in Application start method: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        shutdownApplication();
    }

    private void shutdownApplication() {
        cattleController.shutdownExecutorService();

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                AppLogger.warn("ExecutorService did not terminate gracefully, forcing shutdown.");
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    AppLogger.error("ExecutorService could not be terminated.");
                }
            }
        } catch (InterruptedException e) {
            AppLogger.error("Interrupted while waiting for ExecutorService termination: " + e.getMessage());
            e.printStackTrace();
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }
}
