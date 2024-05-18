package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.ProductionSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductionSessionDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<ProductionSession> getAllProductionSessions() throws SQLException {
        List<ProductionSession> productionSessions = new ArrayList<>();
        Connection connection;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM productionsession";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int sessionID = resultSet.getInt("SessionID");
                int lactationPeriodID = resultSet.getInt("LactationPeriodID");
                Timestamp startTime = resultSet.getTimestamp("StartTime");
                Timestamp endTime = resultSet.getTimestamp("EndTime");
                int duration = resultSet.getInt("Duration");
                int qualityScore = resultSet.getInt("QualityScore");

                ProductionSession productionSession = new ProductionSession(sessionID, lactationPeriodID, startTime, endTime, duration, qualityScore);
                productionSessions.add(productionSession);
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
        return productionSessions;
    }

    public static void insertProductionSession(ProductionSession productionSession) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "INSERT INTO productionsession (LactationPeriodID, StartTime, EndTime, Duration, QualityScore) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, productionSession.getLactationPeriodID());
            preparedStatement.setTimestamp(2, productionSession.getStartTime());
            preparedStatement.setTimestamp(3, productionSession.getEndTime());
            preparedStatement.setInt(4, productionSession.getDuration());
            preparedStatement.setInt(5, productionSession.getQualityScore());

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    public static void updateProductionSession(ProductionSession productionSession) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "UPDATE productionsession SET LactationPeriodID=?, StartTime=?, EndTime=?, Duration=?, QualityScore=? WHERE SessionID=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, productionSession.getLactationPeriodID());
            preparedStatement.setTimestamp(2, productionSession.getStartTime());
            preparedStatement.setTimestamp(3, productionSession.getEndTime());
            preparedStatement.setInt(4, productionSession.getDuration());
            preparedStatement.setInt(5, productionSession.getQualityScore());
            preparedStatement.setInt(6, productionSession.getSessionID());

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    public static void deleteProductionSession(int sessionID) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "DELETE FROM productionsession WHERE SessionID=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sessionID);

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
