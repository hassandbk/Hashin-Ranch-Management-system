
package com.example.hashinfarm.utils;

import java.io.IOException;

import com.example.hashinfarm.utils.logging.AppLogger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class FXMLLoaderUtil {

    public static void loadFXML(String fxmlPath, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FXMLLoaderUtil.class.getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            // Log the exception with AppLogger (error level)
            AppLogger.error("Error loading FXML file: " + fxmlPath, e);
            // Optionally, display a user-friendly error message
            showAlert("Error", "Failed to load FXML: " + fxmlPath);
        }
    }
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
