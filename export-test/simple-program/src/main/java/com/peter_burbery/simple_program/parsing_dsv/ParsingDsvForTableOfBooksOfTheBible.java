package com.peter_burbery.simple_program.parsing_dsv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.peter_burbery.simple_program.Function_Repository;

public class ParsingDsvForTableOfBooksOfTheBible {

    // Oracle Database connection parameters
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521/BIBLE_APP_PDB";
    private static final String ORACLE_USERNAME = "schema_test_backup";
    private static final String ORACLE_PASSWORD = "1234";

    // File path for the DSV file
    private static final String FILE_PATH = "C:\\repository-for-bible\\export-of-tables\\table-of-books-of-the-Bible.dsv";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
             BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            System.out.println("Connected to Oracle database successfully!");

            // Skip the header line
            br.readLine();

            // Prepare the SQL insert statement
            String insertSQL = "INSERT INTO schema_test_backup.table_of_books_of_the_bible " +
                               "(book_id, date_created, date_updated, name_of_book, book_number) " +
                               "VALUES (?, TO_TIMESTAMP_TZ(?, 'YYYY-MM-DD\"T\"HH24.MI.SS.FF9TZH:TZM'), " +
                               "TO_TIMESTAMP_TZ(?, 'YYYY-MM-DD\"T\"HH24.MI.SS.FF9TZH:TZM'), ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split("\\|");

                    if (values.length == 5) {
                        // Convert UUID to RAW(16)
                        byte[] bookId = Function_Repository.convertUUIDToRaw(values[0]);

                        // Use timestamp format directly from file
                        String dateCreated = values[1]; // Oracle handles the conversion
                        String dateUpdated = values[2];

                        // Remove double quotes from the book name
                        String nameOfBook = values[3].replace("\"", "");
                        
                        // Convert book number to integer
                        int bookNumber = Integer.parseInt(values[4]);

                        // Set parameters
                        stmt.setBytes(1, bookId);
                        stmt.setString(2, dateCreated);
                        stmt.setString(3, dateUpdated);
                        stmt.setString(4, nameOfBook);
                        stmt.setInt(5, bookNumber);

                        // Execute the insert statement
                        stmt.executeUpdate();
                        System.out.println("Inserted: " + nameOfBook);
                    }
                }
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
