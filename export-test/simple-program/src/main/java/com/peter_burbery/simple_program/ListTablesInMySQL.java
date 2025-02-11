package com.peter_burbery.simple_program;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListTablesInMySQL {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3307/bible_number_001?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "cullen";
    private static final String DATABASE_NAME = "bible_number_001";

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to MySQL database successfully!");

            // Query to fetch table names in the specified database
            String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, DATABASE_NAME);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    System.out.println("Tables in database '" + DATABASE_NAME + "':");
                    while (resultSet.next()) {
                        String tableName = resultSet.getString("table_name");
                        System.out.println(tableName);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to MySQL database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
