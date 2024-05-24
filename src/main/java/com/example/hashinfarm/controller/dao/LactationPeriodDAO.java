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

    public static void deleteLactationPeriod(int lactationPeriodID) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "DELETE FROM lactationperiod WHERE LactationPeriodID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lactationPeriodID);
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


}


