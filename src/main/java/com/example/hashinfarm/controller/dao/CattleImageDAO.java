package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.controller.utility.CattleImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CattleImageDAO {
  private final DatabaseConnection databaseConnection;

  public CattleImageDAO() {
    this.databaseConnection = DatabaseConnection.getInstance();
  }

  // Create operation - Insert a new cattle image record
  public boolean insertCattleImage(CattleImage cattleImage) {
    String query = "INSERT INTO cattleimages (cattle_id, imagePath, created_at) VALUES (?, ?, ?)";
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, cattleImage.getCattleId());
      preparedStatement.setString(2, cattleImage.getImagePath());
      preparedStatement.setTimestamp(3, cattleImage.getCreatedAt());
      int rowsAffected = preparedStatement.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      // Use AppLogger for error handling with a descriptive message
      AppLogger.error("Error inserting cattle image for cattle: " + cattleImage.getCattleId(), e);
      return false;
    }
  }


  // Read operation - Retrieve all cattle images for a specific cattle ID
  public List<CattleImage> getCattleImagesByCattleId(int cattleId) {
    List<CattleImage> cattleImages = new ArrayList<>();
    String query = "SELECT * FROM cattleimages WHERE cattle_id = ?";
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, cattleId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          CattleImage cattleImage = extractCattleImageFromResultSet(resultSet);
          cattleImages.add(cattleImage);
        }
      }
    } catch (SQLException e) {
      // Use AppLogger for error handling with a specific message
      AppLogger.error("Error fetching cattle images for cattle: " + cattleId, e);
      e.printStackTrace();
    }
    return cattleImages;
  }



  // Delete operation - Delete a cattle image record by ID
  public boolean deleteCattleImage(int imageId) {
    String query = "DELETE FROM cattleimages WHERE image_id = ?";
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, imageId);
      int rowsAffected = preparedStatement.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      // Use AppLogger for error handling with a specific message
      AppLogger.error("Error deleting cattle image: " + imageId, e);
      e.printStackTrace();
      return false;
    }
  }

  // Utility method to extract a CattleImage object from a ResultSet
  private CattleImage extractCattleImageFromResultSet(ResultSet resultSet) throws SQLException {
    CattleImage cattleImage = new CattleImage();
    cattleImage.setImageId(resultSet.getInt("image_id"));
    cattleImage.setCattleId(resultSet.getInt("cattle_id"));
    cattleImage.setImagePath(resultSet.getString("imagePath"));
    cattleImage.setCreatedAt(resultSet.getTimestamp("created_at"));
    return cattleImage;
  }

  // Read operation - Retrieve the timestamp of the latest update for cattle images

}
