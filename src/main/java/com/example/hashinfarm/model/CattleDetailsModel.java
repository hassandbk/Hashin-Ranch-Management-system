package com.example.hashinfarm.model;

import com.example.hashinfarm.controller.utility.ImageUtil;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CattleDetailsModel {

    private int currentIndex = 0;
    private final List<ImageView> imageViews = new ArrayList<>();
    private final List<Path> imagePaths = new ArrayList<>();

    public void populateImages(HBox imageContainer) {
        int totalImages = getTotalImages();

        for (int i = 1; i <= totalImages; i++) {
            String imageName = "cow (" + i + ").png";
            InputStream imageStream = getClass().getResourceAsStream("/images/" + imageName);

            if (imageStream != null) {
                Image image = new Image(imageStream);

                double maxWidth = 438.0;
                double maxHeight = 205.0;

                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(maxWidth);
                imageView.setFitHeight(maxHeight);
                imageView.setOpacity(0.0); // Initially set opacity to 0

                imageViews.add(imageView);
                imageContainer.getChildren().add(imageView);
            } else {
                System.err.println("Image not found: " + imageName);
            }
        }

        // Start automatic transition after a delay
        startAutomaticTransition();
    }


    private void startAutomaticTransition() {
        Duration duration = Duration.seconds(5); // 5 seconds delay between transitions
        Timeline timeline = new Timeline(new KeyFrame(duration, event -> nextImage()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    private int getTotalImages() {
        // You need to implement the logic to determine the total number of images dynamically.
        // For example, you can list files in the "/images/" directory and count them.
        // Replace this with the actual logic based on your application's structure.

        // For demonstration purposes, let's assume there are 5 images.
        return 5;
    }

    public void previousImage() {
        currentIndex = (currentIndex - 1 + imageViews.size()) % imageViews.size();
        transitionToCurrentIndex();
    }

    public void nextImage() {
        currentIndex = (currentIndex + 1) % imageViews.size();
        transitionToNextImage();
    }

    private void transitionToNextImage() {
        ImageView currentImageView = imageViews.get(currentIndex);
        currentIndex = (currentIndex + 1) % imageViews.size();
        ImageView nextImageView = imageViews.get(currentIndex);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), currentImageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), nextImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        SequentialTransition sequentialTransition = new SequentialTransition(fadeOut, fadeIn);
        sequentialTransition.play();
    }

    private void transitionToCurrentIndex() {
        ImageView currentImageView = imageViews.get(currentIndex);
        currentIndex = (currentIndex - 1 + imageViews.size()) % imageViews.size();
        ImageView previousImageView = imageViews.get(currentIndex);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), currentImageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), previousImageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        SequentialTransition sequentialTransition = new SequentialTransition(fadeOut, fadeIn);
        sequentialTransition.play();
    }


    public Path getCurrentImagePath() {
        // Retrieve the path of the currently displayed image
        return imagePaths.get(currentIndex);
    }
    public int getCurrentIndex() {
        return currentIndex;
    }
}