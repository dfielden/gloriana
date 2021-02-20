package com.danfielden.gloriana;

public final class Search {
    private Search() {}

    public static boolean checkLeMatchesSearch(String[] searchTerm, LibraryEntry le) {
        for (int i = 0; i < searchTerm.length; i++) {
            String term = searchTerm[i];
            if (le.getAccompanied() == null) {
                le.setAccompanied("");
            }
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
