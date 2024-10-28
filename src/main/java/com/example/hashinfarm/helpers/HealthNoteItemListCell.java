package com.example.hashinfarm.helpers;

import com.example.hashinfarm.data.records.HealthNoteItem;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class HealthNoteItemListCell extends ListCell<HealthNoteItem> {
    private static final String CATEGORY_STYLE = "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: darkblue; -fx-background-color: lightgray;";
    private static final String SUBLIST_STYLE = "-fx-padding: 5 0 5 50;";
    private static final String HOVER_STYLE = "-fx-background-color: lightblue;";
    private static final int PREF_WIDTH = 600;

    private final VBox vBox; // Use VBox to hold the labels

    public HealthNoteItemListCell() {
        vBox = new VBox();
        vBox.setPadding(new Insets(5, 10, 5, 10)); // Padding for the VBox
        vBox.setStyle("-fx-pref-width: " + PREF_WIDTH + ";"); // Set preferred width for VBox
    }

    @Override
    protected void updateItem(HealthNoteItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            clearCell(); // Clear the cell if empty
            return;
        }

        clearPreviousContent(); // Clear previous content
        Label label = createLabel(item); // Create a new label for the item
        vBox.getChildren().add(label); // Add the label to the VBox
        setGraphic(vBox); // Set the VBox as the graphic for the cell

        setHoverEffects(item); // Set hover effects based on item type
    }

    private void clearCell() {
        setGraphic(null); // Clear graphic for empty items
        setStyle(""); // Clear style for empty items
    }

    private void clearPreviousContent() {
        vBox.getChildren().clear(); // Clear previous content
    }

    private Label createLabel(HealthNoteItem item) {
        Label label = new Label(item.isCategory() ? item.text() : "• " + item.text());
        label.setWrapText(true); // Enable text wrapping
        label.setMaxWidth(PREF_WIDTH); // Prevent exceeding the fixed width

        // Apply styles based on whether the item is a category or not
        if (item.isCategory()) {
            label.setStyle(CATEGORY_STYLE);
        } else {
            label.setStyle(SUBLIST_STYLE);
        }

        return label; // Return the created label
    }

    private void setHoverEffects(HealthNoteItem item) {
        // Highlighting mechanism for hover effects
        setOnMouseEntered(e -> {
            if (!item.isCategory()) {
                setStyle(HOVER_STYLE); // Hover effect for sublist items
            }
        });

        setOnMouseExited(e -> {
            if (!item.isCategory()) {
                setStyle(""); // Remove hover effect
            }
        });
    }
}
