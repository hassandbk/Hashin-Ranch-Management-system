package com.example.hashinfarm.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TableColumnUtils {

    // Private constructor to prevent instantiation
    private TableColumnUtils() {
    }


    public static <S, T> void centerAlignColumn(TableColumn<S, T> column) {
        column.setCellFactory(new Callback<TableColumn<S, T>, TableCell<S, T>>() {
            @Override
            public TableCell<S, T> call(TableColumn<S, T> param) {
                return new TableCell<S, T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item.toString());
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });
    }
}
