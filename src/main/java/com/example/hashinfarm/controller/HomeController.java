package com.example.hashinfarm.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import java.time.Year;

public class HomeController {

    @FXML
    public Button backButton;
    @FXML
    public Button forwardButton;
    @FXML
    public Label yearLabel;

    private int currentYear;

    private final MenuBarController menuBarController;

    public HomeController() {
        this.menuBarController = new MenuBarController();
        currentYear = Year.now().getValue();

    }

    @FXML
    public void initialize() {
        // Set the initial text of the yearLabel when the window loads
        updateYearLabel();
    }

    @FXML
    public void handleMenuAction(ActionEvent actionEvent) {
        // Retrieve the fx:id of the clicked menu item
        String fxId = ((MenuItem) actionEvent.getTarget()).getId();

        // Pass the fx:id to the menuBarController for handling
        menuBarController.handleAction(fxId, actionEvent);
        // Additional logic in the home controller, if needed
    }

    @FXML
    public void adjustYear(ActionEvent event) {
        // Get the source of the event (either backButton or forwardButton)
        Button sourceButton = (Button) event.getSource();

        // Adjust the currentYear based on the button clicked
        if (sourceButton.getId().equals("decrement")) {
            currentYear--;
        } else if (sourceButton.getId().equals("increment")) {
            currentYear++;
        }

        // Update the yearLabel text
        updateYearLabel();
    }

    private void updateYearLabel() {
        // Set the text of the yearLabel with the current year
        yearLabel.setText(String.valueOf(currentYear));
    }
}
