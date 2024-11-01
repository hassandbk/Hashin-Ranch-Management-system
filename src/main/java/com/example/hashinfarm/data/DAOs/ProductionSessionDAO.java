package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.data.DTOs.records.ProductionSession;
import com.example.hashinfarm.app.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static void saveProductionSession(ProductionSession productionSession) throws SQLException {
        String query = "INSERT INTO productionsession (LactationPeriodID, CattleID, StartTime, EndTime, QualityScore, ProductionVolume) VALUES (?, ?, ?, ?, ?, ?)";
        executeUpdate(query, productionSession, false);
    }

    public static void updateProductionSession(ProductionSession productionSession) throws SQLException {
        String query = "UPDATE productionsession SET LactationPeriodID=?, CattleID=?, StartTime=?, EndTime=?, QualityScore=?, ProductionVolume=? WHERE SessionID=?";
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

            preparedStatement.setInt(1, productionSession.lactationPeriodID());
            preparedStatement.setInt(2, productionSession.cattleID());
            preparedStatement.setTimestamp(3, productionSession.startTime());
            preparedStatement.setTimestamp(4, productionSession.endTime());
            preparedStatement.setString(5, productionSession.qualityScore());
            preparedStatement.setDouble(6, productionSession.productionVolume());

            if (includeSessionID) {
                preparedStatement.setInt(7, productionSession.sessionID());
            }

            preparedStatement.executeUpdate();
        }
    }

    public static List<ProductionSession> getProductionSessionsByLactationIdAndDateRange(int lactationPeriodId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ProductionSession> productionSessions = new ArrayList<>();
        String query = "SELECT * FROM productionsession WHERE LactationPeriodID = ? AND DATE(StartTime) >= ? AND DATE(StartTime) <= ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, lactationPeriodId);
            preparedStatement.setDate(2, Date.valueOf(startDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    productionSessions.add(extractProductionSessionFromResultSet(resultSet));
                }
            }
        }
        return productionSessions;
    }

    public static List<ProductionSession> getProductionSessionsByLactationIdAndDate(int lactationPeriodId, LocalDate specifiedDate) throws SQLException {
        List<ProductionSession> productionSessions = new ArrayList<>();
        String query = "SELECT * FROM productionsession WHERE LactationPeriodID = ? AND DATE(StartTime) = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, lactationPeriodId);
            preparedStatement.setDate(2, Date.valueOf(specifiedDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    productionSessions.add(extractProductionSessionFromResultSet(resultSet));
                }
            }
        }
        return productionSessions;
    }

    private static ProductionSession extractProductionSessionFromResultSet(ResultSet resultSet) throws SQLException {
        int sessionID = resultSet.getInt("SessionID");
        int lactationPeriodID = resultSet.getInt("LactationPeriodID");
        int cattleID = resultSet.getInt("CattleID");
        Timestamp startTime = resultSet.getTimestamp("StartTime");
        Timestamp endTime = resultSet.getTimestamp("EndTime");
        String qualityScore = resultSet.getString("QualityScore");
        double productionVolume = resultSet.getDouble("ProductionVolume");

        return new ProductionSession(sessionID, lactationPeriodID, cattleID, startTime, endTime, qualityScore, productionVolume);
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

    public static boolean deleteProductionSession(Connection connection, int sessionID) throws SQLException {
        String query = "DELETE FROM productionsession WHERE SessionID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static List<ProductionSession> getProductionSessionsByDateAndLactationPeriodId(LocalDate date, int lactationPeriodId) throws SQLException {
        List<ProductionSession> productionSessions = new ArrayList<>();
        String query = "SELECT * FROM productionsession WHERE DATE(StartTime) = ? AND LactationPeriodID = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setInt(2, lactationPeriodId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    productionSessions.add(extractProductionSessionFromResultSet(resultSet));
                }
            }
        }
        return productionSessions;
    }

    public static Map<String, List<ProductionSession>> categorizeSessionsByTimeOfDay(List<ProductionSession> sessions) {
        Map<String, List<ProductionSession>> categorizedSessions = new HashMap<>();

        List<ProductionSession> morningSessions = new ArrayList<>();
        List<ProductionSession> afternoonSessions = new ArrayList<>();
        List<ProductionSession> eveningSessions = new ArrayList<>();

        for (ProductionSession session : sessions) {
            LocalDateTime startTime = session.startTime().toLocalDateTime();
            LocalTime timeOfDay = startTime.toLocalTime();

            if (timeOfDay.isBefore(LocalTime.NOON)) {
                morningSessions.add(session);
            } else if (timeOfDay.isBefore(LocalTime.of(16, 0))) {
                afternoonSessions.add(session);
            } else {
                eveningSessions.add(session);
            }
        }

        categorizedSessions.put("Morning", morningSessions);
        categorizedSessions.put("Afternoon", afternoonSessions);
        categorizedSessions.put("Evening", eveningSessions);

        return categorizedSessions;
    }
}
