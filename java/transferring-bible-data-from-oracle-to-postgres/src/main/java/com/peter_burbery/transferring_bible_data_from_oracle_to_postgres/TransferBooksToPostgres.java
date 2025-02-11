package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres;

import java.sql.*;
import java.util.UUID;
public class TransferBooksToPostgres {

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

            // Query Oracle for books
            String fetchBooksQuery = "SELECT RAWTOHEX(book_id) AS book_id, date_created, date_updated, name_of_book, book_number FROM table_of_books_of_the_bible";
            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchBooksQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {
                
                String insertQuery = "INSERT INTO " + POSTGRES_SCHEMA + ".table_of_books_of_the_bible (book_id, date_created, date_updated, name_of_book, book_number) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement postgresStmt = postgresConnection.prepareStatement(insertQuery)) {
                    
                    while (resultSet.next()) {
                        UUID bookId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("book_id"));
                        postgresStmt.setObject(1, bookId);
                        postgresStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        postgresStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        postgresStmt.setString(4, resultSet.getString("name_of_book"));
                        postgresStmt.setInt(5, resultSet.getInt("book_number"));
                        postgresStmt.executeUpdate();
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
