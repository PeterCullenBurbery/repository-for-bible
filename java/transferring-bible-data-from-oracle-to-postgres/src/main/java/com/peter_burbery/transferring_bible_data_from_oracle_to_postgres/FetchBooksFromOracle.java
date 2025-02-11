package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FetchBooksFromOracle {

    // Database connection parameters
    private static final String URL = "jdbc:oracle:thin:@localhost/BIBLE_APP_PDB";
    private static final String USERNAME = "BIBLE_CURLY_BRACKET";
    private static final String PASSWORD = "1234";
    private static final int LIMIT = 10; // Limit the output to 10 entries

    public static void main(String[] args) {
        // Establish the connection and perform the query
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to Oracle database successfully!");

            // Query to fetch books
            String query = "SELECT book_id, date_created, date_updated, name_of_book, book_number FROM table_of_books_of_the_bible FETCH FIRST ? ROWS ONLY";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, LIMIT);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    System.out.println("Books from table_of_books_of_the_bible:");
                    while (resultSet.next()) {
                        String bookId = resultSet.getString("book_id");
                        String dateCreated = resultSet.getString("date_created");
                        String dateUpdated = resultSet.getString("date_updated");
                        String nameOfBook = resultSet.getString("name_of_book");
                        int bookNumber = resultSet.getInt("book_number");
                        System.out.printf("ID: %s, Created: %s, Updated: %s, Name: %s, Number: %d%n", bookId, dateCreated, dateUpdated, nameOfBook, bookNumber);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to Oracle database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}