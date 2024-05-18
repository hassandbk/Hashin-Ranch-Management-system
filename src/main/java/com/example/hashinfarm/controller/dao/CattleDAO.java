package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.model.Cattle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CattleDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<Cattle> getCattleForHerd(int herdId) throws SQLException {
        String query =
                "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, dam_breed.BreedName AS DamBreedName "
                        + "FROM cattle c "
                        + "LEFT JOIN breed ON c.BreedID = breed.BreedID "
                        + "LEFT JOIN cattle sire ON c.SireID = sire.CattleID "
                        + "LEFT JOIN cattle dam ON c.DamID = dam.CattleID "
                        + "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID "
                        + "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID "
                        + "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID "
                        + "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID "
                        + "WHERE c.HerdID = ?";
        return getCattleByQuery(query, herdId);
    }

    public static List<Cattle> getCattleForGender(String gender) throws SQLException {
        String query =
                "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, dam_breed.BreedName AS DamBreedName "
                        + "FROM cattle c "
                        + "LEFT JOIN breed ON c.BreedID = breed.BreedID "
                        + "LEFT JOIN cattle sire ON c.SireID = sire.CattleID "
                        + "LEFT JOIN cattle dam ON c.DamID = dam.CattleID "
                        + "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID "
                        + "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID "
                        + "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID "
                        + "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID "
                        + "WHERE c.Gender = ?";
        return getCattleByQuery(query, gender);
    }

    private static List<Cattle> getCattleByQuery(String query, Object parameter) throws SQLException {
        List<Cattle> cattleList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, parameter);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cattleList.add(mapResultSetToCattle(resultSet));
                }
            }
        }
        return cattleList;
    }

    public static String getTagIdByCattleId(int cattleId) throws SQLException {
        String tagId = null;
        String query = "SELECT TagID FROM cattle WHERE CattleID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    tagId = resultSet.getString("TagID");
                }
            }
        }
        return tagId;
    }

    public static int addCattle(Cattle cattle) throws SQLException {
        String query =
                "INSERT INTO cattle (TagID, HerdID, ColorMarkings, Name, Gender, DateOfBirth, Age, WeightID, BCS, BreedID, SireID, DamID, DamsHerd, SiresHerd) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setCattlePreparedStatementValues(preparedStatement, cattle);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated cattle ID
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
            throw e; // Re-throw the exception for higher-level handling
        }
        return -1; // Return -1 if insertion failed
    }


    private static void setCattlePreparedStatementValues(
            PreparedStatement preparedStatement, Cattle cattle) throws SQLException {
        preparedStatement.setString(1, cattle.getTagId());
        preparedStatement.setInt(2, cattle.getHerdId());
        preparedStatement.setString(3, cattle.getColorMarkings());
        preparedStatement.setString(4, cattle.getName());
        preparedStatement.setString(5, cattle.getGender());
        preparedStatement.setDate(6, Date.valueOf(String.valueOf(cattle.getDateOfBirth())));
        preparedStatement.setInt(7, cattle.getAge());
        preparedStatement.setInt(8, cattle.getWeightId());
        preparedStatement.setString(9, cattle.getBcs());
        preparedStatement.setInt(10, cattle.getBreedId());
        preparedStatement.setInt(11, cattle.getSireId());
        preparedStatement.setInt(12, cattle.getDamId());
        preparedStatement.setInt(13, cattle.getDamsHerd());
        preparedStatement.setInt(14, cattle.getSiresHerd());
    }

    public static void deleteCattleByHerdId(int herdId) throws SQLException {
        String query = "DELETE FROM cattle WHERE HerdID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, herdId);
            preparedStatement.executeUpdate();
        }
    }

    public static void deleteCattleById(int cattleId) throws SQLException {
        String query = "DELETE FROM cattle WHERE CattleID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            preparedStatement.executeUpdate();
        }
    }

    public static void updateCattle(Cattle cattle) {
        String query =
                "UPDATE cattle "
                        + "SET TagID=?, ColorMarkings=?, Name=?, Gender=?, DateOfBirth=?, Age=?, WeightID=?, BCS=?, BreedID=?, SireID=?, DamID=?, DamsHerd=?, SiresHerd=? "
                        + "WHERE CattleID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cattle.getTagId());
            preparedStatement.setString(2, cattle.getColorMarkings());
            preparedStatement.setString(3, cattle.getName());
            preparedStatement.setString(4, cattle.getGender());
            preparedStatement.setDate(5, cattle.getDateOfBirth());
            preparedStatement.setInt(6, cattle.getAge());
            preparedStatement.setInt(7, cattle.getWeightId());
            preparedStatement.setString(8, cattle.getBcs());
            preparedStatement.setInt(9, cattle.getBreedId());
            preparedStatement.setInt(10, cattle.getSireId());
            preparedStatement.setInt(11, cattle.getDamId());
            preparedStatement.setInt(12, cattle.getDamsHerd());
            preparedStatement.setInt(13, cattle.getSiresHerd());
            preparedStatement.setInt(14, cattle.getCattleId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update cattle with ID: " + cattle.getCattleId());
            }
        } catch (SQLException e) {
            // Use AppLogger for error handling with a specific message
            AppLogger.error("Error updating cattle: " + cattle.getCattleId(), e);
        }
//        catch (SQLException e) {
//            // Print error message and stack trace to console
//            e.printStackTrace();
//            System.err.println("Error updating cattle: " + cattle.getCattleId() + ": " + e.getMessage());
//        }
    }

    private static Cattle mapResultSetToCattle(ResultSet resultSet) throws SQLException {
        return new Cattle(
                resultSet.getInt("CattleID"),
                resultSet.getString("TagID"),
                resultSet.getInt("HerdID"),
                resultSet.getString("ColorMarkings"),
                resultSet.getString("Name"),
                resultSet.getString("Gender"),
                resultSet.getDate("DateOfBirth"),
                resultSet.getInt("Age"),
                resultSet.getInt("WeightID"),
                resultSet.getString("BCS"),
                resultSet.getInt("BreedID"),
                resultSet.getString("BreedName"),
                resultSet.getInt("SireID"),
                resultSet.getString("SireName"),
                resultSet.getInt("DamID"),
                resultSet.getString("DamName"),
                resultSet.getInt("DamsHerd"),
                resultSet.getInt("SiresHerd"),
                resultSet.getString("DamHerdName"),
                resultSet.getString("SireHerdName"),
                resultSet.getString("SireBreedName"),
                resultSet.getString("DamBreedName"));
    }

    public static Cattle getCattleByID(int cattleID) throws SQLException {
        String query =
                "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, dam_breed.BreedName AS DamBreedName "
                        + "FROM cattle c "
                        + "LEFT JOIN breed ON c.BreedID = breed.BreedID "
                        + "LEFT JOIN cattle sire ON c.SireID = sire.CattleID "
                        + "LEFT JOIN cattle dam ON c.DamID = dam.CattleID "
                        + "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID "
                        + "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID "
                        + "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID "
                        + "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID "
                        + "WHERE c.CattleID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCattle(resultSet);
                }
            }
        }
        return null;
    }

    private static void handleSQLException(SQLException e) {
        System.err.println("SQLException occurred: " + e.getMessage());
        // Additional error handling logic can be added as per requirements
    }

    public static Cattle findCattleByBirthdateAndDamId(LocalDate calvingDate, int damId) throws SQLException {
        String query =
                "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, dam_breed.BreedName AS DamBreedName "
                        + "FROM cattle c "
                        + "LEFT JOIN breed ON c.BreedID = breed.BreedID "
                        + "LEFT JOIN cattle sire ON c.SireID = sire.CattleID "
                        + "LEFT JOIN cattle dam ON c.DamID = dam.CattleID "
                        + "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID "
                        + "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID "
                        + "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID "
                        + "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID "
                        + "WHERE c.DamID = ? AND c.DateOfBirth = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, damId);
            preparedStatement.setDate(2, Date.valueOf(calvingDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCattle(resultSet);
                } else {
                    // No matching cattle record found, return null
                    return null;
                }
            }
        }
    }

    public static boolean updateCattleDateOfBirth(int cattleID, LocalDate calvingDate) {
        try {
            Connection connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE Cattle SET dateOfBirth = ? WHERE damID = ?");
            statement.setDate(1, Date.valueOf(calvingDate));
            statement.setInt(2, cattleID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Handle any potential SQL exceptions here
            AppLogger.error("Failed to update cattle's date of birth: " + e.getMessage());
            return false;
        }
    }

    // Fetch DateOfBirth from cattle table based on CattleID
    public LocalDate fetchDateOfBirth(int cattleID) {
        // Retrieve cattle information using CattleID
        Cattle cattle;
        try {
            cattle = CattleDAO.getCattleByID(cattleID);
            if (cattle != null && cattle.getDateOfBirth() != null) {
                return cattle.getDateOfBirth().toLocalDate();
            }
        } catch (SQLException e) {
            // Use AppLogger for error handling with a specific message
            AppLogger.error("Error fetching date of birth for cattle: " + cattleID, e);
        }
        return null;
    }

}
