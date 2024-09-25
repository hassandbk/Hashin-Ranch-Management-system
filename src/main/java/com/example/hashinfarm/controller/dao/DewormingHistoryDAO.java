package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.model.DewormingHistory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DewormingHistoryDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static DewormingHistory getDewormingHistoryById(int id) throws SQLException {
        String query = "SELECT * FROM deworminghistory WHERE id = ?";
        List<DewormingHistory> dewormingHistoryList = getDewormingHistoryByQuery(query, id);
        return dewormingHistoryList.isEmpty() ? null : dewormingHistoryList.getFirst();
    }
    public static List<DewormingHistory> getDewormingHistoriesByCattleId(int cattleId) throws SQLException {
        String query = "SELECT * FROM deworminghistory WHERE cattleId = ?";
        return getDewormingHistoryByQuery(query, cattleId);
    }

    private static List<DewormingHistory> getDewormingHistoryByQuery(String query, Object parameter) throws SQLException {
        List<DewormingHistory> dewormingHistoryList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setObject(1, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    dewormingHistoryList.add(mapResultSetToDewormingHistory(resultSet));
                }
            }
        }
        return dewormingHistoryList;
    }

    public static void updateDewormingHistory(DewormingHistory dewormingHistory) throws SQLException {
        String query = "UPDATE deworminghistory SET cattleId = ?, dewormerType = ?, dosage = ?, weightAtTime = ?, " +
                "administeredBy = ?, routeOfAdministration = ?, dateOfDeworming = ?, manufacturerDetails = ?, " +
                "contactDetails = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setDewormingHistoryPreparedStatementValues(preparedStatement, dewormingHistory);
            preparedStatement.setInt(10, dewormingHistory.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update deworming history with ID: " + dewormingHistory.getId());
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating deworming history: " + dewormingHistory.getId(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }

    public static void deleteDewormingHistoryByCattleIdAndId(int cattleId, int id) throws SQLException {
        String query = "DELETE FROM deworminghistory WHERE id = ? AND cattleId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, cattleId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No deworming history found for cattleId: " + cattleId + " and id: " + id);
            }
        } catch (SQLException e) {
            AppLogger.error("Error deleting deworming history for cattleId: " + cattleId + " and id: " + id, e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }


    private static void setDewormingHistoryPreparedStatementValues(PreparedStatement preparedStatement, DewormingHistory dewormingHistory) throws SQLException {
        preparedStatement.setObject(1, dewormingHistory.getCattleId(), Types.INTEGER);
        preparedStatement.setString(2, dewormingHistory.getDewormerType());
        preparedStatement.setDouble(3, dewormingHistory.getDosage());
        preparedStatement.setObject(4, dewormingHistory.getWeightAtTime(), Types.DOUBLE);
        preparedStatement.setString(5, dewormingHistory.getAdministeredBy());
        preparedStatement.setString(6, dewormingHistory.getRouteOfAdministration());
        preparedStatement.setDate(7, java.sql.Date.valueOf(dewormingHistory.getDateOfDeworming()));
        preparedStatement.setString(8, dewormingHistory.getManufacturerDetails());
        preparedStatement.setString(9, dewormingHistory.getContactDetails());
    }

    private static DewormingHistory mapResultSetToDewormingHistory(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        Integer cattleId = resultSet.getObject("cattleId", Integer.class);
        String dewormerType = resultSet.getString("dewormerType");
        double dosage = resultSet.getDouble("dosage");
        Double weightAtTime = resultSet.getObject("weightAtTime", Double.class);
        String administeredBy = resultSet.getString("administeredBy");
        String routeOfAdministration = resultSet.getString("routeOfAdministration");
        LocalDate dateOfDeworming = resultSet.getDate("dateOfDeworming").toLocalDate();
        String manufacturerDetails = resultSet.getString("manufacturerDetails");
        String contactDetails = resultSet.getString("contactDetails");

        return new DewormingHistory(id, cattleId, dewormerType, dosage, weightAtTime, administeredBy,
                routeOfAdministration, dateOfDeworming, manufacturerDetails, contactDetails);
    }



    public static void insertDewormingHistory(DewormingHistory dewormingHistory) throws SQLException {
        String query = "INSERT INTO deworminghistory (cattleId, dewormerType, dosage, weightAtTime, " +
                "administeredBy, routeOfAdministration, dateOfDeworming, manufacturerDetails, contactDetails) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setDewormingHistoryPreparedStatementValues(preparedStatement, dewormingHistory);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AppLogger.error("Error inserting deworming history with ID: " + dewormingHistory.getId(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }
}
