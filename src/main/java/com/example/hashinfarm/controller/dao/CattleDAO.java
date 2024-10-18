package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.model.FilteredCattle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CattleDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<Cattle> getCattleForHerd(int herdId) throws SQLException {
        String query = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, " +
                "sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, " +
                "dam_breed.BreedName AS DamBreedName FROM cattle c " +
                "LEFT JOIN breed ON c.BreedID = breed.BreedID " +
                "LEFT JOIN cattle sire ON c.SireID = sire.CattleID " +
                "LEFT JOIN cattle dam ON c.DamID = dam.CattleID " +
                "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID " +
                "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID " +
                "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID " +
                "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID " +
                "WHERE c.HerdID = ?";
        return getCattleByQuery(query, herdId);
    }

    public static List<Cattle> getCattleForGender(String gender) throws SQLException {
        String query = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, " +
                "sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, " +
                "dam_breed.BreedName AS DamBreedName FROM cattle c " +
                "LEFT JOIN breed ON c.BreedID = breed.BreedID " +
                "LEFT JOIN cattle sire ON c.SireID = sire.CattleID " +
                "LEFT JOIN cattle dam ON c.DamID = dam.CattleID " +
                "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID " +
                "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID " +
                "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID " +
                "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID " +
                "WHERE c.Gender = ?";
        return getCattleByQuery(query, gender);
    }
    public static Cattle getCattleByTagAndName(String tagId, String name) throws SQLException {
        String query = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, " +
                "sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, " +
                "dam_breed.BreedName AS DamBreedName FROM cattle c " +
                "LEFT JOIN breed ON c.BreedID = breed.BreedID " +
                "LEFT JOIN cattle sire ON c.SireID = sire.CattleID " +
                "LEFT JOIN cattle dam ON c.DamID = dam.CattleID " +
                "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID " +
                "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID " +
                "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID " +
                "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID " +
                "WHERE c.TagID = ? AND c.Name = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tagId);
            preparedStatement.setString(2, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCattle(resultSet);
                }
            }
        }
        return null; // Return null if no cattle found
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
        String query = "INSERT INTO cattle (TagID, HerdID, ColorMarkings, Name, Gender, DateOfBirth, WeightID, BCS, BreedID, SireID, DamID, DamsHerd, SiresHerd) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
        return -1; // Return -1 if insertion failed
    }

    private static void setCattlePreparedStatementValues(PreparedStatement preparedStatement, Cattle cattle) throws SQLException {
        preparedStatement.setString(1, cattle.getTagId());
        preparedStatement.setInt(2, cattle.getHerdId());
        preparedStatement.setString(3, cattle.getColorMarkings());
        preparedStatement.setString(4, cattle.getName());
        preparedStatement.setString(5, cattle.getGender());
        preparedStatement.setDate(6, Date.valueOf(cattle.getDateOfBirth()));
        preparedStatement.setInt(7, cattle.getWeightId());
        preparedStatement.setString(8, cattle.getBcs());
        preparedStatement.setInt(9, cattle.getBreedId());
        preparedStatement.setInt(10, cattle.getSireId());
        preparedStatement.setInt(11, cattle.getDamId());
        preparedStatement.setInt(12, cattle.getDamsHerd());
        preparedStatement.setInt(13, cattle.getSiresHerd());
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
        String query = "UPDATE cattle SET TagID=?, ColorMarkings=?, Name=?, Gender=?, DateOfBirth=?, WeightID=?, BCS=?, BreedID=?, SireID=?, DamID=?, DamsHerd=?, SiresHerd=? WHERE CattleID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cattle.getTagId());
            preparedStatement.setString(2, cattle.getColorMarkings());
            preparedStatement.setString(3, cattle.getName());
            preparedStatement.setString(4, cattle.getGender());
            preparedStatement.setDate(5, Date.valueOf(cattle.getDateOfBirth()));
            preparedStatement.setInt(6, cattle.getWeightId());
            preparedStatement.setString(7, cattle.getBcs());
            preparedStatement.setInt(8, cattle.getBreedId());
            preparedStatement.setInt(9, cattle.getSireId());
            preparedStatement.setInt(10, cattle.getDamId());
            preparedStatement.setInt(11, cattle.getDamsHerd());
            preparedStatement.setInt(12, cattle.getSiresHerd());
            preparedStatement.setInt(13, cattle.getCattleId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update cattle with ID: " + cattle.getCattleId());
            }
        } catch (SQLException e) {
            // Use AppLogger for error handling with a specific message
            AppLogger.error("Error updating cattle: " + cattle.getCattleId(), e);
            e.printStackTrace();
        }
    }

    private static Cattle mapResultSetToCattle(ResultSet resultSet) throws SQLException {
        int cattleID = resultSet.getInt("CattleID");
        String tagID = resultSet.getString("TagID");
        int herdID = resultSet.getInt("HerdID");
        String colorMarkings = resultSet.getString("ColorMarkings");
        String name = resultSet.getString("Name");
        String gender = resultSet.getString("Gender");
        LocalDate dateOfBirth = resultSet.getDate("DateOfBirth") != null ? resultSet.getDate("DateOfBirth").toLocalDate() : null;
        int weightID = resultSet.getInt("WeightID");
        String bcs = resultSet.getString("BCS");
        int breedID = resultSet.getInt("BreedID");
        String breedName = resultSet.getString("BreedName");
        int sireID = resultSet.getInt("SireID");
        String sireName = resultSet.getString("SireName");
        int damID = resultSet.getInt("DamID");
        String damName = resultSet.getString("DamName");
        int damsHerd = resultSet.getInt("DamsHerd");
        int siresHerd = resultSet.getInt("SiresHerd");
        String damHerdName = resultSet.getString("DamHerdName");
        String sireHerdName = resultSet.getString("SireHerdName");
        String sireBreedName = resultSet.getString("SireBreedName");
        String damBreedName = resultSet.getString("DamBreedName");

        // Return a new Cattle object with the retrieved data
        return new Cattle(cattleID, tagID, herdID, colorMarkings, name, gender, dateOfBirth, weightID,
                bcs, breedID, breedName, sireID, sireName, damID, damName, damsHerd, siresHerd,
                damHerdName, sireHerdName, sireBreedName, damBreedName);
    }


    public static Cattle getCattleByID(int cattleID) throws SQLException {
        String query = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, " +
                "sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, " +
                "dam_breed.BreedName AS DamBreedName FROM cattle c " +
                "LEFT JOIN breed ON c.BreedID = breed.BreedID " +
                "LEFT JOIN cattle sire ON c.SireID = sire.CattleID " +
                "LEFT JOIN cattle dam ON c.DamID = dam.CattleID " +
                "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID " +
                "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID " +
                "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID " +
                "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID " +
                "WHERE c.CattleID = ?";
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
        e.printStackTrace();
    }

    public static Cattle findCattleByBirthdateAndDamId(LocalDate calvingDate, int damId) throws SQLException {
        String query = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, " +
                "sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, " +
                "dam_breed.BreedName AS DamBreedName FROM cattle c " +
                "LEFT JOIN breed ON c.BreedID = breed.BreedID " +
                "LEFT JOIN cattle sire ON c.SireID = sire.CattleID " +
                "LEFT JOIN cattle dam ON c.DamID = dam.CattleID " +
                "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID " +
                "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID " +
                "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID " +
                "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID " +
                "WHERE c.DamID = ? AND c.DateOfBirth = ?";
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
            PreparedStatement statement = connection.prepareStatement("UPDATE Cattle SET dateOfBirth = ? WHERE CattleID = ?");
            statement.setDate(1, Date.valueOf(calvingDate));
            statement.setInt(2, cattleID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Handle any potential SQL exceptions here
            AppLogger.error("Failed to update cattle's date of birth: " + e.getMessage());
            e.printStackTrace();
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
                return cattle.getDateOfBirth();
            }
        } catch (SQLException e) {
            // Use AppLogger for error handling with a specific message
            AppLogger.error("Error fetching date of birth for cattle: " + cattleID, e);
            e.printStackTrace();
        }
        return null;
    }
    public static Cattle findCattleByBirthdateAndDamId(Connection connection, LocalDate calvingDate, int damId) throws SQLException {
        String query = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, " +
                "sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, " +
                "dam_breed.BreedName AS DamBreedName FROM cattle c " +
                "LEFT JOIN breed ON c.BreedID = breed.BreedID " +
                "LEFT JOIN cattle sire ON c.SireID = sire.CattleID " +
                "LEFT JOIN cattle dam ON c.DamID = dam.CattleID " +
                "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID " +
                "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID " +
                "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID " +
                "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID " +
                "WHERE c.DamID = ? AND c.DateOfBirth = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, damId);
            preparedStatement.setDate(2, Date.valueOf(calvingDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCattle(resultSet);
                } else {
                    return null;
                }
            }
        }
    }
    public static void deleteCattleById(Connection connection, int cattleId) throws SQLException {
        String query = "DELETE FROM cattle WHERE CattleID=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            int rowsAffected = preparedStatement.executeUpdate();
        }
    }
    public List<FilteredCattle> getAllCattleBySelectedCriteria(String selectedCriteria, String selectedValue) throws SQLException {
        List<FilteredCattle> filteredCattleList = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection()) {
            String herdQuery = "SELECT HerdID FROM herd WHERE ";

            switch (selectedCriteria) {
                case "Feed Type":
                    herdQuery += "FeedBasis = ?";
                    break;
                case "Solution Type":
                    herdQuery += "SolutionType = ?";
                    break;
                case "Breed System":
                    herdQuery += "BreedSystem = ?";
                    break;
                case "Breed Type":
                    herdQuery += "BreedType = ?";
                    break;
                case "Animal Class":
                    herdQuery += "AnimalsClass = ?";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid selected criteria: " + selectedCriteria);
            }

            try (PreparedStatement stmt = connection.prepareStatement(herdQuery)) {
                stmt.setString(1, selectedValue);

                ResultSet herdResultSet = stmt.executeQuery();

                while (herdResultSet.next()) {
                    int herdID = herdResultSet.getInt("HerdID");

                    String cattleQuery = "SELECT * FROM cattle WHERE Gender = 'Female' AND HerdID = ?";

                    try (PreparedStatement cattleStmt = connection.prepareStatement(cattleQuery)) {
                        cattleStmt.setInt(1, herdID);
                        ResultSet cattleResultSet = cattleStmt.executeQuery();

                        while (cattleResultSet.next()) {
                            FilteredCattle cattle = extractFilteredCattleFromResultSet(cattleResultSet);
                            filteredCattleList.add(cattle);
                        }
                    }
                }
            }
        }

        return filteredCattleList;
    }






    private static FilteredCattle extractFilteredCattleFromResultSet(ResultSet resultSet) throws SQLException {
        int cattleID = resultSet.getInt("CattleID");
        String tagID = resultSet.getString("TagID");
        String name = resultSet.getString("Name");
        int herdID = resultSet.getInt("HerdID");

        return new FilteredCattle(cattleID, tagID, name, herdID);
    }

    public static List<Cattle> getProgenyByCattleId(int cattleId) throws SQLException {
        List<Cattle> progenyList = new ArrayList<>();
        String query = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, " +
                "sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, " +
                "dam_breed.BreedName AS DamBreedName FROM cattle c " +
                "LEFT JOIN breed ON c.BreedID = breed.BreedID " +
                "LEFT JOIN cattle sire ON c.SireID = sire.CattleID " +
                "LEFT JOIN cattle dam ON c.DamID = dam.CattleID " +
                "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID " +
                "LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID " +
                "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID " +
                "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID " +
                "WHERE (c.SireID = ? OR c.DamID = ?) AND c.CattleID != ?";  // Exclude the cattle itself from the results
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            preparedStatement.setInt(2, cattleId);
            preparedStatement.setInt(3, cattleId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Cattle progeny = mapResultSetToCattle(resultSet);
                    // Identify dam or sire based on cattleId match
                    if (cattleId == progeny.getSireId()) {
                        progeny.setDamList(true);  // Flag this record as dam's progeny
                    } else {
                        progeny.setSireList(true);  // Flag this record as sire's progeny
                    }
                    progenyList.add(progeny);
                }
            }
        }
        return progenyList;
    }
    public static boolean updateCattleGender(int cattleId, String newGender) throws SQLException {
        String query = "UPDATE cattle SET Gender = ? WHERE CattleID = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newGender);
            preparedStatement.setInt(2, cattleId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // return true if at least one row was updated
        }
    }

}
