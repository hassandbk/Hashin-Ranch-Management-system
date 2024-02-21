package com.example.hashinfarm.controller.handlers.imagesHandlers;

import com.example.hashinfarm.controller.utility.GenericFileUploader;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;

public class ImageFileUploader extends GenericFileUploader {

    private static final int TARGET_WIDTH = 800;
    private static final int TARGET_HEIGHT = 600;
    private static final String DESTINATION_FOLDER = "src/main/resources/images";
    private static final String FILE_EXTENSION = ".png";

    @Override
    protected void handleSpecificFileUpload(Path selectedFilePath) {
        try {
            // Implement specific logic for image file upload
            // For example, resize the image, save it to a specific folder, etc.
            resizeAndSaveImage(selectedFilePath);

            // Additional logic: update a database or trigger additional processes
            updateDatabase(selectedFilePath);
            triggerAdditionalProcesses();

            // Show success dialog box
            showSuccessDialog();
        } catch (IOException e) {
            // Handle IO exception
            showFailureDialog("Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            showFailureDialog("Unexpected error: " + e.getMessage());
        }
    }

    private void resizeAndSaveImage(Path selectedFilePath) throws IOException {
        try {
            // Read the original image
            BufferedImage originalImage = ImageIO.read(selectedFilePath.toFile());

            // Resize the image
            BufferedImage resizedImage = Scalr.resize(originalImage, TARGET_WIDTH, TARGET_HEIGHT);

            // Define the destination folder and file name
            Path destinationDirectory = Paths.get(DESTINATION_FOLDER);
            String resizedFileName = generateUniqueFileName(destinationDirectory, "cow", FILE_EXTENSION);

            // Create destination path
            createDirectoryIfNotExists(destinationDirectory);
            Path destinationPath = destinationDirectory.resolve(resizedFileName);

            // Save the resized image as PNG
            ImageIO.write(resizedImage, "png", destinationPath.toFile());
        } catch (IOException e) {
            // Handle IO exception during image processing
            throw new IOException("Error resizing and saving image: " + e.getMessage(), e);
        }
    }

    private String generateUniqueFileName(Path destinationDirectory, String baseName, String fileExtension) {
        Path destinationPath = destinationDirectory.resolve(baseName + fileExtension);
        int counter = 1;

        // Check for existing files
        while (Files.exists(destinationPath)) {
            String newFileName = baseName + " (" + counter + ")" + fileExtension;
            destinationPath = destinationDirectory.resolve(newFileName);
            counter++;
        }

        return destinationPath.getFileName().toString();
    }

    private void createDirectoryIfNotExists(Path directory) throws IOException {
        if (Files.notExists(directory)) {
            Files.createDirectories(directory);
        }
    }
    private String getBaseFileName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    private void updateDatabase(Path selectedFilePath) {
        // Add logic to update a database based on the file upload
        // This can include inserting information about the uploaded file.
        // Example: Update a database table with file details such as name, path, and timestamp.
    }

    private void triggerAdditionalProcesses() {
        // Add logic to trigger any additional processes needed after file upload.
    }

    // Dialog box methods
    private void showSuccessDialog() {
        JOptionPane.showMessageDialog(null, "File uploaded successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showFailureDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
