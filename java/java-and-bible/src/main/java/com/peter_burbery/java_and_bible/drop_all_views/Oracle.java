package com.peter_burbery.java_and_bible.drop_all_views;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Oracle {

    // Database connection parameters
    private static final String URL = "jdbc:oracle:thin:@localhost/PDB_FOR_BIBLE_NUMBER_001";
    private static final String USERNAME = "SCHEMA_FOR_BIBLE_NUMBER_001";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to Oracle database successfully!");

            // Query to list and drop all views in the schema
            String listViewsQuery = "SELECT view_name FROM all_views WHERE owner = ?";
            try (PreparedStatement listViewsStmt = connection.prepareStatement(listViewsQuery)) {
                listViewsStmt.setString(1, USERNAME.toUpperCase());
                try (ResultSet viewsResultSet = listViewsStmt.executeQuery()) {
                    while (viewsResultSet.next()) {
                        String viewName = viewsResultSet.getString("view_name");
                        String dropViewQuery = "DROP VIEW \"" + viewName + "\"";
                        try (PreparedStatement dropViewStmt = connection.prepareStatement(dropViewQuery)) {
                            dropViewStmt.executeUpdate();
                            System.out.println("Dropped view: " + viewName);
                        } catch (SQLException dropException) {
                            System.err.println("Error dropping view " + viewName + ": " + dropException.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error connecting to Oracle database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
