package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.transferring_tables.sqlite;

import java.sql.*;
import java.util.UUID;
import com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.Function_Repository;

public class TransferVerseDatumsToSQLite {

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

            // Query Oracle for verse datums
            String fetchDatumsQuery = "SELECT RAWTOHEX(verse_datum_id) AS verse_datum_id, date_created, date_updated, RAWTOHEX(verse_id) AS verse_id, RAWTOHEX(type_of_verse_id) AS type_of_verse_id, data_of_verse_datum FROM table_of_verse_datums";

            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchDatumsQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {

                String insertQuery = "INSERT INTO table_of_verse_datums (verse_datum_id, date_created, date_updated, verse_id, type_of_verse_id, data_of_verse_datum) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement sqliteStmt = sqliteConnection.prepareStatement(insertQuery)) {

                    while (resultSet.next()) {
                        // Convert Oracle RAW(16) UUIDs to Java UUIDs
                        UUID verseDatumId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("verse_datum_id"));
                        UUID verseId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("verse_id"));
                        UUID typeOfVerseId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("type_of_verse_id"));

                        // Store UUIDs as text in SQLite
                        sqliteStmt.setString(1, verseDatumId.toString());
                        sqliteStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        sqliteStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        sqliteStmt.setString(4, verseId.toString());
                        sqliteStmt.setString(5, typeOfVerseId.toString());
                        sqliteStmt.setString(6, resultSet.getString("data_of_verse_datum"));

                        sqliteStmt.executeUpdate();
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
