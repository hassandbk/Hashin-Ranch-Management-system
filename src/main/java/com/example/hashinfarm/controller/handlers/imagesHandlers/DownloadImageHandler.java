package com.example.hashinfarm.controller.handlers.imagesHandlers;


import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import java.nio.file.StandardCopyOption;

public class DownloadImageHandler {

    public DownloadImageHandler() {
    }

    public void handleImageDownload(Stage stage, int currentIndex) {
        // Handle image download logic
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        // Set the initial file name (optional)
        fileChooser.setInitialFileName("downloaded_image.png");

        // Show save dialog
        File fileToSave = fileChooser.showSaveDialog(stage);

        if (fileToSave != null) {
            // Get the name of the currently displayed image
            String imageName = "cow (" + (currentIndex + 1) + ").png";
            InputStream imageStream = getClass().getResourceAsStream("/images/" + imageName);

            // Copy the image to the selected destination
            try {
                if (imageStream != null) {
                    Files.copy(imageStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    showAlert("Image Downloaded", "Image has been successfully downloaded to:\n" + fileToSave.getAbsolutePath(), Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to download the image.", Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "Failed to download the image.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
