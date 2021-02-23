package com.danfielden.gloriana;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryLibraryMemory implements QueryLibrary {
    private final Map<Long, LibraryEntryNode> map = new HashMap<>();

    QueryLibraryMemory() {
        map.put(0L, new LibraryEntryNode(new LibraryEntry(0, "Good Song", "Daniel", "Fielden",
                "", "SATB", "Unaccompanied", "", "", "basement", "")));
        map.put(1L, new LibraryEntryNode(new LibraryEntry(1, "Bless the Sheep", "Tally", "",
                "", "SA", "Accompanied", "Spring", "", "field", "")));
        map.put(2L, new LibraryEntryNode(new LibraryEntry(2, "Brighter than 1000 happy faces", "Happy", "Face",
                "doggubby", "SATB", "Accompanied", "All year", "", "cupboard",
                "Happy songs vol 3")));
    }

    @Override
    public void addEntry(LibraryEntry entry) {
        if (entry.getId() != -1) {
            throw new IllegalArgumentException("ID must be -1 for new insertion");
        }
        entry.setId(getNextUnusedID());
        map.put(entry.getId(), new LibraryEntryNode(entry));
    }

    @Override
    public void updateEntry(long id, LibraryEntry entry) {
        if (!map.containsKey(id)) {
            throw new IllegalArgumentException("id " + id + ": entry does not exist");
        }
        if (id != entry.getId()) {
            throw new IllegalStateException("id: " + id + ", entry.get(id): " + entry.getId() + ". IDs do not match.");
        }
        map.put(id, new LibraryEntryNode(entry));
    }

    @Override
    public void deleteEntry(long id) {
        if (map.get(id) == null) {
                throw new IllegalArgumentException("No entry with id=" + id + " found");
        } else {
            map.get(id).setDeleted(true);
        }
    }

    @Override
    public Map<Long, LibraryEntry> getAllEntries() {
        Map<Long, LibraryEntry> allCurrentEntries = new HashMap<>();
        for (LibraryEntryNode len : map.values()) {
            if (!len.isDeleted()) {
                allCurrentEntries.put(len.getLe().getId(), len.getLe());
            }
        }
        return allCurrentEntries;
    }

    @Override
    @Nullable
    public LibraryEntry getEntry(long id) {
        LibraryEntryNode node = map.get(id);
        if (node == null || node.deleted) {
            return null;
        }
        return node.getLe();
    }

    @Override
    public Map<Long, LibraryEntry> searchEntries(String searchTerm) throws Exception {
        return null;
    }

    private long getNextUnusedID() {
        Set<Long> keys = map.keySet();
        long checkKey = 0L;
        while(true) {
            if (keys.contains(checkKey)) {
                if (checkKey == Long.MAX_VALUE) {  // have run out of longs.
                    throw new IllegalStateException("Run out of longs");
                }
                checkKey += 1;
            } else {
                return checkKey;  // key is available.
            }
        }
    }

    private static final class LibraryEntryNode {
        private LibraryEntry le;
        private boolean deleted;

        LibraryEntryNode(LibraryEntry le) {
            if (le == null) {
                throw new NullPointerException("LibraryEntry le");
            }
            this.le = le;
            this.deleted = false;
        }

        public LibraryEntry getLe() {
            return le;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            if (this.deleted) {
                throw new IllegalStateException("LibraryEntry " + le.getId() + " already deleted");
            }
            this.deleted = deleted;

        }
    }

}
