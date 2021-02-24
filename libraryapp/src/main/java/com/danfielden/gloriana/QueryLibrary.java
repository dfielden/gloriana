package com.danfielden.gloriana;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * This interface defines the CRUD methods for editing the music library
 */
public interface QueryLibrary {
    /**
     * Adds a single new entry to the music library.
     */
    void addEntry(LibraryEntry entry) throws Exception;

    /**
     * Updates a single entry in the music library, identified by the id input parameter such that all parameters match
     * those of the LibraryEntry input parameter.
     */
    void updateEntry(LibraryEntry entry) throws Exception;

    /**
     * Removes a single entry from the music library as identified by the id input parameter. If the entry doesn't
     * exist, an IllegalArgumentException is thrown.
     */
    void deleteEntry(long id) throws Exception;

    /**
     * Returns an implementation of Map containing all saved Library entry IDs (key) to their respective LibraryEntry (value).
     */
    Map<Long, LibraryEntry> getAllEntries() throws Exception;

    /**
     * Returns a single LibraryEntry with id matching the input parameter or null if no matching Library entry is found.
     */
    @Nullable
    LibraryEntry getEntry(long id) throws Exception;

    /**
     * Returns an implementation of Map containing any saved Library entry IDs (key) mapped to their respective
     * LibraryEntry (value) where at least one parameter value is contained within searchTerm.
     */
    Map<Long, LibraryEntry> searchEntries(String searchTerm) throws Exception;


}
