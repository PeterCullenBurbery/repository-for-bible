package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListTablesInPostgres {

    // Database connection parameters
    private static final String URL = "jdbc:postgresql://localhost:5432/bible";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "1234";
    private static final String SCHEMA_NAME = "bible_schema_number_001";

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to PostgreSQL database successfully!");

            // Query to fetch table names in the specified schema
            String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, SCHEMA_NAME);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    System.out.println("Tables in schema '" + SCHEMA_NAME + "':");
                    while (resultSet.next()) {
                        String tableName = resultSet.getString("table_name");
                        System.out.println(tableName);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to PostgreSQL database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
