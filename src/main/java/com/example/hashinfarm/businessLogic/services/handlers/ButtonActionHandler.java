// ButtonActionHandler.java
package com.example.hashinfarm.businessLogic.services.handlers;

import com.example.hashinfarm.businessLogic.services.interfaces.ActionHandler;
import com.example.hashinfarm.utils.interfaces.ViewInitializer;
import com.example.hashinfarm.utils.FXMLLoaderUtil;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;

import java.util.function.BiConsumer;

public class ButtonActionHandler implements ActionHandler, ViewInitializer {

    private final String fxId;
    private final BiConsumer<ActionEvent, GridPane> customHandler;

    public ButtonActionHandler(String fxId, BiConsumer<ActionEvent, GridPane> customHandler) {
        this.fxId = fxId;
        this.customHandler = customHandler;
    }

    @Override
    public void handle(ActionEvent event) {
        initializeView();
        if (customHandler != null && event.getSource() instanceof GridPane) {
            customHandler.accept(event, (GridPane) event.getSource());
        }
    }

    @Override
    public void handle(GridPane gridPane) {
        // Implement if needed
    }

    @Override
    public void initializeView() {
        String fxmlPath = String.format("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerRightViews/cattleDetailMoreButtons/%s.fxml", fxId);
        String viewTitle = fxId.replaceFirst("^\\w", String.valueOf(Character.toUpperCase(fxId.charAt(0))));
        FXMLLoaderUtil.loadFXML(fxmlPath, viewTitle);
    }
}
