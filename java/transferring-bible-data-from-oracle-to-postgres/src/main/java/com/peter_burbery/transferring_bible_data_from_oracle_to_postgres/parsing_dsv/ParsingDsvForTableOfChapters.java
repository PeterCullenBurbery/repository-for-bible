package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.parsing_dsv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.Function_Repository;

public class ParsingDsvForTableOfChapters {

    // Oracle Database connection parameters
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521/BIBLE_APP_PDB";
    private static final String ORACLE_USERNAME = "schema_test_backup";
    private static final String ORACLE_PASSWORD = "1234";

    // File path for the DSV file
    private static final String FILE_PATH = "C:\\repository-for-bible\\export-of-tables\\table-of-chapters.dsv";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
             BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            System.out.println("Connected to Oracle database successfully!");

            // Skip the header line
            br.readLine();

            // Prepare the SQL insert statement
            String insertSQL = "INSERT INTO schema_test_backup.table_of_chapters " +
                               "(chapter_id, date_created, date_updated, chapter_number, book_id, global_chapter_number) " +
                               "VALUES (?, TO_TIMESTAMP_TZ(?, 'YYYY-MM-DD\"T\"HH24.MI.SS.FF9TZH:TZM'), " +
                               "TO_TIMESTAMP_TZ(?, 'YYYY-MM-DD\"T\"HH24.MI.SS.FF9TZH:TZM'), ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split("\\|");

                    if (values.length == 6) {
                        // Convert CHAPTER_ID and BOOK_ID to RAW(16)
                        byte[] chapterId = Function_Repository.convertUUIDToRaw(values[0]);
                        byte[] bookId = Function_Repository.convertUUIDToRaw(values[4]);

                        // Use timestamp format directly from file
                        String dateCreated = values[1];
                        String dateUpdated = values[2];

                        // Convert chapter number and global chapter number to integer
                        int chapterNumber = Integer.parseInt(values[3]);
                        int globalChapterNumber = Integer.parseInt(values[5]);

                        // Set parameters
                        stmt.setBytes(1, chapterId);
                        stmt.setString(2, dateCreated);
                        stmt.setString(3, dateUpdated);
                        stmt.setInt(4, chapterNumber);
                        stmt.setBytes(5, bookId);
                        stmt.setInt(6, globalChapterNumber);

                        // Execute the insert statement
                        stmt.executeUpdate();
                        System.out.println("Inserted chapter: " + chapterNumber + " for book ID: " + values[4]);
                    }
                }
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
