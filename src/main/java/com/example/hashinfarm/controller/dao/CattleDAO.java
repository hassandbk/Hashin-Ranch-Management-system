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
        List<Cattle> cattleList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
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



            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, herdId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                // Retrieve all columns including the new ones
                int cattleId = resultSet.getInt("CattleID");
                String tagId = resultSet.getString("TagID");
                int herdID = resultSet.getInt("HerdID");
                String colorMarkings = resultSet.getString("ColorMarkings");
                String name = resultSet.getString("Name");
                String gender = resultSet.getString("Gender");
                java.sql.Date dateOfBirth = resultSet.getDate("DateOfBirth");
                int age = resultSet.getInt("Age");
                int weightId = resultSet.getInt("WeightID");
                String bcs = resultSet.getString("BCS");
                int breedId = resultSet.getInt("BreedID");
                String breedName = resultSet.getString("BreedName");
                int sireId = resultSet.getInt("SireID");
                String sireName = resultSet.getString("SireName");
                int damId = resultSet.getInt("DamID");
                String damName = resultSet.getString("DamName");
                int damsHerd = resultSet.getInt("DamsHerd");
                int siresHerd = resultSet.getInt("SiresHerd");
                String damHerdName = resultSet.getString("DamHerdName");
                String sireHerdName = resultSet.getString("SireHerdName");
                String sireBreedName = resultSet.getString("SireBreedName");
                String damBreedName = resultSet.getString("DamBreedName");

                // Create Cattle object with all retrieved columns
                Cattle cattle = new Cattle(cattleId, tagId, herdID, colorMarkings, name, gender, dateOfBirth, age, weightId,
                        bcs, breedId, breedName, sireId, sireName, damId, damName, damsHerd, siresHerd,damHerdName,sireHerdName,
                        sireBreedName, damBreedName);
                cattleList.add(cattle);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
        return cattleList;
    }
}
