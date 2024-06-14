package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.model.LactationPeriod;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.util.function.Consumer;

public class TimePickerController {

    @FXML private Label dateLabel;
    @FXML private Label timeSelectedLabel;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private ToggleButton amPmToggleButton;
    @FXML private Button cancelButton;
    @FXML private Button setButton;

    private Stage stage;
    private Consumer<LocalTime> timeSelectedCallback;


    public void setInitialTimeParts(int[] initialTimeParts) {
        if (initialTimeParts != null) {
            hourSpinner.getValueFactory().setValue(initialTimeParts[0]);
            minuteSpinner.getValueFactory().setValue(initialTimeParts[1]);
            amPmToggleButton.setSelected(initialTimeParts[2] == 1);
            updateToggleButtonText(); // Update text based on selected state

        } else {
            LocalTime currentTime = LocalTime.now();
            int hour = currentTime.getHour() % 12;
            if (hour == 0) hour = 12;
            hourSpinner.getValueFactory().setValue(hour);
            minuteSpinner.getValueFactory().setValue(currentTime.getMinute());
            amPmToggleButton.setSelected(currentTime.getHour() >= 12);
            updateToggleButtonText(); // Update text based on selected state
             }
        updateTimeSelectedLabel();
    }

    @FXML
    public void initialize() {
        setupTimeSpinners();
        setupAmPmToggleButton();
        setButtonActions();
    }

    public void setTimeSelectedCallback(Consumer<LocalTime> callback) {
        this.timeSelectedCallback = callback;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLactationPeriodStartDate(LactationPeriod lactationPeriod) {
        if (lactationPeriod != null) {
            dateLabel.setText(String.valueOf(lactationPeriod.getStartDate()));

        }
    }



    private void setupTimeSpinners() {
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));

        setupSpinnerConverter(hourSpinner);
        setupSpinnerConverter(minuteSpinner);

        hourSpinner.valueProperty().addListener((observable, oldValue, newValue) -> updateTimeSelectedLabel());
        minuteSpinner.valueProperty().addListener((observable, oldValue, newValue) -> updateTimeSelectedLabel());
    }

    private void setupSpinnerConverter(Spinner<Integer> spinner) {
        spinner.getValueFactory().setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return String.format("%02d", value);
            }

            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        });
    }

    private void setupAmPmToggleButton() {
        amPmToggleButton.setOnAction(e -> {
            updateToggleButtonStyle(amPmToggleButton);
            updateTimeSelectedLabel();
            updateToggleButtonText();
        });
    }

    private void setButtonActions() {
        setButton.setOnAction(this::handleSetButtonAction);
        cancelButton.setOnAction(e -> closeWindow());
    }

    @FXML
    private void handleSetButtonAction(ActionEvent event) {
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        if (amPmToggleButton.isSelected() && hour != 12) {
            hour += 12;
        } else if (!amPmToggleButton.isSelected() && hour == 12) {
            hour = 0;
        }
        LocalTime selectedTime = LocalTime.of(hour, minute);
        if (timeSelectedCallback != null) {
            timeSelectedCallback.accept(selectedTime);
        }
        closeWindow();
    }

    private void closeWindow() {
        if (stage != null) {
            stage.close();

        }
    }

    private void updateTimeSelectedLabel() {
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        String amPm = amPmToggleButton.isSelected() ? "PM" : "AM";
        timeSelectedLabel.setText(String.format("%02d:%02d %s", hour, minute, amPm));

    }

    private void updateToggleButtonStyle(ToggleButton button) {
        String baseStyle = "-fx-text-fill: #fdfdfd; -fx-font-weight: bold;";
        String selectedStyle = "-fx-background-color: rgba(123,181,239,0.51);";
        String unselectedStyle = "-fx-background-color: #fdfdfd; -fx-text-fill: #09190A;";

        String style = button.isSelected() ? baseStyle + selectedStyle : baseStyle + unselectedStyle;
        button.setStyle(style);
    }

    private void updateToggleButtonText() {
        amPmToggleButton.setText(amPmToggleButton.isSelected() ? "PM" : "AM");
        updateToggleButtonStyle(amPmToggleButton);
           }
}
