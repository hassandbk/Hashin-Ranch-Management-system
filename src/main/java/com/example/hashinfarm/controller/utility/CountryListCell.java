package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.model.Country;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CountryListCell extends ListCell<Country> {

    private final Label nameLabel = new Label();
    private final Label callingCodeLabel = new Label();
    private final ImageView flagImageView = new ImageView();
    private final HBox hbox = new HBox(10, flagImageView, nameLabel, callingCodeLabel);
    private final ConcurrentHashMap<String, Image> flagImageCache;

    public CountryListCell(ConcurrentHashMap<String, Image> flagImageCache) {
        this.flagImageCache = flagImageCache;

        hbox.setStyle("-fx-padding: 5; -fx-alignment: center-left;");

        // Set flag image size (4:3 ratio)
        flagImageView.setFitWidth(32);
        flagImageView.setFitHeight(24);
        flagImageView.setPreserveRatio(true);

        setGraphic(hbox);  // Set the HBox as the graphic for the ListCell
    }

    @Override
    protected void updateItem(Country country, boolean empty) {
        super.updateItem(country, empty);
        if (empty || country == null) {
            setText(null);
            setGraphic(null);
        } else {
            nameLabel.setText(country.getName());
            callingCodeLabel.setText(country.getCallingCode());

            // Use cached flag images if available
            String flagName = Optional.ofNullable(country.getFlagName()).orElse("placeholder.png");
            Image flagImage = flagImageCache.get(flagName);

            if (flagImage != null) {
                flagImageView.setImage(flagImage);
            } else {
                // Fallback if image is not in cache
                flagImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png"))));
            }

            setGraphic(hbox);  // Make sure the HBox is set each time the cell is updated
        }
    }
}
