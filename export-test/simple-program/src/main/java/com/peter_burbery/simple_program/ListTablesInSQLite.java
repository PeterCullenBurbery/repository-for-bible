package com.peter_burbery.simple_program;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListTablesInSQLite {

    // SQLite database file path
    private static final String URL = "jdbc:sqlite:C:\\repository-for-bible\\database\\sqlite\\bible-number-001.db";

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL)) {
            System.out.println("Connected to SQLite database successfully!");

            // Query to fetch table names in the SQLite database
            String query = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet resultSet = stmt.executeQuery()) {

                System.out.println("Tables in SQLite database:");

                while (resultSet.next()) {
                    String tableName = resultSet.getString("name");
                    System.out.println(tableName);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error connecting to SQLite database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
