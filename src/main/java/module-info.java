module com.example.hashinfarm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires imgscalr.lib;
    requires java.sql;



    opens com.example.hashinfarm to javafx.fxml;
    opens com.example.hashinfarm.controller to javafx.fxml; // Add this line to open the controller package
    opens com.example.hashinfarm.controller.homePanels to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.cattleDetailMoreControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.matingAndBreedingControllers to javafx.fxml;
    opens com.example.hashinfarm.model to javafx.base;

    exports com.example.hashinfarm;
    exports com.example.hashinfarm.controller;
    exports com.example.hashinfarm.controller.utility;
    opens com.example.hashinfarm.controller.utility to javafx.fxml;


}
