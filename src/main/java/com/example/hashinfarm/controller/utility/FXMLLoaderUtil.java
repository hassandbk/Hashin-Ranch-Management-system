
package com.example.hashinfarm.controller.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FXMLLoaderUtil {

    public static void loadFXML(String fxmlPath, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FXMLLoaderUtil.class.getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Add more detailed error handling here
        }
    }

}
