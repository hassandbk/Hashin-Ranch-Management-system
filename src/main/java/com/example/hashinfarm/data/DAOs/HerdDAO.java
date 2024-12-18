package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.data.DTOs.Cattle;
import com.example.hashinfarm.data.DTOs.records.Herd;
import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger; // Assuming you have an AppLogger utility for logging
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HerdDAO {

    // Method to add a new herd to the database
    public static void addHerd(Herd herd) throws SQLException {
        String query = "INSERT INTO herd (Name, TotalAnimals, AnimalsClass, BreedType, AgeClass, BreedSystem, SolutionType, FeedBasis, Location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        executeUpdate(query, herd);
    }

    // Method to retrieve all herds from the database
    public static List<Herd> getAllHerds() throws SQLException {
        String query = "SELECT * FROM herd";
        List<Herd> herds = new ArrayList<>();

        // First, load all herds into the list without animals
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Herd herd = extractHerdFromResultSet(resultSet);
                herds.add(herd); // Add the herd to the list without animals
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AppLogger.error("Error retrieving herds", e);
            throw e;
        }

        // Now, iterate over the herds list and populate the animals for each herd
        for (Herd herd : herds) {
            int herdId = herd.id();
            List<Cattle> animals = CattleDAO.getCattleByHerdId(herdId);
            herd.animals().addAll(animals); // Adds animals to the Herd's animals list
        }

        return herds;
    }


    // Method to retrieve unique names from herds in the database
    public static List<String> getUniqueNamesFromHerd() throws SQLException {
        String query = "SELECT DISTINCT Name FROM herd";
        List<String> uniqueNames = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                uniqueNames.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AppLogger.error("Error retrieving unique herd names", e);
            throw e;
        }
        return uniqueNames;
    }

    // Method to update an existing herd in the database
    public static void updateHerd(Herd herd) throws SQLException {
        String query = "UPDATE herd SET Name=?, TotalAnimals=?, AnimalsClass=?, BreedType=?, AgeClass=?, BreedSystem=?, SolutionType=?, FeedBasis=?, Location=? WHERE HerdID=?";
        executeUpdate(query, herd);
    }



    // Common method to execute SQL update
    private static void executeUpdate(String query, Herd herd) throws SQLException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            prepareStatementForHerd(preparedStatement, herd);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            AppLogger.error("Error executing update: " + query, e);
            throw e;
        }
    }

    // Method to extract Herd object from ResultSet
    private static Herd extractHerdFromResultSet(ResultSet resultSet) throws SQLException {
        int herdID = resultSet.getInt("HerdID");
        String name = resultSet.getString("Name");
        int totalAnimals = resultSet.getInt("TotalAnimals");
        String animalsClass = resultSet.getString("AnimalsClass");
        String breedType = resultSet.getString("BreedType");
        String ageClass = resultSet.getString("AgeClass");
        String breedSystem = resultSet.getString("BreedSystem");
        String solutionType = resultSet.getString("SolutionType");
        String feedBasis = resultSet.getString("FeedBasis");
        String location = resultSet.getString("Location");
        return new Herd(herdID, name, totalAnimals, animalsClass, breedType, ageClass, breedSystem, solutionType, feedBasis, location, "");
    }

    // Method to prepare statement with herd data
    private static void prepareStatementForHerd(PreparedStatement preparedStatement, Herd herd) throws SQLException {
        preparedStatement.setString(1, herd.name());
        preparedStatement.setInt(2, herd.totalAnimals());
        preparedStatement.setString(3, herd.animalClass());
        preparedStatement.setString(4, herd.breedType());
        preparedStatement.setString(5, herd.ageClass());
        preparedStatement.setString(6, herd.breedSystem());
        preparedStatement.setString(7, herd.solutionType());
        preparedStatement.setString(8, herd.feedBasis());
        preparedStatement.setString(9, herd.location());

        // Check if the ID should be set, as in an update statement
        if (preparedStatement.getParameterMetaData().getParameterCount() == 10) {
            preparedStatement.setInt(10, herd.id());
        }
    }

    public static void deleteHerd(int herdId) throws SQLException {
        // First, delete cattle records associated with the herd
        CattleDAO.deleteCattleByHerdId(herdId);

        // Now, delete the herd itself
        String query = "DELETE FROM herd WHERE HerdID=?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, herdId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            AppLogger.error("Error deleting herd with ID: " + herdId, e);
            throw e;
        }
    }

    // Method to retrieve a herd by its name
    public static Herd getHerdByName(String name) throws SQLException {
        String query = "SELECT * FROM herd WHERE Name=?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractHerdFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AppLogger.error("Error retrieving herd with name: " + name, e);
            throw e;
        }
        return null; // If no herd with the given name is found
    }



}