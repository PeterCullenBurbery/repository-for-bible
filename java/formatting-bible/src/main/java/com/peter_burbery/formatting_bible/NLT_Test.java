package com.peter_burbery.formatting_bible;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NLT_Test {

    private static final String FILE_PATH = "C:\\repository-for-bible\\export-of-translations-with-chapter-separation\\wbt.dsv";
    private static final String SEPARATOR_CHAR = "_";  // Separator character
    private static final int SEPARATOR_LENGTH = 700;   // Length of the separator line
    private static final String TYPE_OF_VERSE = "wbt"; // Configurable type of verse

    private static final String QUERY = 
        "SELECT b.name_of_book, LPAD(c.chapter_number, 3, '0') AS chapter_number, " +
        "LPAD(v.verse_number, 3, '0') AS verse_number, d.data_of_verse_datum " +
        "FROM table_of_verse_datums d " +
        "JOIN table_of_verses v ON d.verse_id = v.verse_id " +
        "JOIN table_of_chapters c ON v.chapter_id = c.chapter_id " +
        "JOIN table_of_books_of_the_bible b ON c.book_id = b.book_id " +
        "JOIN table_of_types_of_verses t ON d.type_of_verse_id = t.type_of_verse_id " +
        "WHERE t.type_of_verse = ? " +  // Use a parameterized query
        "ORDER BY b.book_number, c.chapter_number, v.verse_number " +
        "FETCH FIRST 200000 ROWS ONLY";

    public static void main(String[] args) {
        try (Connection connection = ConnectingToDatabase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY);
             BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {

            // Set the type_of_verse dynamically
            preparedStatement.setString(1, TYPE_OF_VERSE);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Write header
                writer.write("\"NAME_OF_BOOK\"|\"CHAPTER_NUMBER\"|\"VERSE_NUMBER\"|\"TEXT_OF_VERSE\"");
                writer.newLine();

                String previousChapter = "";
                while (resultSet.next()) {
                    String bookName = resultSet.getString("name_of_book");
                    String chapterNumber = resultSet.getString("chapter_number");
                    String verseNumber = resultSet.getString("verse_number");
                    String verseText = resultSet.getString("data_of_verse_datum");

                    // Escape double quotes in verse text properly
                    verseText = verseText.replace("\"", "\"\"");

                    // Write separator if a new chapter starts
                    if (!previousChapter.equals(chapterNumber) && !previousChapter.isEmpty()) {
                        writer.write(generateSeparator());
                        writer.newLine();
                    }

                    // Write the formatted verse to file
                    writer.write(String.format("\"%s\"|\"%s\"|\"%s\"|\"%s\"", 
                            bookName, chapterNumber, verseNumber, verseText));
                    writer.newLine();

                    previousChapter = chapterNumber;
                }

                System.out.println("File successfully written to: " + FILE_PATH);
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to generate the separator dynamically
    private static String generateSeparator() {
        return SEPARATOR_CHAR.repeat(SEPARATOR_LENGTH);
    }
}
