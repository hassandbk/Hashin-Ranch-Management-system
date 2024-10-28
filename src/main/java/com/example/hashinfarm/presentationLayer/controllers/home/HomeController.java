package com.example.hashinfarm.presentationLayer.controllers.home;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import java.time.Year;

public class HomeController {

    private final MenuBarController menuBarController;

    @FXML
    public Button backButton;
    @FXML
    public Button forwardButton;
    @FXML
    public Label yearLabel;
    private int currentYear;

    public HomeController() {
        this.menuBarController = new MenuBarController();
        currentYear = Year.now().getValue();
    }

    @FXML
    public void initialize() {
        updateYearLabel();
    }

    @FXML
    public void handleMenuAction(ActionEvent actionEvent) {
        String fxId = ((MenuItem) actionEvent.getTarget()).getId();
        menuBarController.handleAction(fxId, actionEvent);
    }

    public void adjustYear(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        if (sourceButton.getId().equals("decrement")) {
            currentYear--;
        } else if (sourceButton.getId().equals("increment")) {
            currentYear++;
        }
        updateYearLabel();
    }

    private void updateYearLabel() {
        yearLabel.setText(String.valueOf(currentYear));
    }


}
