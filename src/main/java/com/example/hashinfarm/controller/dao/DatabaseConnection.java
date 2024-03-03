package com.example.hashinfarm.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/HashinFarm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Consider externalizing this

    private static volatile DatabaseConnection instance; // volatile for thread safety
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Log the exception or handle appropriately
            throw new IllegalStateException("MySQL JDBC driver not found", e);
        }
    }

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

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            // Log the exception or handle appropriately
            throw new IllegalStateException("Failed to establish database connection", e);
        }
        return connection;
    }


}
