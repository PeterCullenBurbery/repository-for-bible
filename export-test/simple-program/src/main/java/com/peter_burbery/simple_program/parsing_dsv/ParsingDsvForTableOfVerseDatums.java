package com.peter_burbery.simple_program.parsing_dsv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.peter_burbery.simple_program.Function_Repository;

public class ParsingDsvForTableOfVerseDatums {

	// Oracle Database connection parameters
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521/BIBLE_APP_PDB";
	private static final String ORACLE_USERNAME = "schema_test_backup";
	private static final String ORACLE_PASSWORD = "1234";

	// File path for the DSV file
	private static final String FILE_PATH = "C:\\repository-for-bible\\export-of-tables\\table-of-verse-datums.dsv";

	// Batch size
	private static final int BATCH_SIZE = 20_000;

	public static void main(String[] args) {
		try (Connection connection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
				BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

			System.out.println("Connected to Oracle database successfully!");
			connection.setAutoCommit(false); // Disable auto-commit for batch inserts

			// Skip the header line
			br.readLine();

			// Prepare the SQL insert statement
			String insertSQL = "INSERT INTO schema_test_backup.table_of_verse_datums "
					+ "(verse_datum_id, date_created, date_updated, verse_id, type_of_verse_id, data_of_verse_datum) "
					+ "VALUES (?, TO_TIMESTAMP_TZ(?, 'YYYY-MM-DD\"T\"HH24.MI.SS.FF9TZH:TZM'), "
					+ "TO_TIMESTAMP_TZ(?, 'YYYY-MM-DD\"T\"HH24.MI.SS.FF9TZH:TZM'), ?, ?, ?)";

			try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
				String line;
				int count = 0;

				while ((line = br.readLine()) != null) {
					String[] values = line.split("\\|");

					if (values.length == 6) { // Ensure correct column count
						// Convert UUIDs to RAW(16)
						byte[] verseDatumId = Function_Repository.convertUUIDToRaw(values[0]);
						byte[] verseId = Function_Repository.convertUUIDToRaw(values[3]);
						byte[] typeOfVerseId = Function_Repository.convertUUIDToRaw(values[4]);

						// Use timestamp format directly from file
						String dateCreated = values[1];
						String dateUpdated = values[2];

						// Remove double quotes from the data_of_verse_datum column
						String dataOfVerseDatum = values[5].replace("\"", "");

						// Set parameters
						stmt.setBytes(1, verseDatumId);
						stmt.setString(2, dateCreated);
						stmt.setString(3, dateUpdated);
						stmt.setBytes(4, verseId);
						stmt.setBytes(5, typeOfVerseId);
						stmt.setString(6, dataOfVerseDatum);

						stmt.addBatch();
						count++;

						// Execute batch every 20,000 rows
						if (count % BATCH_SIZE == 0) {
							stmt.executeBatch();
							connection.commit();
							System.out.println("Inserted " + count + " records so far...");
						}
					} else {
						System.err.println("Skipping malformed line: " + line);
					}
				}

				// Execute remaining records in batch
				if (count > 0) {
					stmt.executeBatch();
					connection.commit();
					System.out.println("Final batch executed. Total Records Inserted: " + count);
				}

			}

		} catch (SQLException | IOException e) {
			System.err.println("Error processing file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
