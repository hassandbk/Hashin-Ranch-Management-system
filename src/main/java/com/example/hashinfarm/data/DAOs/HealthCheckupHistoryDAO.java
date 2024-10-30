package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.records.FollowUpRecommendation;
import com.example.hashinfarm.data.DTOs.records.HealthCheckupRecord;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class HealthCheckupHistoryDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<HealthCheckupRecord> getHealthCheckupHistoriesByCattleId(int cattleId) throws SQLException {
        String query = "SELECT * FROM healthCheckupHistory WHERE cattleID = ?";
        return getHealthCheckupHistoriesByQuery(query, cattleId);
    }

    private static List<HealthCheckupRecord> getHealthCheckupHistoriesByQuery(String query, Object parameter) throws SQLException {
        List<HealthCheckupRecord> healthCheckupList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setObject(1, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    healthCheckupList.add(mapResultSetToHealthCheckupRecord(resultSet));
                }
            }
        }
        return healthCheckupList;
    }

    public static void updateHealthCheckupHistory(HealthCheckupRecord healthCheckup) throws SQLException {
        String updateQuery = "UPDATE healthCheckupHistory SET cattleID = ?, checkupDate = ?, temperature = ?, " +
                "heartRate = ?, respiratoryRate = ?, bloodPressure = ?, behavioralObservations = ?, " +
                "physicalExaminationFindings = ?, healthIssues = ?, specificObservations = ?, " +
                "checkupNotes = ?, chronicConditions = ? WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            // Set the parameters for the update statement
            setHealthCheckupPreparedStatementValues(preparedStatement, healthCheckup);
            preparedStatement.setInt(13, healthCheckup.id()); // Setting the ID for WHERE clause

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Updating health checkup failed, no rows affected.");
            }

            // Now manage the follow-up recommendations
            updateFollowUpRecommendations(healthCheckup.followUpRecommendations(), healthCheckup.id());
        } catch (SQLException e) {
            AppLogger.error("Error updating health checkup history with ID: " + healthCheckup.id(), e);
            throw e;
        }
    }
    private static void updateFollowUpRecommendations(List<FollowUpRecommendation> recommendations, int healthCheckupId) throws SQLException {
        List<FollowUpRecommendation> existingRecommendations = FollowUpRecommendationDAO.getFollowUpRecommendationsByHealthCheckupId(healthCheckupId);
        Set<Integer> existingIds = new HashSet<>();
        for (FollowUpRecommendation recommendation : existingRecommendations) {
            existingIds.add(recommendation.id());
        }

        for (FollowUpRecommendation recommendation : recommendations) {
            if (recommendation.id() == 0) {
                // Create a new FollowUpRecommendation record with the healthCheckupId
                FollowUpRecommendation newRecommendation = new FollowUpRecommendation(
                        0, healthCheckupId, recommendation.recommendation(), recommendation.createdAt()
                );
                FollowUpRecommendationDAO.insertFollowUpRecommendation(newRecommendation);
            } else {
                FollowUpRecommendationDAO.updateFollowUpRecommendation(recommendation);
                existingIds.remove(recommendation.id());
            }
        }

        for (Integer id : existingIds) {
            FollowUpRecommendationDAO.deleteFollowUpRecommendation(id);
        }
    }

    public static void insertHealthCheckupHistory(HealthCheckupRecord healthCheckup) throws SQLException {
        String query = "INSERT INTO healthCheckupHistory (cattleID, checkupDate, temperature, heartRate, " +
                "respiratoryRate, bloodPressure, behavioralObservations, physicalExaminationFindings, " +
                "healthIssues, specificObservations, checkupNotes, chronicConditions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            setHealthCheckupPreparedStatementValues(preparedStatement, healthCheckup);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int healthCheckupId = generatedKeys.getInt(1);

                insertFollowUpRecommendations(healthCheckup.followUpRecommendations(), healthCheckupId);
            } else {
                throw new SQLException("Creating health checkup failed, no ID obtained.");
            }
        } catch (SQLException e) {
            AppLogger.error("Error inserting health checkup history", e);
            throw e;
        }
    }

    private static void insertFollowUpRecommendations(List<FollowUpRecommendation> recommendations, int healthCheckupId) throws SQLException {
        for (FollowUpRecommendation recommendation : recommendations) {
            // Create a new FollowUpRecommendation with the updated healthCheckupId
            FollowUpRecommendation newRecommendation = new FollowUpRecommendation(
                    recommendation.id(), healthCheckupId, recommendation.recommendation(), recommendation.createdAt()
            );
            FollowUpRecommendationDAO.insertFollowUpRecommendation(newRecommendation);
        }
    }


    public static void deleteHealthCheckupHistoryByCattleIdAndId(int cattleId, int healthCheckupId) throws SQLException {
        String deleteHealthCheckupQuery = "DELETE FROM healthCheckupHistory WHERE cattleID = ? AND id = ?";
        String deleteFollowUpQuery = "DELETE FROM followUpRecommendation WHERE healthCheckupID = ?";

        try (Connection connection = dbConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteFollowUpStmt = connection.prepareStatement(deleteFollowUpQuery);
                 PreparedStatement deleteHealthCheckupStmt = connection.prepareStatement(deleteHealthCheckupQuery)) {

                deleteFollowUpStmt.setInt(1, healthCheckupId);
                deleteFollowUpStmt.executeUpdate();

                deleteHealthCheckupStmt.setInt(1, cattleId);
                deleteHealthCheckupStmt.setInt(2, healthCheckupId);
                int rowsAffected = deleteHealthCheckupStmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("No health checkup history found for cattle ID: " + cattleId + " and health checkup ID: " + healthCheckupId);
                }

                connection.commit();
            }
        } catch (SQLException e) {
            AppLogger.error("Error deleting health checkup history and follow-up recommendations for cattle ID: " + cattleId + " and health checkup ID: " + healthCheckupId, e);
            throw e;
        }
    }

    private static void setHealthCheckupPreparedStatementValues(PreparedStatement preparedStatement, HealthCheckupRecord healthCheckup) throws SQLException {
        preparedStatement.setInt(1, healthCheckup.cattleId());
        preparedStatement.setDate(2, java.sql.Date.valueOf(healthCheckup.checkupDate()));
        preparedStatement.setString(3, healthCheckup.temperature());
        preparedStatement.setString(4, healthCheckup.heartRate());
        preparedStatement.setString(5, healthCheckup.respiratoryRate());
        preparedStatement.setString(6, healthCheckup.bloodPressure());
        preparedStatement.setString(7, healthCheckup.behavioralObservations());
        preparedStatement.setString(8, healthCheckup.physicalExaminationFindings());
        preparedStatement.setString(9, healthCheckup.healthIssues());
        preparedStatement.setString(10, healthCheckup.specificObservations());
        preparedStatement.setString(11, healthCheckup.checkupNotes());
        preparedStatement.setString(12, healthCheckup.chronicConditions());
    }

    private static HealthCheckupRecord mapResultSetToHealthCheckupRecord(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int cattleId = resultSet.getInt("cattleID");
        LocalDate checkupDate = resultSet.getDate("checkupDate").toLocalDate();
        String temperature = resultSet.getString("temperature");
        String heartRate = resultSet.getString("heartRate");
        String respiratoryRate = resultSet.getString("respiratoryRate");
        String bloodPressure = resultSet.getString("bloodPressure");

        Map<String, String> observations = extractObservations(resultSet);
        LocalDateTime createdAt = resultSet.getTimestamp("createdAt").toLocalDateTime();

        return new HealthCheckupRecord(id, cattleId, checkupDate, temperature, heartRate, respiratoryRate,
                bloodPressure, observations.get("behavioralObservations"), observations.get("physicalExaminationFindings"),
                observations.get("healthIssues"), observations.get("specificObservations"),
                observations.get("checkupNotes"), observations.get("chronicConditions"), createdAt, null);
    }

    private static Map<String, String> extractObservations(ResultSet resultSet) throws SQLException {
        Map<String, String> observations = new HashMap<>();
        observations.put("behavioralObservations", resultSet.getString("behavioralObservations"));
        observations.put("physicalExaminationFindings", resultSet.getString("physicalExaminationFindings"));
        observations.put("healthIssues", resultSet.getString("healthIssues"));
        observations.put("specificObservations", resultSet.getString("specificObservations"));
        observations.put("checkupNotes", resultSet.getString("checkupNotes"));
        observations.put("chronicConditions", resultSet.getString("chronicConditions"));
        return observations;
    }


// Method to retrieve categorized health checkup history by cattle ID
    public static Map<String, List<String>> getCategorizedHealthCheckupHistoriesByCattleId(int cattleId) throws SQLException {
        String query = "SELECT * FROM healthCheckupHistory WHERE cattleID = ?";
        Map<String, List<String>> categorizedHealthNotes = new HashMap<>();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Use the new method to extract observations
                    Map<String, String> observations = extractObservations(resultSet);

                    // Populate the map with categorized data
                    addToCategory(categorizedHealthNotes, "Behavioral Observations", observations.get("behavioralObservations"));
                    addToCategory(categorizedHealthNotes, "Physical Examination Findings", observations.get("physicalExaminationFindings"));
                    addToCategory(categorizedHealthNotes, "Health Issues", observations.get("healthIssues"));
                    addToCategory(categorizedHealthNotes, "Specific Observations", observations.get("specificObservations"));
                    addToCategory(categorizedHealthNotes, "Checkup Notes", observations.get("checkupNotes"));
                    addToCategory(categorizedHealthNotes, "Chronic Conditions", observations.get("chronicConditions"));
                }
            }
        }
        return categorizedHealthNotes;
    }
    // Helper method to add an observation to the respective category
    private static void addToCategory(Map<String, List<String>> map, String category, String observation) {
        if (observation != null && !observation.trim().isEmpty()) {
            map.computeIfAbsent(category, k -> new ArrayList<>()).add(observation);
        }
    }


}
