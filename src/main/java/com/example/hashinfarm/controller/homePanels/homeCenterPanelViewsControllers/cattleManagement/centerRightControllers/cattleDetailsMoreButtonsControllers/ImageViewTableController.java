package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.cattleDetailsMoreButtonsControllers;

import com.example.hashinfarm.controller.dao.CattleImageDAO;
import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.controller.utility.CattleImageManager;
import com.example.hashinfarm.controller.utility.CattleImage;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class ImageViewTableController {

    private final CattleImageDAO cattleImageDAO = new CattleImageDAO();
    @FXML
    private TableView<CattleImage> imageViewTable;

    @NotNull
    private static TableColumn<CattleImage, Timestamp> getCattleImageTimestampTableColumn() {
        TableColumn<CattleImage, Timestamp> dateCreatedColumn = new TableColumn<>("Date Created");
        dateCreatedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCreatedAt()));
        dateCreatedColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        return dateCreatedColumn;
    }

    public void initialize(int selectedCattleId) {
        initializeTableView();

        Task<List<CattleImage>> task = new Task<>() {
            @Override
            protected List<CattleImage> call() {
                return cattleImageDAO.getCattleImagesByCattleId(selectedCattleId);
            }
        };

        task.setOnSucceeded(event -> {
            List<CattleImage> cattleImages = task.getValue();
            imageViewTable.getItems().addAll(cattleImages);
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            if (exception != null) {
                AppLogger.error("Failed to get cattle images", exception);
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void initializeTableView() {
        TableColumn<CattleImage, Image> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(getImageForTableCell(cellData.getValue())));
        imageColumn.setCellFactory(param -> new ImageViewTableCell());

        TableColumn<CattleImage, Timestamp> dateCreatedColumn = getCattleImageTimestampTableColumn();

        TableColumn<CattleImage, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button viewButton = new Button("View");

            {
                deleteButton.setOnAction(event -> {
                    CattleImage image = getTableView().getItems().get(getIndex());
                    handleDeleteAction(image);
                });

                viewButton.setOnAction(event -> {
                    CattleImage image = getTableView().getItems().get(getIndex());
                    handleViewAction(image);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(deleteButton, viewButton);
                    hbox.setSpacing(5);
                    setGraphic(hbox);
                }
            }
        });

        //noinspection unchecked
        imageViewTable.getColumns().addAll(imageColumn, dateCreatedColumn, actionColumn);
    }

    private Image getImageForTableCell(CattleImage cattleImage) {
        String imageUrl = "file:src/main/resources/images/" + cattleImage.getImagePath();
        return new Image(imageUrl);
    }


    private void handleDeleteAction(CattleImage image) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Confirm Deletion");
        confirmationAlert.setContentText("Are you sure you want to delete this image?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleted = cattleImageDAO.deleteCattleImage(image.getImageId());
                if (deleted) {
                    imageViewTable.getItems().remove(image);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Image Deleted Successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete image");
                }
            }
        });
    }

    public void handleViewAction(CattleImage image) {
        Image imageToShow = new Image(new File("src/main/resources/images/" + image.getImagePath()).toURI().toString());

        Stage stage = new Stage();
        stage.setTitle("View Image");
        stage.setResizable(false);

        ImageView imageView = new ImageView(imageToShow);
        imageView.setFitWidth(500);
        imageView.setFitHeight(500);

        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Detect scroll event for zooming
        scrollPane.setOnScroll(this::handleScroll);

        stage.setOnCloseRequest(event -> stage.close());

        stage.setScene(new Scene(scrollPane));
        stage.show();
    }

    private void handleScroll(ScrollEvent event) {
        ScrollPane scrollPane = (ScrollPane) event.getSource();
        ImageView imageView = (ImageView) scrollPane.getContent();

        if (event.isControlDown()) {
            double deltaY = event.getDeltaY();
            Point2D pivotOnImageView = imageView.parentToLocal(new Point2D(event.getX(), event.getY()));
            zoom(imageView, deltaY > 0, pivotOnImageView);
            event.consume();
        }
    }

    private void zoom(ImageView imageView, boolean zoomIn, Point2D pivotOnImageView) {
        CattleImageManager.zoomingImages(imageView, zoomIn, pivotOnImageView);
    }



    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class ImageViewTableCell extends TableCell<CattleImage, Image> {
        private final ImageView imageView = new ImageView();

        @Override
        protected void updateItem(Image item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                imageView.setImage(item);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                setGraphic(imageView);
            }
        }
    }
}
