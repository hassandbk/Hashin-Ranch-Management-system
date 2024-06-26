package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.LactationPeriod;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LactationPeriodDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<LactationPeriod> getLactationPeriodsByCattleId(int cattleId) throws SQLException {
        List<LactationPeriod> lactationPeriods = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM lactationperiod WHERE CattleID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, cattleId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int lactationPeriodID = resultSet.getInt("LactationPeriodID");
                int cattleID = resultSet.getInt("CattleID");
                LocalDate startDate = resultSet.getDate("StartDate") != null ? resultSet.getDate("StartDate").toLocalDate() : null;
                LocalDate endDate = resultSet.getDate("EndDate") != null ? resultSet.getDate("EndDate").toLocalDate() : null;
                int milkYield = resultSet.getInt("MilkYield");
                double relativeMilkYield = resultSet.getDouble("RelativeMilkYield");

                LactationPeriod lactationPeriod = new LactationPeriod(lactationPeriodID, cattleID, startDate, endDate, milkYield, relativeMilkYield);
                lactationPeriods.add(lactationPeriod);
            }
        } finally {
            // Close resources in reverse order of their creation to avoid any resource leak
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return lactationPeriods;
    }


    public static void updateLactationPeriodEndDate(int lactationPeriodID, LocalDate newEndDate) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "UPDATE lactationperiod SET EndDate = ? WHERE LactationPeriodID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, newEndDate != null ? Date.valueOf(newEndDate) : null);
            preparedStatement.setInt(2, lactationPeriodID);
            preparedStatement.executeUpdate();
        } finally {
            // Close resources in reverse order of their creation to avoid any resource leak
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static boolean updateLactationPeriodStartDate(int lactationPeriodID, LocalDate newStartDate) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = dbConnection.getConnection();
            String query = "UPDATE lactationperiod SET StartDate = ? WHERE LactationPeriodID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, newStartDate != null ? Date.valueOf(newStartDate) : null);
            preparedStatement.setInt(2, lactationPeriodID);
            int rowsUpdated = preparedStatement.executeUpdate();

            // Check if any rows were updated
            if (rowsUpdated > 0) {
                success = true;
            }
        } finally {
            // Close resources in reverse order of their creation to avoid any resource leak
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return success;
    }

    public static int getLactationIdByCattleIdAndStartDate(int cattleId, LocalDate startDate) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int lactationPeriodId = -1;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT LactationPeriodID FROM lactationperiod WHERE CattleID = ? AND StartDate = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, cattleId);
            preparedStatement.setDate(2, Date.valueOf(startDate));
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                lactationPeriodId = resultSet.getInt("LactationPeriodID");
            }
        } finally {
            // Close resources in reverse order of their creation to avoid any resource leak
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return lactationPeriodId;
    }
    public static boolean addLactationPeriod(int cattleId, LocalDate startDate) throws SQLException {
        String insertQuery = "INSERT INTO lactationperiod (CattleID, StartDate) VALUES (?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, cattleId);
            preparedStatement.setDate(2, Date.valueOf(startDate));
            int rowsInserted = preparedStatement.executeUpdate();

            return rowsInserted > 0;
        }
    }
    public static int getLactationIdByCattleIdAndStartDate(Connection connection, int cattleId, LocalDate startDate) throws SQLException {
        String query = "SELECT LactationPeriodID FROM lactationperiod WHERE CattleID = ? AND StartDate = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cattleId);
            preparedStatement.setDate(2, Date.valueOf(startDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("LactationPeriodID");
                } else {
                    return -1;
                }
            }
        }
    }
    public static void deleteLactationPeriod(Connection connection, int lactationPeriodID) throws SQLException {
        String query = "DELETE FROM lactationperiod WHERE LactationPeriodID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, lactationPeriodID);
            preparedStatement.executeUpdate();
        }
    }

}


