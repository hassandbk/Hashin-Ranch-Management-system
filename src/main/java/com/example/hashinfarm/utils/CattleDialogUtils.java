package com.example.hashinfarm.utils;

import com.example.hashinfarm.businessLogic.services.SelectedCattleManager;
import com.example.hashinfarm.data.DTOs.Cattle;
import com.example.hashinfarm.data.DTOs.records.CattleUIInfo;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CattleDialogUtils {
    public static <T> T showSelectionDialog(String title, String headerText, List<T> items, Function<T, String> displayMapper) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);
        ListView<T> listView = new ListView<>();
        listView.getItems().addAll(items);
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || displayMapper.apply(item) == null) {
                    setText(null);
                } else {
                    setText(displayMapper.apply(item));
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                dialog.setResult(listView.getSelectionModel().getSelectedItem());
                dialog.close();
            }
        });

        dialog.getDialogPane().setContent(listView);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });
        Optional<T> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static void handleCattleTagID(Button buttonClicked, List<Cattle> cattleList, Button damTagButton, Button sireTagButton, Label damNameLabel, Label sireNameLabel) {
        Cattle selectedCattle = showSelectionDialog("Select Cattle", "Select a cattle from the list below", cattleList, Cattle::getTagId);
        if (selectedCattle != null) {
            if (buttonClicked == damTagButton) {
                damTagButton.setText(selectedCattle.getTagId());
                damNameLabel.setText(selectedCattle.getName());
            } else if (buttonClicked == sireTagButton) {
                sireTagButton.setText(selectedCattle.getTagId());
                sireNameLabel.setText(selectedCattle.getName());
            }
        }
    }

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static Cattle createCattle(
            String tagId,
            int herdId,
            String name,
            String gender,
            String colorMarkings,
            LocalDate dateOfBirth,
            int weightId,
            String bcs,
            int breedId,
            int sireId,
            int damId,
            int damsHerd,
            int siresHerd,
            int cattleId) {

        // Get instance of SelectedCattleManager
        SelectedCattleManager cattleManager = SelectedCattleManager.getInstance();

        // Retrieve names and herd names from the selected cattle
        String breedName = breedId != 0 ? cattleManager.getSelectedCattle().getBreedName() : "";
        String sireName = sireId != 0 ? cattleManager.getSelectedCattle().getSireName() : "";
        String damName = damId != 0 ? cattleManager.getSelectedCattle().getDamName() : "";
        String damHerdName = damId != 0 ? cattleManager.getSelectedCattle().getDamHerdName() : "";
        String sireHerdName = sireId != 0 ? cattleManager.getSelectedCattle().getSireHerdName() : "";
        String sireBreedName = sireId != 0 ? cattleManager.getSelectedCattle().getSireBreedName() : "";
        String damBreedName = damId != 0 ? cattleManager.getSelectedCattle().getDamBreedName() : "";

        return new Cattle(
                cattleId,
                tagId,
                herdId,
                colorMarkings,
                name,
                gender,
                dateOfBirth,
                weightId,
                bcs,
                breedId,
                breedName,
                sireId,
                sireName,
                damId,
                damName,
                damsHerd,
                siresHerd,
                damHerdName,
                sireHerdName,
                sireBreedName,
                damBreedName
        );
    }

    public static CattleUIInfo gatherCommonUIInfo(
            TextField tagIDTextField,
            TextField nameTextField,
            ComboBox<String> genderComboBox,
            TextField colorMarkingTextField,
            DatePicker dateOfBirthDatePicker,
            TextField weightTextField,
            Label BCSSliderLabel) {

        String tagId = tagIDTextField.getText().trim(); // Trim whitespace
        String name = nameTextField.getText().trim(); // Trim whitespace
        String gender = genderComboBox.getValue();
        String colorMarkings = colorMarkingTextField.getText().trim(); // Trim whitespace
        LocalDate dateOfBirth = dateOfBirthDatePicker.getValue();
        int weightId = Integer.parseInt(weightTextField.getText().trim()); // Trim whitespace
        String bcs = BCSSliderLabel.getText().trim(); // Trim whitespace

        return new CattleUIInfo(tagId, name, gender, colorMarkings, dateOfBirth, weightId, bcs);
    }
}
