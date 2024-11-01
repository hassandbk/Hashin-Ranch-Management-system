package com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers;
import com.example.hashinfarm.businessLogic.services.CattleImageManager;
import com.example.hashinfarm.businessLogic.services.SelectedCattleManager;
import com.example.hashinfarm.data.DAOs.CattleImageDAO;
import com.example.hashinfarm.businessLogic.services.handlers.ActionHandlerFactory;

import com.example.hashinfarm.data.DTOs.records.CattleImage;
import com.example.hashinfarm.data.DTOs.Cattle;
import com.example.hashinfarm.businessLogic.services.SelectedHerdManager;
import com.example.hashinfarm.data.DTOs.records.Herd;
import com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers.cattleDetailsMoreButtonsControllers.ImageViewTableController;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.utils.validation.FileValidationController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CharacteristicsController {



  private static final Map<String, Integer> BCS_VALUES = Map.of(
          "Emaciated", 1,
          "Very thin", 2,
          "Thin", 3,
          "Borderline", 4,
          "Moderate", 5,
          "Good", 6,
          "Very good", 7,
          "Fat", 8,
          "Very fat", 9);
  private final CattleImageManager cattleImageManager = new CattleImageManager();
  private final Map<Button, String> buttonHandlersMap = new HashMap<>();

  @FXML private Slider BCSSlider;
  @FXML private TextField BCSTextField;
  @FXML private Label ageClassLabel, herdSolutionTypeLabel, breedTypeLabel, breedSystemLabel, cattleHerdLabel, animalClassLabel,
          dateOfBirthLabel, sireIDLabel, sireHerdNameLabel, sireBreedNameLabel, damIDLabel,
          damHerdNameLabel, damBreedNameLabel, cattleNameLabel, cattleIdLabel,
          ageLabel, breedIDLabel, cattleBreedLabel, damNameLabel, sireNameLabel;
  @FXML private HBox imageContainer;
  @FXML private Button modifyCattle, uploadImage, addBreed;


  private FileValidationController fileValidationController;
  private int selectedCattleId;

  public CharacteristicsController() {}

  public void initialize() {

    initializeButtonHandlers(modifyCattle, addBreed);
    initializeCattleAndHerdListeners();
    fileValidationController = new FileValidationController();
    initBCSSlider();
    initBCSSliderListener();
    initSelectedCattleListeners();
    initDateOfBirthLabelStyle();

    // Listen for changes from SelectedCattleManager
  }

  private void initSelectedCattleListeners() {
    SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();

    // Listener for changes in the entire Cattle object
    selectedCattleManager.selectedCattleProperty().addListener((observable, oldCattle, newCattle) -> {
      if (newCattle != null) {
        // Update selectedCattleId from the new Cattle object
        selectedCattleId = newCattle.getCattleId();
        fetchAndPopulateCarousel(); // Populate the carousel based on the new cattle

        // Update the age and date of birth labels
        updateAgeLabel(newCattle.getDateOfBirth());
        updateDateOfBirthLabel(newCattle.getDateOfBirth());
      }
    });
  }



  private void initDateOfBirthLabelStyle() {
    dateOfBirthLabel.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold;");
  }


  private void updateDateOfBirthLabel(LocalDate date) {
    if (date != null) {
      dateOfBirthLabel.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    } else {
      dateOfBirthLabel.setText("");
    }
  }


  private void fetchAndPopulateCarousel() {
    if (selectedCattleId != 0) {
      cattleImageManager.fetchAndPopulateCarousel(selectedCattleId, imageContainer);
    } else {
      System.out.println("Invalid selected cattle ID");
    }
  }

  private void initBCSSlider() {
    BCSSlider.setMajorTickUnit(1);
    BCSSlider.setMinorTickCount(0);
    BCSSlider.setShowTickLabels(true);
    BCSSlider.setShowTickMarks(true);
    BCSSlider.setSnapToTicks(true);
    BCSSlider.setMin(1);
    BCSSlider.setMax(9);
  }

  private void initBCSSliderListener() {
    BCSTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      Integer bcsValue = BCS_VALUES.get(newValue);
      if (bcsValue != null) {
        BCSSlider.setValue(bcsValue);
      }
    });

    BCSSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      String bcsLabel = getBCSLabel(newValue.intValue());
      BCSTextField.setText(bcsLabel);
    });
  }

  private String getBCSLabel(int value) {
    return BCS_VALUES.entrySet().stream()
            .filter(entry -> entry.getValue() == value)
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse("");
  }



  // Update labels based on selected Cattle object
  private void updateCattleLabels(Cattle cattle) {
    cattleNameLabel.setText(cattle.getName() != null ? cattle.getName() : "");
    cattleIdLabel.setText(String.valueOf(cattle.getCattleId()));
    ageLabel.setText(String.valueOf(cattle.getAge())); // Use getAge() to display age
    breedIDLabel.setText(String.valueOf(cattle.getBreedId()));
    cattleBreedLabel.setText(cattle.getBreedName() != null ? cattle.getBreedName() : "");
    damNameLabel.setText(cattle.getDamName() != null ? cattle.getDamName() : "");
    sireNameLabel.setText(cattle.getSireName() != null ? cattle.getSireName() : "");
    sireIDLabel.setText(String.valueOf(cattle.getSireId()));
    damIDLabel.setText(String.valueOf(cattle.getDamId()));
    sireHerdNameLabel.setText(cattle.getSireHerdName() != null ? cattle.getSireHerdName() : "");
    damHerdNameLabel.setText(cattle.getDamHerdName() != null ? cattle.getDamHerdName() : "");
    sireBreedNameLabel.setText(cattle.getSireBreedName() != null ? cattle.getSireBreedName() : "");
    damBreedNameLabel.setText(cattle.getDamBreedName() != null ? cattle.getDamBreedName() : "");
  }

  // Clear labels when no Cattle is selected
  private void clearCattleLabels() {
    cattleNameLabel.setText("");
    cattleIdLabel.setText("");
    ageLabel.setText("");
    breedIDLabel.setText("");
    cattleBreedLabel.setText("");
    damNameLabel.setText("");
    sireNameLabel.setText("");
    sireIDLabel.setText("");
    damIDLabel.setText("");
    sireHerdNameLabel.setText("");
    damHerdNameLabel.setText("");
    sireBreedNameLabel.setText("");
    damBreedNameLabel.setText("");
  }
  private void initializeCattleAndHerdListeners() {
    addCattleListeners(SelectedCattleManager.getInstance());
    addHerdListeners(SelectedHerdManager.getInstance());
  }

  // New method to handle Cattle updates
  private void addCattleListeners(SelectedCattleManager manager) {
    manager.selectedCattleProperty().addListener((observable, oldCattle, newCattle) -> {
      if (newCattle != null) {
        updateCattleLabels(newCattle);
      } else {
        clearCattleLabels();
      }
    });
  }
  // Add listeners for Herd updates
  private void addHerdListeners(SelectedHerdManager herdManager) {
    herdManager.selectedHerdProperty().addListener((observable, oldHerd, newHerd) -> {
      if (newHerd != null) {
        updateHerdLabels(newHerd);
      } else {
        clearHerdLabels();
      }
    });
  }

  // Method to update herd-related labels
  private void updateHerdLabels(Herd herd) {
    ageClassLabel.setText(herd.ageClass());
    herdSolutionTypeLabel.setText(herd.solutionType());
    breedTypeLabel.setText(herd.breedType());
    breedSystemLabel.setText(herd.breedSystem());
    cattleHerdLabel.setText(herd.name());
    animalClassLabel.setText(herd.animalClass());
  }

  // Method to clear herd-related labels when no herd is selected
  private void clearHerdLabels() {
    ageClassLabel.setText("");
    herdSolutionTypeLabel.setText("");
    breedTypeLabel.setText("");
    breedSystemLabel.setText("");
    cattleHerdLabel.setText("");
    animalClassLabel.setText("");
  }



  private void updateAgeLabel(LocalDate birthdate) {
    if (birthdate == null) {
      ageLabel.setText("N/A");
    } else {
      LocalDate now = LocalDate.now();
      Period period = Period.between(birthdate, now);

      // Create styled Text objects from the StringBuilder
      TextFlow ageTextFlow = new TextFlow();

      if (period.getYears() > 0) {
        String yearsText = period.getYears() + " year" + (period.getYears() > 1 ? "s" : "");
        Text years = new Text(yearsText);
        years.setFill(Color.BLUE);
        ageTextFlow.getChildren().add(years);
        ageTextFlow.getChildren().add(new Text(" "));
      }
      if (period.getMonths() > 0) {
        String monthsText = period.getMonths() + " month" + (period.getMonths() > 1 ? "s" : "");
        Text months = new Text(monthsText);
        months.setFill(Color.ORANGE);
        ageTextFlow.getChildren().add(months);
        ageTextFlow.getChildren().add(new Text(" "));
      }
      if (period.getDays() > 0) {
        String daysText = period.getDays() + " day" + (period.getDays() > 1 ? "s" : "");
        Text days = new Text(daysText);
        days.setFill(Color.GREEN);
        ageTextFlow.getChildren().add(days);
        ageTextFlow.getChildren().add(new Text(" "));
      }

      // Handle case where all parts are 0
      if (ageTextFlow.getChildren().isEmpty()) {
        ageTextFlow.getChildren().add(new Text("0 days"));
      }

      ageLabel.setText(null); // Clear existing text (optional)
      ageLabel.setGraphic(ageTextFlow); // Set TextFlow as graphic
    }
  }

  private void initializeButtonHandlers(Button... buttons) {
    for (Button button : buttons) {
      String buttonId = button.getId();
      buttonHandlersMap.put(button, buttonId);
      button.setOnAction(this::handleButtonAction);
    }
  }

  @FXML
  private void handleButtonAction(ActionEvent event) {
    Button button = (Button) event.getSource();
    String buttonId = buttonHandlersMap.get(button);
    Objects.requireNonNull(ActionHandlerFactory.createActionHandler(buttonId)).handle(event);
  }

  @FXML
  private void handleImageDownload() {
    cattleImageManager.saveImagesToZip(selectedCattleId);
  }

  @FXML
  private void handleViewCurrentImage() {
    cattleImageManager.displayCurrentImage();
  }

  @FXML
  private void previousImage() {
    cattleImageManager.previousImage(imageContainer);
  }

  @FXML
  private void nextImage() {
    cattleImageManager.nextImage(imageContainer);
  }

  @FXML
  private void handleImageUpload() {
    File selectedFile = chooseImageFile();
    if (selectedFile != null) {
      try {
        uploadAndInsertImage(selectedFile);
      } catch (IOException | FileValidationController.InvalidImageFormatException | FileValidationController.ImageSizeException e) {
        handleUploadImageError();
      }
    }
  }

  @FXML
  private void handleImagesTable() {
    CompletableFuture.supplyAsync(this::loadFXML)
            .thenAcceptAsync(root -> {
              if (root != null) {
                Platform.runLater(() -> showImagesTableStage(root));
              }
            });
  }

  private AnchorPane loadFXML() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerRightViews/cattleDetailMoreButtons/viewCattleImages.fxml"));
      AnchorPane root = loader.load();
      ImageViewTableController controller = loader.getController();
      controller.initialize(selectedCattleId);
      return root;
    } catch (IOException e) {
      AppLogger.error("Error loading " + "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerRightViews/cattleDetailMoreButtons/viewCattleImages.fxml", e);
      return null;
    }
  }

  private File chooseImageFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Upload Image");
    Stage stage = (Stage) uploadImage.getScene().getWindow();
    return fileChooser.showOpenDialog(stage);
  }

  private void uploadAndInsertImage(File selectedFile) throws IOException,
          FileValidationController.InvalidImageFormatException,
          FileValidationController.ImageSizeException {
    Path selectedImagePath = selectedFile.toPath();
    fileValidationController.validateImageFile(selectedImagePath);
    String filename = generateUniqueFilename(selectedFile.getName());
    Path destination = Paths.get("src/main/resources/images/", filename);
    Files.copy(selectedImagePath, destination);

    if (Files.exists(destination)) {
      insertCattleImageRecord(filename);
    } else {
      showAlert(Alert.AlertType.ERROR, "Error", "Failed to copy the image file. Please try again later.");
    }
  }
  private void insertCattleImageRecord(String filename) {
    // Directly create the CattleImage record with its constructor
    CattleImage cattleImage = new CattleImage(
            0, // Assuming 0 or another default value for imageId if it's auto-generated in the database
            selectedCattleId,
            filename,
            new Timestamp(System.currentTimeMillis())
    );

    boolean imageInserted = new CattleImageDAO().insertCattleImage(cattleImage);
    if (imageInserted) {
      showAlert(Alert.AlertType.INFORMATION, "Success", "The image has been successfully uploaded and inserted. The carousel will be updated upon restarting.");
    } else {
      showAlert(Alert.AlertType.ERROR, "Error", "Failed to insert the image record into the database. Please try again later.");
    }
  }

  private void handleUploadImageError() {
    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while uploading the image.");
  }

  private String generateUniqueFilename(String filename) {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return timestamp + "_" + filename;
  }

  private void showImagesTableStage(AnchorPane root) {
    Scene scene = new Scene(root);
    Stage stage = new Stage();
    stage.setTitle("Cattle Images");
    stage.setScene(scene);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setResizable(false);
    stage.setMaximized(false);
    stage.showAndWait();
  }

  private void showAlert(Alert.AlertType type, String title, String content) {
    Platform.runLater(() -> {
      Alert alert = new Alert(type);
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(content);
      alert.showAndWait();
    });
  }
}
