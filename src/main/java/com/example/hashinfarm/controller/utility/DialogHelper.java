package com.example.hashinfarm.controller.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DialogHelper {

    // Method to show a confirmation dialog
    public static void showConfirmationDialog(String title, String contentText,
                                              List<ButtonType> buttonTypes,
                                              List<Consumer<Void>> actions) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(contentText);
        alert.getButtonTypes().setAll(buttonTypes);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            int index = buttonTypes.indexOf(result.get());
            if (index >= 0 && index < actions.size()) {
                Consumer<Void> action = actions.get(index);
                if (action != null) {
                    action.accept(null);
                }
            }
        }
    }

    // Method to show an update dialog
    public static void showUpdateDialog(String title,
                                        boolean isModified,
                                        Consumer<Void> updateAction,
                                        Consumer<Void> restoreAction,
                                        Consumer<Void> deleteAction,
                                        Consumer<Void> clearFieldsAction) {
        List<ButtonType> buttonTypes = new ArrayList<>();

        if (isModified) {
            buttonTypes.add(new ButtonType("Update"));
            buttonTypes.add(new ButtonType("Restore"));
        }
        buttonTypes.add(new ButtonType("Delete"));
        if (!isModified) {
            buttonTypes.add(new ButtonType("New Record"));
        }
        buttonTypes.add(new ButtonType("Cancel"));

        List<Consumer<Void>> actions = new ArrayList<>();
        if (isModified) {
            actions.add(updateAction);
            actions.add(restoreAction);
        }
        actions.add(deleteAction);
        actions.add(clearFieldsAction);
        actions.add(v -> {}); // Cancel does nothing as it's handled automatically

        showConfirmationDialog(title, "What would you like to do?", buttonTypes, actions);
    }
}
