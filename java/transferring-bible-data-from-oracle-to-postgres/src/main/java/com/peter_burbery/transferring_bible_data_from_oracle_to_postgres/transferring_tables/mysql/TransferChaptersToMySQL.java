package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.transferring_tables.mysql;

import java.sql.*;
import java.util.UUID;

import com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.Function_Repository;

public class TransferChaptersToMySQL {

    // Oracle Database connection parameters
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost/BIBLE_APP_PDB";
    private static final String ORACLE_USERNAME = "BIBLE_CURLY_BRACKET";
    private static final String ORACLE_PASSWORD = "1234";

    // MySQL Database connection parameters
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3307/bible_number_001?serverTimezone=UTC";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "cullen";

    public static void main(String[] args) {
        try (Connection oracleConnection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
             Connection mysqlConnection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD)) {

            System.out.println("Connected to both Oracle and MySQL databases successfully!");

            // Query Oracle for chapters
            String fetchChaptersQuery = "SELECT RAWTOHEX(chapter_id) AS chapter_id, date_created, date_updated, chapter_number, RAWTOHEX(book_id) AS book_id, global_chapter_number FROM table_of_chapters";

            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchChaptersQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {

                String insertQuery = "INSERT INTO table_of_chapters (chapter_id, date_created, date_updated, chapter_number, book_id, global_chapter_number) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement mysqlStmt = mysqlConnection.prepareStatement(insertQuery)) {

                    while (resultSet.next()) {
                        // Convert Oracle RAW(16) UUID to Java UUID
                        UUID chapterId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("chapter_id"));
                        UUID bookId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("book_id"));

                        // Convert Java UUID to byte array for MySQL BINARY(16)
                        mysqlStmt.setBytes(1, Function_Repository.convertUUIDToBytes(chapterId));
                        mysqlStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        mysqlStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        mysqlStmt.setInt(4, resultSet.getInt("chapter_number"));
                        mysqlStmt.setBytes(5, Function_Repository.convertUUIDToBytes(bookId));
                        mysqlStmt.setInt(6, resultSet.getInt("global_chapter_number"));

                        mysqlStmt.executeUpdate();
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
