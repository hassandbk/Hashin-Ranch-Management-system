module com.example.hashinfarm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires imgscalr.lib;
    requires java.sql;
    requires annotations;
    requires mysql.connector.j;
    requires com.jfoenix;
    requires commons.math3;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires libphonenumber;


    opens com.example.hashinfarm to javafx.fxml;
    opens com.example.hashinfarm.controller to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.cattleDetailMoreControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.matingAndBreedingControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.cattleDetailsMoreButtonsControllers to javafx.fxml;
    opens com.example.hashinfarm.controller.utility to javafx.base, javafx.fxml;
    opens com.example.hashinfarm.controller.records to javafx.base, javafx.fxml;


    exports com.example.hashinfarm;
    exports com.example.hashinfarm.controller;
    exports com.example.hashinfarm.controller.utility;
    exports com.example.hashinfarm.model;
    exports com.example.hashinfarm.exceptions;
    exports com.example.hashinfarm.controller.homePanels;
    exports com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers;
    exports com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers;
    exports com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;
    exports com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.matingAndBreedingControllers;
    exports com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.cattleDetailMoreControllers;
    exports com.example.hashinfarm.controller.dao;
    exports com.example.hashinfarm.controller.interfaces;
    exports com.example.hashinfarm.controller.records;
    opens com.example.hashinfarm.model to com.fasterxml.jackson.databind, javafx.base, javafx.fxml;

}
