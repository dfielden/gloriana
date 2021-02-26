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
                    !objectPartMatchesQuery(le.getComposerLastName(), term) &&
                    !objectPartMatchesQuery(le.getComposerFirstName(), term) &&
                    !objectPartMatchesQuery(le.getArranger(), term) &&
                    !objectPartMatchesQuery(le.getVoiceParts(), term) &&
                    !objectPartMatchesQuery(le.getAccompanied(), term) &&
                    !objectPartMatchesQuery(le.getSeason(), term) &&
                    !objectPartMatchesQuery(le.getSeasonAdditional(), term) &&
                    !objectPartMatchesQuery(le.getLocation(), term) &&
                    !objectPartMatchesQuery(le.getCollection(), term)) {
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
        return string.toLowerCase().contains(q.toLowerCase());
    }
}
