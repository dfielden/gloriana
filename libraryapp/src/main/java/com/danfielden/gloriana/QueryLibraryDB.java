package com.danfielden.gloriana;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public final class QueryLibraryDB implements QueryLibrary {
    private final Connection connect;
    private final File file;

    public QueryLibraryDB(File file) throws Exception {
        this.file = file;
        connect = DriverManager.getConnection("jdbc:sqlite:" + file.toString(), "root", "");
        String query = "CREATE TABLE IF NOT EXISTS ngc_music_library (id INTEGER PRIMARY KET NOT NULL, title TEXT, " +
                "composer_first_name TEXT, composer_last_name TEXT, arranger TEXT, voice_parts TEXT, accompanied TEXT, " +
                "season TEXT, season_additional TEXT, location TEXT, collection TEXT, deleted BOOLEAN)";
        connect.createStatement().execute(query);
    }
    @Override
    public void addEntry(LibraryEntry entry) throws Exception {
        String query = "INSERT INTO ngc_music_library (title, composer_first_name, composer_last_name, arranger, " +
                "voice_parts, accompanied, season, season_additional, location, collection) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entry.getTitle());
            stmt.setString(2, entry.getComposerFirstName());
            stmt.setString(3, entry.getComposerLastName());
            stmt.setString(4, entry.getArranger());
            stmt.setString(5, entry.getVoiceParts());
            stmt.setString(6, entry.getAccompanied());
            stmt.setString(7, entry.getSeason());
            stmt.setString(8, entry.getSeasonAdditional());
            stmt.setString(9, entry.getLocation());
            stmt.setString(10, entry.getCollection());
            stmt.setBoolean(11, false);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (!rs.next()) {
                throw new IllegalStateException("Unable to get generated key for added address");
            }
        }
    }

    @Override
    public void updateEntry(long id, LibraryEntry entry) throws Exception {
        if (id != entry.getId()) {
            throw new IllegalStateException("id: " + id + ", entry.get(id): " + entry.getId() + ". IDs do not match.");
        }
        String query = "UPDATE ngc_music_library SET (title = ?, composer_first_name = ?, composer_last_name = ?, " +
                "arranger = ?, voice_parts = ?, accompanied = ?, season = ?, season_additional = ?, location = ?, " +
                "collection = ? WHERE id = ?)";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, entry.getTitle());
            stmt.setString(2, entry.getComposerFirstName());
            stmt.setString(3, entry.getComposerLastName());
            stmt.setString(4, entry.getArranger());
            stmt.setString(5, entry.getVoiceParts());
            stmt.setString(6, entry.getAccompanied());
            stmt.setString(7, entry.getSeason());
            stmt.setString(8, entry.getSeasonAdditional());
            stmt.setString(9, entry.getLocation());
            stmt.setString(10, entry.getCollection());

            // Execute SQL query.
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteEntry(long id) throws Exception {
        String query =  "UPDATE ngc_music_library SET deleted = true WHERE id = ?)";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public Map<Long, LibraryEntry> getAllEntries() throws Exception {
        Map<Long, LibraryEntry> entries = new HashMap<>();
        String query = "SELECT * FROM ngc_music_library";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (!rs.getBoolean("deleted")) {
                    long id = rs.getLong("id");
                    String title = rs.getString("title");
                    String composerFirstName = rs.getString("composer_first_name");
                    String composerLastName = rs.getString("composer_last_name");
                    String arranger = rs.getString("arranger");
                    String voiceParts = rs.getString("voice_parts");
                    String accompanied = rs.getString("accompanied");
                    String season = rs.getString("season");
                    String seasonAdditional = rs.getString("seasonAdditional");
                    String location = rs.getString("location");
                    String collection = rs.getString("collection");

                    LibraryEntry le = new LibraryEntry(id, title, composerFirstName, composerLastName, arranger,
                            voiceParts, accompanied, season, seasonAdditional, location, collection);
                    entries.put(le.getId(), le);
                }
            }
        }
        return entries;
    }


    @Override
    public LibraryEntry getEntry(long id) throws Exception {
        LibraryEntry le = null;
        String query = "SELECT * FROM ngc_music_library WHERE id = ?";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                    String title = rs.getString("title");
                    String composerFirstName = rs.getString("composer_first_name");
                    String composerLastName = rs.getString("composer_last_name");
                    String arranger = rs.getString("arranger");
                    String voiceParts = rs.getString("voice_parts");
                    String accompanied = rs.getString("accompanied");
                    String season = rs.getString("season");
                    String seasonAdditional = rs.getString("seasonAdditional");
                    String location = rs.getString("location");
                    String collection = rs.getString("collection");

                    le = new LibraryEntry(id, title, composerFirstName, composerLastName, arranger,
                            voiceParts, accompanied, season, seasonAdditional, location, collection);
                }
            }
        return le;
    }
}
