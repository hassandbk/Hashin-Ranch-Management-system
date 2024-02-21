package com.example.hashinfarm.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/HashinFarm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Singleton instance
    private static DatabaseConnection instance;

    // Database connection
    private static Connection connection;

    // Private constructor to prevent instantiation from outside
    private DatabaseConnection() {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Open a connection
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }

    // Method to get singleton instance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    // Method to get database connection
    public static Connection getConnection() {
        return connection;
    }

    // Method to close database connection
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exceptions appropriately
            }
        }
    }
}
