package com.danfielden.gloriana;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.management.Query;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

final class QueryLibraryDBTest {
    private static final Random random = new Random();

    @TempDir
    File tempDir;

    private QueryLibraryDB db;

    @BeforeEach
    public void createDbForTest() throws Exception {
        File dbFile = new File(tempDir, "test_db_" + System.currentTimeMillis() + ".sqlite");
        System.out.println("Using test DB: " + dbFile);

        db = new QueryLibraryDB(dbFile);
    }

    @AfterEach
    public void closeDb() throws SQLException {
        db.close();
    }

    @Test
    public void addEntry() throws Exception {
        // This test adds an entry, and then tests that all the relevant getById/getAll methods work.

        // Write.
        LibraryEntry entry = randomLibraryEntry();
        db.addEntry(entry);
        assertNotEquals(0L, entry.getId());  // Make sure addEntry() sets the ID.

        // Test getAllEntries().
        assertEquals(Collections.singletonMap(entry.getId(), entry), db.getAllEntries());

        // Test getEntry(long).
        assertEquals(entry, db.getEntry(entry.getId()));


    }

    @Test
    public void addMultipleEntry() throws Exception {
        // This test adds two entries, and checks that all the relevant get (etc.) methods work correctly.

        // Write.
        LibraryEntry entry1 = randomLibraryEntry();
        LibraryEntry entry2 = randomLibraryEntry();
        db.addEntry(entry1);
        db.addEntry(entry2);
        assertNotEquals(0L, entry1.getId());
        assertNotEquals(0L, entry2.getId());
        assertNotEquals(entry1.getId(), entry2.getId()); // Make sure distinct ids are set.

        // Test getAllEntries().
        Map<Long, LibraryEntry> expected = new HashMap<>();
        expected.put(entry1.getId(), entry1);
        expected.put(entry2.getId(), entry2);
        assertEquals(expected, db.getAllEntries());

        // Test getEntry(long).
        assertEquals(entry1, db.getEntry(entry1.getId()));
        assertEquals(entry2, db.getEntry(entry2.getId()));
    }

    @Test
    public void getNonExistentEntry() throws Exception {
        // Tests that getAllEntries() and getEntry(long) return empty map/null when the entry doesn't exist.

        // Test get all entries().
        Map<Long, LibraryEntry> entries = db.getAllEntries();
        assertTrue(entries.isEmpty());

        // Test getEntry(long).
        assertNull(db.getEntry(42));
        assertNull(db.getEntry(123));
    }

    @Test
    public void deleteEntry() throws Exception {
        // This test adds three entries and then deletes them, ensuring that the relevant get (etc.) methods work correctly.

        // Add entries.
        Map<Long, LibraryEntry> expected = new HashMap<>();
        LibraryEntry entry1 = randomLibraryEntry();
        db.addEntry(entry1);
        expected.put(entry1.getId(), entry1);
        LibraryEntry entry2 = randomLibraryEntry();
        db.addEntry(entry2);
        expected.put(entry2.getId(), entry2);
        LibraryEntry entry3 = randomLibraryEntry();
        db.addEntry(entry3);
        expected.put(entry3.getId(), entry3);

        // Remove entry.
        db.deleteEntry(entry1.getId());
        expected.remove(entry1.getId());
        // Test getEntry(long) - only #1 should have been deleted.
        assertNull(db.getEntry(entry1.getId()));
        assertEquals(entry2, db.getEntry(entry2.getId()));
        assertEquals(entry3, db.getEntry(entry3.getId()));
        // Test getAllEntries().
        assertEquals(expected, db.getAllEntries());

        // Remove all remaining entries and re-check getEntry(long) and getAllEntries().

        // Remove entries.
        db.deleteEntry(entry2.getId());
        expected.remove(entry2.getId());
        db.deleteEntry(entry3.getId());
        expected.remove(entry3.getId());

        // Test getEntry(long).
        assertNull(db.getEntry(entry2.getId()));
        assertNull(db.getEntry(entry3.getId()));

        // Test getAllEntries().
        assertTrue(db.getAllEntries().isEmpty());
    }

    @Test
    public void deleteEntry_doesntExist() throws Exception {
        // This test verifies that the behaviour of deleting that doesnt exist is consistent with the QueryLibrary
        // interface.
        //assertThrows(IllegalArgumentException.class, db.deleteEntry(42));

        dannyAssertThrows(IllegalArgumentException.class, () -> {
            db.deleteEntry(123);
        });
    }


    private interface RunnableThatThrows {
        void run() throws Exception;
    }

    private static void dannyAssertThrows(Class<?> expectedClass, RunnableThatThrows code) {
        try {
            code.run();
            fail("The code should have thrown " + expectedClass);
        } catch (Exception e) {
            assertEquals(expectedClass, e.getClass());
        }
    }

    private static LibraryEntry randomLibraryEntry() {
        String title = String.format("%x", random.nextLong());
        String composerFirstName = String.format("%x", random.nextLong());
        String composerLastName = String.format("%x", random.nextLong());
        String arranger = String.format("%x", random.nextLong());
        String voiceParts = String.format("%x", random.nextLong());
        String accompanied = String.format("%x", random.nextLong());
        String season = String.format("%x", random.nextLong());
        String seasonAdditional = String.format("%x", random.nextLong());
        String location = String.format("%x", random.nextLong());
        String collection = String.format("%x", random.nextLong());
        return new LibraryEntry(
                0,
                title,
                composerFirstName,
                composerLastName,
                arranger,
                voiceParts,
                accompanied,
                season,
                seasonAdditional,
                location,
                collection);
    }
}