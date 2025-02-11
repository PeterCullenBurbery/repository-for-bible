package com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.transferring_tables.mysql;

import java.sql.*;
import java.util.UUID;

import com.peter_burbery.transferring_bible_data_from_oracle_to_postgres.Function_Repository;

public class TransferVerseDatumsToMySQL {

    // Oracle Database connection parameters
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost/BIBLE_APP_PDB";
    private static final String ORACLE_USERNAME = "BIBLE_CURLY_BRACKET";
    private static final String ORACLE_PASSWORD = "1234";

    // MySQL Database connection parameters
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3307/bible_number_001?serverTimezone=UTC";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "cullen";

    public static void main(String[] args) {
        try (Connection oracleConnection = DriverManager.getConnection(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
             Connection mysqlConnection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD)) {

            System.out.println("Connected to both Oracle and MySQL databases successfully!");

            // Query Oracle for verse datums
            String fetchDatumsQuery = "SELECT RAWTOHEX(verse_datum_id) AS verse_datum_id, date_created, date_updated, RAWTOHEX(verse_id) AS verse_id, RAWTOHEX(type_of_verse_id) AS type_of_verse_id, data_of_verse_datum FROM table_of_verse_datums";

            try (PreparedStatement oracleStmt = oracleConnection.prepareStatement(fetchDatumsQuery);
                 ResultSet resultSet = oracleStmt.executeQuery()) {

                String insertQuery = "INSERT INTO table_of_verse_datums (verse_datum_id, date_created, date_updated, verse_id, type_of_verse_id, data_of_verse_datum) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement mysqlStmt = mysqlConnection.prepareStatement(insertQuery)) {

                    while (resultSet.next()) {
                        // Convert Oracle RAW(16) UUIDs to Java UUIDs
                        UUID verseDatumId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("verse_datum_id"));
                        UUID verseId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("verse_id"));
                        UUID typeOfVerseId = Function_Repository.convertOracleUUIDToJavaUUID(resultSet.getString("type_of_verse_id"));

                        // Convert Java UUIDs to byte arrays for MySQL BINARY(16)
                        mysqlStmt.setBytes(1, Function_Repository.convertUUIDToBytes(verseDatumId));
                        mysqlStmt.setTimestamp(2, resultSet.getTimestamp("date_created"));
                        mysqlStmt.setTimestamp(3, resultSet.getTimestamp("date_updated"));
                        mysqlStmt.setBytes(4, Function_Repository.convertUUIDToBytes(verseId));
                        mysqlStmt.setBytes(5, Function_Repository.convertUUIDToBytes(typeOfVerseId));
                        mysqlStmt.setString(6, resultSet.getString("data_of_verse_datum"));

                        mysqlStmt.executeUpdate();
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
