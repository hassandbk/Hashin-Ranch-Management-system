package com.example.hashinfarm.controller.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.hashinfarm.model.Breed;

public class BreedDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<Breed> getAllBreeds() throws SQLException {
        List<Breed> breeds = new ArrayList<>();
        Connection connection;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM breed";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int breedId = resultSet.getInt("BreedID");
                String breedName = resultSet.getString("BreedName");
                String origin = resultSet.getString("Origin");
                boolean recognition = resultSet.getBoolean("Recognition");
                String comments = resultSet.getString("Comments");

                Breed breed = new Breed(breedId, breedName, origin, recognition, comments);
                breeds.add(breed);
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
        return breeds;
    }

    public static void insertBreed(Breed breed) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "INSERT INTO breed (BreedName, Origin, Recognition, Comments) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, breed.getBreedName());
            preparedStatement.setString(2, breed.getOrigin());
            preparedStatement.setBoolean(3, breed.isRecognition());
            preparedStatement.setString(4, breed.getComments());

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }



}
