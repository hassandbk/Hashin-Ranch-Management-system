package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.records.UnifiedOffspring;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OffspringDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static UnifiedOffspring getTheOffspringDetailsByItsCattleId(int cattleId) throws SQLException {
        String query = "SELECT o.OffspringID, o.BirthWeight, o.EaseOfCalving, o.GestationLength, " +
                "o.MeasuredWeight, o.LastDateWeightTaken, o.IntendedUse, " +
                "o.CattleID, o.BreedingMethod, c.DateOfBirth, c.SireID, c.Name AS CattleName, c.Gender " +
                "FROM offspring o JOIN cattle c ON o.CattleID = c.CattleID WHERE o.CattleID = ?";
        List<UnifiedOffspring> offspringList = getOffspringByQuery(query, cattleId);
        return offspringList.isEmpty() ? null : offspringList.get(0);
    }

    private static List<UnifiedOffspring> getOffspringByQuery(String query, Object parameter) throws SQLException {
        List<UnifiedOffspring> offspringList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setObject(1, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    offspringList.add(mapResultSetToUnifiedOffspring(resultSet));
                }
            }
        }
        return offspringList;
    }

    public static void updateOffspring(UnifiedOffspring offspring) throws SQLException {
        String query = "UPDATE offspring SET BirthWeight = ?, EaseOfCalving = ?, GestationLength = ?, " +
                "MeasuredWeight = ?, LastDateWeightTaken = ?, IntendedUse = ?, CattleID = ?, BreedingMethod = ? " +
                "WHERE OffspringID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setOffspringPreparedStatementValues(preparedStatement, offspring);
            preparedStatement.setInt(9, offspring.offspringId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update offspring with ID: " + offspring.offspringId());
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating offspring: " + offspring.offspringId(), e);
            throw e;
        }
    }

    public static void deleteOffspringById(int cattleId) throws SQLException {
        String query = "DELETE FROM offspring WHERE CattleID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            preparedStatement.executeUpdate();
        }
    }

    private static void setOffspringPreparedStatementValues(PreparedStatement preparedStatement, UnifiedOffspring offspring) throws SQLException {
        preparedStatement.setDouble(1, offspring.birthWeight());
        preparedStatement.setInt(2, offspring.easeOfCalving());
        preparedStatement.setInt(3, offspring.gestationLength());
        preparedStatement.setDouble(4, offspring.measuredWeight());

        LocalDate lastDateWeightTaken = offspring.lastDateWeightTaken();
        if (lastDateWeightTaken != null) {
            preparedStatement.setDate(5, java.sql.Date.valueOf(lastDateWeightTaken));
        } else {
            preparedStatement.setNull(5, java.sql.Types.DATE);
        }

        preparedStatement.setString(6, offspring.intendedUse());
        preparedStatement.setString(7, offspring.cattleId());
        preparedStatement.setString(8, offspring.breedingMethod());
    }

    private static UnifiedOffspring mapResultSetToUnifiedOffspring(ResultSet resultSet) throws SQLException {
        int offspringId = resultSet.getInt("OffspringID");
        String cattleId = resultSet.getString("CattleID");
        String dateOfBirth = resultSet.getString("DateOfBirth");
        String cattleName = resultSet.getString("CattleName");
        String gender = resultSet.getString("Gender");
        String breedingMethod = resultSet.getString("BreedingMethod");

        // Handle null values for numerical fields and provide default values if necessary
        Double birthWeight = resultSet.getObject("BirthWeight") != null ? resultSet.getDouble("BirthWeight") : 0.0;
        Integer easeOfCalving = resultSet.getObject("EaseOfCalving") != null ? resultSet.getInt("EaseOfCalving") : 0;
        Integer gestationLength = resultSet.getObject("GestationLength") != null ? resultSet.getInt("GestationLength") : 283; // Default to 283 days
        Double measuredWeight = resultSet.getObject("MeasuredWeight") != null ? resultSet.getDouble("MeasuredWeight") : 0.0;

        LocalDate lastDateWeightTaken = resultSet.getDate("LastDateWeightTaken") != null ?
                resultSet.getDate("LastDateWeightTaken").toLocalDate() : null;

        String intendedUse = resultSet.getString("IntendedUse") != null ? resultSet.getString("IntendedUse") : "";
        String sireId = resultSet.getString("SireID") != null ? resultSet.getString("SireID") : "";

        return new UnifiedOffspring(offspringId, cattleId, cattleName, gender, breedingMethod, birthWeight, easeOfCalving,
                gestationLength, measuredWeight, lastDateWeightTaken, dateOfBirth, intendedUse, sireId);
    }


    public static boolean isOffspringOfSelectedCattle(int cattleId) throws SQLException {
        String query = "SELECT COUNT(*) FROM offspring WHERE CattleID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static void insertOffspring(UnifiedOffspring offspring) throws SQLException {
        String query = "INSERT INTO offspring (BirthWeight, EaseOfCalving, GestationLength, MeasuredWeight, " +
                "LastDateWeightTaken, IntendedUse, CattleID, BreedingMethod) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setOffspringPreparedStatementValues(preparedStatement, offspring);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AppLogger.error("Error inserting offspring with CattleID: " + offspring.cattleId(), e);
            throw e;
        }
    }
}
