package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.model.FollowUpRecommendation;
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
            preparedStatement.setInt(1, followUpRecommendation.getHealthCheckupId());
            preparedStatement.setString(2, followUpRecommendation.getRecommendation());
            preparedStatement.setInt(3, followUpRecommendation.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update follow-up recommendation with ID: " + followUpRecommendation.getId());
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating follow-up recommendation: " + followUpRecommendation.getId(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }



    public static void insertFollowUpRecommendation(FollowUpRecommendation followUpRecommendation) throws SQLException {
        String query = "INSERT INTO followUpRecommendation (healthCheckupID, recommendation) VALUES (?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, followUpRecommendation.getHealthCheckupId());
            preparedStatement.setString(2, followUpRecommendation.getRecommendation());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AppLogger.error("Error inserting follow-up recommendation with ID: " + followUpRecommendation.getId(), e);
            e.printStackTrace();
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
