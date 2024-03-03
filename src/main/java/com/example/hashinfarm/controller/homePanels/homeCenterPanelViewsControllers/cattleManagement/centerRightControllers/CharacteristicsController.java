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
        initializeButtonHandlers(
                modifyCow, viewImage, viewGallery,
                addBreed, downloadImage
        );

        addSelectedCowListeners();
        addSelectedHerdListeners();

        cattleDetailsModel = new CattleDetailsModel();
        cattleDetailsModel.populateImages(imageContainer);

        fileValidationController = new FileValidationController();
        downloadImageHandler = new DownloadImageHandler();
    }



    private void addSelectedCowListeners() {
        SelectedCowManager cowManager = SelectedCowManager.getInstance();

        cowManager.selectedNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(cattleNameLabel, newValue));
        cowManager.selectedCowIdProperty().addListener((observable, oldValue, newValue) -> updateLabel(cattleIdLabel, String.valueOf(newValue)));
        cowManager.selectedDateOfBirthProperty().addListener((observable, oldValue, newValue) -> dobDatePicker.setValue(newValue.toLocalDate()));
        cowManager.selectedAgeProperty().addListener((observable, oldValue, newValue) -> updateLabel(agelabel, String.valueOf(newValue)));
        cowManager.selectedBreedIdProperty().addListener((observable, oldValue, newValue) -> updateLabel(breedIDLabel, String.valueOf(newValue)));
        cowManager.selectedBreedNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(cattleBreedLabel, newValue));
        cowManager.selectedDamNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(damNameLabel, newValue));
        cowManager.selectedSireNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(sireNameLabel, newValue));
        cowManager.selectedSireIdProperty().addListener((observable, oldValue, newValue) -> updateLabel(sireIDLabel, String.valueOf(newValue)));
        cowManager.selectedDamIdProperty().addListener((observable, oldValue, newValue) -> updateLabel(damIDLabel, String.valueOf(newValue)));
        cowManager.selectedSireHerdNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(sireHerdNameLabel, newValue));
        cowManager.selectedDamHerdNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(damHerdNameLabel, newValue));
        cowManager.selectedSireBreedNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(sireBreedNameLabel, newValue));
        cowManager.selectedDamBreedNameProperty().addListener((observable, oldValue, newValue) -> updateLabel(damBreedNameLabel, newValue));
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
