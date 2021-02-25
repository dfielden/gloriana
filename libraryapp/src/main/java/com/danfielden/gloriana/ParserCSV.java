package com.danfielden.gloriana;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParserCSV {
    private static final boolean DRY_RUN = true;
    private static final String CSV_FILE_PATH = "original_excel_library/music_library_catalogue_csv.csv";
    private static final String DB_PATH = "dev_music_library.db";

    public static void main(String[] args) throws IOException {
        final List<LibraryEntry> excelLibrary = new ArrayList<>();

        // Parse CSV.
        try (
                Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        ) {
            for (CSVRecord csvRecord : csvParser) {
                // Accessing Values by Column Index
                String title = csvRecord.get(0);
                String composerFirstName = csvRecord.get(1);
                String composerLastName = csvRecord.get(2);
                String arranger = csvRecord.get(3);
                String voiceParts = csvRecord.get(4);
                String accompanied = csvRecord.get(5);
                String season = csvRecord.get(6);
                String seasonAdditional = csvRecord.get(7);
                String location = csvRecord.get(8);
                String collection = csvRecord.get(9);

                LibraryEntry entry = new LibraryEntry(
                        -1L,
                        title,
                        composerFirstName,
                        composerLastName,
                        arranger,
                        voiceParts,
                        convertAccompanied(accompanied),
                        season,
                        seasonAdditional,
                        location,
                        collection);

                if (entry.getTitle().isEmpty() && entry.getComposerFirstName().isEmpty() && entry.getComposerLastName().isEmpty()) {
                    continue;  // Skip blank/bad data.
                }

                excelLibrary.add(entry);
            }
        }
        System.out.println("Parsed " + excelLibrary.size() + " entries");

        // Print resulting records to screen.
        for (LibraryEntry entry : excelLibrary) {
            System.out.println(entry);
        }

        // Insert into DB.
        if (!DRY_RUN) {
            try {
                GlorianaApplication ga = new GlorianaApplication(DB_PATH);
                for (int i = 0; i < excelLibrary.size(); i++) {
                    System.out.println(excelLibrary.get(i));
                    ga.newEntry(excelLibrary.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String convertAccompanied(String excelValue) {
        if (excelValue.equalsIgnoreCase("TRUE")) {
            return "Accompanied";
        }
        if (excelValue.equalsIgnoreCase("FALSE")) {
            return "Unaccompanied";
        }
        return "Unknown";
    }
}
