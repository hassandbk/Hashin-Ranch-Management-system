package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.handlers.ActionHandlerFactory;
import com.example.hashinfarm.controller.utility.SelectedCowManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class DetailedInformationController {
    @FXML
    private TextField cattleNameTextField;
    @FXML
    private Label cattleIdLabel;

    @FXML
    private Button vaccinationHistory, updateVaccination, healthInfoHistory, healthInfoUpdate,
            viewCalvingHistory, saveCalvingChanges, viewDewormingHistory, saveDewormingChanges,
            viewMoreAdditionalInfo, saveAdditionalInfo,
            addBreed,viewBreed;


    // Map to associate buttons with their respective handlers
    private final Map<Button, String> buttonHandlersMap = new HashMap<>();

    public void initialize() {
        // Initialize button handlers with respective fx:id using a loop
        initializeButtonHandlers(
                vaccinationHistory, updateVaccination, healthInfoHistory, healthInfoUpdate,
                viewCalvingHistory, saveCalvingChanges, viewDewormingHistory, saveDewormingChanges,
                viewMoreAdditionalInfo, saveAdditionalInfo,
                addBreed,viewBreed
        );


        // Add a listener to update the name dynamically
        SelectedCowManager.getInstance().selectedNameProperty().addListener((observable, oldValue, newValue) -> {
            cattleNameTextField.setText(newValue); // Update cattle name
        });

        // Add a listener to update the cattle ID dynamically
        SelectedCowManager.getInstance().selectedCowIdProperty().addListener((observable, oldValue, newValue) -> {
            cattleIdLabel.setText(String.valueOf(newValue)); // Update cattle ID label
        });



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


}
