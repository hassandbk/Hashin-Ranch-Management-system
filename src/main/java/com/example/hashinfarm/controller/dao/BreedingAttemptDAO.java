package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.model.BreedingAttempt;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BreedingAttemptDAO {

    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    // Fetch breeding attempts by cattle ID
    public static List<BreedingAttempt> getBreedingAttemptsByCattleId(int cattleId) throws SQLException {
        String query = "SELECT * FROM breedingattempts WHERE cattleId = ?";
        List<BreedingAttempt> breedingAttempts = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, cattleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    breedingAttempts.add(mapResultSetToBreedingAttempt(resultSet));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
            throw e;
        }

        return breedingAttempts;
    }

    // Update a specific breeding attempt
    public static void updateBreedingAttempt(BreedingAttempt breedingAttempt) throws SQLException {
        String query = "UPDATE breedingattempts SET estrusDate = ?, breedingMethod = ?, sireId = ?, notes = ?, attemptDate = ?, attemptStatus = ? " +
                "WHERE breedingAttemptId = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDate(1, Date.valueOf(breedingAttempt.getEstrusDate()));
            preparedStatement.setString(2, breedingAttempt.getBreedingMethod());
            preparedStatement.setInt(3, breedingAttempt.getSireId());
            preparedStatement.setString(4, breedingAttempt.getNotes());
            preparedStatement.setDate(5, Date.valueOf(breedingAttempt.getAttemptDate()));
            preparedStatement.setString(6, breedingAttempt.getAttemptStatus());
            preparedStatement.setInt(7, breedingAttempt.getBreedingAttemptId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw e;
        }
    }

    // Delete a specific breeding attempt by ID
    public static void deleteBreedingAttemptById(int breedingAttemptId) throws SQLException {
        String query = "DELETE FROM breedingattempts WHERE breedingAttemptId = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, breedingAttemptId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            handleSQLException(e);
            throw e;
        }
    }

    // Map ResultSet to BreedingAttempt model
    private static BreedingAttempt mapResultSetToBreedingAttempt(ResultSet resultSet) throws SQLException {
        LocalDate estrusDate = resultSet.getDate("estrusDate").toLocalDate();
        String estrusDateString = estrusDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return new BreedingAttempt(
                resultSet.getInt("breedingAttemptId"),
                resultSet.getInt("cattleId"),
                estrusDateString,
                resultSet.getString("breedingMethod"),
                resultSet.getInt("sireId"),
                resultSet.getString("notes"),
                resultSet.getDate("attemptDate").toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                resultSet.getString("attemptStatus")
        );
    }

    // Handle SQL exceptions
    private static void handleSQLException(SQLException e) {
        AppLogger.error("SQLException occurred: " + e.getMessage());
    }
}
