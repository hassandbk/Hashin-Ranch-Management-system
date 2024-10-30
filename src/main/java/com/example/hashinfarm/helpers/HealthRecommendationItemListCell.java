package com.example.hashinfarm.helpers;

import com.example.hashinfarm.data.DTOs.records.HealthRecommendationItem;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class HealthRecommendationItemListCell extends ListCell<HealthRecommendationItem> {
    private static final String RECOMMENDATION_STYLE = "-fx-padding: 5;";
    private static final String HOVER_STYLE = "-fx-background-color: lightblue;";
    private static final int PREF_WIDTH = 300;

    private final VBox vBox; // Use VBox to hold the labels

    public HealthRecommendationItemListCell() {
        vBox = new VBox();
        vBox.setPadding(new Insets(1, 10, 1, 10)); // Padding for the VBox
        vBox.setStyle("-fx-pref-width: " + PREF_WIDTH + ";"); // Set preferred width for VBox
    }

    @Override
    protected void updateItem(HealthRecommendationItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            clearCell(); // Clear the cell if empty
            return;
        }

        clearPreviousContent(); // Clear previous content
        Label label = createLabel(item); // Create a new label for the item
        vBox.getChildren().add(label); // Add the label to the VBox
        setGraphic(vBox); // Set the VBox as the graphic for the cell

        setHoverEffects(); // Set hover effects for the recommendation items
    }

    private void clearCell() {
        setGraphic(null); // Clear graphic for empty items
        setStyle(""); // Clear style for empty items
    }

    private void clearPreviousContent() {
        vBox.getChildren().clear(); // Clear previous content
    }

    private Label createLabel(HealthRecommendationItem item) {
        // Correct the method call to recommendation()
        Label label = new Label(item.recommendation());
        label.setWrapText(true); // Enable text wrapping
        label.setMaxWidth(PREF_WIDTH); // Prevent exceeding the fixed width
        label.setStyle(RECOMMENDATION_STYLE); // Apply styles
        return label; // Return the created label
    }

    private void setHoverEffects() {
        // Highlighting mechanism for hover effects
        setOnMouseEntered(e -> {
            setStyle(HOVER_STYLE); // Hover effect for recommendation items
        });

        setOnMouseExited(e -> {
            setStyle(""); // Remove hover effect
        });
    }
}
