package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.Herd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HerdDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<Herd> getAllHerds() throws SQLException {
        List<Herd> herds = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM herd";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int herdID = resultSet.getInt("HerdID");
                String name = resultSet.getString("Name");
                int totalAnimals = resultSet.getInt("TotalAnimals");
                String animalsClass = resultSet.getString("AnimalsClass");
                String breedType = resultSet.getString("BreedType");
                String ageClass = resultSet.getString("AgeClass"); // Fetch ageClass column
                String breedSystem = resultSet.getString("BreedSystem"); // Fetch breedSystem column
                String solutionType = resultSet.getString("SolutionType");
                String feedBasis = resultSet.getString("FeedBasis");
                String location = resultSet.getString("Location");
                Herd herd = new Herd(herdID, name, totalAnimals, animalsClass, breedType, ageClass, breedSystem, solutionType,  feedBasis, location,"");
                herds.add(herd);
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

        return herds;
    }
}
