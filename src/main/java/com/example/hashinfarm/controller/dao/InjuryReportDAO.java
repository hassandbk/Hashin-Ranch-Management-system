package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.InjuryReport;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InjuryReportDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static void insertInjuryReport(InjuryReport injuryReport) throws SQLException {
        String query = "INSERT INTO injuryreport (cattleId, dateOfOccurrence, typeOfInjury, specificBodyPart, " +
                "severity, causeOfInjury, firstAidMeasures, followUpTreatmentType, monitoringInstructions, " +
                "scheduledProcedures, followUpMedications, medicationCost, medicationHistoryId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Set common fields
            prepareStatementForInjuryReport(preparedStatement, injuryReport);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Injury report creation failed, no rows affected.");
            }

            // Get the generated ID (if applicable)
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    injuryReport.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Injury report creation failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void updateInjuryReport(InjuryReport injuryReport) throws SQLException {
        String query = "UPDATE injuryreport SET cattleId = ?, dateOfOccurrence = ?, typeOfInjury = ?, " +
                "specificBodyPart = ?, severity = ?, causeOfInjury = ?, firstAidMeasures = ?, " +
                "followUpTreatmentType = ?, monitoringInstructions = ?, scheduledProcedures = ?, " +
                "followUpMedications = ?, medicationCost = ?, medicationHistoryId = ? WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set common fields
            prepareStatementForInjuryReport(preparedStatement, injuryReport);

            // Add ID for the update query
            preparedStatement.setInt(14, injuryReport.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update injury report with ID: " + injuryReport.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void deleteInjuryReportById(int id) throws SQLException {
        String query = "DELETE FROM injuryreport WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No injury report found with ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static List<InjuryReport> getInjuryReportsByCattleId(int cattleId) throws SQLException {
        List<InjuryReport> injuryReports = new ArrayList<>();
        String query = "SELECT * FROM injuryreport WHERE cattleId = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, cattleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    injuryReports.add(mapResultSetToInjuryReport(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return injuryReports;
    }

    private static InjuryReport mapResultSetToInjuryReport(ResultSet resultSet) throws SQLException {
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

        return new InjuryReport(id, cattleId, dateOfOccurrence, typeOfInjury, specificBodyPart, severity,
                causeOfInjury, firstAidMeasures, followUpTreatmentType, monitoringInstructions,
                scheduledProcedures, followUpMedications, medicationCost, medicationHistoryId);
    }

    private static void prepareStatementForInjuryReport(PreparedStatement preparedStatement, InjuryReport injuryReport) throws SQLException {
        // Set common fields used in both insert and update queries
        preparedStatement.setObject(1, injuryReport.getCattleId());
        preparedStatement.setDate(2, Date.valueOf(injuryReport.getDateOfOccurrence()));
        preparedStatement.setString(3, injuryReport.getTypeOfInjury());
        preparedStatement.setString(4, injuryReport.getSpecificBodyPart());
        preparedStatement.setString(5, injuryReport.getSeverity());
        preparedStatement.setString(6, injuryReport.getCauseOfInjury());
        preparedStatement.setString(7, injuryReport.getFirstAidMeasures());
        preparedStatement.setString(8, injuryReport.getFollowUpTreatmentType());
        preparedStatement.setString(9, injuryReport.getMonitoringInstructions());
        preparedStatement.setString(10, injuryReport.getScheduledProcedures());
        preparedStatement.setString(11, injuryReport.getFollowUpMedications());
        preparedStatement.setBigDecimal(12, injuryReport.getMedicationCost());
        preparedStatement.setInt(13, injuryReport.getMedicationHistoryId());
    }
}
