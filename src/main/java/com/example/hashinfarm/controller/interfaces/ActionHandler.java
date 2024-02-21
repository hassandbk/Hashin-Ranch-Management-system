package com.example.hashinfarm.controller.interfaces;

import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;

public interface
ActionHandler {
    void handle(ActionEvent event);

    void handle(GridPane gridPane);
}
