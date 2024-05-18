package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers;

import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.controller.dao.HerdDAO;
import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.controller.utility.SelectedCattleManager;
import com.example.hashinfarm.controller.utility.SelectedHerdManager;
import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.model.Herd;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;


public class HerdList {

  private final ToggleGroup cowToggleGroup = new ToggleGroup();
  private final ObservableList<Herd> herds = FXCollections.observableArrayList();
  private final ObservableList<Herd> filteredHerds = FXCollections.observableArrayList();
  private final List<String> columnNames =
      Arrays.asList(
          "Herd ID",
          "Herd Name",
          "Total Animals",
          "Animals Class",
          "Breed Type",
          "Age Class",
          "Breed System",
          "Solution Type",
          "Feed Basis",
          "Location");
  private final Callback<ListView<Cattle>, ListCell<Cattle>> animalCellFactory =
      new Callback<>() {
        @Override
        public ListCell<Cattle> call(ListView<Cattle> param) {
          return new ListCell<>() {
            @Override
            protected void updateItem(Cattle item, boolean empty) {
              super.updateItem(item, empty);
              if (empty || item == null) {
                setText(null);
                setGraphic(null);
              } else {
                RadioButton radioButton = new RadioButton(item.getName());
                radioButton.setToggleGroup(cowToggleGroup);
                radioButton
                    .selectedProperty()
                    .addListener(
                        (observable, oldValue, newValue) -> {
                          if (newValue) {
                            unselectCattleInOtherHerds(item);
                          }
                          item.setSelected(newValue);
                        });
                radioButton.setSelected(item.isSelected());
                setGraphic(radioButton);
              }
            }
          };
        }
      };
  @FXML private TextField searchField;
  @FXML private CheckBox ascendingOrderCheckBox;
  @FXML
  private ComboBox<String> bulkActionsComboBox,
      sortingComboBox; // Assuming columnNames is a list of Strings
  @FXML private TableView<Herd> tableView;
  @FXML private Label selectedAnimal;
  private Timer timer;

  @FXML
  private void initialize() {
    setupTableColumns();
    setupData();
    setupAnimalSelectionListener();
    setupSearchField();
    setupSortingComboBox();
  }

//  private void setupSearchField() {
//    searchField
//        .textProperty()
//        .addListener(
//            (observable, oldValue, newValue) -> {
//              if (timer != null) {
//                timer.cancel();
//              }
//              timer = new Timer();
//              timer.schedule(
//                  new TimerTask() {
//                    @Override
//                    public void run() {
//                      Platform.runLater(() -> filterTable(newValue));
//                    }
//                  },
//                  500);
//            });
//
//    try {
//      List<String> uniqueNames = HerdDAO.getUniqueNamesFromHerd();
//      String[] possibleSuggestions = uniqueNames.toArray(new String[0]);
//      bindAutoCompletion(searchField, possibleSuggestions);
//    } catch (SQLException e) {
//      AppLogger.error("Failed to load unique names for autocompletion", e);
//    }
//  }

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

    try {
      List<String> uniqueNames = HerdDAO.getUniqueNamesFromHerd();
      String[] possibleSuggestions = uniqueNames.toArray(new String[0]);
      setupAutoCompletion(searchField, possibleSuggestions);
    } catch (SQLException e) {
      AppLogger.error("Failed to load unique names for autocompletion", e);
    }
  }

  private void setupAutoCompletion(TextField textField, String[] possibleSuggestions) {
    textField.setOnKeyReleased(event -> {
      if (!textField.getText().isEmpty()) {
        String input = textField.getText().toLowerCase();
        List<String> filteredSuggestions = Arrays.stream(possibleSuggestions)
                .filter(suggestion -> suggestion.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
        if (!filteredSuggestions.isEmpty()) {
          showSuggestions(textField, filteredSuggestions);
        }
      }
    });
  }

  private void showSuggestions(TextField textField, List<String> suggestions) {
    ListView<String> suggestionList = new ListView<>();
    suggestionList.getItems().addAll(suggestions);
    suggestionList.setOnMouseClicked(event -> {
      String selectedSuggestion = suggestionList.getSelectionModel().getSelectedItem();
      textField.setText(selectedSuggestion);
      suggestionList.getItems().clear(); // Clear suggestion list after selection
    });
    suggestionList.setLayoutX(textField.getLayoutX());
    suggestionList.setLayoutY(textField.getLayoutY() + textField.getHeight());
    suggestionList.setPrefWidth(textField.getWidth());
    suggestionList.setMaxHeight(150);
    textField.getParent().getChildrenUnmodifiable().add(suggestionList);
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

  private void setupTableColumns() {
    addColumn("Herd ID", "id");
    addColumn("Herd Name", "name");
    addAnimalsColumn();
    addColumn("Total Animals", "totalAnimals");
    addColumn("Animals Class", "animalClass");
    addColumn("Breed Type", "breedType");
    addColumn("Age Class", "ageClass");
    addColumn("Breed System", "breedSystem");
    addColumn(); // Specify width for Solution Type column
    addColumn("Feed Basis", "feedBasis");
    addColumn("Location", "location");
    addActionColumn(); // Specify width for Action column
    // Specify width for Animals column
    tableView.setItems(filteredHerds);
  }

  private <T> void addColumn(String title, String propertyName) {
    TableColumn<Herd, T> column = new TableColumn<>(title);
    column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
    tableView.getColumns().add(column);
  }

  private void addColumn() {
    addColumn("Solution Type", "solutionType");
    tableView.getColumns().getLast().setPrefWidth(200);
  }

  private void addActionColumn() {
    TableColumn<Herd, String> column = new TableColumn<>("Action");
    column.setCellValueFactory(new PropertyValueFactory<>("action"));
    column.setCellFactory(getActionCellFactory());
    column.setPrefWidth(130);
    tableView.getColumns().add(column);
  }

  private void addAnimalsColumn() {
    TableColumn<Herd, String> column = new TableColumn<>("Animals");
    column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>("Click to Expand"));
    column.setCellFactory(getAnimalsCellFactory());
    column.setPrefWidth(100);
    tableView.getColumns().add(column);
  }

  private Callback<TableColumn<Herd, String>, TableCell<Herd, String>> getAnimalsCellFactory() {
    return param ->
        new TableCell<>() {
          @Override
          protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
              setText(null);
            } else {
              setText(item);
              setStyle("-fx-text-fill: blue;");
              setOnMouseClicked(
                  event -> {
                    Herd herd = getTableView().getItems().get(getIndex());
                    if (!herd.getAnimals().isEmpty()) {
                      openAnimalListView(herd);
                    } else {
                      System.out.println("No animals in this herd.");
                    }
                  });
            }
          }
        };
  }

  private Callback<TableColumn<Herd, String>, TableCell<Herd, String>> getActionCellFactory() {
    return param ->
        new TableCell<>() {
          private final Button viewButton = new Button("View");
          private final Button editButton = new Button("Edit");
          private final Button deleteButton = new Button("Delete");
          private final HBox buttonsBox = new HBox(viewButton, editButton, deleteButton);

          {
            viewButton.setOnAction(
                event -> showViewStage(param.getTableView().getItems().get(getIndex()).getId()));
            editButton.setOnAction(
                event -> showEditStage(param.getTableView().getItems().get(getIndex()).getId()));
            deleteButton.setOnAction(
                event ->
                    showDeleteConfirmation(
                        param.getTableView().getItems().get(getIndex()).getId()));
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
      List<Herd> herdsFromDB = HerdDAO.getAllHerds();

      for (Herd herd : herdsFromDB) {
        List<Cattle> cattleForHerd = CattleDAO.getCattleForHerd(herd.getId());
        ObservableList<Cattle> cattleObservableList =
            FXCollections.observableArrayList(cattleForHerd);
        herd.setAnimals(cattleObservableList);
      }

      herds.addAll(herdsFromDB);
      filteredHerds.addAll(herdsFromDB);
    } catch (SQLException e) {
      AppLogger.error("Failed to load herd data", e);
      // Handle data loading error (optional)
      handleError(); // You can define a method to handle the error
    }
  }

  private void handleError() {
    // Display an error message to the user
    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load herd data. Please try again later.");
  }

  private void setupAnimalSelectionListener() {
    for (Herd herd : herds) {
      for (Cattle cattle : herd.getAnimals()) {
        cattle
            .selectedProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  if (newValue) {
                    SelectedCattleManager.getInstance().setSelectedCattle(cattle);
                    SelectedHerdManager.getInstance().setSelectedHerd(herd);
                    // Update the selectedAnimal label with the name of the selected cattle
                    selectedAnimal.setText(cattle.getName());
                  }
                });
      }
    }
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
          herd.getName().toLowerCase().contains(token)
              || String.valueOf(herd.getId()).contains(token)
              || herd.getAnimalClass().toLowerCase().contains(token)
              || herd.getBreedType().toLowerCase().contains(token)
              || herd.getAgeClass().toLowerCase().contains(token)
              || herd.getBreedSystem().toLowerCase().contains(token)
              || herd.getSolutionType().toLowerCase().contains(token)
              || herd.getFeedBasis().toLowerCase().contains(token)
              || herd.getLocation().toLowerCase().contains(token);
      if (!tokenMatch) {
        return false;
      }
    }
    return true;
  }

  private void openAnimalListView(Herd herd) {
    ListView<Cattle> animalListView = new ListView<>(herd.getAnimals());
    animalListView.setCellFactory(animalCellFactory);

    // Set preferred width to 400
    animalListView.setPrefWidth(400);

    // Make the ListView scrollable
    animalListView.setPrefHeight(Region.USE_COMPUTED_SIZE);

    // Create the stage
    Stage animalStage = new Stage();
    animalStage.initModality(Modality.APPLICATION_MODAL);
    animalStage.setTitle("Animals in " + herd.getName());
    animalStage.setScene(new Scene(animalListView, 400, 300));

    // Enable double-click selection, checking, and auto-close
    animalListView.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            Cattle selectedCattle = animalListView.getSelectionModel().getSelectedItem();
            if (selectedCattle != null) {
              // Check the item
              selectedCattle.setSelected(true);
              // Select the item
              SelectedCattleManager.getInstance().setSelectedCattle(selectedCattle);
              SelectedHerdManager.getInstance().setSelectedHerd(herd);
              // Close the ListView
              animalStage.close();
            }
          }
        });

    // Close the ListView when Escape key is pressed
    animalListView.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ESCAPE) {
            animalStage.close();
          }
        });

    // Show the stage
    animalStage.show();
  }

  private void showViewStage(int herdId) {
    try {
      FXMLLoader loader =
          new FXMLLoader(
              getClass()
                  .getResource(
                      "/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/ViewSelectedHerd.fxml"));
      Parent root = loader.load();
      ViewSelectedHerdController controller = loader.getController();

      // Retrieve the Herd object from the list
      Herd selectedHerd =
          herds.stream().filter(herd -> herd.getId() == herdId).findFirst().orElse(null);
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
      // Handle FXML loading error (optional)
      handleShowViewError(); // You can define a method to handle the error
    }
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
          herds.stream().filter(herd -> herd.getId() == herdId).findFirst().orElse(null);
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
          herds.stream().filter(herd -> herd.getId() == herdId).findFirst().orElse(null);
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

  private void unselectCattleInOtherHerds(Cattle selectedCattle) {
    herds.forEach(
        herd -> {
          if (herd != null) {
            herd.getAnimals()
                .forEach(
                    cattle -> {
                      if (!cattle.equals(selectedCattle)) {
                        cattle.setSelected(false);
                      }
                    });
          }
        });
  }

  @FXML
  private void addNewHerd(ActionEvent actionEvent) {
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

  public void refreshTable() {
    herds.clear(); // Clear the existing data
    try {
      List<Herd> herdsFromDB = HerdDAO.getAllHerds();
      for (Herd herd : herdsFromDB) {
        List<Cattle> cattleForHerd = CattleDAO.getCattleForHerd(herd.getId());
        ObservableList<Cattle> cattleObservableList =
            FXCollections.observableArrayList(cattleForHerd);
        herd.setAnimals(cattleObservableList);
      }
      herds.addAll(herdsFromDB); // Add the updated data
    } catch (SQLException e) {
      // Use AppLogger for error handling
      AppLogger.error("Error refreshing herd table", e);
      // Update error message in showAlert
      showAlert(
          Alert.AlertType.ERROR, "Error", "Failed to refresh herd table. See logs for details.");
    }
  }

  public void refreshHerdTable() {
    herds.clear(); // Clear the existing data
    try {
      List<Herd> herdsFromDB = HerdDAO.getAllHerds();
      for (Herd herd : herdsFromDB) {
        List<Cattle> cattleForHerd = CattleDAO.getCattleForHerd(herd.getId());
        ObservableList<Cattle> cattleObservableList =
            FXCollections.observableArrayList(cattleForHerd);
        herd.setAnimals(cattleObservableList);
      }
      herds.addAll(herdsFromDB); // Add the updated data
      showAlert(Alert.AlertType.INFORMATION, "Success", "Herd table refreshed successfully.");
    } catch (SQLException e) {
      // Use AppLogger for error handling
      AppLogger.error("Error refreshing herd table", e);
      // Update error message in showAlert
      showAlert(
          Alert.AlertType.ERROR, "Error", "Failed to refresh herd table. See logs for details.");
    }
  }

  private void showAlert(Alert.AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
