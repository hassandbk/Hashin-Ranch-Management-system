package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.data.DTOs.Herd;
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
        return executeQuery(query);
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

    // Common method to execute SQL query and return result
    private static List<Herd> executeQuery(String query) throws SQLException {
        List<Herd> herds = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Herd herd = extractHerdFromResultSet(resultSet);
                herds.add(herd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AppLogger.error("Error executing query: " + query, e);
            throw e;
        }
        return herds;
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
        preparedStatement.setString(1, herd.getName());
        preparedStatement.setInt(2, herd.getTotalAnimals());
        preparedStatement.setString(3, herd.getAnimalClass());
        preparedStatement.setString(4, herd.getBreedType());
        preparedStatement.setString(5, herd.getAgeClass());
        preparedStatement.setString(6, herd.getBreedSystem());
        preparedStatement.setString(7, herd.getSolutionType());
        preparedStatement.setString(8, herd.getFeedBasis());
        preparedStatement.setString(9, herd.getLocation());
        if (preparedStatement.getParameterMetaData().getParameterCount() == 10) {
            preparedStatement.setInt(10, herd.getId());
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
