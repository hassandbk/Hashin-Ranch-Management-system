package com.example.hashinfarm.controller;

import com.example.hashinfarm.controller.handlers.menuBarHandlers.NewRanchHandler;
import javafx.event.ActionEvent;

public class MenuBarController {
    private final NewRanchHandler newRanchHandler;
    // Declare other action handlers

    public MenuBarController() {
        newRanchHandler = new NewRanchHandler();
        // Initialize other action handlers
    }

    public void handleAction(String actionCommand, ActionEvent actionEvent) {
        switch (actionCommand) {
            case "New Ranch":
                newRanchHandler.handle(actionEvent);
                break;
            default:
                // Handle unknown action
                break;
        }
    }
}