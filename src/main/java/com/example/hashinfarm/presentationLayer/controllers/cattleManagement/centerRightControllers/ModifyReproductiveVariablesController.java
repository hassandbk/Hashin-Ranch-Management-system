package com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.data.DAOs.ReproductiveVariablesDAO;
import com.example.hashinfarm.data.DAOs.CattleDAO;
import com.example.hashinfarm.data.DTOs.records.ReproductiveVariables; // Ensure this refers to the new record

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class ModifyReproductiveVariablesController {

    @FXML
    private DatePicker breedingDatePicker;

    @FXML
    private DatePicker calvingDatePicker;

    @FXML
    private TextField calvingIntervalField;

    private ReproductiveVariables originalReproductiveVariables;

    private final ReproductiveVariablesDAO reproductiveVariablesDAO = new ReproductiveVariablesDAO();
    private CattleDAO cattleDAO;
    private ReproductiveVariables modifiedReproductiveVariables;

    @FXML
    private void initialize() {

        cattleDAO = new CattleDAO();

        calvingDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> computeCalvingInterval(newValue));
    }

    private void computeCalvingInterval(LocalDate newValue) {
        if (newValue != null && originalReproductiveVariables != null) {
            List<ReproductiveVariables> reproductiveVariablesList = reproductiveVariablesDAO.getAllReproductiveVariablesForCattle(originalReproductiveVariables.cattleID());

            List<ReproductiveVariables> sortedList = reproductiveVariablesList.stream()
                    .filter(rv -> rv.breedingDate() != null)
                    .sorted(Comparator.comparing(ReproductiveVariables::breedingDate))
                    .toList();

            if (!sortedList.isEmpty()) {
                ReproductiveVariables previousEntry = null;

                for (ReproductiveVariables currentEntry : sortedList) {
                    LocalDate breedingDate = currentEntry.breedingDate();

                    if (breedingDate.equals(originalReproductiveVariables.breedingDate())) {
                        int currentIndex = sortedList.indexOf(currentEntry);

                        for (int i = currentIndex - 1; i >= 0; i--) {
                            ReproductiveVariables previous = sortedList.get(i);
                            if (previous.calvingDate() != null) {
                                previousEntry = previous;
                                break;
                            }
                        }
                        if (previousEntry != null) {
                            break;
                        }
                    }
                }

                if (previousEntry != null) {
                    long days = newValue.toEpochDay() - previousEntry.calvingDate().toEpochDay();
                    calvingIntervalField.setText(String.valueOf(days));
                } else {
                    LocalDate dateOfBirth = cattleDAO.fetchDateOfBirth(originalReproductiveVariables.cattleID());
                    if (dateOfBirth != null) {
                        long days = newValue.toEpochDay() - dateOfBirth.toEpochDay();
                        calvingIntervalField.setText(String.valueOf(days));
                    }
                }
            }
        }
    }

    public void setReproductiveVariables(ReproductiveVariables reproductiveVariables) {
        this.originalReproductiveVariables = reproductiveVariables;
        if (reproductiveVariables != null) {
            breedingDatePicker.setValue(reproductiveVariables.breedingDate());
            calvingDatePicker.setValue(reproductiveVariables.calvingDate());
            calvingIntervalField.setText(String.valueOf(reproductiveVariables.calvingInterval()));

            LocalDate previousCalvingDate = getPreviousCalvingDate(reproductiveVariables.breedingDate());
            LocalDate earliestValidBreedingDate = calculateValidBreedingDate(previousCalvingDate, reproductiveVariables.calvingDate(), true);
            LocalDate latestValidBreedingDate = calculateValidBreedingDate(previousCalvingDate, reproductiveVariables.calvingDate(), false);

            breedingDatePicker.setDayCellFactory(datePicker -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && (earliestValidBreedingDate == null || latestValidBreedingDate == null || item.isBefore(earliestValidBreedingDate) || item.isAfter(latestValidBreedingDate))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });

            calvingDatePicker.setDayCellFactory(datePicker -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && breedingDatePicker.getValue() != null) {
                        LocalDate minDate = breedingDatePicker.getValue().plusDays(265);
                        LocalDate maxDate = breedingDatePicker.getValue().plusDays(295);
                        if (item.isBefore(minDate) || item.isAfter(maxDate)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                }
            });
        }
    }

    private LocalDate calculateValidBreedingDate(LocalDate previousCalvingDate, LocalDate currentCalvingDate, boolean isEarliest) {
        if (currentCalvingDate != null) {
            LocalDate referenceDate = isEarliest ? currentCalvingDate.minusDays(295) : currentCalvingDate.minusDays(265);
            LocalDate date = previousCalvingDate != null
                    ? previousCalvingDate.plusDays(isEarliest ? 40 : 60)
                    : cattleDAO.fetchDateOfBirth(originalReproductiveVariables.cattleID()).plusMonths(14);
            return date.isAfter(referenceDate) ? date : referenceDate;
        } else {
            return previousCalvingDate != null
                    ? previousCalvingDate.plusDays(isEarliest ? 40 : 60)
                    : cattleDAO.fetchDateOfBirth(originalReproductiveVariables.cattleID()).plusMonths(14);
        }
    }

    private LocalDate getPreviousCalvingDate(LocalDate breedingDate) {
        if (originalReproductiveVariables != null) {
            List<ReproductiveVariables> reproductiveVariablesList = reproductiveVariablesDAO.getAllReproductiveVariablesForCattle(originalReproductiveVariables.cattleID());
            if (reproductiveVariablesList != null) {
                reproductiveVariablesList.sort(Comparator.comparing(ReproductiveVariables::calvingDate, Comparator.nullsLast(Comparator.naturalOrder())));
                LocalDate previousCalvingDate = null;
                for (ReproductiveVariables entry : reproductiveVariablesList) {
                    LocalDate calvingDate = entry.calvingDate();
                    if (calvingDate != null && calvingDate.isBefore(breedingDate)) {
                        previousCalvingDate = calvingDate;
                    }
                }
                return previousCalvingDate;
            }
        }
        return null;
    }

    @FXML
    private void handleSave() {
        LocalDate breedingDate = breedingDatePicker.getValue();
        LocalDate calvingDate = calvingDatePicker.getValue();
        if (breedingDate != null && calvingDate != null) {
            int calvingInterval;
            try {
                calvingInterval = Integer.parseInt(calvingIntervalField.getText());
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid calving interval. Please enter a valid number.");
                return;
            }
            modifiedReproductiveVariables = new ReproductiveVariables(
                    originalReproductiveVariables.reproductiveVariableID(),
                    originalReproductiveVariables.cattleID(),
                    breedingDate,
                    originalReproductiveVariables.gestationPeriod(),
                    calvingDate,
                    calvingInterval
            );
            closeWindow();
        } else {
            showErrorAlert("Please select both breeding and calving dates.");
        }
    }

    public ReproductiveVariables getModifiedReproductiveVariables() {
        return modifiedReproductiveVariables;
    }

    @FXML
    private void handleCancel() {
        modifiedReproductiveVariables = null;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) breedingDatePicker.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
