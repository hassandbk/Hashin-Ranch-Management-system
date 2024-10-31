package com.example.hashinfarm.utils.validation;

import com.example.hashinfarm.data.DTOs.LactationPeriodWithSelection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MissingEndDateCellFactory implements Callback<TableColumn<LactationPeriodWithSelection, LocalDate>, TableCell<LactationPeriodWithSelection, LocalDate>> {

    private final DateTimeFormatter dateFormatter;

    public MissingEndDateCellFactory(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    @Override
    public TableCell<LactationPeriodWithSelection, LocalDate> call(TableColumn<LactationPeriodWithSelection, LocalDate> param) {
        return new TableCell<>() {

            private final HBox content = new HBox();
            private final ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/exclamation_mark.png"))));
            private final Timeline blinkTimeline;

            {
                imageView.setFitWidth(16); // Adjust image size as needed
                imageView.setFitHeight(16);
                content.getChildren().addAll(imageView);
                // Set HBox properties to fill the cell horizontally
                content.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(content, Priority.ALWAYS);
                content.setAlignment(Pos.CENTER); // Center the content

                blinkTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), event -> imageView.setVisible(true)),
                        new KeyFrame(Duration.seconds(1), event -> imageView.setVisible(false))
                );
                blinkTimeline.setCycleCount(Animation.INDEFINITE);
            }

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                    setTooltip(null);
                    blinkTimeline.stop(); // Stop blinking when cell is empty
                    setStyle(null); // Clear any style applied
                } else {
                    LactationPeriodWithSelection lactationPeriodWithSelection = getTableView().getItems().get(getIndex());
                    LocalDate startDate = lactationPeriodWithSelection.getLactationPeriod().startDate();
                    LocalDate endDate = lactationPeriodWithSelection.getLactationPeriod().endDate();
                    LocalDate currentDate = LocalDate.now();

                    long daysSinceStart = ChronoUnit.DAYS.between(startDate, currentDate);

                    if (endDate == null && daysSinceStart > 365) {
                        setText("Missing End Date");
                        setTooltip(new Tooltip("This lactation period is missing an end date. Please update."));
                        blinkTimeline.play();
                        setGraphic(content);
                        // Set cell style for missing end date
                        setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    } else if (endDate == null && daysSinceStart > 305) {
                        setText("Threshold Nearing");
                        setTooltip(new Tooltip("This lactation period is nearing the one-year mark. Please provide the end date at the earliest possible date."));
                        blinkTimeline.play();
                        setGraphic(content);
                        // Set cell style for end date nearing
                        setStyle("-fx-background-color: lightyellow; -fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setText(endDate != null ? endDate.format(dateFormatter) : null);
                        setGraphic(null);
                        setTooltip(null);
                        blinkTimeline.stop(); // Stop blinking when cell is not empty
                        setStyle(null); // Clear any style applied
                    }

                    setAlignment(Pos.CENTER); // Center the text
                }
            }
        };
    }
}
