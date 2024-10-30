package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.records.DewormingRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DewormingHistoryDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<DewormingRecord> getDewormingHistoriesByCattleId(int cattleId) throws SQLException {
        String query = "SELECT * FROM deworminghistory WHERE cattleId = ?";
        return getDewormingHistoryByQuery(query, cattleId);
    }

    private static List<DewormingRecord> getDewormingHistoryByQuery(String query, Object parameter) throws SQLException {
        List<DewormingRecord> dewormingHistoryList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setObject(1, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    dewormingHistoryList.add(mapResultSetToDewormingRecord(resultSet));
                }
            }
        }
        return dewormingHistoryList;
    }

    public static void updateDewormingRecord(DewormingRecord dewormingRecord) throws SQLException {
        String query = "UPDATE deworminghistory SET cattleId = ?, dewormerType = ?, dosage = ?, weightAtTime = ?, " +
                "administeredBy = ?, routeOfAdministration = ?, dateOfDeworming = ?, manufacturerDetails = ?, " +
                "contactDetails = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setDewormingRecordPreparedStatementValues(preparedStatement, dewormingRecord);
            preparedStatement.setInt(10, dewormingRecord.id());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update deworming history with ID: " + dewormingRecord.id());
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating deworming history: " + dewormingRecord.id(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }

    public static void deleteDewormingRecordByCattleIdAndId(int cattleId, int id) throws SQLException {
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

    private static void setDewormingRecordPreparedStatementValues(PreparedStatement preparedStatement, DewormingRecord dewormingRecord) throws SQLException {
        preparedStatement.setObject(1, dewormingRecord.cattleId(), Types.INTEGER);
        preparedStatement.setString(2, dewormingRecord.dewormerType());
        preparedStatement.setDouble(3, dewormingRecord.dosage());
        preparedStatement.setObject(4, dewormingRecord.weightAtTime(), Types.DOUBLE);
        preparedStatement.setString(5, dewormingRecord.administeredBy());
        preparedStatement.setString(6, dewormingRecord.routeOfAdministration());
        preparedStatement.setDate(7, java.sql.Date.valueOf(dewormingRecord.dateOfDeworming()));
        preparedStatement.setString(8, dewormingRecord.manufacturerDetails());
        preparedStatement.setString(9, dewormingRecord.contactDetails());
    }

    private static DewormingRecord mapResultSetToDewormingRecord(ResultSet resultSet) throws SQLException {
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

        return new DewormingRecord(id, cattleId, dewormerType, dosage, weightAtTime, administeredBy,
                routeOfAdministration, dateOfDeworming, manufacturerDetails, contactDetails);
    }

    public static void insertDewormingRecord(DewormingRecord dewormingRecord) throws SQLException {
        String query = "INSERT INTO deworminghistory (cattleId, dewormerType, dosage, weightAtTime, " +
                "administeredBy, routeOfAdministration, dateOfDeworming, manufacturerDetails, contactDetails) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setDewormingRecordPreparedStatementValues(preparedStatement, dewormingRecord);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AppLogger.error("Error inserting deworming history with ID: " + dewormingRecord.id(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }
}
