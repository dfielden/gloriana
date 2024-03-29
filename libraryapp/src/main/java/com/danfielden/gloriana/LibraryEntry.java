package com.danfielden.gloriana;

import com.google.gson.JsonObject;

import java.util.Comparator;
import java.util.Objects;

final class LibraryEntry {
    // parameter names must match those from the JSON String produced by ajax call from the addEditForm in index.html for the gson to work
    private long id;
    private String title;
    private String composerLastName;
    private String composerFirstName;
    private String arranger;
    private String voiceParts;
    private String accompanied;
    private String season;
    private String seasonAdditional;
    private String location;
    private String collection;

    LibraryEntry(long id, String title, String composerLastName, String composerFirstName, String arranger,
                 String voiceParts, String accompanied, String season, String seasonAdditional, String location,
                 String collection) {
        this.id = id;
        this.title = title;
        this.composerLastName = composerLastName;
        this.composerFirstName = composerFirstName;
        this.arranger = arranger;
        this.voiceParts = voiceParts;
        this.accompanied = accompanied;
        this.season = season;
        this.seasonAdditional = seasonAdditional;
        this.location = location;
        this.collection = collection;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComposerLastName() {
        return composerLastName;
    }

    public void setComposerLastName(String composerLastName) {
        this.composerLastName = composerLastName;
    }

    public String getComposerFirstName() {
        return composerFirstName;
    }

    public void setComposerFirstName(String composerFirstName) { this.composerFirstName = composerFirstName; }

    public String getArranger() {
        return arranger;
    }

    public void setArranger(String arranger) {
        this.arranger = arranger;
    }

    public String getVoiceParts() {
        return voiceParts;
    }

    public void setVoiceParts(String voiceParts) {
        this.voiceParts = voiceParts;
    }

    public String getAccompanied() {
        return accompanied;
    }

    public void setAccompanied(String accompanied) { this.accompanied = accompanied; }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getSeasonAdditional() {
        return seasonAdditional;
    }

    public void setSeasonAdditional(String seasonAdditional) {
        this.seasonAdditional = seasonAdditional;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryEntry that = (LibraryEntry) o;
        return id == that.id
                && Objects.equals(title, that.title)
                && Objects.equals(composerLastName, that.composerLastName)
                &&  Objects.equals(composerFirstName, that.composerFirstName)
                && Objects.equals(arranger, that.arranger)
                && accompanied.equals(that.accompanied)
                && Objects.equals(voiceParts, that.voiceParts)
                && Objects.equals(season, that.season)
                && Objects.equals(seasonAdditional, that.seasonAdditional)
                && Objects.equals(location, that.location)
                && Objects.equals(collection, that.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, composerLastName, composerFirstName, arranger, voiceParts, accompanied, season,
                seasonAdditional, location, collection);
    }

    @Override
    public String toString() {
        return "LibraryEntry{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", composerLastName='" + composerLastName + '\'' +
                ", composerFirstName='" + composerFirstName + '\'' +
                ", arranger='" + arranger + '\'' +
                ", voiceParts='" + voiceParts + '\'' +
                ", accompanied=" + accompanied +
                ", season='" + season + '\'' +
                ", seasonAdditional='" + seasonAdditional + '\'' +
                ", location='" + location + '\'' +
                ", collection='" + collection + '\'' +
                '}';
    }

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("id", getId());
        o.addProperty("title", getTitle());
        o.addProperty("composerLastName", getComposerLastName());
        o.addProperty("composerFirstName", getComposerFirstName());
        o.addProperty("arranger", getArranger());
        o.addProperty("voiceParts", getVoiceParts());
        o.addProperty("accompanied", getAccompanied());
        o.addProperty("season", getSeason());
        o.addProperty("seasonAdditional", getSeasonAdditional());
        o.addProperty("location", getLocation());
        o.addProperty("collection", getCollection());

        return o;
    }

    public static LibraryEntry fromJSON(JsonObject json) {
        return new LibraryEntry(json.get("id").getAsLong(),
                json.get("title").getAsString(),
                json.get("composerLastName").getAsString(),
                json.get("composerFirstName").getAsString(),
                json.get("arranger").getAsString(),
                json.get("voiceParts").getAsString(),
                json.get("accompanied").getAsString(),
                json.get("season").getAsString(),
                json.get("seasonAdditional").getAsString(),
                json.get("location").getAsString(),
                json.get("collection").getAsString());
    }

}

// Sorts based on last name then first name then title.
class LibraryEntrySort implements Comparator<LibraryEntry> {
    @Override
    public int compare(LibraryEntry le1, LibraryEntry le2) {
        int compareLastName = le1.getComposerLastName().compareTo(le2.getComposerLastName());
        int compareFirstName = le1.getComposerFirstName().compareTo(le2.getComposerFirstName());
        int compareTitle = le1.getTitle().compareTo(le2.getTitle());

        if (compareLastName == 0) {
            return ((compareFirstName == 0) ? compareTitle : compareFirstName);
        } else {
            return compareLastName;
        }
    }
}
