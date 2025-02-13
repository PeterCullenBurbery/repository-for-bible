package com.peter_burbery.java_and_bible.drop_all_views;

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

            // Query to list and drop all views in the schema
            String listViewsQuery = "SELECT table_name FROM information_schema.views WHERE table_schema = ?";
            try (PreparedStatement listViewsStmt = connection.prepareStatement(listViewsQuery)) {
                listViewsStmt.setString(1, SCHEMA_NAME);
                try (ResultSet viewsResultSet = listViewsStmt.executeQuery()) {
                    while (viewsResultSet.next()) {
                        String viewName = viewsResultSet.getString("table_name");
                        String dropViewQuery = "DROP VIEW IF EXISTS " + SCHEMA_NAME + "." + viewName + " CASCADE";
                        try (PreparedStatement dropViewStmt = connection.prepareStatement(dropViewQuery)) {
                            dropViewStmt.executeUpdate();
                            System.out.println("Dropped view: " + viewName);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error connecting to PostgreSQL database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}