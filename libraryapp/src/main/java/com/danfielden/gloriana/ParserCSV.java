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
    private static final String CSV_FILE_PATH = "original_excel_library/music_library_catalogue_csv.csv";
    private static final List<LibraryEntry> excelLibrary = new ArrayList<>();

    public static void main(String[] args) throws IOException {
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

                System.out.println("Record No - " + csvRecord.getRecordNumber());
                System.out.println("---------------");
                System.out.println("title : " + title);
                System.out.println("composerFirstName : " + composerFirstName);
                System.out.println("composerLastName : " + composerLastName);
                System.out.println("arranger : " + arranger);
                System.out.println("voiceParts : " + voiceParts);
                System.out.println("accompanied : " + accompanied);
                System.out.println("season : " + season);
                System.out.println("seasonAdditional : " + seasonAdditional);
                System.out.println("location : " + location);
                System.out.println("collection : " + collection);
                System.out.println("---------------\n\n");

                excelLibrary.add(new LibraryEntry(
                        -1,
                        title,
                        composerFirstName,
                        composerLastName,
                        arranger,
                        voiceParts,
                        convertAccompanied(accompanied),
                        season,
                        seasonAdditional,
                        location,
                        collection)
                );
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
