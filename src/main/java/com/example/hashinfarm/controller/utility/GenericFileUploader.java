package com.example.hashinfarm.controller.utility;

import java.io.IOException;
import java.nio.file.*;

public abstract class GenericFileUploader {

    public void handleFileUpload(Path selectedFilePath) throws IOException {
        try {
            // Perform generic operations using selectedFilePath
            validateFile(selectedFilePath);

            // Delegate to specific implementation
            handleSpecificFileUpload(selectedFilePath);
        } catch (IOException e) {
            // Handle exceptions related to file operations
            e.printStackTrace(); // Log or handle the exception based on your application's needs
        }
    }

    protected abstract void handleSpecificFileUpload(Path selectedFilePath) throws IOException;

    private void validateFile(Path selectedFilePath) throws IOException {
        // Add logic to validate the file, such as file type, size, etc.
        // This can be customized based on your requirements.
        // Throw an IOException if the file is not valid.
        // Example: Check if it's a valid file type (e.g., .txt, .pdf) and within an acceptable size range.
    }
}
