package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.CattleImageDAO;
import com.example.hashinfarm.controller.handlers.ActionHandlerFactory;
import com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.cattleDetailsMoreButtonsControllers.ImageViewTableController;
import com.example.hashinfarm.controller.utility.*;
import com.example.hashinfarm.controller.utility.CattleImage;
import javafx.application.Platform;
import javafx.beans.property.Property;
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
  @FXML private Label ageClassLabel,dateOfBirthLabel, herdSolutionTypeLabel, breedTypeLabel, breedSystemLabel,
          animalClassLabel, sireIDLabel, sireHerdNameLabel, sireBreedNameLabel, damIDLabel,
          damHerdNameLabel, damBreedNameLabel, cattleHerdLabel, cattleNameLabel, cattleIdLabel,
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

    selectedCattleManager.selectedCattleIDProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && newValue.intValue() != 0) {
        selectedCattleId = newValue.intValue(); // Update selectedCattleId
        fetchAndPopulateCarousel();
      }
    });

    selectedCattleManager.selectedDateOfBirthProperty().addListener((observable, oldValue, newValue) -> {
      updateAgeLabel(newValue);
      updateDateOfBirthLabel(newValue);
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

  private void initializeCattleAndHerdListeners() {
    addListeners(SelectedCattleManager.getInstance(),
            new Label[]{cattleNameLabel, cattleIdLabel, ageLabel, breedIDLabel, cattleBreedLabel,
                    damNameLabel, sireNameLabel, sireIDLabel, damIDLabel, sireHerdNameLabel,
                    damHerdNameLabel, sireBreedNameLabel, damBreedNameLabel},
            new String[]{"selectedNameProperty", "selectedCattleIDProperty", "selectedAgeProperty",
                    "selectedBreedIdProperty", "selectedBreedNameProperty", "selectedDamNameProperty",
                    "selectedSireNameProperty", "selectedSireIdProperty", "selectedDamIdProperty",
                    "selectedSireHerdNameProperty", "selectedDamHerdNameProperty", "selectedSireBreedNameProperty",
                    "selectedDamBreedNameProperty"});

    addListeners(SelectedHerdManager.getInstance(),
            new Label[]{cattleHerdLabel, animalClassLabel, breedTypeLabel, ageClassLabel, breedSystemLabel, herdSolutionTypeLabel},
            new String[]{"selectedHerdNameProperty", "selectedHerdAnimalClassProperty", "selectedHerdBreedTypeProperty",
                    "selectedHerdAgeClassProperty", "selectedHerdBreedSystemProperty", "selectedHerdSolutionTypeProperty"});
  }

  private void addListeners(Object manager, Label[] labels, String[] properties) {
    for (int i = 0; i < labels.length; i++) {
      final Label label = labels[i];
      try {
        // Get the property
        Object property = manager.getClass().getMethod(properties[i]).invoke(manager);
        // Ensure the property is observable and add a listener
        if (property instanceof Property<?>) {
          ((Property<?>) property).addListener(
                  (observable, oldValue, newValue) -> updateLabel(label, newValue != null ? newValue.toString() : ""));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void updateLabel(Label label, String text) {
    label.setText(text);
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
    CattleImage cattleImage = new CattleImage();
    cattleImage.setCattleId(selectedCattleId);
    cattleImage.setImagePath(filename);
    cattleImage.setCreatedAt(new Timestamp(System.currentTimeMillis()));

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
