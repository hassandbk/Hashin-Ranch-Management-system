package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.MedicationHistory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicationHistoryDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<MedicationHistory> getMedicationHistoriesByCattleId(int cattleId) throws SQLException {
        String query = "SELECT * FROM medicationhistory WHERE cattleId = ?";
        return getMedicationHistoryByQuery(query, cattleId);
    }

    public static MedicationHistory getMedicationHistoryById(int id) throws SQLException {
        String query = "SELECT * FROM medicationhistory WHERE id = ?";
        List<MedicationHistory> medicationHistories = getMedicationHistoryByQuery(query, id);

        // Return the first result, or null if no result found
        return medicationHistories.isEmpty() ? null : medicationHistories.getFirst();
    }

    private static List<MedicationHistory> getMedicationHistoryByQuery(String query, Object parameter) throws SQLException {
        List<MedicationHistory> medicationHistoryList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setObject(1, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    medicationHistoryList.add(mapResultSetToMedicationHistory(resultSet));
                }
            }
        }
        return medicationHistoryList;
    }

    public static void updateMedicationHistory(MedicationHistory medicationHistory) throws SQLException {
        String query = "UPDATE medicationhistory SET cattleId = ?, dosage = ?, frequency = ?, " +
                "dateTaken = ?, nextSchedule = ?, administeredBy = ?, telNo = ?, category = ?, responseType = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setMedicationHistoryPreparedStatementValues(preparedStatement, medicationHistory);
            preparedStatement.setString(9, medicationHistory.getResponseType()); // Set responseType
            preparedStatement.setInt(10, medicationHistory.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update medication history with ID: " + medicationHistory.getId());
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating medication history: " + medicationHistory.getId(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }

    public static void deleteMedicationHistoryByCattleIdAndId(int cattleId, int id) throws SQLException {
        String query = "DELETE FROM medicationhistory WHERE id = ? AND cattleId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, cattleId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No medication history found for cattleId: " + cattleId + " and id: " + id);
            }
        } catch (SQLException e) {
            AppLogger.error("Error deleting medication history for cattleId: " + cattleId + " and id: " + id, e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }

    private static void setMedicationHistoryPreparedStatementValues(PreparedStatement preparedStatement, MedicationHistory medicationHistory) throws SQLException {
        preparedStatement.setObject(1, medicationHistory.getCattleId(), Types.INTEGER);
        preparedStatement.setString(2, medicationHistory.getDosage());
        preparedStatement.setString(3, medicationHistory.getFrequency());
        preparedStatement.setDate(4, java.sql.Date.valueOf(medicationHistory.getDateTaken()));
        preparedStatement.setDate(5, java.sql.Date.valueOf(medicationHistory.getNextSchedule()));
        preparedStatement.setString(6, medicationHistory.getAdministeredBy());
        preparedStatement.setString(7, medicationHistory.getTelNo());
        preparedStatement.setString(8, medicationHistory.getCategory());
    }

    private static MedicationHistory mapResultSetToMedicationHistory(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        Integer cattleId = resultSet.getObject("cattleId", Integer.class);
        String dosage = resultSet.getString("dosage");
        String frequency = resultSet.getString("frequency");
        LocalDate dateTaken = resultSet.getDate("dateTaken").toLocalDate();
        LocalDate nextSchedule = resultSet.getDate("nextSchedule").toLocalDate();
        String administeredBy = resultSet.getString("administeredBy");
        String telNo = resultSet.getString("telNo");
        String category = resultSet.getString("category");
        String responseType = resultSet.getString("responseType"); // Get responseType

        return new MedicationHistory(id, cattleId, dosage, frequency, dateTaken, nextSchedule, administeredBy, telNo, category, responseType); // Include responseType in constructor
    }

    public static void insertMedicationHistory(MedicationHistory medicationHistory) throws SQLException {
        String query = "INSERT INTO medicationhistory (cattleId, dosage, frequency, dateTaken, nextSchedule, " +
                "administeredBy, telNo, category, responseType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"; // Removed type
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setMedicationHistoryPreparedStatementValues(preparedStatement, medicationHistory);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AppLogger.error("Error inserting medication history with ID: " + medicationHistory.getId(), e);
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }
    }
}
