package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.model.FollowUpRecommendation;
import com.example.hashinfarm.model.HealthCheckupHistory;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HealthCheckupHistoryDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<HealthCheckupHistory> getHealthCheckupHistoriesByCattleId(int cattleId) throws SQLException {
        String query = "SELECT * FROM healthCheckupHistory WHERE cattleID = ?";
        return getHealthCheckupHistoriesByQuery(query, cattleId);
    }

    private static List<HealthCheckupHistory> getHealthCheckupHistoriesByQuery(String query, Object parameter) throws SQLException {
        List<HealthCheckupHistory> healthCheckupList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setObject(1, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    healthCheckupList.add(mapResultSetToHealthCheckupHistory(resultSet));
                }
            }
        }
        return healthCheckupList;
    }

    public static void updateHealthCheckupHistory(HealthCheckupHistory healthCheckup) throws SQLException {
        String query = "UPDATE healthCheckupHistory SET cattleID = ?, checkupDate = ?, temperature = ?, " +
                "heartRate = ?, respiratoryRate = ?, bloodPressure = ?, behavioralObservations = ?, " +
                "physicalExaminationFindings = ?, healthIssues = ?, specificObservations = ?, " +
                "checkupNotes = ?, chronicConditions = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setHealthCheckupPreparedStatementValues(preparedStatement, healthCheckup);
            preparedStatement.setInt(13, healthCheckup.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update health checkup history with ID: " + healthCheckup.getId());
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating health checkup history: " + healthCheckup.getId(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }



    public static void insertHealthCheckupHistory(HealthCheckupHistory healthCheckup) throws SQLException {
        String query = "INSERT INTO healthCheckupHistory (cattleID, checkupDate, temperature, heartRate, " +
                "respiratoryRate, bloodPressure, behavioralObservations, physicalExaminationFindings, " +
                "healthIssues, specificObservations, checkupNotes, chronicConditions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            setHealthCheckupPreparedStatementValues(preparedStatement, healthCheckup);
            preparedStatement.executeUpdate();

            // Get the generated key for the health checkup
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int healthCheckupId = generatedKeys.getInt(1); // Get the generated health checkup ID

                // Now insert follow-up recommendations
                insertFollowUpRecommendations(healthCheckup.getFollowUpRecommendations(), healthCheckupId);
            } else {
                throw new SQLException("Creating health checkup failed, no ID obtained.");
            }
        } catch (SQLException e) {
            AppLogger.error("Error inserting health checkup history with ID: " + healthCheckup.getId(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }

    private static void insertFollowUpRecommendations(List<FollowUpRecommendation> recommendations, int healthCheckupId) throws SQLException {
        for (FollowUpRecommendation recommendation : recommendations) {
            recommendation.setHealthCheckupId(healthCheckupId); // Set the health checkup ID in recommendation
            FollowUpRecommendationDAO.insertFollowUpRecommendation(recommendation);
        }
    }

    public static void deleteHealthCheckupHistoryByCattleIdAndId(int cattleId, int healthCheckupId) throws SQLException {
        String deleteHealthCheckupQuery = "DELETE FROM healthCheckupHistory WHERE cattleID = ? AND id = ?";
        String deleteFollowUpQuery = "DELETE FROM followUpRecommendation WHERE healthCheckupID = ?";

        Connection connection = null;
        PreparedStatement deleteHealthCheckupStmt = null;
        PreparedStatement deleteFollowUpStmt = null;

        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Step 1: Delete follow-up recommendations for this health checkup
            deleteFollowUpStmt = connection.prepareStatement(deleteFollowUpQuery);
            deleteFollowUpStmt.setInt(1, healthCheckupId);
            deleteFollowUpStmt.executeUpdate();

            // Step 2: Delete the health checkup history record
            deleteHealthCheckupStmt = connection.prepareStatement(deleteHealthCheckupQuery);
            deleteHealthCheckupStmt.setInt(1, cattleId);
            deleteHealthCheckupStmt.setInt(2, healthCheckupId);
            int rowsAffected = deleteHealthCheckupStmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No health checkup history found for cattle ID: " + cattleId + " and health checkup ID: " + healthCheckupId);
            }

            connection.commit(); // Commit transaction if both deletions are successful
        } catch (SQLException e) {
            connection.rollback(); // Rollback transaction in case of error
            AppLogger.error("Error deleting health checkup history and follow-up recommendations for cattle ID: " + cattleId + " and health checkup ID: " + healthCheckupId, e);
            throw e; // Re-throw exception for higher-level handling
        } finally {
            // Ensure the resources are closed
            if (deleteHealthCheckupStmt != null) {
                deleteHealthCheckupStmt.close();
            }
            if (deleteFollowUpStmt != null) {
                deleteFollowUpStmt.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true); // Reset autocommit to true
                connection.close();
            }
        }
    }


    private static void setHealthCheckupPreparedStatementValues(PreparedStatement preparedStatement, HealthCheckupHistory healthCheckup) throws SQLException {
        preparedStatement.setInt(1, healthCheckup.getCattleId());
        preparedStatement.setDate(2, java.sql.Date.valueOf(healthCheckup.getCheckupDate()));
        preparedStatement.setString(3, healthCheckup.getTemperature());
        preparedStatement.setString(4, healthCheckup.getHeartRate());
        preparedStatement.setString(5, healthCheckup.getRespiratoryRate());
        preparedStatement.setString(6, healthCheckup.getBloodPressure());
        preparedStatement.setString(7, healthCheckup.getBehavioralObservations());
        preparedStatement.setString(8, healthCheckup.getPhysicalExaminationFindings());
        preparedStatement.setString(9, healthCheckup.getHealthIssues());
        preparedStatement.setString(10, healthCheckup.getSpecificObservations());
        preparedStatement.setString(11, healthCheckup.getCheckupNotes());
        preparedStatement.setString(12, healthCheckup.getChronicConditions());
    }

    private static HealthCheckupHistory mapResultSetToHealthCheckupHistory(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int cattleId = resultSet.getInt("cattleID");
        LocalDate checkupDate = resultSet.getDate("checkupDate").toLocalDate();
        String temperature = resultSet.getString("temperature");
        String heartRate = resultSet.getString("heartRate");
        String respiratoryRate = resultSet.getString("respiratoryRate");
        String bloodPressure = resultSet.getString("bloodPressure");
        String behavioralObservations = resultSet.getString("behavioralObservations");
        String physicalExaminationFindings = resultSet.getString("physicalExaminationFindings");
        String healthIssues = resultSet.getString("healthIssues");
        String specificObservations = resultSet.getString("specificObservations");
        String checkupNotes = resultSet.getString("checkupNotes");
        String chronicConditions = resultSet.getString("chronicConditions");
        LocalDateTime createdAt = resultSet.getTimestamp("createdAt").toLocalDateTime(); // assuming the database has a timestamp for createdAt

        return new HealthCheckupHistory(id, cattleId, checkupDate, temperature, heartRate, respiratoryRate,
                bloodPressure, behavioralObservations, physicalExaminationFindings, healthIssues,
                specificObservations, checkupNotes, chronicConditions, createdAt, null); // You can fetch follow-up recommendations later
    }

}
