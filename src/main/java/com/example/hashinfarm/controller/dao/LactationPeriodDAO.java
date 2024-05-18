package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.LactationPeriod;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LactationPeriodDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<LactationPeriod> getAllLactationPeriods() throws SQLException {
        List<LactationPeriod> lactationPeriods = new ArrayList<>();
        Connection connection;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM lactationperiod";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int lactationPeriodID = resultSet.getInt("LactationPeriodID");
                int cattleID = resultSet.getInt("CattleID");
                Timestamp startDate = resultSet.getTimestamp("StartDate");
                Timestamp endDate = resultSet.getTimestamp("EndDate");
                int milkYield = resultSet.getInt("MilkYield");
                double relativeMilkYield = resultSet.getDouble("RelativeMilkYield");

                LactationPeriod lactationPeriod = new LactationPeriod(lactationPeriodID, cattleID, startDate, endDate, milkYield, relativeMilkYield);
                lactationPeriods.add(lactationPeriod);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
        return lactationPeriods;
    }

    public static void insertLactationPeriod(LactationPeriod lactationPeriod) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "INSERT INTO lactationperiod (CattleID, StartDate, EndDate, MilkYield, RelativeMilkYield) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lactationPeriod.getCattleID());
            preparedStatement.setTimestamp(2, lactationPeriod.getStartDate());
            preparedStatement.setTimestamp(3, lactationPeriod.getEndDate());
            preparedStatement.setInt(4, lactationPeriod.getMilkYield());
            preparedStatement.setDouble(5, lactationPeriod.getRelativeMilkYield());

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    public static void updateLactationPeriod(LactationPeriod lactationPeriod) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "UPDATE lactationperiod SET CattleID=?, StartDate=?, EndDate=?, MilkYield=?, RelativeMilkYield=? WHERE LactationPeriodID=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lactationPeriod.getCattleID());
            preparedStatement.setTimestamp(2, lactationPeriod.getStartDate());
            preparedStatement.setTimestamp(3, lactationPeriod.getEndDate());
            preparedStatement.setInt(4, lactationPeriod.getMilkYield());
            preparedStatement.setDouble(5, lactationPeriod.getRelativeMilkYield());
            preparedStatement.setInt(6, lactationPeriod.getLactationPeriodID());

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    public static void deleteLactationPeriod(int lactationPeriodID) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "DELETE FROM lactationperiod WHERE LactationPeriodID=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lactationPeriodID);

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    // Add other CRUD methods as needed
}
