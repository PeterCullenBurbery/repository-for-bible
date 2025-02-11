package com.peter_burbery.simple_program;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectingToMySQLDatabase {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3307/bible_number_001?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "cullen";

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to MySQL database successfully!");

            // Query MySQL version
            String versionQuery = "SELECT VERSION()";
            try (PreparedStatement versionStmt = connection.prepareStatement(versionQuery);
                 ResultSet versionResultSet = versionStmt.executeQuery()) {
                if (versionResultSet.next()) {
                    System.out.println("MySQL Version: " + versionResultSet.getString(1));
                }
            }

            // Query current timestamp
            String timestampQuery = "SELECT CURRENT_TIMESTAMP(6)";
            try (PreparedStatement timestampStmt = connection.prepareStatement(timestampQuery);
                 ResultSet timestampResultSet = timestampStmt.executeQuery()) {
                if (timestampResultSet.next()) {
                    System.out.println("Current Timestamp: " + timestampResultSet.getString(1));
                }
            }

        } catch (Exception e) {
            System.err.println("Error connecting to MySQL database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get a connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
