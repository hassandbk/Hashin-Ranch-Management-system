package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.data.DTOs.Cattle;
import com.example.hashinfarm.data.DTOs.records.FilteredCattle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CattleDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    private static final String BASE_QUERY = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, " +
            "dam.Name AS DamName, sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, " +
            "sire_breed.BreedName AS SireBreedName, dam_breed.BreedName AS DamBreedName " +
            "FROM cattle c LEFT JOIN breed ON c.BreedID = breed.BreedID " +
            "LEFT JOIN cattle sire ON c.SireID = sire.CattleID LEFT JOIN cattle dam ON c.DamID = dam.CattleID " +
            "LEFT JOIN herd sireHerd ON c.SiresHerd = sireHerd.HerdID LEFT JOIN herd damHerd ON c.DamsHerd = damHerd.HerdID " +
            "LEFT JOIN breed AS sire_breed ON sire.BreedID = sire_breed.BreedID " +
            "LEFT JOIN breed AS dam_breed ON dam.BreedID = dam_breed.BreedID ";

    public static List<Cattle> getCattleByHerdId(int herdId) throws SQLException {
        return getCattleList(BASE_QUERY + "WHERE c.HerdID = ?", herdId);
    }

    public static List<Cattle> getCattleForGender(String gender) throws SQLException {
        return getCattleList(BASE_QUERY + "WHERE c.Gender = ?", gender);
    }

    public static Cattle getCattleByTagAndName(String tagId, String name) throws SQLException {
        return getCattle(BASE_QUERY + "WHERE c.TagID = ? AND c.Name = ?", tagId, name);
    }

    public static Cattle getCattleByID(int cattleID) throws SQLException {
        return getCattle(BASE_QUERY + "WHERE c.CattleID = ?", cattleID);
    }



    public static List<Cattle> getProgenyByCattleId(int cattleId) throws SQLException {
        return getCattleList(BASE_QUERY + "WHERE (c.SireID = ? OR c.DamID = ?) AND c.CattleID != ?", cattleId, cattleId, cattleId);
    }
    public static Cattle findCattleByBirthdateAndDamId(LocalDate calvingDate, int damId) throws SQLException {
        return getCattle(BASE_QUERY + "WHERE c.DamID = ? AND c.DateOfBirth = ?", damId, Date.valueOf(calvingDate));
    }
    public static Cattle findCattleByBirthdateAndDamIdAtomically(Connection connection, LocalDate calvingDate, int damId) throws SQLException {
        String query = BASE_QUERY + "WHERE c.DamID = ? AND c.DateOfBirth = ?";
        try (PreparedStatement preparedStatement = (connection != null)
                ? connection.prepareStatement(query)
                : dbConnection.getConnection().prepareStatement(query)) {
            preparedStatement.setInt(1, damId);
            preparedStatement.setDate(2, Date.valueOf(calvingDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCattle(resultSet);
                }
            }
        }
        return null; // No matching cattle record found
    }
    public static int addCattle(Cattle cattle) {
        String query = "INSERT INTO cattle (TagID, HerdID, ColorMarkings, Name, Gender, DateOfBirth, WeightID, BCS, BreedID, SireID, DamID, DamsHerd, SiresHerd) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setCattlePreparedStatementValues(preparedStatement, cattle);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1 && preparedStatement.getGeneratedKeys().next()) {
                return preparedStatement.getGeneratedKeys().getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean updateCattleDateOfBirth(int cattleID, LocalDate calvingDate) {
        return executeUpdate("UPDATE Cattle SET dateOfBirth = ? WHERE CattleID = ?", Date.valueOf(calvingDate), cattleID);
    }

    public static boolean updateCattleGender(int cattleId, String newGender) {
        return executeUpdate("UPDATE cattle SET Gender = ? WHERE CattleID = ?", newGender, cattleId);
    }

    public static void updateCattle(Cattle cattle) {
        String query = "UPDATE cattle SET TagID=?, ColorMarkings=?, Name=?, Gender=?, DateOfBirth=?, WeightID=?, BCS=?, BreedID=?, SireID=?, DamID=?, DamsHerd=?, SiresHerd=? WHERE CattleID=?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setCattlePreparedStatementValues(preparedStatement, cattle);
            preparedStatement.setInt(13, cattle.getCattleId());
            if (preparedStatement.executeUpdate() == 0) {
                throw new SQLException("Failed to update cattle with ID: " + cattle.getCattleId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private static List<Cattle> getCattleList(String query, Object... params) throws SQLException {
        List<Cattle> cattleList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cattleList.add(mapResultSetToCattle(resultSet));
                }
            }
        }
        return cattleList;
    }

    private static Cattle getCattle(String query, Object... params) throws SQLException {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCattle(resultSet);
                }
            }
        }
        return null;
    }

    private static boolean executeUpdate(String query, Object... params) {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            setParameters(statement, params);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteCattleByHerdId(int herdId) throws SQLException {
        executeDelete("DELETE FROM cattle WHERE HerdID = ?", herdId);
    }

    public static void deleteCattleById(int cattleId) throws SQLException {
        executeDelete("DELETE FROM cattle WHERE CattleID = ?", cattleId);
    }

    public static void deleteCattleByIdAtomically(Connection connection, int cattleId) throws SQLException {
        executeDelete(connection, cattleId);
    }

    private static void executeDelete(String query, Object... params) throws SQLException {
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParameters(preparedStatement, params);
            preparedStatement.executeUpdate();
        }
    }

    private static void executeDelete(Connection connection, Object... params) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM cattle WHERE CattleID = ?")) {
            setParameters(preparedStatement, params);
            preparedStatement.executeUpdate();
        }
    }
    private static void setParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    private static Cattle mapResultSetToCattle(ResultSet resultSet) throws SQLException {
        return new Cattle(
                resultSet.getInt("CattleID"),
                resultSet.getString("TagID"),
                resultSet.getInt("HerdID"),
                resultSet.getString("ColorMarkings"),
                resultSet.getString("Name"),
                resultSet.getString("Gender"),
                resultSet.getDate("DateOfBirth").toLocalDate(),
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
                resultSet.getString("DamBreedName")
        );
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
            e.printStackTrace();
        }
        return null;
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




}
