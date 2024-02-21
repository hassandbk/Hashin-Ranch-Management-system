package com.example.hashinfarm.controller.dao;

import com.example.hashinfarm.model.Cattle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CattleDAO {
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public static List<Cattle> getCattleForHerd(int herdId) throws SQLException {
        List<Cattle> cattleList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            String query = "SELECT CattleID, name FROM cattle WHERE HerdID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, herdId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int cattleId = resultSet.getInt("CattleID");
                String cattleName = resultSet.getString("name");
                Cattle cattle = new Cattle(cattleId, cattleName);
                cattleList.add(cattle);
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
        return cattleList;
    }
}
