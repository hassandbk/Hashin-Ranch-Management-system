package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.ProductionSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductionSessionDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<ProductionSession> getAllProductionSessions() throws SQLException {
        List<ProductionSession> productionSessions = new ArrayList<>();
        String query = "SELECT * FROM productionsession";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                productionSessions.add(extractProductionSessionFromResultSet(resultSet));
            }
        }
        return productionSessions;
    }

    public static void insertProductionSession(ProductionSession productionSession) throws SQLException {
        String query = "INSERT INTO productionsession (LactationPeriodID, CattleID, StartTime, EndTime, Duration, QualityScore) VALUES (?, ?, ?, ?, ?, ?)";
        executeUpdate(query, productionSession, false);
    }

    public static void updateProductionSession(ProductionSession productionSession) throws SQLException {
        String query = "UPDATE productionsession SET LactationPeriodID=?, CattleID=?, StartTime=?, EndTime=?, Duration=?, QualityScore=? WHERE SessionID=?";
        executeUpdate(query, productionSession, true);
    }

    public static boolean deleteProductionSession(int sessionID) throws SQLException {
        String query = "DELETE FROM productionsession WHERE SessionID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, sessionID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }


    private static void executeUpdate(String query, ProductionSession productionSession, boolean includeSessionID) throws SQLException {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productionSession.getLactationPeriodID());
            preparedStatement.setInt(2, productionSession.getCattleID());
            preparedStatement.setTimestamp(3, productionSession.getStartTime());
            preparedStatement.setTimestamp(4, productionSession.getEndTime());
            preparedStatement.setInt(5, productionSession.getDuration());
            preparedStatement.setString(6, productionSession.getQualityScore());

            if (includeSessionID) {
                preparedStatement.setInt(7, productionSession.getSessionID());
            }

            preparedStatement.executeUpdate();
        }
    }

    private static ProductionSession extractProductionSessionFromResultSet(ResultSet resultSet) throws SQLException {
        int sessionID = resultSet.getInt("SessionID");
        int lactationPeriodID = resultSet.getInt("LactationPeriodID");
        int cattleID = resultSet.getInt("CattleID");
        Timestamp startTime = resultSet.getTimestamp("StartTime");
        Timestamp endTime = resultSet.getTimestamp("EndTime");
        int duration = resultSet.getInt("Duration");
        String qualityScore = resultSet.getString("QualityScore");

        return new ProductionSession(sessionID, lactationPeriodID, cattleID, startTime, endTime, duration, qualityScore);
    }

    public static boolean updateSessionDates(int cattleID, int lactationPeriodID, LocalDate newDate) {
        String selectQuery = "SELECT SessionID, StartTime, EndTime FROM productionsession WHERE CattleID = ? AND LactationPeriodID = ?";
        String updateQuery = "UPDATE productionsession SET StartTime = ?, EndTime = ? WHERE SessionID = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            selectStmt.setInt(1, cattleID);
            selectStmt.setInt(2, lactationPeriodID);

            try (ResultSet resultSet = selectStmt.executeQuery()) {
                while (resultSet.next()) {
                    int sessionID = resultSet.getInt("SessionID");
                    Timestamp startTime = resultSet.getTimestamp("StartTime");
                    Timestamp endTime = resultSet.getTimestamp("EndTime");

                    // Retain the time part and update the date part
                    LocalDateTime newStartDateTime = LocalDateTime.of(newDate, startTime.toLocalDateTime().toLocalTime());
                    LocalDateTime newEndDateTime = LocalDateTime.of(newDate, endTime.toLocalDateTime().toLocalTime());

                    updateStmt.setTimestamp(1, Timestamp.valueOf(newStartDateTime));
                    updateStmt.setTimestamp(2, Timestamp.valueOf(newEndDateTime));
                    updateStmt.setInt(3, sessionID);

                    updateStmt.addBatch();
                }
                updateStmt.executeBatch();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<ProductionSession> getProductionSessionsByLactationPeriodId(int lactationPeriodId) throws SQLException {
        List<ProductionSession> productionSessions = new ArrayList<>();
        String query = "SELECT * FROM productionsession WHERE LactationPeriodID = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, lactationPeriodId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    productionSessions.add(extractProductionSessionFromResultSet(resultSet));
                }
            }
        }
        return productionSessions;
    }
    public static List<ProductionSession> getProductionSessionsByLactationPeriodId(Connection connection, int lactationPeriodId) throws SQLException {
        List<ProductionSession> productionSessions = new ArrayList<>();
        String query = "SELECT * FROM productionsession WHERE LactationPeriodID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, lactationPeriodId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    productionSessions.add(extractProductionSessionFromResultSet(resultSet));
                }
            }
        }
        return productionSessions;
    }
    public static boolean deleteProductionSession(Connection connection, int sessionID) throws SQLException {
        String query = "DELETE FROM productionsession WHERE SessionID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }


}
