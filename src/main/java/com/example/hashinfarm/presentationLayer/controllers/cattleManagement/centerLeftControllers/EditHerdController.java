package com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerLeftControllers;

import com.example.hashinfarm.data.DAOs.CattleDAO;
import com.example.hashinfarm.data.DAOs.HerdDAO;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.Cattle;
import com.example.hashinfarm.data.DTOs.Herd;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditHerdController {

  @FXML private TextField herdNameTextField, locationTextField;
  @FXML
  private ComboBox<String> animalClassComboBox,
          breedTypeComboBox,
          breedSystemComboBox,
          solutionTypeComboBox,
          ageClassComboBox,
          feedBasisComboBox;

  @FXML private TableView<Cattle> cattleTableView;

  @FXML
  private TableColumn<Cattle, Integer> cattleIdColumn,
          cattleAgeColumn,
          cattleWeightIdColumn,
          cattleBreedIdColumn;
  @FXML
  private TableColumn<Cattle, String> cattleTagIdColumn,
          cattleColorMarkingsColumn,
          cattleNameColumn,
          cattleGenderColumn,
          cattleBcsColumn;

  @FXML private TableColumn<Cattle, Date> cattleDateOfBirthColumn;
  private Herd selectedHerd;

  private <T> void setCattleColumn(TableColumn<Cattle, T> column, Function<Cattle, T> mapper) {
    column.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(mapper.apply(cellData.getValue())));
  }

  public void initData(Herd herd) {
    if (herd == null) {
      throw new IllegalArgumentException("Herd cannot be null");
    }
    this.selectedHerd = herd;
    populateFields();
    populateCattleTable();
  }

  private void populateFields() {
    herdNameTextField.setText(selectedHerd.getName());
    locationTextField.setText(selectedHerd.getLocation());
    animalClassComboBox.setValue(selectedHerd.getAnimalClass());
    breedTypeComboBox.setValue(selectedHerd.getBreedType());
    breedSystemComboBox.setValue(selectedHerd.getBreedSystem());
    solutionTypeComboBox.setValue(selectedHerd.getSolutionType());
    ageClassComboBox.setValue(selectedHerd.getAgeClass());
    feedBasisComboBox.setValue(selectedHerd.getFeedBasis());
  }

  private void populateCattleTable() {
    ObservableList<Cattle> cattleList =
            FXCollections.observableArrayList(selectedHerd.getAnimals());
    cattleTableView.setItems(cattleList);
    setCattleTableColumns();
  }

  private void setCattleTableColumns() {
    setCattleColumn(cattleIdColumn, Cattle::getCattleId);
    setCattleColumn(cattleTagIdColumn, Cattle::getTagId);
    setCattleColumn(cattleColorMarkingsColumn, Cattle::getColorMarkings);
    setCattleColumn(cattleNameColumn, Cattle::getName);
    setCattleColumn(cattleGenderColumn, Cattle::getGender);
    setCattleColumn(cattleAgeColumn, Cattle::getAge);
    setCattleColumn(cattleWeightIdColumn, Cattle::getWeightId);
    setCattleColumn(cattleBcsColumn, Cattle::getBcs);
    setCattleColumn(cattleBreedIdColumn, Cattle::getBreedId);

    // Handle the LocalDate to java.sql.Date conversion for the Date of Birth column
    cattleDateOfBirthColumn.setCellValueFactory(
            cellData -> new SimpleObjectProperty<>(Date.valueOf(cellData.getValue().getDateOfBirth())));
  }

  public void handleEditHerd() {
    try {
      updateHerdDetails();
      showAlert(Alert.AlertType.INFORMATION, "Success", "Herd Updated Successfully");
      closeEditWindow();
    } catch (SQLException e) {
      handleDatabaseError(e);
    }
  }

  private void updateHerdDetails() throws SQLException {
    selectedHerd.setName(herdNameTextField.getText());
    selectedHerd.setLocation(locationTextField.getText());
    selectedHerd.setAnimalClass(animalClassComboBox.getValue());
    selectedHerd.setBreedType(breedTypeComboBox.getValue());
    selectedHerd.setBreedSystem(breedSystemComboBox.getValue());
    selectedHerd.setSolutionType(solutionTypeComboBox.getValue());
    selectedHerd.setAgeClass(ageClassComboBox.getValue());
    selectedHerd.setFeedBasis(feedBasisComboBox.getValue());
    HerdDAO.updateHerd(selectedHerd);
  }

  private void handleDatabaseError(SQLException e) {
    AppLogger.error("Error updating herd", e);
    showAlert(Alert.AlertType.ERROR, "Error", "Failed to Update Herd. See logs for details.");
  }

  private void closeEditWindow() {
    ((Stage) herdNameTextField.getScene().getWindow()).close();
  }

  public void handleAddCattleToHerd() {
    try {
      FXMLLoader loader =
              new FXMLLoader(
                      getClass()
                              .getResource(
                                      "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/addCattle.fxml"));
      Parent root = loader.load();
      AddNewCattleController addCattleController = loader.getController();
      addCattleController.initData(selectedHerd.getId(), this);

      // Set the title for the stage
      Stage stage = new Stage();
      stage.setTitle("Add Cattle to Herd");
      stage.setScene(new Scene(root));
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
    } catch (IOException e) {
      // Use AppLogger for error handling
      AppLogger.error("Error loading Add Cattle form", e);
      // Update error message in showAlert
      showAlert(
              Alert.AlertType.ERROR, "Error", "Failed to load Add Cattle form. See logs for details.");
    }
  }

  public void handleDeleteCattleFromHerd() {
    // Get the selected cattle from the table
    Cattle selectedCattle = cattleTableView.getSelectionModel().getSelectedItem();
    if (selectedCattle == null) {
      showAlert(Alert.AlertType.WARNING, "Warning", "Please select a cattle to delete.");
      return;
    }

    // Show confirmation dialog
    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmationAlert.setTitle("Confirmation");
    confirmationAlert.setHeaderText(null);
    confirmationAlert.setContentText("Are you sure you want to delete the selected cattle?");

    confirmationAlert
            .showAndWait()
            .ifPresent(
                    response -> {
                      if (response == ButtonType.OK) {
                        try {
                          // Delete the selected cattle from the database
                          CattleDAO.deleteCattleById(selectedCattle.getCattleId());

                          // Remove the selected cattle from the table view
                          cattleTableView.getItems().remove(selectedCattle);

                          showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle deleted successfully.");
                        } catch (SQLException e) {
                          // Use AppLogger for error handling
                          AppLogger.error("Error deleting cattle", e);
                          // Update error message in showAlert
                          showAlert(
                                  Alert.AlertType.ERROR,
                                  "Error",
                                  "Failed to delete cattle. See logs for details.");
                        }
                      }
                    });
  }

  public void handleRefreshCattleFromHerd() {
    try {
      List<Cattle> updatedCattleList = CattleDAO.getCattleForHerd(selectedHerd.getId());
      ObservableList<Cattle> cattleList = FXCollections.observableArrayList(updatedCattleList);
      cattleTableView.setItems(cattleList);
      showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle list refreshed successfully.");
    } catch (SQLException e) {
      // Use AppLogger for error handling
      AppLogger.error("Error refreshing cattle list", e);
      // Update error message in showAlert
      showAlert(
              Alert.AlertType.ERROR, "Error", "Failed to refresh cattle list. See logs for details.");
    }
  }

  private void showAlert(Alert.AlertType type, String title, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
