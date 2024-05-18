package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.HealthAlert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HealthAlertDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<HealthAlert> getAllHealthAlerts() throws SQLException {
        List<HealthAlert> healthAlerts = new ArrayList<>();
        Connection connection;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM healthalert";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int alertID = resultSet.getInt("AlertID");
                int cattleID = resultSet.getInt("CattleID");
                String alertType = resultSet.getString("AlertType");
                String notes = resultSet.getString("Notes");

                HealthAlert healthAlert = new HealthAlert(alertID, cattleID, alertType, notes);
                healthAlerts.add(healthAlert);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
        return healthAlerts;
    }

    public static void insertHealthAlert(HealthAlert healthAlert) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "INSERT INTO healthalert (CattleID, AlertType, Notes) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, healthAlert.getCattleID());
            preparedStatement.setString(2, healthAlert.getAlertType());
            preparedStatement.setString(3, healthAlert.getNotes());

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    public static void updateHealthAlert(HealthAlert healthAlert) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "UPDATE healthalert SET CattleID=?, AlertType=?, Notes=? WHERE AlertID=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, healthAlert.getCattleID());
            preparedStatement.setString(2, healthAlert.getAlertType());
            preparedStatement.setString(3, healthAlert.getNotes());
            preparedStatement.setInt(4, healthAlert.getAlertID());

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    public static void deleteHealthAlert(int alertID) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "DELETE FROM healthalert WHERE AlertID=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, alertID);

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    // Add other CRUD methods as needed
}
