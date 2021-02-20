package com.danfielden.gloriana;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;


@SpringBootApplication
@RestController
public class GlorianaApplication {
    private static final Gson gson = new Gson();
    private final QueryLibrary ql;
    //private final QueryLibrary ql = new QueryLibraryMemory();

    public GlorianaApplication(@Value("${goat}") String database) throws Exception {
        ql = new QueryLibraryDB(new File(database));
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected: <port> <database path>");
        }
        int port = Integer.parseInt(args[0]);
        String dbPath = args[1];

        SpringApplication app = new SpringApplication(GlorianaApplication.class);

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("server.port", port);
        properties.put("goat", dbPath);
        app.setDefaultProperties(properties);

        app.run(args);
    }

    @GetMapping("/entries")
    public String entries() throws Exception{
        JsonArray entries = new JsonArray();
        for (LibraryEntry le : ql.getAllEntries().values()) {
            if (le.getAccompanied() == null) {
                le.setAccompanied("");
            }
            entries.add(le.toJson());
        }
        JsonObject result = new JsonObject();
        result.add("library_entries", entries);
        return gson.toJson(result);
    }

    @PostMapping(value="/newentry",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String newEntry(@RequestBody LibraryEntry le) throws Exception {
        if (le.getAccompanied() == null) {
            le.setAccompanied("");
        }
        ql.addEntry(le);
        return le.toJson().toString();
    }

    @RequestMapping(value="/delete/{id}")
    public @ResponseBody long getId(@PathVariable(value="id") long id) throws Exception {
        ql.deleteEntry(id);
        return id;
    }

    @RequestMapping(value="/entry/{id}")
    public @ResponseBody LibraryEntry getEntryById(@PathVariable(value="id") long id) throws Exception {
        return ql.getEntry(id);
    }

    @PostMapping(value="/edit/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String editEntry(@RequestBody LibraryEntry le, @PathVariable(value="id") long id) throws Exception {
        ql.updateEntry(id, le);
        return le.toJson().toString();
    }

    @PostMapping(value="/searchentries",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchEntries(@RequestBody String s) throws Exception {
        JsonArray entries = new JsonArray();
        for (LibraryEntry le : ql.searchEntries(s).values()) {
            if (le.getAccompanied() == null) {
                le.setAccompanied("");
            }
            entries.add(le.toJson());
        }
        JsonObject result = new JsonObject();
        result.add("library_entries", entries);
        return gson.toJson(result);
    }

}

