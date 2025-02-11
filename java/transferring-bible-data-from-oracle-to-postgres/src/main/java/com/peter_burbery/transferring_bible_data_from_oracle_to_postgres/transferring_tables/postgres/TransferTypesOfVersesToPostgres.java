package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.transferring_tables.postgres;

import java.sql.*;
import java.util.UUID;

import com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.Function_Repository;

public class TransferTypesOfVersesToPostgres {

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
				Connection postgresConnection = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USERNAME,
						POSTGRES_PASSWORD)) {

			System.out.println("Connected to both Oracle and PostgreSQL databases successfully!");

			// Query Oracle for types of verses
			String fetchTypesQuery = "SELECT RAWTOHEX(type_of_verse_id) AS type_of_verse_id, date_created, date_updated, type_of_verse, description FROM table_of_types_of_verses";
			try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchTypesQuery);
					ResultSet resultSet = oracleStmt.executeQuery()) {

				String insertQuery = "INSERT INTO " + POSTGRES_SCHEMA
						+ ".table_of_types_of_verses (type_of_verse_id, date_created, date_updated, type_of_verse, description) VALUES (?, ?, ?, ?, ?)";
				try (PreparedStatement postgresStmt = postgresConnection.prepareStatement(insertQuery)) {

					while (resultSet.next()) {
						UUID typeOfVerseId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("type_of_verse_id"));
						postgresStmt.setObject(1, typeOfVerseId);
						postgresStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
						postgresStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
						postgresStmt.setString(4, resultSet.getString("type_of_verse"));
						postgresStmt.setString(5, resultSet.getString("description"));
						postgresStmt.executeUpdate();
						System.out.println("Inserted type of verse: " + resultSet.getString("type_of_verse"));
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error transferring data: " + e.getMessage());
			e.printStackTrace();
		}
	}

}