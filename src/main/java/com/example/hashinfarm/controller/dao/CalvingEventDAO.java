package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.controller.utility.AppLogger;
import com.example.hashinfarm.model.CalvingEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalvingEventDAO {

    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<CalvingEvent> getCalvingEventsBySireOrDam(int cattleId) throws SQLException {
        String query = "SELECT ce.CalvingEventID, ce.CattleID, ce.ReproductiveVariableID, ce.OffspringID, " +
                "ce.AssistanceRequired, ce.DurationOfCalving, ce.PhysicalConditionCalf, " +
                "ce.NumberOfCalvesBorn, ce.CalvesBornAlive, ce.Stillbirths " +
                "FROM cattle c " +
                "INNER JOIN reproductivevariables rv ON rv.CalvingDate = c.DateOfBirth " +
                "LEFT JOIN calvingevents ce ON ce.CattleID = c.CattleID " +
                "WHERE (c.DamID = ? OR c.SireID = ?) AND rv.CattleID = ? " +
                "AND EXISTS (SELECT 1 FROM offspring o WHERE o.OffspringID = ce.OffspringID)";

        List<CalvingEvent> calvingEvents = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, cattleId);
            preparedStatement.setInt(2, cattleId);
            preparedStatement.setInt(3, cattleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    calvingEvents.add(mapResultSetToCalvingEvent(resultSet));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
            throw e; // Rethrow the exception for higher-level handling
        }

        return calvingEvents;
    }

    public static void updateCalvingEvent(CalvingEvent calvingEvent) throws SQLException {
        String query = "UPDATE calvingevents SET CattleID = ?, ReproductiveVariableID = ?, OffspringID = ?, " +
                "AssistanceRequired = ?, DurationOfCalving = ?, PhysicalConditionCalf = ?, " +
                "NumberOfCalvesBorn = ?, CalvesBornAlive = ?, Stillbirths = ? " +
                "WHERE CalvingEventID = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setPreparedStatementValues(preparedStatement, calvingEvent);
            preparedStatement.setInt(11, calvingEvent.getCalvingEventId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw e; // Rethrow the exception for higher-level handling
        }
    }

    public static void deleteCalvingEventById(int calvingEventId) throws SQLException {
        String query = "DELETE FROM calvingevents WHERE CalvingEventID = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, calvingEventId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            handleSQLException(e);
            throw e; // Rethrow the exception for higher-level handling
        }
    }

    private static void setPreparedStatementValues(PreparedStatement preparedStatement, CalvingEvent calvingEvent) throws SQLException {
        preparedStatement.setInt(1, calvingEvent.getCattleId());
        preparedStatement.setInt(2, calvingEvent.getReproductiveVariableId());
        preparedStatement.setInt(3, calvingEvent.getOffspringId());
        preparedStatement.setString(4, calvingEvent.getAssistanceRequired());
        preparedStatement.setObject(5, calvingEvent.getDurationOfCalving(), Types.INTEGER);
        preparedStatement.setString(6, calvingEvent.getPhysicalConditionCalf());
        preparedStatement.setInt(7, calvingEvent.getNumberOfCalvesBorn());
        preparedStatement.setInt(8, calvingEvent.getCalvesBornAlive());
        preparedStatement.setObject(9, calvingEvent.getStillbirths(), Types.INTEGER);
    }

    private static CalvingEvent mapResultSetToCalvingEvent(ResultSet resultSet) throws SQLException {
        return new CalvingEvent(
                resultSet.getInt("CalvingEventID"),
                resultSet.getInt("CattleID"),
                resultSet.getInt("ReproductiveVariableID"),
                resultSet.getInt("OffspringID"),
                resultSet.getString("AssistanceRequired"),
                resultSet.getObject("DurationOfCalving", Integer.class),
                resultSet.getString("PhysicalConditionCalf"),
                resultSet.getInt("NumberOfCalvesBorn"),
                resultSet.getInt("CalvesBornAlive"),
                resultSet.getObject("Stillbirths", Integer.class)
        );
    }



    private static void handleSQLException(SQLException e) {
        AppLogger.error("SQLException occurred: " + e.getMessage());
        // Additional error handling logic can be added as per requirements
    }




}
