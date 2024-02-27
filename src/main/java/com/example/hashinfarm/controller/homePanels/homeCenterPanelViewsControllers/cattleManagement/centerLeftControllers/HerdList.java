package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers;

import com.example.hashinfarm.controller.dao.CattleDAO;
import com.example.hashinfarm.controller.dao.HerdDAO;
import com.example.hashinfarm.controller.utility.SelectedCowManager;
import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.model.Herd;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HerdList {

    @FXML
    private TableView<Herd> tableView;

    @FXML
    private Label selectedAnimal;

    private final ToggleGroup cowToggleGroup = new ToggleGroup();
    private ObservableList<Herd> herds = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupData();
        setupAnimalSelectionListener();

        // Add a listener to update the name dynamically
        SelectedCowManager.getInstance().selectedNameProperty().addListener((observable, oldValue, newValue) -> {
            selectedAnimal.setText(newValue); // Update cattle name
        });


    }

    private void setupTableColumns() {
        TableColumn<Herd, Integer> idCol = new TableColumn<>("Herd ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Herd, String> nameCol = new TableColumn<>("Herd Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Herd, Integer> totalAnimalsCol = new TableColumn<>("Total Animals");
        totalAnimalsCol.setCellValueFactory(new PropertyValueFactory<>("totalAnimals"));

        TableColumn<Herd, String> animalsClassCol = new TableColumn<>("Animals Class");
        animalsClassCol.setCellValueFactory(new PropertyValueFactory<>("animalClass"));

        TableColumn<Herd, String> breedTypeCol = new TableColumn<>("Breed Type");
        breedTypeCol.setCellValueFactory(new PropertyValueFactory<>("breedType"));

        TableColumn<Herd, String> ageClassCol = new TableColumn<>("Age Class");
        ageClassCol.setCellValueFactory(new PropertyValueFactory<>("ageClass"));


        TableColumn<Herd, String> breedSystemCol = new TableColumn<>("Breed System");
        breedSystemCol.setCellValueFactory(new PropertyValueFactory<>("breedSystem"));


        TableColumn<Herd, String> solutionTypeCol = new TableColumn<>("Solution Type");
        solutionTypeCol.setCellValueFactory(new PropertyValueFactory<>("solutionType"));

        TableColumn<Herd, String> feedBasisCol = new TableColumn<>("Feed Basis");
        feedBasisCol.setCellValueFactory(new PropertyValueFactory<>("feedBasis"));

        TableColumn<Herd, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Herd, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionCol.setCellFactory(getActionCellFactory());
        double normalWidth = 100; // Example width for other columns
        double solutionTypeWidth = normalWidth * 2; // Double the width
        double actionWidth = normalWidth * 2; // Double the width
        solutionTypeCol.setPrefWidth(solutionTypeWidth);
        actionCol.setPrefWidth(actionWidth);

        TableColumn<Herd, String> animalsCol = new TableColumn<>("Animals");
        animalsCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>("Click to Expand"));
        animalsCol.setCellFactory(getAnimalsCellFactory());

        tableView.getColumns().addAll(idCol, nameCol, totalAnimalsCol, animalsCol, animalsClassCol, breedTypeCol,
                ageClassCol, breedSystemCol, solutionTypeCol, feedBasisCol, locationCol, actionCol);
        tableView.setItems(herds);
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

            private final Button deleteButton = new Button("Delete");


            private final HBox buttonsBox = new HBox(viewButton,deleteButton);

            {
                viewButton.setOnAction(event -> {
                    Herd herd = getTableView().getItems().get(getIndex());
                    showViewStage(herd.getId());
                });


                deleteButton.setOnAction(event -> {
                    Herd herd = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(herd.getId());
                });


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
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
    }
    private void setupAnimalSelectionListener() {
        for (Herd herd : herds) {
            for (Cattle cattle : herd.getAnimals()) {
                cattle.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        SelectedCowManager.getInstance().setSelectedCow(cattle);
                    }
                });
            }
        }
    }


    private void openAnimalListView(Herd herd) {
        ListView<Cattle> animalListView = new ListView<>(herd.getAnimals());
        animalListView.setCellFactory(animalCellFactory);
        Stage animalStage = new Stage();
        animalStage.initModality(Modality.APPLICATION_MODAL);
        animalStage.setTitle("Animals in " + herd.getName());
        animalStage.setScene(new Scene(new VBox(animalListView), 200, 300));
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
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
