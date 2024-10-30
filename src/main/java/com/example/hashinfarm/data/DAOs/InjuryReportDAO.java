package com.example.hashinfarm.data.DAOs;

import com.example.hashinfarm.data.DTOs.records.InjuryRecord;
import com.example.hashinfarm.app.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InjuryReportDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static int insertInjuryRecord(InjuryRecord injuryRecord) throws SQLException {
        String query = "INSERT INTO injuryreport (cattleId, dateOfOccurrence, typeOfInjury, specificBodyPart, " +
                "severity, causeOfInjury, firstAidMeasures, followUpTreatmentType, monitoringInstructions, " +
                "scheduledProcedures, followUpMedications, medicationCost, medicationHistoryId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatementForInjuryRecord(preparedStatement, injuryRecord);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Injury report creation failed, no rows affected.");
            }

            // Retrieve the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Injury report creation failed, no ID obtained.");
                }
            }
        }
    }

    public static void updateInjuryRecord(InjuryRecord injuryRecord) throws SQLException {
        String query = "UPDATE injuryreport SET cattleId = ?, dateOfOccurrence = ?, typeOfInjury = ?, " +
                "specificBodyPart = ?, severity = ?, causeOfInjury = ?, firstAidMeasures = ?, " +
                "followUpTreatmentType = ?, monitoringInstructions = ?, scheduledProcedures = ?, " +
                "followUpMedications = ?, medicationCost = ?, medicationHistoryId = ? WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            prepareStatementForInjuryRecord(preparedStatement, injuryRecord);
            preparedStatement.setInt(14, injuryRecord.id());  // Set ID for the WHERE clause

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update injury report with ID: " + injuryRecord.id());
            }
        }
    }

    public static void deleteInjuryRecordById(int id) throws SQLException {
        String query = "DELETE FROM injuryreport WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No injury report found with ID: " + id);
            }
        }
    }

    public static List<InjuryRecord> getInjuryRecordsByCattleId(int cattleId) throws SQLException {
        List<InjuryRecord> injuryRecords = new ArrayList<>();
        String query = "SELECT * FROM injuryreport WHERE cattleId = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, cattleId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    injuryRecords.add(mapResultSetToInjuryRecord(resultSet));
                }
            }
        }
        return injuryRecords;
    }

    private static InjuryRecord mapResultSetToInjuryRecord(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        Integer cattleId = resultSet.getObject("cattleId", Integer.class);
        LocalDate dateOfOccurrence = resultSet.getDate("dateOfOccurrence").toLocalDate();
        String typeOfInjury = resultSet.getString("typeOfInjury");
        String specificBodyPart = resultSet.getString("specificBodyPart");
        String severity = resultSet.getString("severity");
        String causeOfInjury = resultSet.getString("causeOfInjury");
        String firstAidMeasures = resultSet.getString("firstAidMeasures");
        String followUpTreatmentType = resultSet.getString("followUpTreatmentType");
        String monitoringInstructions = resultSet.getString("monitoringInstructions");
        String scheduledProcedures = resultSet.getString("scheduledProcedures");
        String followUpMedications = resultSet.getString("followUpMedications");
        BigDecimal medicationCost = resultSet.getBigDecimal("medicationCost");
        int medicationHistoryId = resultSet.getInt("medicationHistoryId");

        return new InjuryRecord(id, cattleId, dateOfOccurrence, typeOfInjury, specificBodyPart, severity,
                causeOfInjury, firstAidMeasures, followUpTreatmentType, monitoringInstructions,
                scheduledProcedures, followUpMedications, medicationCost, medicationHistoryId);
    }

    private static void prepareStatementForInjuryRecord(PreparedStatement preparedStatement, InjuryRecord injuryRecord) throws SQLException {
        preparedStatement.setObject(1, injuryRecord.cattleId());
        preparedStatement.setDate(2, Date.valueOf(injuryRecord.dateOfOccurrence()));
        preparedStatement.setString(3, injuryRecord.typeOfInjury());
        preparedStatement.setString(4, injuryRecord.specificBodyPart());
        preparedStatement.setString(5, injuryRecord.severity());
        preparedStatement.setString(6, injuryRecord.causeOfInjury());
        preparedStatement.setString(7, injuryRecord.firstAidMeasures());
        preparedStatement.setString(8, injuryRecord.followUpTreatmentType());
        preparedStatement.setString(9, injuryRecord.monitoringInstructions());
        preparedStatement.setString(10, injuryRecord.scheduledProcedures());
        preparedStatement.setString(11, injuryRecord.followUpMedications());
        preparedStatement.setBigDecimal(12, injuryRecord.medicationCost());
        preparedStatement.setInt(13, injuryRecord.medicationHistoryId());
    }
}
