package com.example.hashinfarm.businessLogic.services.interfaces;

import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;

public interface
ActionHandler {
    void handle(ActionEvent event);

    void handle(GridPane gridPane);
}
