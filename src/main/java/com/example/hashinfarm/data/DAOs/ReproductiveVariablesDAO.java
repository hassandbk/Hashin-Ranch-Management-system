package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.records.ReproductiveVariables;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReproductiveVariablesDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    // Insert a new reproductive variable record
    public int addReproductiveVariableAndGetId(ReproductiveVariables reproductiveVariables) {
        String query = "INSERT INTO reproductivevariables (CattleID, BreedingDate, GestationPeriod, CalvingDate, CalvingInterval) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, reproductiveVariables.cattleID());
            preparedStatement.setObject(2, reproductiveVariables.breedingDate() != null ? Date.valueOf(reproductiveVariables.breedingDate()) : null);
            preparedStatement.setInt(3, reproductiveVariables.gestationPeriod());
            preparedStatement.setObject(4, reproductiveVariables.calvingDate() != null ? Date.valueOf(reproductiveVariables.calvingDate()) : null);
            preparedStatement.setInt(5, reproductiveVariables.calvingInterval());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated ID
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error adding reproductive variable for cattle: " + reproductiveVariables.cattleID(), e);
        }
        return -1; // Return -1 if insertion fails
    }

    // Retrieve all reproductive variables for a given cattle ID
    public List<ReproductiveVariables> getAllReproductiveVariablesForCattle(int cattleID) {
        List<ReproductiveVariables> reproductiveVariablesList = new ArrayList<>();
        String query = "SELECT * FROM reproductivevariables WHERE CattleID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ReproductiveVariables reproductiveVariables = mapResultSetToReproductiveVariables(resultSet);
                    reproductiveVariablesList.add(reproductiveVariables);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error fetching reproductive variables for cattle: " + cattleID, e);
        }
        return reproductiveVariablesList;
    }

    // Update reproductive variable record
    public boolean updateReproductiveVariable(ReproductiveVariables reproductiveVariables) {
        String query = "UPDATE reproductivevariables SET BreedingDate=?, GestationPeriod=?, CalvingDate=?, CalvingInterval=? WHERE ReproductiveVariableID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDate(1, reproductiveVariables.breedingDate() != null ? Date.valueOf(reproductiveVariables.breedingDate()) : null);
            preparedStatement.setInt(2, reproductiveVariables.gestationPeriod());
            preparedStatement.setDate(3, reproductiveVariables.calvingDate() != null ? Date.valueOf(reproductiveVariables.calvingDate()) : null);
            preparedStatement.setInt(4, reproductiveVariables.calvingInterval());
            preparedStatement.setInt(5, reproductiveVariables.reproductiveVariableID());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            AppLogger.error("Error updating reproductive variable: " + reproductiveVariables.reproductiveVariableID(), e);
            return false;
        }
    }

    // Delete reproductive variable record by ID
    public static boolean deleteReproductiveVariable(int reproductiveVariableID) {
        String query = "DELETE FROM reproductivevariables WHERE ReproductiveVariableID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reproductiveVariableID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            AppLogger.error("Error deleting reproductive variable: " + reproductiveVariableID, e);
            return false;
        }
    }

    public boolean deleteReproductiveVariableAtomically(Connection connection, int reproductiveVariableID) throws SQLException {
        String query = "DELETE FROM reproductivevariables WHERE ReproductiveVariableID=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reproductiveVariableID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to map ResultSet to ReproductiveVariables record
    private ReproductiveVariables mapResultSetToReproductiveVariables(ResultSet resultSet) throws SQLException {
        int reproductiveVariableID = resultSet.getInt("ReproductiveVariableID");
        int cattleID = resultSet.getInt("CattleID");
        LocalDate breedingDate = resultSet.getDate("BreedingDate") != null ? resultSet.getDate("BreedingDate").toLocalDate() : null;
        int gestationPeriod = resultSet.getInt("GestationPeriod");
        LocalDate calvingDate = resultSet.getDate("CalvingDate") != null ? resultSet.getDate("CalvingDate").toLocalDate() : null;
        int calvingInterval = resultSet.getInt("CalvingInterval");

        return new ReproductiveVariables(reproductiveVariableID, cattleID, breedingDate, gestationPeriod, calvingDate, calvingInterval);
    }

    public ReproductiveVariables getReproductiveVariableByCattleIdAndBreedingDate(int cattleId, LocalDate breedingDate) throws SQLException {
        String query = "SELECT * FROM reproductivevariables WHERE CattleID = ? AND BreedingDate = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            preparedStatement.setDate(2, Date.valueOf(breedingDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToReproductiveVariables(resultSet);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error fetching reproductive variable for cattle: " + cattleId + " and breeding date: " + breedingDate, e);
            throw e;
        }
        return null;
    }

    public ReproductiveVariables getNextBreedingAttempt(int cattleId, LocalDate afterDate) throws SQLException {
        String query = "SELECT * FROM reproductivevariables WHERE CattleID = ? AND BreedingDate > ? ORDER BY BreedingDate ASC LIMIT 1";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            preparedStatement.setDate(2, Date.valueOf(afterDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToReproductiveVariables(resultSet);
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error fetching next breeding attempt for cattle: " + cattleId + " after date: " + afterDate, e);
            throw e;
        }
        return null;
    }

    public static boolean updateGestationPeriod(int cattleId, int newGestationLength) throws SQLException {
        String query = "UPDATE reproductivevariables SET GestationPeriod = ? WHERE CattleID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, newGestationLength);
            preparedStatement.setInt(2, cattleId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
