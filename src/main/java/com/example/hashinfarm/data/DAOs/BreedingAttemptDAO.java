package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.records.BreedingAttempt;

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

    // Fetch breeding attempts by cattle ID and estrus date
    public static List<BreedingAttempt> getBreedingAttemptsByCattleIdAndEstrusDate(int cattleId, LocalDate estrusDate) throws SQLException {
        String query = "SELECT * FROM breedingattempts WHERE cattleId = ? AND estrusDate = ?";
        List<BreedingAttempt> breedingAttempts = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, cattleId);
            preparedStatement.setDate(2, Date.valueOf(estrusDate));

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

            preparedStatement.setDate(1, Date.valueOf(breedingAttempt.estrusDate()));
            preparedStatement.setString(2, breedingAttempt.breedingMethod());
            preparedStatement.setInt(3, breedingAttempt.sireId());
            preparedStatement.setString(4, breedingAttempt.notes());
            preparedStatement.setDate(5, Date.valueOf(breedingAttempt.attemptDate()));
            preparedStatement.setString(6, breedingAttempt.attemptStatus());
            preparedStatement.setInt(7, breedingAttempt.breedingAttemptId());

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

    // Map ResultSet to BreedingAttempt record
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

    // Fetch the previous breeding attempt date for a given cattle ID and current estrus date
    private static LocalDate fetchEstrusDate(int cattleId, LocalDate currentEstrusDate, String query) throws SQLException {
        LocalDate estrusDate = null;

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, cattleId);
            preparedStatement.setDate(2, Date.valueOf(currentEstrusDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Date date = resultSet.getDate(1);
                    if (date != null) {
                        estrusDate = date.toLocalDate();
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
            throw e;
        }

        return estrusDate;
    }

    // Fetch the previous estrus date for a given cattle ID and current estrus date
    public static LocalDate getPreviousEstrusDate(int cattleId, LocalDate currentEstrusDate) throws SQLException {
        String query = "SELECT MAX(estrusDate) FROM breedingattempts WHERE cattleId = ? AND estrusDate < ?";
        return fetchEstrusDate(cattleId, currentEstrusDate, query);
    }

    // Fetch the subsequent estrus date for a given cattle ID and current estrus date
    public static LocalDate getSubsequentEstrusDate(int cattleId, LocalDate currentEstrusDate) throws SQLException {
        String query = "SELECT MIN(estrusDate) FROM breedingattempts WHERE cattleId = ? AND estrusDate > ?";
        return fetchEstrusDate(cattleId, currentEstrusDate, query);
    }

    // Handle SQL exceptions
    private static void handleSQLException(SQLException e) {
        AppLogger.error("SQLException occurred: " + e.getMessage());
    }

    // Save a new breeding attempt
    public static void saveBreedingAttempt(BreedingAttempt breedingAttempt) throws SQLException {
        String query = "INSERT INTO breedingattempts (cattleId, estrusDate, breedingMethod, sireId, notes, attemptDate, attemptStatus) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, breedingAttempt.cattleId());
            preparedStatement.setDate(2, Date.valueOf(breedingAttempt.estrusDate()));
            preparedStatement.setString(3, breedingAttempt.breedingMethod());
            preparedStatement.setInt(4, breedingAttempt.sireId());
            preparedStatement.setString(5, breedingAttempt.notes());
            preparedStatement.setDate(6, Date.valueOf(breedingAttempt.attemptDate()));
            preparedStatement.setString(7, breedingAttempt.attemptStatus());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw e;
        }
    }
}
