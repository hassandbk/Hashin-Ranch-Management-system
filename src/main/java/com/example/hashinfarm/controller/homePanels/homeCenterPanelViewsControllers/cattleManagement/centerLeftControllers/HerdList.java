package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers;

import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.controller.dao.HerdDAO;
import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.model.Herd;
import com.example.hashinfarm.controller.utility.SelectedCowManager;
import com.example.hashinfarm.controller.utility.SelectedHerdManager;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import static org.controlsfx.control.textfield.TextFields.bindAutoCompletion;

public class HerdList {

    @FXML
    private TextField searchField;
    @FXML
    private CheckBox ascendingOrderCheckBox;

    @FXML
    private ComboBox<String> bulkActionsComboBox, sortingComboBox; // Assuming columnNames is a list of Strings

    @FXML
    private TableView<Herd> tableView;
    @FXML
    private Label selectedAnimal;

    private final ToggleGroup cowToggleGroup = new ToggleGroup();
    private final ObservableList<Herd> herds = FXCollections.observableArrayList();
    private final ObservableList<Herd> filteredHerds = FXCollections.observableArrayList();

    private Timer timer;
    private final List<String> columnNames = Arrays.asList("Herd ID", "Herd Name", "Total Animals", "Animals Class", "Breed Type", "Age Class", "Breed System", "Solution Type", "Feed Basis", "Location");

    @FXML
    private void initialize() {
        setupTableColumns();
        setupData();
        setupAnimalSelectionListener();
        setupSearchField();
        setupSortingComboBox();
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
        try {
            List<String> uniqueNames = HerdDAO.getUniqueNamesFromHerd();
            String[] possibleSuggestions = uniqueNames.toArray(new String[0]);
            bindAutoCompletion(searchField, possibleSuggestions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupSortingComboBox() {
        sortingComboBox.setItems(FXCollections.observableArrayList(columnNames));
        sortingComboBox.setOnAction(event -> sortTable());
        ascendingOrderCheckBox.setOnAction(event -> sortTable());
    }

    private void sortTable() {
        String selectedColumn = sortingComboBox.getSelectionModel().getSelectedItem();
        boolean ascending = ascendingOrderCheckBox.isSelected();
        TableColumn<Herd, ?> column = tableView.getColumns().stream()
                .filter(col -> col.getText().equals(selectedColumn))
                .findFirst().orElse(null);
        if (column != null) {
            column.setSortType(ascending ? TableColumn.SortType.ASCENDING : TableColumn.SortType.DESCENDING);
            tableView.getSortOrder().clear();
            tableView.getSortOrder().add(column);
        }
    }

    private void setupTableColumns() {
        addColumn("Herd ID", "id");
        addColumn("Herd Name", "name");
        addColumn("Total Animals", "totalAnimals");
        addColumn("Animals Class", "animalClass");
        addColumn("Breed Type", "breedType");
        addColumn("Age Class", "ageClass");
        addColumn("Breed System", "breedSystem");
        addColumn(); // Specify width for Solution Type column
        addColumn("Feed Basis", "feedBasis");
        addColumn("Location", "location");
        addActionColumn(); // Specify width for Action column
        addAnimalsColumn(); // Specify width for Animals column
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
        column.setPrefWidth(130);
        tableView.getColumns().add(column);
    }

    private Callback<TableColumn<Herd, String>, TableCell<Herd, String>> getAnimalsCellFactory() {
        return param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: blue;");
                    setOnMouseClicked(event -> {
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
        return param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsBox = new HBox(viewButton, editButton, deleteButton);

            {
                viewButton.setOnAction(event -> showViewStage(param.getTableView().getItems().get(getIndex()).getId()));
                editButton.setOnAction(event -> showEditStage(param.getTableView().getItems().get(getIndex()).getId()));
                deleteButton.setOnAction(event -> showDeleteConfirmation(param.getTableView().getItems().get(getIndex()).getId()));
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
                ObservableList<Cattle> cattleObservableList = FXCollections.observableArrayList(cattleForHerd);
                herd.setAnimals(cattleObservableList);
            }
            herds.addAll(herdsFromDB);
            filteredHerds.addAll(herdsFromDB);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupAnimalSelectionListener() {
        for (Herd herd : herds) {
            for (Cattle cattle : herd.getAnimals()) {
                cattle.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        SelectedCowManager.getInstance().setSelectedCow(cattle);
                        SelectedHerdManager.getInstance().setSelectedHerd(herd);
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
            boolean tokenMatch = herd.getName().toLowerCase().contains(token)
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

        // Create a transparent overlay to prevent clicking elsewhere
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        // Combine ListView and overlay in a VBox
        VBox vBox = new VBox(animalListView);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(overlay);

        // Create the stage
        Stage animalStage = new Stage();
        animalStage.initModality(Modality.APPLICATION_MODAL);
        animalStage.setTitle("Animals in " + herd.getName());
        animalStage.setScene(new Scene(vBox, 400, 300));

        // Set the stage to not resizable
        animalStage.setResizable(false);

        // Set the stage to not maximizable
        animalStage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (isNowMaximized) {
                Platform.runLater(() -> animalStage.setMaximized(false));
            }
        });

        // Show the stage and wait for it to be closed
        animalStage.showAndWait();
    }

    private void showViewStage(int herdId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/ViewSelectedHerd.fxml"));
            Parent root = loader.load();
            ViewSelectedHerdController controller = loader.getController();

            // Retrieve the Herd object from the list
            Herd selectedHerd = herds.stream().filter(herd -> herd.getId() == herdId).findFirst().orElse(null);
            if (selectedHerd != null) {
                controller.initData(selectedHerd);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("View Herd");
                stage.setScene(new Scene(root));
                stage.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEditStage(int herdId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/EditHerd.fxml"));
            Parent root = loader.load();
            EditHerdController controller = loader.getController();

            // Retrieve the Herd object from the list
            Herd selectedHerd = herds.stream().filter(herd -> herd.getId() == herdId).findFirst().orElse(null);
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
            e.printStackTrace();
        }
    }



    private void showDeleteConfirmation(int herdId) {
        // Implement logic to show delete confirmation
    }


    private final Callback<ListView<Cattle>, ListCell<Cattle>> animalCellFactory = new Callback<>() {
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
                        radioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
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
    private void unselectCattleInOtherHerds(Cattle selectedCattle) {
        herds.forEach(herd -> {
            if (herd != null) {
                herd.getAnimals().forEach(cattle -> {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/AddNewHerd.fxml"));
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
            e.printStackTrace();
        }
    }


    public void refreshTable() {
        herds.clear(); // Clear the existing data
        try {
            List<Herd> herdsFromDB = HerdDAO.getAllHerds();
            for (Herd herd : herdsFromDB) {
                List<Cattle> cattleForHerd = CattleDAO.getCattleForHerd(herd.getId());
                ObservableList<Cattle> cattleObservableList = FXCollections.observableArrayList(cattleForHerd);
                herd.setAnimals(cattleObservableList);
            }
            herds.addAll(herdsFromDB); // Add the updated data
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
    }
    public void refreshHerdTable() {
        herds.clear(); // Clear the existing data
        try {
            List<Herd> herdsFromDB = HerdDAO.getAllHerds();
            for (Herd herd : herdsFromDB) {
                List<Cattle> cattleForHerd = CattleDAO.getCattleForHerd(herd.getId());
                ObservableList<Cattle> cattleObservableList = FXCollections.observableArrayList(cattleForHerd);
                herd.setAnimals(cattleObservableList);
            }
            herds.addAll(herdsFromDB); // Add the updated data
            showAlert(Alert.AlertType.INFORMATION, "Success", "Herd table refreshed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to refresh herd table: " + e.getMessage());
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
