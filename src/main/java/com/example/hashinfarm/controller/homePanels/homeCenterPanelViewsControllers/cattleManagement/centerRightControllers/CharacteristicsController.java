package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.CattleImageDAO;
import com.example.hashinfarm.controller.handlers.ActionHandlerFactory;
import com.example.hashinfarm.controller.handlers.imagesHandlers.DownloadImageHandler;
import com.example.hashinfarm.controller.utility.FileValidationController;
import com.example.hashinfarm.controller.utility.SelectedCattleManager;
import com.example.hashinfarm.controller.utility.SelectedHerdManager;
import com.example.hashinfarm.controller.utility.CattleImageManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.CompletableFuture;

public class CharacteristicsController {
    @FXML
    private Slider BCSSlider;
    @FXML
    private TextField BCSTextField;
    @FXML private Label ageClassLabel, herdSolutionTypeLabel, breedTypeLabel, breedSystemLabel, animalClassLabel, sireIDLabel,
                        sireHerdNameLabel, sireBreedNameLabel, damIDLabel, damHerdNameLabel, damBreedNameLabel, cattleHerdLabel,
                         cattleNameLabel, cattleIdLabel, agelabel, breedIDLabel, cattleBreedLabel, damNameLabel, sireNameLabel;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private HBox imageContainer;

    private CattleImageManager cattleImageManager;

    @FXML
    private Button modifyCattle, viewCurrentImage, uploadImage,
            addBreed, downloadImage;

    private DownloadImageHandler downloadImageHandler;
    private FileValidationController fileValidationController;

    // Map to associate buttons with their respective handlers
    private final Map<Button, String> buttonHandlersMap = new HashMap<>();

    public void initialize() {
        initializeButtonHandlers(
                modifyCattle,
                addBreed, downloadImage
        );

        addSelectedCowListeners();
        addSelectedHerdListeners();


        fileValidationController = new FileValidationController();
        downloadImageHandler = new DownloadImageHandler();
        initBCSSlider();
        initBCSSliderListener();


        cattleImageManager = new CattleImageManager();
        // Add listener to trigger fetchAndPopulateCarousel() when selectedCattleIDProperty changes
        SelectedCattleManager.getInstance().selectedCattleIDProperty().addListener((observable, oldValue, newValue) -> {
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
        for (Map.Entry<String, Integer> entry : BCS_VALUES.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return "";
    }
    private static final Map<String, Integer> BCS_VALUES = Map.of(
            "Emaciated", 1,
            "Very thin", 2,
            "Thin", 3,
            "Borderline", 4,
            "Moderate", 5,
            "Good", 6,
            "Very good", 7,
            "Fat", 8,
            "Very fat", 9
    );

    private void addSelectedCowListeners() {
        SelectedCattleManager cattleManager = SelectedCattleManager.getInstance();

        cattleManager.selectedNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(cattleNameLabel, newValue));
        cattleManager.selectedCattleIDProperty().addListener((observable, oldValue, newValue) -> updateLabel(cattleIdLabel, String.valueOf(newValue)));
        cattleManager.selectedDateOfBirthProperty().addListener((observable, oldValue, newValue) -> dobDatePicker.setValue(newValue));
        cattleManager.selectedAgeProperty().addListener((observable, oldValue, newValue) -> updateLabel(agelabel, String.valueOf(newValue)));
        cattleManager.selectedBreedIdProperty().addListener((observable, oldValue, newValue) -> updateLabel(breedIDLabel, String.valueOf(newValue)));
        cattleManager.selectedBreedNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(cattleBreedLabel, newValue));
        cattleManager.selectedDamNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(damNameLabel, newValue));
        cattleManager.selectedSireNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(sireNameLabel, newValue));
        cattleManager.selectedSireIdProperty().addListener((observable, oldValue, newValue) -> updateLabel(sireIDLabel, String.valueOf(newValue)));
        cattleManager.selectedDamIdProperty().addListener((observable, oldValue, newValue) -> updateLabel(damIDLabel, String.valueOf(newValue)));
        cattleManager.selectedSireHerdNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(sireHerdNameLabel, newValue));
        cattleManager.selectedDamHerdNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(damHerdNameLabel, newValue));
        cattleManager.selectedSireBreedNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(sireBreedNameLabel, newValue));
        cattleManager.selectedDamBreedNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(damBreedNameLabel, newValue));
        cattleManager.selectedBcsProperty().addListener((observable, oldValue, newValue) ->BCSTextField.setText(newValue));
    }

    private void addSelectedHerdListeners() {
        SelectedHerdManager herdManager = SelectedHerdManager.getInstance();

        herdManager.selectedHerdNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(cattleHerdLabel, newValue));
        herdManager.selectedHerdAnimalClassProperty().addListener((observable, oldValue, newValue) -> updateLabel(animalClassLabel, newValue));
        herdManager.selectedHerdBreedTypeProperty().addListener((observable, oldValue, newValue) -> updateLabel(breedTypeLabel, newValue));
        herdManager.selectedHerdAgeClassProperty().addListener((observable, oldValue, newValue) -> updateLabel(ageClassLabel, newValue));
        herdManager.selectedHerdBreedSystemProperty().addListener((observable, oldValue, newValue) -> updateLabel(breedSystemLabel, newValue));
        herdManager.selectedHerdSolutionTypeProperty().addListener((observable, oldValue, newValue) -> updateLabel(herdSolutionTypeLabel, newValue));
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


    // Button action method
    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonId = buttonHandlersMap.get(button);
        ActionHandlerFactory.createActionHandler(buttonId).handle(event);
    }

    @FXML
    private void handleImageUpload(ActionEvent event) {
        // Handle image upload button action
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Image");
        Stage stage = (Stage) uploadImage.getScene().getWindow();
        Path selectedImagePath = fileChooser.showOpenDialog(stage).toPath();

        // Pass the selected image path for validation and processing
        fileValidationController.validateImageFile(selectedImagePath);
        // Additional logic can be added here if needed
    }

    @FXML
    private void handleImageDownload(ActionEvent event) {

    }

    @FXML
    private void handleViewCurrentImage(ActionEvent actionEvent) {
        cattleImageManager.displayCurrentImage();
    }

    @FXML
    private void previousImage(ActionEvent event) {
        // Navigate to the previous image
        cattleImageManager.previousImage();
    }

    @FXML
    private void nextImage(ActionEvent event) {
        // Navigate to the next image
        cattleImageManager.nextImage();
    }



}
