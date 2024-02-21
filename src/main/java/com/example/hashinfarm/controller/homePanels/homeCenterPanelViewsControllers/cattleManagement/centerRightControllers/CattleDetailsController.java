package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.handlers.ActionHandlerFactory;
import com.example.hashinfarm.controller.handlers.imagesHandlers.DownloadImageHandler;
import com.example.hashinfarm.controller.utility.FileValidationController;
import com.example.hashinfarm.controller.utility.SelectedCowManager;
import com.example.hashinfarm.model.CattleDetailsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CattleDetailsController {
    @FXML
    private TextField cattleNameTextField;
    @FXML
    private Label cattleIdLabel;

    @FXML
    private HBox imageContainer;

    @FXML
    private Button vaccinationHistory, updateVaccination, healthInfoHistory, healthInfoUpdate,
            viewCalvingHistory, saveCalvingChanges, viewDewormingHistory, saveDewormingChanges,
            viewMoreAdditionalInfo, saveAdditionalInfo, viewImage, viewGallery, uploadImage,
            addBreed, downloadImage,viewBreed;

    private CattleDetailsModel cattleDetailsModel;
    private DownloadImageHandler downloadImageHandler;
    private FileValidationController fileValidationController;

    // Map to associate buttons with their respective handlers
    private final Map<Button, String> buttonHandlersMap = new HashMap<>();

    public void initialize() {
        // Initialize button handlers with respective fx:id using a loop
        initializeButtonHandlers(
                vaccinationHistory, updateVaccination, healthInfoHistory, healthInfoUpdate,
                viewCalvingHistory, saveCalvingChanges, viewDewormingHistory, saveDewormingChanges,
                viewMoreAdditionalInfo, saveAdditionalInfo, viewImage, viewGallery,
                addBreed,viewBreed, downloadImage
        );


        // Add a listener to update the name dynamically
        SelectedCowManager.getInstance().selectedCowNameProperty().addListener((observable, oldValue, newValue) -> {
            cattleNameTextField.setText(newValue); // Update cattle name
        });

        // Add a listener to update the cattle ID dynamically
        SelectedCowManager.getInstance().selectedCowIdProperty().addListener((observable, oldValue, newValue) -> {
            cattleIdLabel.setText(newValue); // Update cattle ID label
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
