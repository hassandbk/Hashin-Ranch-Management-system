package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.controller.dao.CattleImageDAO;
import com.example.hashinfarm.model.CattleImage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.HBox;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CattleImageManager {
    private final CattleImageDAO cattleImageDAO;
    private final int MAX_IMAGES_DISPLAYED = 5;
    private int currentIndex = 0;
    private final Timeline timeline;
    private List<CattleImage> cattleImages; // Store fetched images
    private final List<ImageView> imageViews = new ArrayList<>();

    public CattleImageManager() {
        this.cattleImageDAO = new CattleImageDAO();
        this.timeline = new Timeline();

    }

    public void fetchAndPopulateCarousel(int cattleId, HBox container) {
        // Clear previous image views
        container.getChildren().clear();
        // Fetch images based on cattle ID
        cattleImages = cattleImageDAO.getCattleImagesByCattleId(cattleId);
        if (cattleImages.isEmpty()) {
            System.out.println("No images found for cattle ID: " + cattleId);
            return;
        }

        // Populate image views in the provided container
        for (int i = 0; i < MAX_IMAGES_DISPLAYED; i++) {
            int imageIndex = (currentIndex + i) % cattleImages.size();
            String imageName = cattleImages.get(imageIndex).getImagePath();
            String imagePath = "/images/" + imageName; // Constructing the image path
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(438); // Set desired width
            imageView.setFitHeight(205); // Set desired height
            imageView.setPreserveRatio(true); // Preserve aspect ratio

            // Apply blue outline and scaling to the third image
            if (i == 2) {
                imageView.setScaleX(1.4); // Scale factor for X dimension
                imageView.setScaleY(1.4); // Scale factor for Y dimension
                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(20.0);
                dropShadow.setOffsetX(5.0);
                dropShadow.setOffsetY(5.0);
                dropShadow.setColor(Color.LIGHTSKYBLUE);
                imageView.setEffect(dropShadow);
            }
            // Apply scaling and shape to specific images
            if (i == 0 || i == 4) {
                imageView.setScaleX(0.5); // Scale factor for X dimension
                imageView.setScaleY(0.5); // Scale factor for Y dimension
                imageView.setClip(new Circle(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, imageView.getFitWidth() / 2));
            } else if (i == 1 || i == 3) {
                imageView.setScaleX(0.8); // Scale factor for X dimension
                imageView.setScaleY(0.8); // Scale factor for Y dimension
                imageView.setClip(new Circle(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, imageView.getFitWidth() / 2));
            }

            container.getChildren().add(imageView);
            imageViews.add(imageView);
        }

        initializeTimeline(); // Call initializeTimeline after fetching and populating images
    }


    private void initializeTimeline() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(5), event -> {
            currentIndex = (currentIndex + 1) % cattleImages.size();
            updateImageContainer();
        }));
        timeline.play();
    }

    private void updateImageContainer() {
        for (int i = 0; i < MAX_IMAGES_DISPLAYED; i++) {
            int imageIndex = (currentIndex + i) % cattleImages.size();
            String imageName = cattleImages.get(imageIndex).getImagePath();
            String imagePath = "/images/" + imageName; // Constructing the image path
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            imageViews.get(i).setImage(image);
        }
    }

    public void nextImage() {
        currentIndex = (currentIndex + 1) % cattleImages.size();
        updateImageContainer();
    }

    public void previousImage() {
        currentIndex = (currentIndex - 1 + cattleImages.size()) % cattleImages.size();
        updateImageContainer();
    }

    public void pauseCarousel() {
        timeline.pause();
    }

    public void resumeCarousel() {
        timeline.play();
    }

    public void displayCurrentImage() {
        // Pause the carousel transition
        pauseCarousel();

        // Get the image path of the third image
        String imagePath = "/images/" + cattleImages.get((currentIndex + 2) % cattleImages.size()).getImagePath();

        // Load the image
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));

        // Calculate stage dimensions based on image dimensions
        double stageWidth = image.getWidth() + 20;
        double stageHeight = Math.min(image.getHeight() + 20, Screen.getPrimary().getVisualBounds().getHeight());

        // Create and configure the stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Current Image");
        stage.setResizable(false);
        stage.setMinWidth(300);
        stage.setMinHeight(200);

        ScrollPane scrollPane = getScrollPane(image);

        // Close the stage and resume carousel transition when the window is closed
        stage.setOnCloseRequest(event -> {
            stage.close();
            resumeCarousel();
        });

        // Set the stage dimensions and scene
        stage.setScene(new Scene(scrollPane, stageWidth, stageHeight));
        stage.show();
    }

    @NotNull
    private static ScrollPane getScrollPane(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Make the image zoom and scrollable
        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() < 0 ? 1 / 1.05 : 1.05;
            imageView.setScaleX(imageView.getScaleX() * zoomFactor);
            imageView.setScaleY(imageView.getScaleY() * zoomFactor);
            event.consume();
        });
        return scrollPane;
    }



    public void saveImagesToZip(int cattleId) {
        // Retrieve images based on cattle ID
        List<CattleImage> cattleImages = cattleImageDAO.getCattleImagesByCattleId(cattleId);
        if (cattleImages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No images found for cattle ID: " + cattleId, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create zip file in Downloads directory
        Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
        File zipFile = new File(downloadsDirectory.toString(), "CattleImages_" + cattleId + ".zip");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (CattleImage cattleImage : cattleImages) {
                String imagePath = "/images/" + cattleImage.getImagePath();
                File imageFile = new File(Objects.requireNonNull(getClass().getResource(imagePath)).getFile());

                ZipEntry zipEntry = new ZipEntry(imageFile.getName());
                zipOutputStream.putNextEntry(zipEntry);

                Files.copy(imageFile.toPath(), zipOutputStream);
                zipOutputStream.closeEntry();
            }
            JOptionPane.showMessageDialog(null, "Images saved successfully to: " + zipFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save images: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }





}
