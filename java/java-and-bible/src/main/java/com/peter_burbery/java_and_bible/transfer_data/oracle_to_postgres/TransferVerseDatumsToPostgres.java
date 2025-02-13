
package com.peter_burbery.java_and_bible.transfer_data.oracle_to_postgres;

import java.sql.*;
import java.util.UUID;

import com.peter_burbery.java_and_bible.utilities.Function_Repository;

public class TransferVerseDatumsToPostgres {

	// Oracle Database connection parameters
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost/PDB_FOR_BIBLE_NUMBER_001";
    private static final String ORACLE_USERNAME = "SCHEMA_FOR_BIBLE_NUMBER_001";
    private static final String ORACLE_PASSWORD = "1234";

	// PostgreSQL Database connection parameters
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/database_for_bible";
    private static final String POSTGRES_USERNAME = "postgres";
    private static final String POSTGRES_PASSWORD = "1234";
    private static final String POSTGRES_SCHEMA = "schema_for_bible";

    public static void main(String[] args) {
        try (Connection oracleConnection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
             Connection postgresConnection = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USERNAME, POSTGRES_PASSWORD)) {
            
            System.out.println("Connected to both Oracle and PostgreSQL databases successfully!");

            // Query Oracle for verse datums
            String fetchDatumsQuery = "SELECT RAWTOHEX(verse_datum_id) AS verse_datum_id, date_created, date_updated, RAWTOHEX(verse_id) AS verse_id, RAWTOHEX(type_of_verse_id) AS type_of_verse_id, data_of_verse_datum FROM table_of_verse_datums";
            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchDatumsQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {
                
                String insertQuery = "INSERT INTO " + POSTGRES_SCHEMA + ".table_of_verse_datums (verse_datum_id, date_created, date_updated, verse_id, type_of_verse_id, data_of_verse_datum) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement postgresStmt = postgresConnection.prepareStatement(insertQuery)) {
                    
                    while (resultSet.next()) {
                        UUID verseDatumId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("verse_datum_id"));
                        UUID verseId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("verse_id"));
                        UUID typeOfVerseId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("type_of_verse_id"));
                        postgresStmt.setObject(1, verseDatumId);
                        postgresStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        postgresStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        postgresStmt.setObject(4, verseId);
                        postgresStmt.setObject(5, typeOfVerseId);
                        postgresStmt.setString(6, resultSet.getString("data_of_verse_datum"));
                        postgresStmt.executeUpdate();
                        System.out.println("Inserted verse datum for verse ID: " + verseId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error transferring data: " + e.getMessage());
            e.printStackTrace();
        }
    }


}