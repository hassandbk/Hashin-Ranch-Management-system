package com.example.hashinfarm.controller.utility;

import javafx.animation.FadeTransition;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
            private final FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), imageView);

            {
                imageView.setFitWidth(16); // Adjust image size as needed
                imageView.setFitHeight(16);
                content.getChildren().addAll(imageView);

                // Set up fade transition
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0.0);
                fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
                fadeTransition.setAutoReverse(true);
            }

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setTooltip(null);
                } else {
                    LactationPeriodWithSelection lactationPeriodWithSelection = getTableView().getItems().get(getIndex());
                    LocalDate startDate = lactationPeriodWithSelection.getLactationPeriod().getStartDate();
                    LocalDate endDate = lactationPeriodWithSelection.getLactationPeriod().getEndDate() != null
                            ? lactationPeriodWithSelection.getLactationPeriod().getEndDate()
                            : null;
                    LocalDate currentDate = LocalDate.now();

                    long daysSinceStart = ChronoUnit.DAYS.between(startDate, currentDate);

                    if (endDate == null && daysSinceStart > 365) {
                        setText("Missing End Date");
                        setTooltip(new Tooltip("This lactation period is missing an end date. Please update."));
                        if (!content.getChildren().contains(imageView)) {
                            content.getChildren().add(imageView);
                            fadeTransition.play(); // Start the animation
                        }
                        setGraphic(content);
                    } else if (endDate == null && daysSinceStart > 305) {
                        setText("End Date Nearing");
                        setTooltip(new Tooltip("This lactation period is nearing the one-year mark. Please provide the end date at the earliest possible date."));
                        if (!content.getChildren().contains(imageView)) {
                            content.getChildren().add(imageView);
                            fadeTransition.play(); // Start the animation
                        }
                        setGraphic(content);
                    } else {
                        setText(endDate != null ? endDate.format(dateFormatter) : null);
                        setGraphic(null);
                        setTooltip(null);
                        if (content.getChildren().contains(imageView)) {
                            content.getChildren().remove(imageView);
                            fadeTransition.stop(); // Stop the animation
                        }
                    }
                }
            }
        };
    }
}
