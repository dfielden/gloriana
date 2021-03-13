package com.danfielden.gloriana;

import javax.annotation.Nullable;
import java.io.File;
import java.sql.*;
import java.util.*;

public final class QueryLibraryDB implements QueryLibrary {
    private final Connection connect;
    private final File file;

    public QueryLibraryDB(File file) throws Exception {
        this.file = file;
        connect = DriverManager.getConnection("jdbc:sqlite:" + file.toString(), "root", "");
        String query = "CREATE TABLE IF NOT EXISTS music_library (" +
                "id INTEGER PRIMARY KEY NOT NULL, " +
                "title TEXT, " +
                "composer_last_name TEXT, " +
                "composer_first_name TEXT, " +
                "arranger TEXT, " +
                "voice_parts TEXT, " +
                "accompanied TEXT, " +
                "season TEXT, " +
                "season_additional TEXT, " +
                "location TEXT, " +
                "collection TEXT, " +
                "deleted BOOLEAN)";
        connect.createStatement().execute(query);

        query = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY NOT NULL, " +
                "user_name TEXT UNIQUE, " +
                "password TEXT, " +
                "salt TEXT)";
        connect.createStatement().execute(query);

        query = "REPLACE INTO users (" +
                "id, user_name, password, salt) " +
                "VALUES(1, 'guest', '71d0758a9ba44213fe49943ce19124567af70e420b663b95c9676d85a13770e2dc90cf07de907ccc64636ceddb38e552a1a0d984743b1f36a447b73877012c39', 'salt1')";
        PreparedStatement stmt = connect.prepareStatement(query);
        stmt.executeUpdate();


        query = "REPLACE INTO users (" +
                "id, user_name, password, salt) " +
                "VALUES(2, 'admin', '71d0758a9ba44213fe49943ce19124567af70e420b663b95c9676d85a13770e2dbc4579ae2b3ab293213f42bb852706ea995c3b5c3987f8aa9faae5004acb3cf', 'salt2')";
        stmt = connect.prepareStatement(query);
        stmt.executeUpdate();
    }

    public synchronized void close() throws SQLException {
        connect.close();
    }

    @Override
    public synchronized void addEntry(LibraryEntry entry) throws Exception {
        if (entry.getId() != -1) {
            throw new IllegalArgumentException("Cannot insert LibraryEntry if ID already set");
        }

        String query = "INSERT INTO music_library (" +
                "title, " +
                "composer_last_name, " +
                "composer_first_name, " +
                "arranger, " +
                "voice_parts, " +
                "accompanied, " +
                "season, " +
                "season_additional, " +
                "location, " +
                "collection, " +
                "deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entry.getTitle());
            stmt.setString(2, entry.getComposerLastName());
            stmt.setString(3, entry.getComposerFirstName());
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
            long id =  rs.getLong(1);
            entry.setId(id);
        }
    }

    @Override
    public synchronized void updateEntry(LibraryEntry entry) throws Exception {
        if (getEntry(entry.getId()) == null) {
            throw new IllegalArgumentException("Cannot delete non-existent LibraryEntry with id=" + entry.getId());
        }

        String query = "UPDATE music_library SET " +
                "title = ?, " +
                "composer_last_name = ?, " +
                "composer_first_name = ?, " +
                "arranger = ?, " +
                "voice_parts = ?, " +
                "accompanied = ?, " +
                "season = ?, " +
                "season_additional = ?, " +
                "location = ?, " +
                "collection = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, entry.getTitle());
            stmt.setString(2, entry.getComposerLastName());
            stmt.setString(3, entry.getComposerFirstName());
            stmt.setString(4, entry.getArranger());
            stmt.setString(5, entry.getVoiceParts());
            stmt.setString(6, entry.getAccompanied());
            stmt.setString(7, entry.getSeason());
            stmt.setString(8, entry.getSeasonAdditional());
            stmt.setString(9, entry.getLocation());
            stmt.setString(10, entry.getCollection());
            stmt.setLong(11, entry.getId());

            // Execute SQL query.
            stmt.executeUpdate();
        }
    }

    @Override
    public synchronized void deleteEntry(long id) throws Exception {
        String query =  "UPDATE music_library SET deleted = ? WHERE id = ?";

        if (getEntry(id) == null) {
            throw new IllegalArgumentException("Cannot delete non-existent LibraryEntry with id=" + id);
        }

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setBoolean(1, true);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public synchronized Map<Long, LibraryEntry> getAllEntries() throws Exception {
        Map<Long, LibraryEntry> entries = new HashMap<>();
        String query = "SELECT * FROM music_library";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (!rs.getBoolean("deleted")) {
                    long id = rs.getLong("id");
                    String title = rs.getString("title");
                    String composerLastName = rs.getString("composer_last_name");
                    String composerFirstName = rs.getString("composer_first_name");
                    String arranger = rs.getString("arranger");
                    String voiceParts = rs.getString("voice_parts");
                    String accompanied = rs.getString("accompanied");
                    String season = rs.getString("season");
                    String seasonAdditional = rs.getString("season_additional");
                    String location = rs.getString("location");
                    String collection = rs.getString("collection");

                    LibraryEntry le = new LibraryEntry(id, title, composerLastName, composerFirstName, arranger,
                            voiceParts, accompanied, season, seasonAdditional, location, collection);
                    entries.put(le.getId(), le);
                }
            }
        }
        return entries;
    }

    @Override
    @Nullable
    public synchronized LibraryEntry getEntry(long id) throws Exception {
        String query = "SELECT * FROM music_library WHERE id = ?";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (rs.getBoolean("deleted")) {
                    return null;
                }
                String title = rs.getString("title");
                String composerLastName = rs.getString("composer_last_name");
                String composerFirstName = rs.getString("composer_first_name");
                String arranger = rs.getString("arranger");
                String voiceParts = rs.getString("voice_parts");
                String accompanied = rs.getString("accompanied");
                String season = rs.getString("season");
                String seasonAdditional = rs.getString("season_additional");
                String location = rs.getString("location");
                String collection = rs.getString("collection");

                if (rs.next()) {
                    throw new IllegalStateException("Multiple entries with id=" + id + " found");
                }
                return new LibraryEntry(id, title, composerLastName, composerFirstName, arranger,
                        voiceParts, accompanied, season, seasonAdditional, location, collection);
            }
        }
        return null;  // Not found.
    }

    @Override
    public synchronized Map<Long, LibraryEntry> searchEntries(String searchTerm) throws Exception {
        String[] searchArray = searchTerm.toLowerCase().split(" ");
        Map<Long, LibraryEntry> entries = getAllEntries();
        Set<Map.Entry<Long, LibraryEntry>> setOfEntries = entries.entrySet();
        Iterator<Map.Entry<Long, LibraryEntry>> iterator = setOfEntries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long, LibraryEntry> entry = iterator.next();
            LibraryEntry le = entry.getValue();
            if (!Search.checkLeMatchesSearch(searchArray, le)) {
                iterator.remove();
            }
        }
        return entries;
    }

    // AUTHENTICATION
    public synchronized Map<String, String> getuserDetails(String username) throws Exception {
        String salt = "";
        Map<String, String> userDetails = new HashMap<>();

        String query = "SELECT * FROM users WHERE user_name = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst() ) {
                throw new IllegalArgumentException("User not found: " + username);
            }

            while (rs.next()) {
                userDetails.put("username",rs.getString("user_name"));
                userDetails.put("hashedPassword",rs.getString("password"));
                userDetails.put("salt",rs.getString("salt"));
                userDetails.put("auth",rs.getString("auth"));
            }
        }
        return userDetails;
    }


}
