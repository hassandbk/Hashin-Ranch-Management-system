package com.example.hashinfarm.controller.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class UnitsTextField {

    public static final String ML_SUFFIX = "  ML";  // Constant "ML" suffix
    public static final String KG_SUFFIX = "  KG";  // Constant "KG" suffix
    private static final Pattern VALID_INPUT_PATTERN = Pattern.compile("-?\\d*\\.?\\d*"); // Pattern to allow only valid numeric input

    public static void initializeTextField(TextField textField, boolean isDosage) {
        String suffix = isDosage ? ML_SUFFIX : KG_SUFFIX; // Determine the suffix

        // Add TextFormatter to restrict input to numeric values and append the appropriate suffix
        TextFormatter<String> textFormatter = getStringTextFormatter(suffix);
        textField.setTextFormatter(textFormatter);

        // Add suffix to the text if it has numeric part and doesn't end with the suffix
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                // Clear the field completely
                textField.setText("");
            } else {
                // Check for numeric part and update suffix
                String numericPart = extractNumericPart(newValue, suffix);
                if (!numericPart.isEmpty() && !newValue.endsWith(suffix)) {
                    textField.setText(numericPart + suffix);
                    textField.positionCaret(numericPart.length()); // Set cursor to the end of the numeric part
                } else if (numericPart.isEmpty()) {
                    textField.setText(""); // Clear the field if no numeric part exists
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
    public static void addValidationListener(TextField textField, Function<String, Boolean> validationFunction) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                String text = textField.getText().trim(); // Get trimmed text

                if (text.isEmpty()) {
                    // Do nothing if the text is empty
                    textField.setStyle(""); // Clear any error style if previously set
                    return;
                }

                // Perform validation only if the text is not empty
                if (!validationFunction.apply(text)) {
                    textField.setStyle("-fx-border-color: red;"); // Indicate an error
                    showErrorAlert();
                } else {
                    textField.setStyle(""); // Clear error style if valid
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

    public static boolean isValidDosage(String text) {
        return isValidMeasurement(text, ML_SUFFIX, 1000); // Valid range for dosage (mL)
    }

    public static boolean isValidWeight(String text) {
        return isValidMeasurement(text, KG_SUFFIX, 2000); // Valid range for weight (kg)
    }

    private static boolean isValidMeasurement(String text, String suffix, double max) {
        if (text != null && text.endsWith(suffix)) {
            String numericPart = text.substring(0, text.length() - suffix.length()).trim(); // Extract numeric part
            try {
                double value = Double.parseDouble(numericPart);  // Convert the numeric part to a double
                return value >= 0 && value <= max;  // Ensure the value is within the valid range
            } catch (NumberFormatException e) {
                return false;  // Return false if the numeric part is not a valid number
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
