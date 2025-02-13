package com.peter_burbery.java_and_bible.connecting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Postgres {

    // Database connection parameters
    private static final String URL = "jdbc:postgresql://localhost:5432/database_for_bible";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "1234";
    private static final String SCHEMA_NAME = "schema_for_bible";

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to PostgreSQL database successfully!");

            // Query PostgreSQL version
            String versionQuery = "SELECT version()";
            try (PreparedStatement versionStmt = connection.prepareStatement(versionQuery);
                 ResultSet versionResultSet = versionStmt.executeQuery()) {
                if (versionResultSet.next()) {
                    System.out.println("PostgreSQL Version: " + versionResultSet.getString(1));
                }
            }

            // Query current timestamp
            String timestampQuery = "SELECT CURRENT_TIMESTAMP";
            try (PreparedStatement timestampStmt = connection.prepareStatement(timestampQuery);
                 ResultSet timestampResultSet = timestampStmt.executeQuery()) {
                if (timestampResultSet.next()) {
                    System.out.println("Current Timestamp: " + timestampResultSet.getString(1));
                }
            }

            // Query to list only tables (excluding views) in the schema
            String listTablesQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = ? AND table_type = 'BASE TABLE'";
            try (PreparedStatement listTablesStmt = connection.prepareStatement(listTablesQuery)) {
                listTablesStmt.setString(1, SCHEMA_NAME);
                try (ResultSet tablesResultSet = listTablesStmt.executeQuery()) {
                    System.out.println("Tables in schema '" + SCHEMA_NAME + "':");
                    while (tablesResultSet.next()) {
                        System.out.println(tablesResultSet.getString("table_name"));
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error connecting to PostgreSQL database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get a connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}