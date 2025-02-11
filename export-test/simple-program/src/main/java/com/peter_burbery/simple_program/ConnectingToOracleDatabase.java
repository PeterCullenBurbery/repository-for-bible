package com.peter_burbery.simple_program;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectingToOracleDatabase {

    // Database connection parameters
    private static final String URL = "jdbc:oracle:thin:@localhost/BIBLE_APP_PDB";
    private static final String USERNAME = "BIBLE_CURLY_BRACKET";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to Oracle database successfully!");

            // Query Oracle version
            String versionQuery = "SELECT banner FROM v$version WHERE rownum = 1";
            try (PreparedStatement versionStmt = connection.prepareStatement(versionQuery);
                 ResultSet versionResultSet = versionStmt.executeQuery()) {
                if (versionResultSet.next()) {
                    System.out.println("Oracle Version: " + versionResultSet.getString(1));
                }
            }

            // Query current timestamp
            String timestampQuery = "SELECT SYSTIMESTAMP(9) FROM DUAL";
            try (PreparedStatement timestampStmt = connection.prepareStatement(timestampQuery);
                 ResultSet timestampResultSet = timestampStmt.executeQuery()) {
                if (timestampResultSet.next()) {
                    System.out.println("Current Timestamp: " + timestampResultSet.getString(1));
                }
            }

        } catch (Exception e) {
            System.err.println("Error connecting to Oracle database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Method to get a connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
