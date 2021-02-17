package com.danfielden.gloriana;

public class Search {
    static boolean checkLeMatchesSearch(String[] searchTerm, LibraryEntry le) {
        for (int i = 0; i < searchTerm.length; i++) {
            System.out.println(le.toString());
            String term = searchTerm[i];
            if (!objectPartMatchesQuery(le.getTitle(), term) &&
                    !le.getComposerFirstName().toLowerCase().contains(term) &&
                    !le.getComposerLastName().toLowerCase().contains(term) &&
                    !le.getArranger().toLowerCase().contains(term) &&
                    !le.getVoiceParts().toLowerCase().contains(term) &&
                    !le.getAccompanied().toLowerCase().contains(term) &&
                    !le.getSeason().toLowerCase().contains(term) &&
                    !le.getSeasonAdditional().toLowerCase().contains(term) &&
                    !le.getLocation().toLowerCase().contains(term) &&
                    !le.getCollection().toLowerCase().contains(term)) {
                return false;
            }
        }
        return true;
    }

    private static boolean objectPartMatchesQuery(Object part, String q) {
        if (part == null) {
            return false;
        }
        String string = part.toString();
        return string.toLowerCase().contains(q);
    }
}
