package com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerLeftControllers;

import com.example.hashinfarm.data.DAOs.HerdDAO;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.businessLogic.services.SelectedCattleManager;
import com.example.hashinfarm.businessLogic.services.SelectedHerdManager;
import com.example.hashinfarm.data.DTOs.Cattle;
import com.example.hashinfarm.data.DTOs.records.Herd;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HerdList {

  private final ObservableList<Herd> herds = FXCollections.observableArrayList();
  private final ObservableList<Herd> filteredHerds = FXCollections.observableArrayList();
  private final List<String> columnNames = Arrays.asList("Herd ID", "Herd Name", "Total Animals", "Animals Class", "Breed Type", "Age Class", "Breed System", "Solution Type", "Feed Basis", "Location");
    private Timer timer;
  @FXML
  private TextField searchField;
  @FXML
  private CheckBox ascendingOrderCheckBox;
  @FXML
  private ComboBox<String> sortingComboBox;
  @FXML
  private TableView<Herd> tableView;
  @FXML
  private Label selectedAnimal;

  public void initialize() {
    setupTableColumns();
    setupData();
    setupAnimalSelectionListener();
    setupSearchField();
    setupSortingComboBox();
  }

  private void setupTableColumns() {
    addColumn("Herd ID", Herd::id);
    addColumn("Herd Name", Herd::name);
    addAnimalsColumn();
    addColumn("Total Animals", Herd::totalAnimals);
    addColumn("Animals Class", Herd::animalClass);
    addColumn("Breed Type", Herd::breedType);
    addColumn("Age Class", Herd::ageClass);
    addColumn("Breed System", Herd::breedSystem);
    addColumn("Solution Type", Herd::solutionType);
    addColumn("Feed Basis", Herd::feedBasis);
    addColumn("Location", Herd::location);
    addActionColumn();
    tableView.setItems(filteredHerds);
  }


  private <T> void addColumn(String title, Function<Herd, T> propertyAccessor) {
    TableColumn<Herd, T> column = new TableColumn<>(title);
    column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(propertyAccessor.apply(cellData.getValue())));
    tableView.getColumns().add(column);
  }


  private void addActionColumn() {
    TableColumn<Herd, String> column = new TableColumn<>("Action");
    column.setCellFactory(getActionCellFactory());  // Use the custom cell factory directly
    column.setPrefWidth(130);
    tableView.getColumns().add(column);
  }


  private void addAnimalsColumn() {
    TableColumn<Herd, String> column = new TableColumn<>("Animals");
    column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>());
    column.setCellFactory(getAnimalsCellFactory());
    column.setPrefWidth(100);
    tableView.getColumns().add(column);
  }


  private Callback<TableColumn<Herd, String>, TableCell<Herd, String>> getAnimalsCellFactory() {
    return param -> new TableCell<>() {
      private final ComboBox<String> animalDropdown = new ComboBox<>();
      private static ComboBox<String> currentlySelectedComboBox = null;

      {
        animalDropdown.setStyle("-fx-font-size: 10px;");
        animalDropdown.setVisibleRowCount(5);
        animalDropdown.setCellFactory(lv -> new ListCell<>() {
          @Override
          protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
            } else {
              setText(item);
              setFont(Font.font("System", 10));
            }
          }
        });
      }

      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setText(null);
          setGraphic(null);
        } else {
          Herd herd = getTableView().getItems().get(getIndex());
          List<Cattle> animals = herd.animals();

          // Create a map for quick lookup of Cattle by their names
          Map<String, Cattle> animalMap = animals.stream()
                  .collect(Collectors.toMap(Cattle::getName, cattle -> cattle));

          List<String> animalNames = new ArrayList<>(animalMap.keySet());
          animalDropdown.setItems(FXCollections.observableArrayList(animalNames));

          animalDropdown.setOnAction(event -> {
            String selectedAnimalName = animalDropdown.getValue();
            Cattle selectedCattle = animalMap.get(selectedAnimalName);

            if (selectedCattle != null) {
              if (currentlySelectedComboBox != null && currentlySelectedComboBox != animalDropdown) {
                currentlySelectedComboBox.getSelectionModel().clearSelection();
              }
              currentlySelectedComboBox = animalDropdown;

              selectedCattle.setSelected(true);
              SelectedCattleManager.getInstance().setSelectedCattle(selectedCattle);
              SelectedHerdManager.getInstance().setSelectedHerd(herd);
            }
            animalDropdown.hide();
          });

          setGraphic(animalDropdown);
        }
      }
    };
  }




  private Callback<TableColumn<Herd, String>, TableCell<Herd, String>> getActionCellFactory() {
    return param -> new TableCell<>() {
      private final Button viewButton = new Button("View");
      private final Button editButton = new Button("Edit");
      private final Button deleteButton = new Button("Delete");
      private final HBox buttonsBox = new HBox(viewButton, editButton, deleteButton);

      {
        viewButton.setOnAction(event -> showViewStage(param.getTableView().getItems().get(getIndex()).id()));
        editButton.setOnAction(event -> showEditStage(param.getTableView().getItems().get(getIndex()).id()));
        deleteButton.setOnAction(event -> showDeleteConfirmation(param.getTableView().getItems().get(getIndex()).id()));
      }

      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          setGraphic(buttonsBox);
        }
      }
    };
  }


  private void setupData() {
    try {
      loadDataFromDatabase();
      filteredHerds.addAll(herds);
    } catch (SQLException e) {
      handleDataLoadException(e);
    }
  }

  private void setupAnimalSelectionListener() {
    for (Herd herd : herds) {
      for (Cattle cattle : herd.animals()) {
        cattle.selectedProperty().addListener((observable, oldValue, newValue) -> {
          if (newValue) {
            SelectedCattleManager.getInstance().setSelectedCattle(cattle);
            SelectedHerdManager.getInstance().setSelectedHerd(herd);
            selectedAnimal.setText(cattle.getName());
          }
        });
      }
    }
  }

  private void showViewStage(int herdId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/ViewSelectedHerd.fxml"));
      Parent root = loader.load();
      ViewSelectedHerdController controller = loader.getController();

      Herd selectedHerd = herds.stream().filter(herd -> herd.id() == herdId).findFirst().orElse(null);
      if (selectedHerd != null) {
        controller.initData(selectedHerd);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("View Herd");
        stage.setScene(new Scene(root));
        stage.showAndWait();
      }
    } catch (IOException e) {
      AppLogger.error("Failed to load ViewSelectedHerd.fxml", e);
      handleShowViewError();
    }
  }

  private void loadDataFromDatabase() throws SQLException {
    List<Herd> herdsFromDB = HerdDAO.getAllHerds(); // This now includes cattle lists within each Herd

    // Create a list to hold updated Herd records
    List<Herd> updatedHerds = new ArrayList<>();

    for (Herd herd : herdsFromDB) {
      // Calculate total animals based on the embedded cattle list
      int totalAnimals = herd.getAnimals().size();

      // Create a new Herd record with updated total animals count
      Herd updatedHerd = new Herd(
              herd.id(),             // Keep the existing ID
              herd.name(),           // Retain the existing name
              totalAnimals,          // Set the new total animals count
              herd.animalClass(),    // Retain the existing animal class
              herd.breedType(),      // Retain the existing breed type
              herd.ageClass(),       // Retain the existing age class
              herd.breedSystem(),    // Retain the existing breed system
              herd.solutionType(),   // Retain the existing solution type
              herd.feedBasis(),      // Retain the existing feed basis
              herd.location(),       // Retain the existing location
              herd.action(),         // Retain the existing action
              herd.getAnimals()      // Use the cattle list from the Herd object
      );

      // Add the updated Herd to the list
      updatedHerds.add(updatedHerd);
    }

    // Add all updated Herds to the main list
    herds.addAll(updatedHerds);
  }





  private void handleDataLoadException(SQLException e) {
    AppLogger.error("Failed to load herd data", e);
    // Handle data loading error (optional)
    handleError(); // You can define a method to handle the error
  }



  public void refreshTable() {
    herds.clear(); // Clear the existing data
    try {
      loadDataFromDatabase();
    } catch (SQLException e) {
      AppLogger.error("Error refreshing herd table", e);
      showAlert(
              Alert.AlertType.ERROR, "Error", "Failed to refresh herd table. See logs for details.");
    }
  }

  public void refreshHerdTable() {
    herds.clear(); // Clear the existing data
    try {
      loadDataFromDatabase();
      showAlert(Alert.AlertType.INFORMATION, "Success", "Herd table refreshed successfully.");
    } catch (SQLException e) {
      AppLogger.error("Error refreshing herd table", e);
      showAlert(
              Alert.AlertType.ERROR, "Error", "Failed to refresh herd table. See logs for details.");
    }
  }

  private void setupSearchField() {
    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (timer != null) {
        timer.cancel();
      }
      timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          Platform.runLater(() -> filterTable(newValue));
        }
      }, 500);
    });


  }







  private void setupSortingComboBox() {
    sortingComboBox.setItems(FXCollections.observableArrayList(columnNames));
    sortingComboBox.setOnAction(event -> sortTable());
    ascendingOrderCheckBox.setOnAction(event -> sortTable());
  }

  private void sortTable() {
    String selectedColumn = sortingComboBox.getSelectionModel().getSelectedItem();
    boolean ascending = ascendingOrderCheckBox.isSelected();
    TableColumn<Herd, ?> column =
            tableView.getColumns().stream()
                    .filter(col -> col.getText().equals(selectedColumn))
                    .findFirst()
                    .orElse(null);
    if (column != null) {
      column.setSortType(
              ascending ? TableColumn.SortType.ASCENDING : TableColumn.SortType.DESCENDING);
      tableView.getSortOrder().clear();
      tableView.getSortOrder().add(column);
    }
  }

  private void handleError() {
    // Display an error message to the user
    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load herd data. Please try again later.");
  }



  private void filterTable(String searchText) {
    filteredHerds.clear();
    if (searchText.isEmpty()) {
      filteredHerds.addAll(herds);
      return;
    }
    filteredHerds.addAll(herds.filtered(herd -> herdMatchesSearchCriteria(herd, searchText)));
  }
  private boolean herdMatchesSearchCriteria(Herd herd, String searchText) {
    String[] searchTokens = searchText.toLowerCase().split("\\s+");

    for (String token : searchTokens) {
      boolean tokenMatch =
              herd.name().toLowerCase().contains(token) ||
                      String.valueOf(herd.id()).contains(token) ||
                      herd.animalClass().toLowerCase().contains(token) ||
                      herd.breedType().toLowerCase().contains(token) ||
                      herd.ageClass().toLowerCase().contains(token) ||
                      herd.breedSystem().toLowerCase().contains(token) ||
                      herd.solutionType().toLowerCase().contains(token) ||
                      herd.feedBasis().toLowerCase().contains(token) ||
                      herd.location().toLowerCase().contains(token);

      // If any token does not match, return false
      if (!tokenMatch) {
        return false;
      }
    }

    return true; // All tokens matched
  }



  private void handleShowViewError() {
    // Display an error message to the user
    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load ViewSelectedHerd.fxml");
  }

  private void showEditStage(int herdId) {
    try {
      FXMLLoader loader =
          new FXMLLoader(
              getClass()
                  .getResource(
                      "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/EditHerd.fxml"));
      Parent root = loader.load();
      EditHerdController controller = loader.getController();

      // Retrieve the Herd object from the list
      Herd selectedHerd =
          herds.stream().filter(herd -> herd.id() == herdId).findFirst().orElse(null);
      if (selectedHerd != null) {
        controller.initData(selectedHerd);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Edit Herd");
        stage.setScene(new Scene(root));
        stage.showAndWait();

        // Update the table after editing
        refreshTable();
      }
    } catch (IOException e) {
      AppLogger.error("Failed to load EditHerd.fxml", e);
      handleEditHerdLoadError(); // You can define a method to handle the error
    }
  }

  private void handleEditHerdLoadError() {
    // Display an error message to the user
    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load EditHerd.fxml");
  }

  private void showDeleteConfirmation(int herdId) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Confirmation");
    alert.setHeaderText("Are you sure you want to delete this herd?");
    alert.setContentText("This action cannot be undone.");

    ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    alert.getButtonTypes().setAll(deleteButton, cancelButton);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == deleteButton) {
      deleteHerd(herdId);
    }
  }

  private void deleteHerd(int herdId) {
    try {
      // Delete the herd from the database using DAO
      HerdDAO.deleteHerd(herdId);

      // Remove the herd from the list and refresh the table
      Herd herdToRemove =
          herds.stream().filter(herd -> herd.id() == herdId).findFirst().orElse(null);
      if (herdToRemove != null) {
        herds.remove(herdToRemove);
        filteredHerds.remove(herdToRemove);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Herd deleted successfully.");
      }
    } catch (SQLException e) {
      AppLogger.error("Failed to delete herd with ID: " + herdId, e);
      // Handle deletion error and inform the user
      showAlert(
          Alert.AlertType.ERROR, "Error", "Failed to delete herd. Please check logs for details.");
    }
  }

  @FXML
  private void addNewHerd() {
    try {
      FXMLLoader loader =
          new FXMLLoader(
              getClass()
                  .getResource(
                      "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/AddNewHerd.fxml"));
      Parent root = loader.load();
      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setTitle("Add New Herd");
      stage.setScene(new Scene(root));

      // Set the userData to pass the controller instance
      HerdList herdListController = this; // Assuming this is the controller instance of HerdList
      stage.setUserData(herdListController);

      stage.showAndWait();
    } catch (IOException e) {
      // Use AppLogger instead of printStackTrace
      AppLogger.error("Error loading Add New Herd FXML", e);
    }
  }
  private void showAlert(Alert.AlertType alertType, String title, String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(alertType);
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }
}
