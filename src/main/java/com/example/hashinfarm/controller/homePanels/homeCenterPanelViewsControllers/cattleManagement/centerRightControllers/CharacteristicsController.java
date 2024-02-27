package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.handlers.ActionHandlerFactory;
import com.example.hashinfarm.controller.handlers.imagesHandlers.DownloadImageHandler;
import com.example.hashinfarm.controller.utility.FileValidationController;
import com.example.hashinfarm.controller.utility.SelectedCowManager;
import com.example.hashinfarm.controller.utility.SelectedHerdManager;
import com.example.hashinfarm.model.CattleDetailsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CharacteristicsController {
    @FXML private Label ageClassLabel, herdSolutionTypeLabel, breedTypeLabel, breedSystemLabel, animalClassLabel, sireIDLabel,
                        sireHerdNameLabel, sireBreedNameLabel, damIDLabel, damHerdNameLabel, damBreedNameLabel, cattleHerdLabel,
                         cattleNameLabel, cattleIdLabel, agelabel, breedIDLabel, cattleBreedLabel, damNameLabel, sireNameLabel;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private HBox imageContainer;

    @FXML
    private Button modifyCow,viewImage, viewGallery, uploadImage,
            addBreed, downloadImage;

    private CattleDetailsModel cattleDetailsModel;
    private DownloadImageHandler downloadImageHandler;
    private FileValidationController fileValidationController;

    // Map to associate buttons with their respective handlers
    private final Map<Button, String> buttonHandlersMap = new HashMap<>();

    public void initialize() {
        // Initialize button handlers with respective fx:id using a loop
        initializeButtonHandlers(
                modifyCow,viewImage, viewGallery,
                addBreed, downloadImage
        );


        // Add listeners to update UI elements based on changes to selected cow properties
        SelectedCowManager.getInstance().selectedNameProperty().addListener((observable, oldValue, newValue) -> {
            cattleNameLabel.setText(newValue); // Update cattle name text field
        });

        SelectedCowManager.getInstance().selectedCowIdProperty().addListener((observable, oldValue, newValue) -> {
            cattleIdLabel.setText(String.valueOf(newValue)); // Update cattle ID label
        });
        SelectedCowManager.getInstance().selectedDateOfBirthProperty().addListener((observable, oldValue, newValue) -> {
             dobDatePicker.setValue(newValue.toLocalDate());
        });

        SelectedCowManager.getInstance().selectedAgeProperty().addListener((observable, oldValue, newValue) -> {
           agelabel.setText(String.valueOf(newValue));
        });

        SelectedCowManager.getInstance().selectedBreedIdProperty().addListener((observable, oldValue, newValue) -> {
            breedIDLabel.setText(String.valueOf(newValue));
        });

        SelectedCowManager.getInstance().selectedBreedNameProperty().addListener((observable, oldValue, newValue) -> {
           cattleBreedLabel.setText(newValue);
        });

        SelectedCowManager.getInstance().selectedDamNameProperty().addListener((observable, oldValue, newValue) -> {
           damNameLabel.setText(newValue);
        });

        SelectedCowManager.getInstance().selectedSireNameProperty().addListener((observable, oldValue, newValue) -> {
           sireNameLabel.setText(newValue);
        });





// Add more listeners for other properties if needed
        SelectedCowManager.getInstance().selectedSireIdProperty().addListener((observable, oldValue, newValue) -> {
            sireIDLabel.setText(String.valueOf(newValue));
        });

        SelectedCowManager.getInstance().selectedDamIdProperty().addListener((observable, oldValue, newValue) -> {
            damIDLabel.setText(String.valueOf(newValue));
        });
        SelectedCowManager.getInstance().selectedSireHerdNameProperty().addListener((observable, oldValue, newValue) -> {
            sireHerdNameLabel.setText(newValue);
        });

        SelectedCowManager.getInstance().selectedDamHerdNameProperty().addListener((observable, oldValue, newValue) -> {
            damHerdNameLabel.setText(newValue);
        });




        SelectedCowManager.getInstance().selectedSireBreedNameProperty().addListener((observable, oldValue, newValue) -> {
            sireBreedNameLabel.setText(newValue);
        });

        SelectedCowManager.getInstance().selectedDamBreedNameProperty().addListener((observable, oldValue, newValue) -> {
            damBreedNameLabel.setText(newValue);
        });






        SelectedHerdManager.getInstance().selectedHerdNameProperty().addListener((observable, oldValue, newValue) -> {
            cattleHerdLabel.setText(newValue);
        });

        SelectedHerdManager.getInstance().selectedHerdNameProperty().addListener((observable, oldValue, newValue) -> {
            cattleHerdLabel.setText(newValue);
        });

        SelectedHerdManager.getInstance().selectedHerdAnimalClassProperty().addListener((observable, oldValue, newValue) -> {
            // Update label for animal class
            animalClassLabel.setText(newValue);
        });

        SelectedHerdManager.getInstance().selectedHerdBreedTypeProperty().addListener((observable, oldValue, newValue) -> {
            // Update label for breed type
            breedTypeLabel.setText(newValue);
        });

        SelectedHerdManager.getInstance().selectedHerdAgeClassProperty().addListener((observable, oldValue, newValue) -> {
            // Update label for age class
            ageClassLabel.setText(newValue);
        });

        SelectedHerdManager.getInstance().selectedHerdBreedSystemProperty().addListener((observable, oldValue, newValue) -> {
            // Update label for breed system
            breedSystemLabel.setText(newValue);
        });

        SelectedHerdManager.getInstance().selectedHerdSolutionTypeProperty().addListener((observable, oldValue, newValue) -> {
            // Update label for animal type
            herdSolutionTypeLabel.setText(newValue);
        });

















        // Initialize the CattleDetailsModel
        cattleDetailsModel = new CattleDetailsModel();
        cattleDetailsModel.populateImages(imageContainer);

        // Initialize the file validation controller
        fileValidationController = new FileValidationController();

        // Other initialization code...
        downloadImageHandler = new DownloadImageHandler();
    }

    private void initializeButtonHandlers(Button... buttons) {
        for (Button button : buttons) {
            String buttonId = button.getId();
            buttonHandlersMap.put(button, buttonId);
            button.setOnAction(this::handleButtonAction);
        }
    }

    @FXML
    private void previousImage(ActionEvent event) {
        cattleDetailsModel.previousImage();
    }

    @FXML
    private void nextImage(ActionEvent event) {
        cattleDetailsModel.nextImage();
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
        // Call the handler method from DownloadImageHandler
        downloadImageHandler.handleImageDownload((Stage) downloadImage.getScene().getWindow(), cattleDetailsModel.getCurrentIndex());
    }
}
