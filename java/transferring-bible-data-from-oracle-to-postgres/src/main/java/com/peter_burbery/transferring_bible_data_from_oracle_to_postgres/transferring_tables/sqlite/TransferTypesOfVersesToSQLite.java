package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.transferring_tables.sqlite;

import java.sql.*;
import java.util.UUID;
import com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.Function_Repository;

public class TransferTypesOfVersesToSQLite {

    // Oracle Database connection parameters
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost/BIBLE_APP_PDB";
    private static final String ORACLE_USERNAME = "BIBLE_CURLY_BRACKET";
    private static final String ORACLE_PASSWORD = "1234";

    // SQLite Database connection parameters
    private static final String SQLITE_URL = "jdbc:sqlite:C:\\repository-for-bible\\database\\sqlite\\bible-number-001.db";

    public static void main(String[] args) {
        try (Connection oracleConnection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
             Connection sqliteConnection = DriverManager.getConnection(SQLITE_URL)) {

            System.out.println("Connected to both Oracle and SQLite databases successfully!");

            // Query Oracle for types of verses
            String fetchTypesQuery = "SELECT RAWTOHEX(type_of_verse_id) AS type_of_verse_id, date_created, date_updated, type_of_verse, description FROM table_of_types_of_verses";

            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchTypesQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {

                String insertQuery = "INSERT INTO table_of_types_of_verses (type_of_verse_id, date_created, date_updated, type_of_verse, description) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement sqliteStmt = sqliteConnection.prepareStatement(insertQuery)) {

                    while (resultSet.next()) {
                        // Convert Oracle RAW(16) UUID to Java UUID
                        UUID typeOfVerseId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("type_of_verse_id"));

                        // Store UUIDs as text in SQLite
                        sqliteStmt.setString(1, typeOfVerseId.toString());
                        sqliteStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        sqliteStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        sqliteStmt.setString(4, resultSet.getString("type_of_verse"));
                        sqliteStmt.setString(5, resultSet.getString("description"));

                        sqliteStmt.executeUpdate();
                        System.out.println("Inserted type of verse: " + resultSet.getString("type_of_verse"));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error transferring data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
