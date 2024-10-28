package com.example.hashinfarm.utils.validation;

import com.example.hashinfarm.utils.exceptions.ValidationException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class InputFieldsValidation {
    public static void validateField(boolean condition, String errorMessage, Control control) throws ValidationException {
        if (condition) {
            // If condition is true, field is invalid, apply error and throw exception
            if (control != null) applyErrorEffect(control);
            throw new ValidationException(errorMessage);
        } else {
            // If condition is false, field is valid, remove any applied error effect
            if (control != null) removeErrorEffect(control);
        }
    }

    private static void applyErrorEffect(Control control) {
        setControlStyle(control, "-fx-border-color: red; -fx-border-width: 2px;");
        createBlinkingEffect(control);
        addInteractionListeners(control);
    }

    private static void setControlStyle(Control control, String style) {
        control.setStyle(style);
    }

    private static void createBlinkingEffect(Control control) {
        // Stop any existing animations before starting a new one
        control.setUserData(null); // Use user data to store the animation if needed

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> setControlStyle(control, "-fx-border-color: transparent;")),
                new KeyFrame(Duration.seconds(1), e -> setControlStyle(control, "-fx-border-color: red; -fx-border-width: 2px;"))
        );
        timeline.setCycleCount(6);
        timeline.play();

        // Store the animation reference if necessary
        control.setUserData(timeline);
    }

    private static void addInteractionListeners(Control control) {
        control.setOnMouseClicked(event -> removeErrorEffect(control));
        control.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) removeErrorEffect(control);
        });

        switch (control) {
            case TextField textField -> textField.textProperty().addListener((observable, oldValue, newValue) -> removeErrorEffect(control));
            case ComboBox<?> comboBox -> comboBox.valueProperty().addListener((observable, oldValue, newValue) -> removeErrorEffect(control));
            case DatePicker datePicker -> datePicker.valueProperty().addListener((observable, oldValue, newValue) -> removeErrorEffect(control));
            default -> {
            }
        }
    }

    private static void removeErrorEffect(Control control) {
        // Immediately reset the control's style
        setControlStyle(control, "");

        // Stop any blinking effect if it's running
        Timeline timeline = (Timeline) control.getUserData();
        if (timeline != null) {
            timeline.stop();
            control.setUserData(null); // Clear user data after stopping the timeline
        }
    }
}
