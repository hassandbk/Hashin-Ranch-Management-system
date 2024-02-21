package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.controller.handlers.imagesHandlers.ImageFileUploader;

import java.io.IOException;
import java.nio.file.Path;

public class FileValidationController {

    private ImageFileUploader imageFileUploader;

    public FileValidationController() {
        // Initialize the image file uploader
        imageFileUploader = new ImageFileUploader();
    }

    public void validateImageFile(Path selectedImagePath) {
        try {
            // Validate the image using the image file uploader
            imageFileUploader.handleFileUpload(selectedImagePath);
        } catch (IOException e) {
            // Handle exceptions related to image validation
            e.printStackTrace(); // Log or handle the exception based on your application's needs
        }
    }
}