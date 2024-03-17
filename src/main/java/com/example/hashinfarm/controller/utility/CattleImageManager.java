package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.controller.dao.CattleImageDAO;
import com.example.hashinfarm.model.CattleImage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CattleImageManager {
    private final CattleImageDAO cattleImageDAO;
    private int currentIndex = 0;
    private final Timeline timeline;
    private List<CattleImage> cattleImages = new ArrayList<>();

    public CattleImageManager() {
        this.cattleImageDAO = new CattleImageDAO();
        this.timeline = new Timeline();
    }

    public void fetchAndPopulateCarousel(int cattleId, HBox container) {
        container.getChildren().clear();
        cattleImages = cattleImageDAO.getCattleImagesByCattleId(cattleId);
        if (cattleImages.isEmpty()) {
            showAlert("No images found for cattle ID: " + cattleId);
            return;
        }
        populateImageViews(container);
        initializeTimeline(container);
    }

    private void initializeTimeline(HBox container) {
        if (cattleImages.isEmpty()) {
            return;
        }

        int maxImagesToDisplay = Math.min(cattleImages.size(), 5);

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(5), event -> {
            currentIndex = (currentIndex + 1) % maxImagesToDisplay;
            updateImageContainer(container);
        }));

        timeline.play();
    }

    private void updateImageContainer(HBox container) {
        container.getChildren().clear();
        populateImageViews(container);
    }

    public void nextImage(HBox container) {
        currentIndex = (currentIndex + 1) % cattleImages.size();
        updateImageContainer(container);
    }

    public void previousImage(HBox container) {
        currentIndex = (currentIndex - 1 + cattleImages.size()) % cattleImages.size();
        updateImageContainer(container);
    }

    private void populateImageViews(HBox container) {
        int MAX_IMAGES_DISPLAYED = 5;
        int maxImagesToDisplay = Math.min(cattleImages.size(), MAX_IMAGES_DISPLAYED);
        for (int i = 0; i < maxImagesToDisplay; i++) {
            int imageIndex = (currentIndex + i) % cattleImages.size();
            String imageName = cattleImages.get(imageIndex).getImagePath();
            String imagePath = "/images/" + imageName;
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(438);
            imageView.setFitHeight(205);
            imageView.setPreserveRatio(true);
            if (i == 2) {
                imageView.setScaleX(1.4);
                imageView.setScaleY(1.4);
                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(20.0);
                dropShadow.setOffsetX(5.0);
                dropShadow.setOffsetY(5.0);
                dropShadow.setColor(Color.LIGHTSKYBLUE);
                imageView.setEffect(dropShadow);
            }
            if (i == 0 || i == 4) {
                imageView.setScaleX(0.5);
                imageView.setScaleY(0.5);
                imageView.setClip(new Circle(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, imageView.getFitWidth() / 2));
            } else if (i == 1 || i == 3) {
                imageView.setScaleX(0.8);
                imageView.setScaleY(0.8);
                imageView.setClip(new Circle(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, imageView.getFitWidth() / 2));
            }
            container.getChildren().add(imageView);
        }
    }

    public void pauseCarousel() {
        timeline.pause();
    }

    public void resumeCarousel() {
        timeline.play();
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void displayCurrentImage() {
        pauseCarousel();

        if (cattleImages.isEmpty()) {
            showAlert("No images to display.");
            return;
        }

        String imagePath = "/images/" + cattleImages.get((currentIndex + 2) % cattleImages.size()).getImagePath();
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));

        Stage stage = getStage(image);
        stage.show();
    }

    @NotNull
    private Stage getStage(Image image) {
        Stage stage = new Stage();
        stage.setTitle("Current Image");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(500);
        imageView.setFitHeight(500);

        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setOnScroll(event -> handleScroll(event, imageView));

        stage.setOnCloseRequest(event -> {
            stage.close();
            resumeCarousel();
        });

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        return stage;
    }

    private void handleScroll(ScrollEvent event, ImageView imageView) {
        if (event.isControlDown()) {
            double deltaY = event.getDeltaY();
            Point2D pivotOnImageView = imageView.parentToLocal(new Point2D(event.getX(), event.getY()));
            zoom(imageView, deltaY > 0, pivotOnImageView);
            event.consume();
        }
    }

    private void zoom(ImageView imageView, boolean zoomIn, Point2D pivotOnImageView) {
        double scaleFactor = zoomIn ? 1.1 : 0.9;
        double oldScale = imageView.getScaleX();
        double newScale = oldScale * scaleFactor;

        imageView.setScaleX(newScale);
        imageView.setScaleY(newScale);

        double deltaX = (pivotOnImageView.getX() - imageView.getBoundsInParent().getWidth() / 2) * (1 - scaleFactor);
        double deltaY = (pivotOnImageView.getY() - imageView.getBoundsInParent().getHeight() / 2) * (1 - scaleFactor);

        imageView.setTranslateX(imageView.getTranslateX() + deltaX);
        imageView.setTranslateY(imageView.getTranslateY() + deltaY);
    }

    public void saveImagesToZip(int cattleId) {
        cattleImages = cattleImageDAO.getCattleImagesByCattleId(cattleId);
        if (cattleImages.isEmpty()) {
            showAlert("No images found for cattle ID: " + cattleId);
            return;
        }

        Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
        File zipFile = new File(downloadsDirectory.toString(), "CattleImages_" + cattleId + ".zip");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (CattleImage cattleImage : cattleImages) {
                String imageName = cattleImage.getImagePath();
                InputStream inputStream = getClass().getResourceAsStream("/images/" + imageName);
                if (inputStream != null) {
                    zipOutputStream.putNextEntry(new ZipEntry(imageName));
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    zipOutputStream.closeEntry();
                }
            }
            showAlert("Images saved successfully to: " + zipFile.getAbsolutePath());
        } catch (IOException e) {
            showAlert("Failed to save images: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
