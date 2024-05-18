package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.CattleImageDAO;
import com.example.hashinfarm.controller.handlers.ActionHandlerFactory;
import com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.cattleDetailsMoreButtonsControllers.ImageViewTableController;
import com.example.hashinfarm.controller.utility.*;
import com.example.hashinfarm.model.CattleImage;
import java.io.*;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CharacteristicsController {

  private static final Map<String, Integer> BCS_VALUES =
      Map.of(
          "Emaciated", 1,
          "Very thin", 2,
          "Thin", 3,
          "Borderline", 4,
          "Moderate", 5,
          "Good", 6,
          "Very good", 7,
          "Fat", 8,
          "Very fat", 9);
  private final CattleImageManager cattleImageManager;
  private final Map<Button, String> buttonHandlersMap = new HashMap<>();
  @FXML private Slider BCSSlider;
  @FXML private TextField BCSTextField;
  @FXML
  private Label ageClassLabel,
      herdSolutionTypeLabel,
      breedTypeLabel,
      breedSystemLabel,
      animalClassLabel,
      sireIDLabel,
      sireHerdNameLabel,
      sireBreedNameLabel,
      damIDLabel,
      damHerdNameLabel,
      damBreedNameLabel,
      cattleHerdLabel,
      cattleNameLabel,
      cattleIdLabel,
      agelabel,
      breedIDLabel,
      cattleBreedLabel,
      damNameLabel,
      sireNameLabel;
  @FXML private DatePicker dobDatePicker;
  @FXML private HBox imageContainer;
  @FXML private Button modifyCattle, viewCurrentImage, uploadImage, addBreed, viewImageTable;
  private FileValidationController fileValidationController;

  public CharacteristicsController() {
    cattleImageManager = new CattleImageManager();
  }

  public void initialize() {
    initializeButtonHandlers(modifyCattle, addBreed);
    addSelectedCowListeners();
    addSelectedHerdListeners();
    fileValidationController = new FileValidationController();
    initBCSSlider();
    initBCSSliderListener();
    SelectedCattleManager.getInstance()
        .selectedCattleIDProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null && newValue.intValue() != 0) {
                fetchAndPopulateCarousel();
              }
            });
  }

  private void fetchAndPopulateCarousel() {
    int selectedCattleId = SelectedCattleManager.getInstance().getSelectedCattleID();
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
    BCSTextField.textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              Integer bcsValue = BCS_VALUES.get(newValue);
              if (bcsValue != null) {
                BCSSlider.setValue(bcsValue);
              }
            });

    BCSSlider.valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              String bcsLabel = getBCSLabel(newValue.intValue());
              BCSTextField.setText(bcsLabel);
            });
  }

  private String getBCSLabel(int value) {
    for (Map.Entry<String, Integer> entry : BCS_VALUES.entrySet()) {
      if (entry.getValue() == value) {
        return entry.getKey();
      }
    }
    return "";
  }

  private void addSelectedCowListeners() {
    SelectedCattleManager cattleManager = SelectedCattleManager.getInstance();

    cattleManager
        .selectedNameProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(cattleNameLabel, newValue));
    cattleManager
        .selectedCattleIDProperty()
        .addListener(
            (observable, oldValue, newValue) ->
                updateLabel(cattleIdLabel, String.valueOf(newValue)));
    cattleManager
        .selectedDateOfBirthProperty()
        .addListener((observable, oldValue, newValue) -> dobDatePicker.setValue(newValue));
    cattleManager
        .selectedAgeProperty()
        .addListener(
            (observable, oldValue, newValue) -> updateLabel(agelabel, String.valueOf(newValue)));
    cattleManager
        .selectedBreedIdProperty()
        .addListener(
            (observable, oldValue, newValue) ->
                updateLabel(breedIDLabel, String.valueOf(newValue)));
    cattleManager
        .selectedBreedNameProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(cattleBreedLabel, newValue));
    cattleManager
        .selectedDamNameProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(damNameLabel, newValue));
    cattleManager
        .selectedSireNameProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(sireNameLabel, newValue));
    cattleManager
        .selectedSireIdProperty()
        .addListener(
            (observable, oldValue, newValue) -> updateLabel(sireIDLabel, String.valueOf(newValue)));
    cattleManager
        .selectedDamIdProperty()
        .addListener(
            (observable, oldValue, newValue) -> updateLabel(damIDLabel, String.valueOf(newValue)));
    cattleManager
        .selectedSireHerdNameProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(sireHerdNameLabel, newValue));
    cattleManager
        .selectedDamHerdNameProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(damHerdNameLabel, newValue));
    cattleManager
        .selectedSireBreedNameProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(sireBreedNameLabel, newValue));
    cattleManager
        .selectedDamBreedNameProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(damBreedNameLabel, newValue));
    cattleManager
        .selectedBcsProperty()
        .addListener((observable, oldValue, newValue) -> BCSTextField.setText(newValue));
  }

  private void addSelectedHerdListeners() {
    SelectedHerdManager herdManager = SelectedHerdManager.getInstance();

    herdManager
        .selectedHerdNameProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(cattleHerdLabel, newValue));
    herdManager
        .selectedHerdAnimalClassProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(animalClassLabel, newValue));
    herdManager
        .selectedHerdBreedTypeProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(breedTypeLabel, newValue));
    herdManager
        .selectedHerdAgeClassProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(ageClassLabel, newValue));
    herdManager
        .selectedHerdBreedSystemProperty()
        .addListener((observable, oldValue, newValue) -> updateLabel(breedSystemLabel, newValue));
    herdManager
        .selectedHerdSolutionTypeProperty()
        .addListener(
            (observable, oldValue, newValue) -> updateLabel(herdSolutionTypeLabel, newValue));
  }

  private void updateLabel(Label label, String text) {
    label.setText(text);
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
    int selectedCattleId = SelectedCattleManager.getInstance().getSelectedCattleID();
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
      } catch (IOException
          | FileValidationController.InvalidImageFormatException
          | FileValidationController.ImageSizeException e) {
        handleUploadImageError();
      }
    }
  }

  @FXML
  private void handleImagesTable() {
    CompletableFuture.supplyAsync(
            () -> {
              try {
                FXMLLoader loader =
                    new FXMLLoader(
                        getClass()
                            .getResource(
                                "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerRightViews/cattleDetailMoreButtons/viewCattleImages.fxml"));
                AnchorPane root = loader.load();
                ImageViewTableController controller = loader.getController();
                controller.initialize(SelectedCattleManager.getInstance().getSelectedCattleID());
                return root;
              } catch (IOException e) {
                // Log the exception with AppLogger (error level)
                AppLogger.error("Error loading viewCattleImages.fxml:", e);
                return null;
              }
            })
        .thenAcceptAsync(
            root -> {
              if (root != null) {
                Platform.runLater(
                    () -> {
                      // Call separate method to show the stage
                      showImagesTableStage(root);
                    });
              }
            });
  }

  private File chooseImageFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Upload Image");
    Stage stage = (Stage) uploadImage.getScene().getWindow();
    return fileChooser.showOpenDialog(stage);
  }

  private void uploadAndInsertImage(File selectedFile)
      throws IOException,
          FileValidationController.InvalidImageFormatException,
          FileValidationController.ImageSizeException {
    Path selectedImagePath = selectedFile.toPath();
    fileValidationController.validateImageFile(selectedImagePath);
    String filename = generateUniqueFilename(selectedFile.getName());
    Path destination = Paths.get("src/main/resources/images/", filename);
    Files.copy(selectedImagePath, destination);

    if (Files.exists(destination)) {
      int selectedCattleId = SelectedCattleManager.getInstance().getSelectedCattleID();
      CattleImage cattleImage = new CattleImage();
      cattleImage.setCattleId(selectedCattleId);
      cattleImage.setImagePath(filename);
      cattleImage.setCreatedAt(new Timestamp(System.currentTimeMillis()));

      boolean imageInserted = new CattleImageDAO().insertCattleImage(cattleImage);
      if (imageInserted) {
        showAlert(
            Alert.AlertType.INFORMATION,
            "Success",
            "The image has been successfully uploaded and inserted. The carousel will be updated upon restarting.");
      } else {
        showAlert(
            Alert.AlertType.ERROR,
            "Error",
            "Failed to insert the image record into the database. Please try again later.");
      }
    } else {
      showAlert(
          Alert.AlertType.ERROR, "Error", "Failed to copy the image file. Please try again later.");
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
    Platform.runLater(
        () -> {
          Alert alert = new Alert(type);
          alert.setTitle(title);
          alert.setHeaderText(null);
          alert.setContentText(content);
          alert.showAndWait();
        });
  }
}
