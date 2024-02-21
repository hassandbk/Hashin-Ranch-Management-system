package com.example.hashinfarm.controller.handlers.imagesHandlers;

import com.example.hashinfarm.controller.handlers.ButtonActionHandler;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;

public class ViewImageHandler extends ButtonActionHandler {

    public ViewImageHandler() {
        super("viewImage", (event, gridPane) -> {
            // Additional logic specific to the "viewImage" button
        });
    }
}