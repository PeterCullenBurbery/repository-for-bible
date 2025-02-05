package com.peter_burbery.web_curly;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CurlyVersesPrinter {

    // Query to find verses with curly brackets
    private static final String CURLY_VERSES_QUERY = """
        SELECT
            LPAD(ROWNUM, 3, '0') AS row_number,
            book_name,
            LPAD(chapter_number, 3, '0') AS chapter_number,
            LPAD(verse_number, 3, '0') AS verse_number,
            text_of_verse
        FROM web_peter_burbery
        WHERE REGEXP_LIKE(text_of_verse, '\\{[^}]+\\}');
        """;

    public static void main(String[] args) {
        try (Connection connection = ConnectingToDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(CURLY_VERSES_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\"ROW_NUMBER\"|\"BOOK_NAME\"|\"CHAPTER_NUMBER\"|\"VERSE_NUMBER\"|\"TEXT_OF_VERSE\"");

            while (resultSet.next()) {
                String rowNumber = resultSet.getString("row_number");
                String bookName = resultSet.getString("book_name");
                String chapterNumber = resultSet.getString("chapter_number");
                String verseNumber = resultSet.getString("verse_number");
                String textOfVerse = resultSet.getString("text_of_verse");

                // Print in pipe-separated format
                System.out.printf("\"%s\"|\"%s\"|\"%s\"|\"%s\"|\"%s\"%n",
                        rowNumber, bookName, chapterNumber, verseNumber, textOfVerse);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving curly verses: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
