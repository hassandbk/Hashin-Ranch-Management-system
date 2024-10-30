module com.example.hashinfarm {
    // Core JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;

    // External libraries
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires imgscalr.lib;
    requires java.sql;
    requires mysql.connector.j;
    requires com.jfoenix;
    requires commons.math3;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires libphonenumber;
    requires annotations;

    // Package exports
    exports com.example.hashinfarm.app;
    exports com.example.hashinfarm.helpers;
    exports com.example.hashinfarm.utils;

    exports com.example.hashinfarm.businessLogic.services;
    exports com.example.hashinfarm.businessLogic.services.handlers;
    exports com.example.hashinfarm.businessLogic.services.interfaces;

    exports com.example.hashinfarm.utils.exceptions;
    exports com.example.hashinfarm.utils.logging;
    exports com.example.hashinfarm.utils.validation;
    exports com.example.hashinfarm.utils.date;
    exports com.example.hashinfarm.utils.interfaces;

    exports com.example.hashinfarm.data.DTOs;
    exports com.example.hashinfarm.data.DAOs;
    exports com.example.hashinfarm.data.DTOs.records;

    exports com.example.hashinfarm.presentationLayer.controllers;
    exports com.example.hashinfarm.presentationLayer.controllers.home;
    exports com.example.hashinfarm.presentationLayer.controllers.cattleManagement.cattleDetailMoreControllers;
    exports com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerLeftControllers;
    exports com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers;
    exports com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers.cattleDetailsMoreButtonsControllers;
    exports com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers.matingAndBreedingControllers;

    // Package opens for reflection access (alphabetized)
    opens com.example.hashinfarm to javafx.fxml;
    opens com.example.hashinfarm.app to javafx.fxml;
    opens com.example.hashinfarm.businessLogic.services to javafx.base, javafx.fxml;
    opens com.example.hashinfarm.data.DTOs to com.fasterxml.jackson.databind, javafx.base, javafx.fxml;
    opens com.example.hashinfarm.helpers to javafx.base, javafx.fxml;
    opens com.example.hashinfarm.presentationLayer.controllers to javafx.fxml;
    opens com.example.hashinfarm.presentationLayer.controllers.home to javafx.fxml;
    opens com.example.hashinfarm.presentationLayer.controllers.cattleManagement.cattleDetailMoreControllers to javafx.fxml;
    opens com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerLeftControllers to javafx.fxml;
    opens com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers to javafx.fxml;
    opens com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers.cattleDetailsMoreButtonsControllers to javafx.fxml;
    opens com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers.matingAndBreedingControllers to javafx.fxml;
    opens com.example.hashinfarm.utils to javafx.base, javafx.fxml;
    opens com.example.hashinfarm.utils.date to javafx.base, javafx.fxml;
    opens com.example.hashinfarm.utils.logging to javafx.base, javafx.fxml;
    opens com.example.hashinfarm.utils.validation to javafx.base, javafx.fxml;
    opens com.example.hashinfarm.data.DTOs.records to com.fasterxml.jackson.databind, javafx.base, javafx.fxml;
}
