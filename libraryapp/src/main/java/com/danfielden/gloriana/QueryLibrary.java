package com.danfielden.gloriana;

/*
This interface defines the CRUD methods for editing the music library
 */

import java.util.Map;

public interface QueryLibrary {
    /**
     * Adds a single new entry to the music library.
     */
    void addEntry(LibraryEntry entry) throws Exception;

    /*
    Updates a single entry in the music library, identified by the id input parameter such that all parameters match
    those of the LibraryEntry input parameter.
    */
    void updateEntry(long id, LibraryEntry entry) throws Exception;

    /*
    Removes a single entry from the music library as identified by the id input parameter.
    */
    void deleteEntry(long id) throws Exception;

    /*
    Returns an implementation of Map containing all saved Library entry IDs (key) to their respective LibraryEntry (value).
     */
    Map<Long, LibraryEntry> getAllEntries() throws Exception;

    /*
    Returns a single LibraryEntry with id matching the input parameter or null if no matching Library entry is found.
    */
    LibraryEntry getEntry(long id) throws Exception;



}
