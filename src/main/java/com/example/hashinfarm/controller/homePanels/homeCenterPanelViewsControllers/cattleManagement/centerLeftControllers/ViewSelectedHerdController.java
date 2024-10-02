package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers;

import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.model.Herd;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class ViewSelectedHerdController {
    @FXML  private Label damHerdName, damName, sireName, sireHerdName, damID, sireID, herdIdLabel, nameLabel,
            totalAnimalsLabel, animalsClassLabel, breedTypeLabel, ageClassLabel,
            breedSystemLabel, solutionTypeLabel, feedBasisLabel, locationLabel;

    @FXML   private TableView<Cattle> cattleTableView;
    @FXML   private TableColumn<Cattle, Integer> cattleIdColumn, cattleAgeColumn, cattleWeightIdColumn, cattleBreedIdColumn;
    @FXML   private TableColumn<Cattle, String> cattleTagIdColumn, cattleColorMarkingsColumn, cattleNameColumn, cattleGenderColumn, cattleBcsColumn;
    @FXML   private TableColumn<Cattle, Date> cattleDateOfBirthColumn;

    public void initData(Herd herd) {
        setHerdLabels(herd);
        populateCattleTable(herd.getAnimals());
        setupCattleTableColumns();
        setupCattleSelectionListener();
    }

    private void setHerdLabels(Herd herd) {
        herdIdLabel.setText(String.valueOf(herd.getId()));
        nameLabel.setText(herd.getName());
        totalAnimalsLabel.setText(String.valueOf(herd.getTotalAnimals()));
        animalsClassLabel.setText(herd.getAnimalClass());
        breedTypeLabel.setText(herd.getBreedType());
        ageClassLabel.setText(herd.getAgeClass());
        breedSystemLabel.setText(herd.getBreedSystem());
        solutionTypeLabel.setText(herd.getSolutionType());
        feedBasisLabel.setText(herd.getFeedBasis());
        locationLabel.setText(herd.getLocation());
    }

    private void populateCattleTable(List<Cattle> cattle) {
        ObservableList<Cattle> cattleList = FXCollections.observableArrayList(cattle);
        cattleTableView.setItems(cattleList);
    }

    private void setupCattleTableColumns() {
        cattleIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCattleId()).asObject());
        cattleTagIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTagId()));
        cattleColorMarkingsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColorMarkings()));
        cattleNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        cattleGenderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));

        // Convert LocalDate to java.sql.Date
        cattleDateOfBirthColumn.setCellValueFactory(cellData -> {
            LocalDate localDate = cellData.getValue().getDateOfBirth();
            return new SimpleObjectProperty<>(Date.valueOf(localDate));
        });

        cattleAgeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAge()).asObject());
        cattleWeightIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getWeightId()).asObject());
        cattleBcsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBcs()));
        cattleBreedIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBreedId()).asObject());
    }

    private void setupCattleSelectionListener() {
        cattleTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updatePedigreeDetails(newValue);
            }
        });
    }

    private void updatePedigreeDetails(Cattle cattle) {
        damHerdName.setText(cattle.getDamHerdName());
        damName.setText(cattle.getDamName());
        sireName.setText(cattle.getSireName());
        sireHerdName.setText(cattle.getSireHerdName());
        damID.setText(String.valueOf(cattle.getDamId()));
        sireID.setText(String.valueOf(cattle.getSireId()));
    }

}
