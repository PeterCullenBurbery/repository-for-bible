package com.peter_burbery.simple_program;

import java.sql.*;
import java.util.UUID;

public class TransferChaptersToPostgres {

    // Oracle Database connection parameters
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost/BIBLE_APP_PDB";
    private static final String ORACLE_USERNAME = "BIBLE_CURLY_BRACKET";
    private static final String ORACLE_PASSWORD = "1234";

    // PostgreSQL Database connection parameters
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/bible";
    private static final String POSTGRES_USERNAME = "postgres";
    private static final String POSTGRES_PASSWORD = "1234";
    private static final String POSTGRES_SCHEMA = "bible_schema_number_001";

    public static void main(String[] args) {
        try (Connection oracleConnection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
             Connection postgresConnection = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USERNAME, POSTGRES_PASSWORD)) {
            
            System.out.println("Connected to both Oracle and PostgreSQL databases successfully!");

            // Query Oracle for chapters
            String fetchChaptersQuery = "SELECT RAWTOHEX(chapter_id) AS chapter_id, date_created, date_updated, chapter_number, RAWTOHEX(book_id) AS book_id, global_chapter_number FROM table_of_chapters";
            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchChaptersQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {
                
                String insertQuery = "INSERT INTO " + POSTGRES_SCHEMA + ".table_of_chapters (chapter_id, date_created, date_updated, chapter_number, book_id, global_chapter_number) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement postgresStmt = postgresConnection.prepareStatement(insertQuery)) {
                    
                    while (resultSet.next()) {
                        UUID chapterId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("chapter_id"));
                        UUID bookId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("book_id"));
                        postgresStmt.setObject(1, chapterId);
                        postgresStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        postgresStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        postgresStmt.setInt(4, resultSet.getInt("chapter_number"));
                        postgresStmt.setObject(5, bookId);
                        postgresStmt.setInt(6, resultSet.getInt("global_chapter_number"));
                        postgresStmt.executeUpdate();
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
