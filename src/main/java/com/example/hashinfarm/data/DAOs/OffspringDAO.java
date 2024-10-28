package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.Offspring;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OffspringDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static Offspring getOffspringByCattleId(int CattleId) throws SQLException {
        String query = "SELECT * FROM offspring WHERE CattleID = ?";
        List<Offspring> offspringList = getOffspringByQuery(query, CattleId);
        return offspringList.isEmpty() ? null : offspringList.getFirst();
    }

    private static List<Offspring> getOffspringByQuery(String query, Object parameter) throws SQLException {
        List<Offspring> offspringList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setObject(1, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    offspringList.add(mapResultSetToOffspring(resultSet));
                }
            }
        }
        return offspringList;
    }



    public static void updateOffspring(Offspring offspring) throws SQLException {
        String query = "UPDATE offspring SET BirthWeight = ?, EaseOfCalving = ?, GestationLength = ?, " +
                "MeasuredWeight = ?, LastDateWeightTaken = ?, IntendedUse = ?, CattleID = ?, BreedingMethod = ? WHERE OffspringID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setOffspringPreparedStatementValues(preparedStatement, offspring);
            preparedStatement.setInt(9, offspring.getOffspringId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update offspring with ID: " + offspring.getOffspringId());
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating offspring: " + offspring.getOffspringId(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
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

    private static void setOffspringPreparedStatementValues(PreparedStatement preparedStatement, Offspring offspring) throws SQLException {
        preparedStatement.setDouble(1, offspring.getBirthWeight());
        preparedStatement.setInt(2, offspring.getEaseOfCalving());
        preparedStatement.setInt(3, offspring.getGestationLength());
        preparedStatement.setDouble(4, offspring.getMeasuredWeight());

        // Handle potential null value for date
        LocalDate lastDateWeightTaken = offspring.getLastDateWeightTaken();
        if (lastDateWeightTaken != null) {
            preparedStatement.setDate(5, java.sql.Date.valueOf(lastDateWeightTaken));
        } else {
            preparedStatement.setNull(5, java.sql.Types.DATE);
        }

        preparedStatement.setString(6, offspring.getIntendedUse());
        preparedStatement.setString(7, offspring.getCattleId());
        preparedStatement.setString(8, offspring.getBreedingMethod());
    }


    private static Offspring mapResultSetToOffspring(ResultSet resultSet) throws SQLException {
        int offspringId = resultSet.getInt("OffspringID");
        double birthWeight = resultSet.getDouble("BirthWeight");
        int easeOfCalving = resultSet.getInt("EaseOfCalving");
        int gestationLength = resultSet.getInt("GestationLength");
        double measuredWeight = resultSet.getDouble("MeasuredWeight");
        LocalDate lastDateWeightTaken = resultSet.getDate("LastDateWeightTaken") != null ? resultSet.getDate("LastDateWeightTaken").toLocalDate() : null;
        String intendedUse = resultSet.getString("IntendedUse");
        String cattleId = resultSet.getString("CattleID");
        String breedingMethod = resultSet.getString("BreedingMethod");

        return new Offspring(offspringId, birthWeight, easeOfCalving, gestationLength, measuredWeight,
                lastDateWeightTaken, intendedUse, cattleId, breedingMethod);
    }



    public static boolean hasOffspring(int cattleId) throws SQLException {
        String query = "SELECT COUNT(*) FROM offspring WHERE CattleID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // Check if there are any offspring records
                }
            }
        }
        return false; // No offspring found by default
    }


    public static void insertOffspring(Offspring offspring) throws SQLException {
        String query = "INSERT INTO offspring (BirthWeight, EaseOfCalving, GestationLength, MeasuredWeight, " +
                "LastDateWeightTaken, IntendedUse, CattleID, BreedingMethod) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setOffspringPreparedStatementValues(preparedStatement, offspring);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AppLogger.error("Error inserting offspring with CattleID: " + offspring.getCattleId(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }

}
