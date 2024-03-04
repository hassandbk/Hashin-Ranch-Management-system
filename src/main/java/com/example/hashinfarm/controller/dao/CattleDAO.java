package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.Cattle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CattleDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<Cattle> getCattleForHerd(int herdId) throws SQLException {
        String query = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, dam_breed.BreedName AS DamBreedName " +
                "FROM cattle c " +
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
        String query = "SELECT c.*, breed.BreedName AS BreedName, sire.Name AS SireName, dam.Name AS DamName, sireHerd.Name AS SireHerdName, damHerd.Name AS DamHerdName, sire_breed.BreedName AS SireBreedName, dam_breed.BreedName AS DamBreedName " +
                "FROM cattle c " +
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

    private static List<Cattle> getCattleByQuery(String query, Object parameter) throws SQLException {
        List<Cattle> cattleList = new ArrayList<>();
        try (
                Connection connection = dbConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setObject(1, parameter);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cattleList.add(mapResultSetToCattle(resultSet));
                }
            }
        }
        return cattleList;
    }

    public static void addCattle(Cattle cattle) throws SQLException {
        String query = "INSERT INTO cattle (TagID, HerdID, ColorMarkings, Name, Gender, DateOfBirth, Age, WeightID, BCS, BreedID, SireID, DamID, DamsHerd, SiresHerd) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection connection = dbConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            setCattlePreparedStatementValues(preparedStatement, cattle);
            preparedStatement.executeUpdate();
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
                resultSet.getString("DamBreedName")
        );
    }

    private static void setCattlePreparedStatementValues(PreparedStatement preparedStatement, Cattle cattle) throws SQLException {
        preparedStatement.setString(1, cattle.getTagId());
        preparedStatement.setInt(2, cattle.getHerdId());
        preparedStatement.setString(3, cattle.getColorMarkings());
        preparedStatement.setString(4, cattle.getName());
        preparedStatement.setString(5, cattle.getGender());
        preparedStatement.setDate(6, cattle.getDateOfBirth());
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


}
