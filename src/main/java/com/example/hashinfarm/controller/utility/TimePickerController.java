package com.example.hashinfarm.controller.utility;

import com.example.hashinfarm.model.LactationPeriod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TimePickerController {

    @FXML private Label lactationPeriodStartDateLabel,selectedProductionSessionDateLabel,timeSelectedLabel;
    @FXML private Spinner<Integer> hourSpinner, minuteSpinner;
    @FXML private ToggleButton amPmToggleButton;
    @FXML private Button cancelButton,setButton;


    private Stage stage;
    private Consumer<LocalTime> timeSelectedCallback;
    private Button morningStartTimeButton, morningEndTimeButton;
    private Button afternoonStartTimeButton, afternoonEndTimeButton;
    private Button eveningStartTimeButton, eveningEndTimeButton;


    public void setInitialTimeParts(int[] initialTimeParts) {
        if (initialTimeParts != null) {
            hourSpinner.getValueFactory().setValue(initialTimeParts[0]);
            minuteSpinner.getValueFactory().setValue(initialTimeParts[1]);
         }
        updateTimeSelectedLabel();
    }

    @FXML
    public void initialize() {
        setupTimeSpinners();
        setButtonActions();
    }
    public void setButtons(Button[] buttons) {
        if (buttons.length == 6) {
            this.morningStartTimeButton = buttons[0];
            this.morningEndTimeButton = buttons[1];
            this.afternoonStartTimeButton = buttons[2];
            this.afternoonEndTimeButton = buttons[3];
            this.eveningStartTimeButton = buttons[4];
            this.eveningEndTimeButton = buttons[5];
        } else {
            throw new IllegalArgumentException("Expected array of length 6.");
        }
    }

    public void setTimeSelectedCallback(Consumer<LocalTime> callback) {
        this.timeSelectedCallback = callback;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLactationPeriodStartDate(LactationPeriod lactationPeriod) {
        if (lactationPeriod != null && lactationPeriod.getStartDate() != null) {
            LocalDate startDate = lactationPeriod.getStartDate();
            HBox formattedDate = formatDate(startDate);
            lactationPeriodStartDateLabel.setText(null);
            lactationPeriodStartDateLabel.setGraphic(formattedDate);
        } else {
            lactationPeriodStartDateLabel.setGraphic(null);
        }
    }

    public void setSelectedProductionSession(LocalDate selectedProductionDate) {
        if (selectedProductionDate != null) {
            HBox formattedDate = formatDate(selectedProductionDate);
            selectedProductionSessionDateLabel.setText(null);
            selectedProductionSessionDateLabel.setGraphic(formattedDate);
        } else {
            selectedProductionSessionDateLabel.setGraphic(null);
        }
    }

    public void setSessionType(Button button) {
        setupSession(button);
        updateToggleButtonText(button);
    }

    private void setupSession(Button button) {
        if (button == morningStartTimeButton || button == morningEndTimeButton) {
            setupMorningSession();
        } else if (button == afternoonStartTimeButton || button == afternoonEndTimeButton) {
            setupAfternoonSession();
        } else if (button == eveningStartTimeButton || button == eveningEndTimeButton) {
            setupEveningSession();
        } else {
            throw new IllegalArgumentException("Unknown session type for button: " + button);
        }
    }


    private void setupMorningSession() {
        amPmToggleButton.setSelected(false);
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 11));
    }

    private void setupAfternoonSession() {
        amPmToggleButton.setSelected(true);
        List<Integer> afternoonHoursList = Arrays.asList(12, 1, 2, 3, 4);
        ObservableList<Integer> observableAfternoonHours = FXCollections.observableArrayList(afternoonHoursList);
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(observableAfternoonHours);
        hourSpinner.setValueFactory(valueFactory);
    }


    private void setupEveningSession() {
        amPmToggleButton.setSelected(true);
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 9));
    }

    // Helper method to format LocalDate as "Fri 15th/05/2015"
    public static HBox formatDate(LocalDate date) {
        // Get day of the week abbreviation (e.g., Fri)
        String dayOfWeek = date.getDayOfWeek().toString().substring(0, 3); // Get first 3 characters in lowercase
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1); // Convert first character to uppercase

        // Get day of the month with proper suffix (e.g., 15th)
        int dayOfMonth = date.getDayOfMonth();
        String dayOfMonthFormatted = dayOfMonth + getDayOfMonthSuffix(dayOfMonth);

        // Get month abbreviation (e.g., 05 for May)
        String month = String.format("%02d", date.getMonthValue()); // 2-digit month

        // Get year (e.g., 2015)
        int year = date.getYear();

        // Create Text nodes for different parts of the formatted date
        Text dayOfWeekText = new Text(dayOfWeek + " ");
        Text dayOfMonthText = new Text(dayOfMonthFormatted + "/");
        Text monthText = new Text(month + "/");
        Text yearText = new Text(String.valueOf(year));


        dayOfMonthText.setFill(Color.WHITE);
        monthText.setFill(Color.WHITE);
        yearText.setFill(Color.WHITE);

        return new HBox(dayOfWeekText, dayOfMonthText, monthText, yearText);
    }

    // Helper method to get the proper suffix for the day of the month (e.g., "st", "nd", "rd", "th")
    private static String getDayOfMonthSuffix(int dayOfMonth) {
        if (dayOfMonth >= 11 && dayOfMonth <= 13) {
            return "th";
        }
        return switch (dayOfMonth % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
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

        if (amPmToggleButton.getText().equals("AM")) {
            String style = baseStyle + (button.isSelected() ? selectedStyle : unselectedStyle);
            button.setStyle(style);
        } else if (amPmToggleButton.getText().equals("PM")) {
            // Adjust styles accordingly for PM
            String style = baseStyle + (button.isSelected() ? selectedStyle : unselectedStyle);
            button.setStyle(style);
        }
    }


    private void updateToggleButtonText(Button button) {
        if (button == morningStartTimeButton || button == morningEndTimeButton) {
            amPmToggleButton.setText("AM");

        } else {
            amPmToggleButton.setText("PM");

        }
        updateToggleButtonStyle(amPmToggleButton);
    }

}
