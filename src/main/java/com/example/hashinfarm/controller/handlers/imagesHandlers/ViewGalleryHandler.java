package com.example.hashinfarm.controller.handlers.imagesHandlers;

import com.example.hashinfarm.controller.handlers.ButtonActionHandler;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;

public class ViewGalleryHandler extends ButtonActionHandler {

    public ViewGalleryHandler() {
        super("viewGallery", (event, gridPane) -> {
            // Additional logic specific to the "viewGallery" button
        });
    }
}