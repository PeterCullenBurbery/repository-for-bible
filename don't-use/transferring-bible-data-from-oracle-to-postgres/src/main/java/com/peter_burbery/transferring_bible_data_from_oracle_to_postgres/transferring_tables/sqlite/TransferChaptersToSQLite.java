package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.transferring_tables.sqlite;

import java.sql.*;
import java.util.UUID;
import com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.Function_Repository;

public class TransferChaptersToSQLite {

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

            // Query Oracle for chapters
            String fetchChaptersQuery = "SELECT RAWTOHEX(chapter_id) AS chapter_id, date_created, date_updated, chapter_number, RAWTOHEX(book_id) AS book_id, global_chapter_number FROM table_of_chapters";

            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchChaptersQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {

                String insertQuery = "INSERT INTO table_of_chapters (chapter_id, date_created, date_updated, chapter_number, book_id, global_chapter_number) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement sqliteStmt = sqliteConnection.prepareStatement(insertQuery)) {

                    while (resultSet.next()) {
                        // Convert Oracle RAW(16) UUID to Java UUID
                        UUID chapterId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("chapter_id"));
                        UUID bookId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("book_id"));

                        // Store UUIDs as text in SQLite
                        sqliteStmt.setString(1, chapterId.toString());
                        sqliteStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        sqliteStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        sqliteStmt.setInt(4, resultSet.getInt("chapter_number"));
                        sqliteStmt.setString(5, bookId.toString());
                        sqliteStmt.setInt(6, resultSet.getInt("global_chapter_number"));

                        sqliteStmt.executeUpdate();
                        System.out.println("Inserted chapter: " + resultSet.getInt("chapter_number") + " for book ID: " + bookId);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error transferring data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
