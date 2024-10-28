package com.example.hashinfarm.utils.validation;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * This class validates image files based on format and dimensions.
 */
public class FileValidationController {

    private static final String[] ALLOWED_IMAGE_FORMATS = {".jpg", ".jpeg", ".png", ".gif"};
    private static final int MAX_IMAGE_WIDTH = 1920;
    private static final int MAX_IMAGE_HEIGHT = 1080;

    /**
     * Validates the image file at the provided path.
     *
     * @param selectedFilePath The path to the image file.
     * @throws InvalidImageFormatException  If the image format is not allowed.
     * @throws ImageSizeException  If the image dimensions exceed the allowed maximum size.
     * @throws IOException If there's an error accessing the file.
     */
    public void validateImageFile(Path selectedFilePath) throws IOException, InvalidImageFormatException, ImageSizeException {
        validateImageFormat(selectedFilePath);
        validateImageDimensions(selectedFilePath);
    }

    /**
     * Validates the image format of the file.
     *
     * @param selectedFilePath The path to the image file.
     * @throws InvalidImageFormatException  If the image format is not allowed.
     */
    private void validateImageFormat(Path selectedFilePath) throws InvalidImageFormatException {
        String fileName = selectedFilePath.getFileName().toString();
        if (Arrays.stream(ALLOWED_IMAGE_FORMATS).noneMatch(fileName::endsWith)) {
            throw new InvalidImageFormatException("Invalid image format. Allowed formats: " + Arrays.toString(ALLOWED_IMAGE_FORMATS));
        }
    }

    /**
     * Validates the dimensions of the image.
     *
     * @param selectedFilePath The path to the image file.
     * @throws ImageSizeException  If the image dimensions exceed the allowed maximum size.
     * @throws IOException If there's an error accessing the file.
     */
    private void validateImageDimensions(Path selectedFilePath) throws IOException, ImageSizeException {
        try (InputStream is = Files.newInputStream(selectedFilePath)) {
            Image image = new Image(is);
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            if (width > MAX_IMAGE_WIDTH || height > MAX_IMAGE_HEIGHT) {
                throw new ImageSizeException("Image dimensions exceed the allowed maximum size.");
            }
        }
    }

    /**
     * Custom exception for invalid image format.
     */
    public static class InvalidImageFormatException extends Exception {
        public InvalidImageFormatException(String message) {
            super(message);
        }
    }

    /**
     * Custom exception for exceeding image size limit.
     */
    public static class ImageSizeException extends Exception {
        public ImageSizeException(String message) {
            super(message);
        }
    }
}