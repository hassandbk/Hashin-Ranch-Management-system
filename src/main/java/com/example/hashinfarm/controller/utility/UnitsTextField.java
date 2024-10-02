package com.example.hashinfarm.controller.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class UnitsTextField {

    private static final Pattern VALID_INPUT_PATTERN = Pattern.compile("-?\\d*\\.?\\d*"); // Pattern to allow only valid numeric input

    public static void initializeTextField(TextField textField, MeasurementType measurementType) {
        String suffix = measurementType.getSuffix(); // Get the suffix for the measurement type

        // Add TextFormatter to restrict input to numeric values and append the appropriate suffix
        TextFormatter<String> textFormatter = getStringTextFormatter(suffix);
        textField.setTextFormatter(textFormatter);

        // Add suffix to the text if it has numeric part and doesn't end with the suffix
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                textField.setText("");
            } else {
                String numericPart = extractNumericPart(newValue, suffix);
                if (!numericPart.isEmpty() && !newValue.endsWith(suffix)) {
                    textField.setText(numericPart + suffix);
                    textField.positionCaret(numericPart.length());
                } else if (numericPart.isEmpty()) {
                    textField.setText("");
                }
            }
        });

        // Handle cursor placement to prevent modification of the suffix
        textField.caretPositionProperty().addListener((observable, oldPos, newPos) -> {
            String text = textField.getText();
            if (newPos.intValue() > text.length() - suffix.length()) {
                textField.positionCaret(text.length() - suffix.length());
            }
        });
    }

    public static void addValidationListener(TextField textField, MeasurementType measurementType, Function<String, Boolean> validationFunction) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                String text = textField.getText().trim();

                if (text.isEmpty()) {
                    textField.setStyle("");
                    return;
                }

                // Perform validation
                if (!validationFunction.apply(text)) {
                    textField.setStyle("-fx-border-color: red;");
                    showErrorAlert();
                } else {
                    textField.setStyle("");
                }
            }
        });
    }

    private static @NotNull TextFormatter<String> getStringTextFormatter(String suffix) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            // Strip out the suffix part when working with the actual text input.
            if (newText.endsWith(suffix)) {
                newText = newText.substring(0, newText.length() - suffix.length());
            }

            // Check if the new text is a valid number or if the field is being cleared
            if (VALID_INPUT_PATTERN.matcher(newText).matches() || newText.isEmpty()) {
                return change;
            } else {
                return null; // Reject the change if it doesn't match the pattern
            }
        };

        return new TextFormatter<>(filter);
    }

    private static String extractNumericPart(String text, String suffix) {
        if (text.endsWith(suffix)) {
            return text.substring(0, text.length() - suffix.length()).trim(); // Extract numeric part
        }
        return text.trim(); // Return trimmed text if it doesn't end with the suffix
    }

    public static boolean isValidMeasurement(String text, MeasurementType measurementType) {
        if (text != null && text.endsWith(measurementType.getSuffix())) {
            String numericPart = text.substring(0, text.length() - measurementType.getSuffix().length()).trim();
            try {
                double value = Double.parseDouble(numericPart);
                return value >= 0 && value <= measurementType.getMax(); // Ensure the value is within the valid range
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }


    private static void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText("Invalid Units. Please enter a valid number");
        alert.showAndWait();
    }
}
