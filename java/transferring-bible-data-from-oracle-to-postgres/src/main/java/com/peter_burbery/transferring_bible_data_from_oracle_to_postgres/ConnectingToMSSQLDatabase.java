package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectingToMSSQLDatabase {

    // Database connection parameters (Windows Authentication)
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bible_mssql_database_number_001;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL)) {
            System.out.println("Connected to MSSQL database successfully!");

            // Query SQL Server version
            String versionQuery = "SELECT @@VERSION";
            try (PreparedStatement versionStmt = connection.prepareStatement(versionQuery);
                 ResultSet versionResultSet = versionStmt.executeQuery()) {
                if (versionResultSet.next()) {
                    System.out.println("MSSQL Version: " + versionResultSet.getString(1));
                }
            }

            // Query current timestamp
            String timestampQuery = "SELECT SYSDATETIMEOFFSET()";
            try (PreparedStatement timestampStmt = connection.prepareStatement(timestampQuery);
                 ResultSet timestampResultSet = timestampStmt.executeQuery()) {
                if (timestampResultSet.next()) {
                    System.out.println("Current Timestamp: " + timestampResultSet.getString(1));
                }
            }

        } catch (Exception e) {
            System.err.println("Error connecting to MSSQL database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get a connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
