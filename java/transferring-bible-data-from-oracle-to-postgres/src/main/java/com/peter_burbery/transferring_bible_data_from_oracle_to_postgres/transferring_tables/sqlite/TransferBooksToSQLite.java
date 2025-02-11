package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.transferring_tables.sqlite;

import java.sql.*;
import java.util.UUID;
import com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.Function_Repository;

public class TransferBooksToSQLite {

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

            // Query Oracle for books
            String fetchBooksQuery = "SELECT RAWTOHEX(book_id) AS book_id, date_created, date_updated, name_of_book, book_number FROM table_of_books_of_the_bible";

            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchBooksQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {

                String insertQuery = "INSERT INTO table_of_books_of_the_bible (book_id, date_created, date_updated, name_of_book, book_number) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement sqliteStmt = sqliteConnection.prepareStatement(insertQuery)) {

                    while (resultSet.next()) {
                        // Convert Oracle RAW(16) UUID to Java UUID
                        UUID bookId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("book_id"));

                        // Convert Java UUID to string for SQLite storage
                        sqliteStmt.setString(1, bookId.toString());
                        sqliteStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        sqliteStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        sqliteStmt.setString(4, resultSet.getString("name_of_book"));
                        sqliteStmt.setInt(5, resultSet.getInt("book_number"));

                        sqliteStmt.executeUpdate();
                        System.out.println("Inserted: " + resultSet.getString("name_of_book"));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error transferring data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
