package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.transferring_tables.sqlite;

import java.sql.*;
import java.util.UUID;
import com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.Function_Repository;

public class TransferVersesToSQLite {

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

            // Query Oracle for verses
            String fetchVersesQuery = "SELECT RAWTOHEX(verse_id) AS verse_id, date_created, date_updated, verse_number, RAWTOHEX(chapter_id) AS chapter_id, global_verse_number FROM table_of_verses";

            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchVersesQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {

                String insertQuery = "INSERT INTO table_of_verses (verse_id, date_created, date_updated, verse_number, chapter_id, global_verse_number) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement sqliteStmt = sqliteConnection.prepareStatement(insertQuery)) {

                    while (resultSet.next()) {
                        // Convert Oracle RAW(16) UUIDs to Java UUIDs
                        UUID verseId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("verse_id"));
                        UUID chapterId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("chapter_id"));

                        // Store UUIDs as text in SQLite
                        sqliteStmt.setString(1, verseId.toString());
                        sqliteStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        sqliteStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        sqliteStmt.setInt(4, resultSet.getInt("verse_number"));
                        sqliteStmt.setString(5, chapterId.toString());
                        sqliteStmt.setInt(6, resultSet.getInt("global_verse_number"));

                        sqliteStmt.executeUpdate();
                        System.out.println("Inserted verse: " + resultSet.getInt("verse_number") + " for chapter ID: " + chapterId);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error transferring data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
