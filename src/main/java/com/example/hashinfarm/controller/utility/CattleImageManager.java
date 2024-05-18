package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.controller.dao.CattleImageDAO;
import com.example.hashinfarm.model.CattleImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CattleImageManager {

  private final CattleImageDAO cattleImageDAO = new CattleImageDAO();
  Map<Integer, Double> scaleMap = Map.of(2, 1.5, 0, 0.6, 4, 0.6);
  private int currentIndex = 0;
  private List<CattleImage> cattleImages;
  private Timeline timeline;

  public void fetchAndPopulateCarousel(int cattleId, HBox container) {
    container.getChildren().clear();
    cattleImages = cattleImageDAO.getCattleImagesByCattleId(cattleId);
    if (cattleImages == null || cattleImages.isEmpty()) {
      return;
    }
    populateImageViews(container);
    initializeTimeline(container);
  }

  private void initializeTimeline(HBox container) {
    timeline =
        new Timeline(new KeyFrame(Duration.seconds(5), e -> updateImageContainer(container)));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  private void updateImageContainer(HBox container) {
    if (cattleImages == null || cattleImages.isEmpty()) {
      return;
    }

    // Retrieve selected cattle ID
    int selectedCattleId = SelectedCattleManager.getInstance().getSelectedCattleID();
    List<CattleImage> updatedImages;
    try {
      updatedImages = cattleImageDAO.getCattleImagesByCattleId(selectedCattleId);
    } catch (Exception e) {
      // Log the exception with AppLogger (error level)
      AppLogger.error("Error retrieving images for cattle ID " + selectedCattleId, e);
      showAlert("Error", "Failed to retrieve images for selected cattle.");
      return;
    }

    // Compare image lists and update if necessary
    if (updatedImages == null || updatedImages.size() != cattleImages.size()) {
      fetchAndPopulateCarousel(selectedCattleId, container);
      return;
    }

    // Check for changes in image paths
    boolean imagesChanged = false;
    for (int i = 0; i < updatedImages.size(); i++) {
      if (!Objects.equals(
          updatedImages.get(i).getImagePath(), cattleImages.get(i).getImagePath())) {
        imagesChanged = true;
        break;
      }
    }

    // Update carousel or populate with existing images
    if (imagesChanged) {
      fetchAndPopulateCarousel(selectedCattleId, container);
    } else {
      // Update index and populate with existing images
      currentIndex = (currentIndex + 1) % cattleImages.size();
      populateImageViews(container);
    }
  }

  private void populateImageViews(HBox container) {
    container.getChildren().clear();
    if (cattleImages == null || cattleImages.isEmpty()) {
      return;
    }
    int numImages = cattleImages.size();
    for (int i = 0; i < Math.min(numImages, 5); i++) {
      double scale = scaleMap.getOrDefault(i, 0.9);
      int index = (currentIndex + i) % numImages;
      String imagePath =
          "C:/Users/123/source/HashinFarm/src/main/resources/images/"
              + cattleImages.get(index).getImagePath();
      Image image = new Image(new File(imagePath).toURI().toString());
      ImageView imageView = new ImageView(image);
      imageView.setFitWidth(200 * scale);
      imageView.setFitHeight(180 * scale);
      container.getChildren().add(imageView);
    }
  }

  public void pauseCarousel() {
    timeline.pause();
  }

  public void resumeCarousel() {
    timeline.play();
  }

  private void showAlert(String title, String content) {
    Platform.runLater(
        () -> {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle(title);
          alert.setHeaderText(null);
          alert.setContentText(content);
          alert.showAndWait();
        });
  }

  public void displayCurrentImage() {
    pauseCarousel();
    if (cattleImages.isEmpty()) {
      showAlert("No images to display.", "No images found to display.");
      return;
    }

    String imagePath =
        "/images/" + cattleImages.get((currentIndex + 2) % cattleImages.size()).getImagePath();
    Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));

    Stage stage = getStage(image);
    stage.show();
  }

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

    stage.setOnCloseRequest(
        event -> {
          stage.close();
          resumeCarousel();
        });

    stage.setScene(new Scene(scrollPane));
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

    double deltaX =
        (pivotOnImageView.getX() - imageView.getBoundsInParent().getWidth() / 2)
            * (1 - scaleFactor);
    double deltaY =
        (pivotOnImageView.getY() - imageView.getBoundsInParent().getHeight() / 2)
            * (1 - scaleFactor);

    imageView.setTranslateX(imageView.getTranslateX() + deltaX);
    imageView.setTranslateY(imageView.getTranslateY() + deltaY);
  }

  public void saveImagesToZip(int cattleId) {
    cattleImages = cattleImageDAO.getCattleImagesByCattleId(cattleId);
    if (cattleImages.isEmpty()) {
      showAlert("No Image", "No images found for cattle ID: " + cattleId);
      return;
    }

    Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
    File zipFile = new File(downloadsDirectory.toString(), "CattleImages_" + cattleId + ".zip");

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
      for (CattleImage cattleImage : cattleImages) {
        String imageName = cattleImage.getImagePath();
        try (InputStream inputStream = getClass().getResourceAsStream("/images/" + imageName)) {
          if (inputStream != null) {
            zipOutputStream.putNextEntry(new ZipEntry(imageName));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
              zipOutputStream.write(buffer, 0, length);
            }
          } else {
            // Log error: Image not found in resources
            AppLogger.error("Image not found: /images/" + imageName);
          }
        } catch (IOException e) {
          // Log the exception with AppLogger
          AppLogger.error("Error processing image: " + imageName, e);
        }
        zipOutputStream.closeEntry();
      }
      showAlert("SUCCESS", "Images saved successfully to: " + zipFile.getAbsolutePath());
    } catch (IOException e) {
      // Log the exception with AppLogger
      AppLogger.error("Error creating zip file:", e);
      showAlert("ERROR", "Failed to save images: " + e.getMessage());
    }
  }

  public void previousImage(HBox container) {
    currentIndex = (currentIndex - 1 + cattleImages.size()) % cattleImages.size();
    populateImageViews(container);
  }

  public void nextImage(HBox container) {
    currentIndex = (currentIndex + 1) % cattleImages.size();
    populateImageViews(container);
  }
}
