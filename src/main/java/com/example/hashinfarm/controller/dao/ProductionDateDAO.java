package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.ProductionDate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductionDateDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<ProductionDate> getAllProductionDates() throws SQLException {
        List<ProductionDate> productionDates = new ArrayList<>();
        Connection connection;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM productiondate";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int productionDateID = resultSet.getInt("ProductionDateID");
                int cattleID = resultSet.getInt("CattleID");
                Date date = resultSet.getDate("Date");

                ProductionDate productionDate = new ProductionDate(productionDateID, cattleID, date);
                productionDates.add(productionDate);
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
        return productionDates;
    }

    public static void insertProductionDate(ProductionDate productionDate) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "INSERT INTO productiondate (CattleID, Date) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, productionDate.getCattleID());
            preparedStatement.setDate(2, productionDate.getDate());

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    public static void updateProductionDate(ProductionDate productionDate) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "UPDATE productiondate SET CattleID=?, Date=? WHERE ProductionDateID=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, productionDate.getCattleID());
            preparedStatement.setDate(2, productionDate.getDate());
            preparedStatement.setInt(3, productionDate.getProductionDateID());

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }

    public static void deleteProductionDate(int productionDateID) throws SQLException {
        Connection connection;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            String query = "DELETE FROM productiondate WHERE ProductionDateID=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, productionDateID);

            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            // Connection will be closed automatically by the DatabaseConnection class
        }
    }
}
