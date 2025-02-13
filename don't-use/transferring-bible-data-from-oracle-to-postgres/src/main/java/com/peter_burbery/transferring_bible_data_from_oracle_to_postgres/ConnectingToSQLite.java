package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectingToSQLite {

    // SQLite database file path
    private static final String URL = "jdbc:sqlite:C:\\repository-for-bible\\database\\sqlite\\bible-number-001.db";

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL)) {
            System.out.println("Connected to SQLite database successfully!");

            // Query SQLite version
            String versionQuery = "SELECT sqlite_version()";
            try (PreparedStatement versionStmt = connection.prepareStatement(versionQuery);
                 ResultSet versionResultSet = versionStmt.executeQuery()) {
                if (versionResultSet.next()) {
                    System.out.println("SQLite Version: " + versionResultSet.getString(1));
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

        } catch (Exception e) {
            System.err.println("Error connecting to SQLite database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get a connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
