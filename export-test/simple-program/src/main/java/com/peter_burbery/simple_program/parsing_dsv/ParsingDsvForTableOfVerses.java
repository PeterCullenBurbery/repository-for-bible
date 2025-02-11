package com.peter_burbery.simple_program.parsing_dsv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.peter_burbery.simple_program.Function_Repository;

public class ParsingDsvForTableOfVerses {

    // Oracle Database connection parameters
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521/BIBLE_APP_PDB";
    private static final String ORACLE_USERNAME = "schema_test_backup";
    private static final String ORACLE_PASSWORD = "1234";

    // File path for the DSV file
    private static final String FILE_PATH = "C:\\repository-for-bible\\export-of-tables\\table-of-verses.dsv";

    // Batch size (Insert every 1000 records)
    private static final int BATCH_SIZE = 1000;

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
             BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            System.out.println("Connected to Oracle database successfully!");
            connection.setAutoCommit(false); // Disable auto-commit for batch processing

            // Skip the header line
            br.readLine();

            // Prepare the SQL insert statement
            String insertSQL = "INSERT INTO schema_test_backup.table_of_verses " +
                    "(verse_id, date_created, date_updated, verse_number, chapter_id, global_verse_number) " +
                    "VALUES (?, TO_TIMESTAMP_TZ(?, 'YYYY-MM-DD\"T\"HH24.MI.SS.FF9TZH:TZM'), " +
                    "TO_TIMESTAMP_TZ(?, 'YYYY-MM-DD\"T\"HH24.MI.SS.FF9TZH:TZM'), ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                String line;
                int count = 0;

                while ((line = br.readLine()) != null) {
                    String[] values = line.split("\\|");

                    if (values.length == 6) {
                        // Convert VERSE_ID and CHAPTER_ID to RAW(16)
                        byte[] verseId = Function_Repository.convertUUIDToRaw(values[0]);
                        byte[] chapterId = Function_Repository.convertUUIDToRaw(values[4]);

                        // Use timestamp format directly from file
                        String dateCreated = values[1];
                        String dateUpdated = values[2];

                        // Convert verse number and global verse number to integer
                        int verseNumber = Integer.parseInt(values[3]);
                        int globalVerseNumber = Integer.parseInt(values[5]);

                        // Set parameters
                        stmt.setBytes(1, verseId);
                        stmt.setString(2, dateCreated);
                        stmt.setString(3, dateUpdated);
                        stmt.setInt(4, verseNumber);
                        stmt.setBytes(5, chapterId);
                        stmt.setInt(6, globalVerseNumber);

                        stmt.addBatch(); // Add to batch
                        count++;

                        // Execute batch every 1000 rows
                        if (count % BATCH_SIZE == 0) {
                            stmt.executeBatch();
                            connection.commit(); // Commit every batch
                            System.out.println("Inserted " + count + " records so far...");
                        }
                    }
                }

                // Execute remaining records in batch
                if (count % BATCH_SIZE != 0) {
                    stmt.executeBatch();
                    connection.commit();
                }

                System.out.println("Bulk Insert Complete! Total Records Inserted: " + count);
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
