package com.example.hashinfarm.controller.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javafx.scene.image.Image;

public class ImageFileValidator {

    private static final String[] ALLOWED_IMAGE_FORMATS = {".jpg", ".jpeg", ".png", ".gif"};
    private static final int MAX_IMAGE_WIDTH = 1920;
    private static final int MAX_IMAGE_HEIGHT = 1080;

    public void validateImageFile(Path selectedFilePath) throws IOException {
        validateImageFormat(selectedFilePath);
        validateImageDimensions(selectedFilePath);
    }

    private void validateImageFormat(Path selectedFilePath) throws IOException {
        String fileName = selectedFilePath.getFileName().toString();

        // Check if the file has an allowed image format
        if (Arrays.stream(ALLOWED_IMAGE_FORMATS).noneMatch(fileName::endsWith)) {
            throw new IOException("Invalid image format. Allowed formats: " + Arrays.toString(ALLOWED_IMAGE_FORMATS));
        }
    }

    private void validateImageDimensions(Path selectedFilePath) throws IOException {
        try (InputStream is = Files.newInputStream(selectedFilePath)) {
            Image image = new Image(is);
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();

            // Check image dimensions
            if (width > MAX_IMAGE_WIDTH || height > MAX_IMAGE_HEIGHT) {
                throw new IOException("Image dimensions exceed the allowed maximum size.");
            }
        }
    }
}
