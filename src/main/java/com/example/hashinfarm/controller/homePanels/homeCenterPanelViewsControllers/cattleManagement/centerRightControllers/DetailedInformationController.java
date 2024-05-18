package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.handlers.ActionHandlerFactory;
import com.example.hashinfarm.controller.utility.SelectedCattleManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DetailedInformationController {
    // Map to associate buttons with their respective handlers
    private final Map<Button, String> buttonHandlersMap = new HashMap<>();
    @FXML
    private TextField cattleNameTextField;
    @FXML
    private Label cattleIdLabel;
    @FXML
    private Button vaccinationHistory,  healthInfoHistory,
            viewCalvingHistory,  viewDewormingHistory,
            viewMoreAdditionalInfo, saveAdditionalInfo;

    public void initialize() {
        // Initialize button handlers with respective fx:id using a loop
        initializeButtonHandlers(
                vaccinationHistory,  healthInfoHistory,
                viewCalvingHistory,  viewDewormingHistory,
                viewMoreAdditionalInfo, saveAdditionalInfo

        );


        // Add a listener to update the name dynamically
        SelectedCattleManager.getInstance().selectedNameProperty().addListener((observable, oldValue, newValue) -> {
            cattleNameTextField.setText(newValue); // Update cattle name
        });

        // Add a listener to update the cattle ID dynamically
        SelectedCattleManager.getInstance().selectedCattleIDProperty().addListener((observable, oldValue, newValue) -> {
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
        Objects.requireNonNull(ActionHandlerFactory.createActionHandler(buttonId)).handle(event);
    }


}
