package com.danfielden.gloriana;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryLibraryMemory implements QueryLibrary {
    private final Map<Long, LibraryEntryNode> map = new HashMap<>();

    QueryLibraryMemory() {
        map.put(1L, new LibraryEntryNode(new LibraryEntry(1, "Good Song", "Daniel", "Fielden",
                "", "SATB", "Unaccompanied", "", "", "basement", "")));
        map.put(2L, new LibraryEntryNode(new LibraryEntry(2, "Bless the Sheep", "Tally", "",
                "", "SA", "Accompanied", "Spring", "", "field", "")));
        map.put(3L, new LibraryEntryNode(new LibraryEntry(3, "Brighter than 1000 happy faces", "Happy", "Face",
                "doggubby", "SATB", "Accompanied", "All year", "", "cupboard",
                "Happy songs vol 3")));
    }

    @Override
    public void addEntry(LibraryEntry entry) {
        if (entry.getId() == -1L) {
            entry.setId(getNextUnusedID());
        }
        map.put(entry.getId(), new LibraryEntryNode(entry));
    }

    @Override
    public void updateEntry(long id, LibraryEntry entry) {
        if (map.get(id) == null) {
            throw new IllegalArgumentException("id " + id + ": entry does not exist");
        }
        if (id != entry.getId()) {
            throw new IllegalStateException("id: " + id + ", entry.get(id): " + entry.getId() + ". IDs do not match.");
        }
        map.put(id, new LibraryEntryNode(entry));
    }

    @Override
    public void deleteEntry(long id) {
        map.get(id).setDeleted(true);
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
    public LibraryEntry getEntry(long id) {
        return map.get(id).getLe();
    }

    private long getNextUnusedID() {
        Set<Long> keys = map.keySet();
        long checkKey = 0L;
        while(true) {
            if (keys.contains(checkKey)) {
                if (checkKey == Long.MAX_VALUE) {
                    return -1;
                }
                checkKey += 1;
            } else {
                return checkKey;
            }
        }
    }

}
