package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.model.ReproductiveVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReproductiveVariablesDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();


    // Insert a new reproductive variable record
    public int addReproductiveVariableAndGetId(ReproductiveVariables reproductiveVariables) {
        String query = "INSERT INTO reproductivevariables (CattleID, BreedingDate, GestationPeriod, CalvingDate, CalvingInterval) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, reproductiveVariables.getCattleID());
            preparedStatement.setObject(2, reproductiveVariables.getBreedingDate() != null ? Date.valueOf(reproductiveVariables.getBreedingDate()) : null);
            preparedStatement.setInt(3, reproductiveVariables.getGestationPeriod());
            preparedStatement.setObject(4, reproductiveVariables.getCalvingDate() != null ? Date.valueOf(reproductiveVariables.getCalvingDate()) : null);
            preparedStatement.setObject(5, reproductiveVariables.getCalvingInterval());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated ID
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Error adding reproductive variable for cattle: " + reproductiveVariables.getCattleID(), e);
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
                    ReproductiveVariables reproductiveVariables =
                            mapResultSetToReproductiveVariables(resultSet);
                    reproductiveVariablesList.add(reproductiveVariables);
                }
            }
        } catch (SQLException e) {
            // Use AppLogger for error handling with a more specific message
            AppLogger.error("Error fetching reproductive variables for cattle: " + cattleID, e);
        }
        return reproductiveVariablesList;
    }

    // Update reproductive variable record
    public boolean updateReproductiveVariable(ReproductiveVariables reproductiveVariables) {
        String query =
                "UPDATE reproductivevariables SET BreedingDate=?, GestationPeriod=?, CalvingDate=?, CalvingInterval=? WHERE ReproductiveVariableID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Check if reproductiveVariables.getBreedingDate() and reproductiveVariables.getCalvingDate() are not null before converting
            Date breedingDate = reproductiveVariables.getBreedingDate() != null ? Date.valueOf(reproductiveVariables.getBreedingDate()) : null;
            Date calvingDate = reproductiveVariables.getCalvingDate() != null ? Date.valueOf(reproductiveVariables.getCalvingDate()) : null;

            preparedStatement.setDate(1, breedingDate);
            preparedStatement.setInt(2, reproductiveVariables.getGestationPeriod());
            preparedStatement.setDate(3, calvingDate);
            preparedStatement.setInt(4, reproductiveVariables.getCalvingInterval());
            preparedStatement.setInt(5, reproductiveVariables.getReproductiveVariableID());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update reproductive variable with ID: " + reproductiveVariables.getReproductiveVariableID());
            } else {
                return true;
            }
        } catch (SQLException e) {
            // Use AppLogger for error handling
            AppLogger.error(
                    "Error updating reproductive variable: "
                            + reproductiveVariables.getReproductiveVariableID(),
                    e);
            return false;
        }
    }


    // Delete reproductive variable record by ID
    public boolean deleteReproductiveVariable(int reproductiveVariableID) {
        String query = "DELETE FROM reproductivevariables WHERE ReproductiveVariableID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reproductiveVariableID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Use AppLogger for error handling
            AppLogger.error("Error deleting reproductive variable: " + reproductiveVariableID, e);
            return false;
        }
    }

    // Helper method to map ResultSet to ReproductiveVariables object
    private ReproductiveVariables mapResultSetToReproductiveVariables(ResultSet resultSet)
            throws SQLException {
        ReproductiveVariables reproductiveVariables = new ReproductiveVariables();
        reproductiveVariables.setReproductiveVariableID(resultSet.getInt("ReproductiveVariableID"));
        reproductiveVariables.setCattleID(resultSet.getInt("CattleID"));

        Date breedingDate = resultSet.getDate("BreedingDate");
        if (breedingDate != null) {
            reproductiveVariables.setBreedingDate(breedingDate.toLocalDate());
        }

        reproductiveVariables.setGestationPeriod(resultSet.getInt("GestationPeriod"));

        Date calvingDate = resultSet.getDate("CalvingDate");
        if (calvingDate != null) {
            reproductiveVariables.setCalvingDate(calvingDate.toLocalDate());
        }

        reproductiveVariables.setCalvingInterval(resultSet.getInt("CalvingInterval"));

        return reproductiveVariables;
    }
    public boolean deleteReproductiveVariable(Connection connection, int reproductiveVariableID) throws SQLException {
        String query = "DELETE FROM reproductivevariables WHERE ReproductiveVariableID=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reproductiveVariableID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

}
