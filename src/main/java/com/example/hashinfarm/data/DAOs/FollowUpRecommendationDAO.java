package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.records.FollowUpRecommendation;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FollowUpRecommendationDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<FollowUpRecommendation> getFollowUpRecommendationsByHealthCheckupId(int healthCheckupId) throws SQLException {
        String query = "SELECT * FROM followUpRecommendation WHERE healthCheckupID = ?";
        return getFollowUpRecommendationsByQuery(query, healthCheckupId);
    }

    private static List<FollowUpRecommendation> getFollowUpRecommendationsByQuery(String query, Object parameter) throws SQLException {
        List<FollowUpRecommendation> followUpList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setObject(1, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    followUpList.add(mapResultSetToFollowUpRecommendation(resultSet));
                }
            }
        }
        return followUpList;
    }

    public static void updateFollowUpRecommendation(FollowUpRecommendation followUpRecommendation) throws SQLException {
        String query = "UPDATE followUpRecommendation SET healthCheckupID = ?, recommendation = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, followUpRecommendation.healthCheckupId());
            preparedStatement.setString(2, followUpRecommendation.recommendation());
            preparedStatement.setInt(3, followUpRecommendation.id());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update follow-up recommendation with ID: " + followUpRecommendation.id());
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating follow-up recommendation: " + followUpRecommendation.id(), e);
            throw e; // Re-throw the exception for higher-level handling
        }
    }

    public static void insertFollowUpRecommendation(FollowUpRecommendation followUpRecommendation) throws SQLException {
        String query = "INSERT INTO followUpRecommendation (healthCheckupID, recommendation) VALUES (?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, followUpRecommendation.healthCheckupId());
            preparedStatement.setString(2, followUpRecommendation.recommendation());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AppLogger.error("Error inserting follow-up recommendation with ID: " + followUpRecommendation.id(), e);
            throw e; // Re-throw the exception for higher-level handling
        }
    }

    public static void deleteFollowUpRecommendation(int recommendationId) throws SQLException {
        String query = "DELETE FROM followUpRecommendation WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, recommendationId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No follow-up recommendation found with ID: " + recommendationId);
            }
        } catch (SQLException e) {
            AppLogger.error("Error deleting follow-up recommendation with ID: " + recommendationId, e);
            throw e; // Re-throw the exception for higher-level handling
        }
    }

    private static FollowUpRecommendation mapResultSetToFollowUpRecommendation(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int healthCheckupId = resultSet.getInt("healthCheckupID");
        String recommendation = resultSet.getString("recommendation");
        LocalDateTime createdAt = resultSet.getTimestamp("createdAt").toLocalDateTime(); // assuming the database has a timestamp for createdAt
        return new FollowUpRecommendation(id, healthCheckupId, recommendation, createdAt);
    }
}
