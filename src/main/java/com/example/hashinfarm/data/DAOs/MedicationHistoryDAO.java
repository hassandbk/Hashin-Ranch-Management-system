package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.data.DTOs.records.MedicationRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicationHistoryDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<MedicationRecord> getMedicationRecordsByCattleId(int cattleId) throws SQLException {
        String query = "SELECT * FROM medicationhistory WHERE cattleId = ?";
        return getMedicationRecordsByQuery(query, cattleId);
    }

    public static MedicationRecord getMedicationRecordById(int id) throws SQLException {
        String query = "SELECT * FROM medicationhistory WHERE id = ?";
        List<MedicationRecord> medicationRecords = getMedicationRecordsByQuery(query, id);

        // Return the first result, or null if no result found
        return medicationRecords.isEmpty() ? null : medicationRecords.get(0);
    }

    private static List<MedicationRecord> getMedicationRecordsByQuery(String query, Object parameter) throws SQLException {
        List<MedicationRecord> medicationRecordList = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (parameter != null) {
                preparedStatement.setObject(1, parameter);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    medicationRecordList.add(mapResultSetToMedicationRecord(resultSet));
                }
            }
        }
        return medicationRecordList;
    }

    public static void updateMedicationRecord(MedicationRecord medicationRecord) throws SQLException {
        String query = "UPDATE medicationhistory SET cattleId = ?, dosage = ?, frequency = ?, " +
                "dateTaken = ?, nextSchedule = ?, administeredBy = ?, telNo = ?, category = ?, responseType = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setMedicationRecordPreparedStatementValues(preparedStatement, medicationRecord);
            preparedStatement.setInt(10, medicationRecord.id());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update medication record with ID: " + medicationRecord.id());
            }
        } catch (SQLException e) {
            AppLogger.error("Error updating medication record: " + medicationRecord.id(), e);
            throw e;
        }
    }

    public static void deleteMedicationRecordByCattleIdAndId(int cattleId, int id) throws SQLException {
        String query = "DELETE FROM medicationhistory WHERE id = ? AND cattleId = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, cattleId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No medication record found for cattleId: " + cattleId + " and id: " + id);
            }
        } catch (SQLException e) {
            AppLogger.error("Error deleting medication record for cattleId: " + cattleId + " and id: " + id, e);
            throw e;
        }
    }

    private static void setMedicationRecordPreparedStatementValues(PreparedStatement preparedStatement, MedicationRecord medicationRecord) throws SQLException {
        preparedStatement.setInt(1, medicationRecord.cattleId());
        preparedStatement.setString(2, medicationRecord.dosage());
        preparedStatement.setString(3, medicationRecord.frequency());
        preparedStatement.setDate(4, java.sql.Date.valueOf(medicationRecord.dateTaken()));
        preparedStatement.setDate(5, java.sql.Date.valueOf(medicationRecord.nextSchedule()));
        preparedStatement.setString(6, medicationRecord.administeredBy());
        preparedStatement.setString(7, medicationRecord.telNo());
        preparedStatement.setString(8, medicationRecord.category());
        preparedStatement.setString(9, medicationRecord.responseType());
    }

    private static MedicationRecord mapResultSetToMedicationRecord(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int cattleId = resultSet.getInt("cattleId");
        String dosage = resultSet.getString("dosage");
        String frequency = resultSet.getString("frequency");
        LocalDate dateTaken = resultSet.getDate("dateTaken").toLocalDate();
        LocalDate nextSchedule = resultSet.getDate("nextSchedule").toLocalDate();
        String administeredBy = resultSet.getString("administeredBy");
        String telNo = resultSet.getString("telNo");
        String category = resultSet.getString("category");
        String responseType = resultSet.getString("responseType");

        return new MedicationRecord(id, cattleId, dosage, frequency, dateTaken, nextSchedule, administeredBy, telNo, category, responseType);
    }

    public static void insertMedicationRecord(MedicationRecord medicationRecord) throws SQLException {
        String query = "INSERT INTO medicationhistory (cattleId, dosage, frequency, dateTaken, nextSchedule, " +
                "administeredBy, telNo, category, responseType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setMedicationRecordPreparedStatementValues(preparedStatement, medicationRecord);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AppLogger.error("Error inserting medication record with ID: " + medicationRecord.id(), e);
            throw e;
        }
    }
}
