package com.danfielden.gloriana;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;


@SpringBootApplication
@RestController
public class GlorianaApplication {
    QueryLibraryMemory qlm = new QueryLibraryMemory();
    Gson gson = new Gson();

    public static void main(String[] args) {
        SpringApplication.run(GlorianaApplication.class, args);
    }


    @GetMapping("/entries")
    public String entries() {
        JsonArray entries = new JsonArray();
        for (LibraryEntry le : qlm.getAllEntries().values()) {
            entries.add(le.toJson());
        }
        JsonObject result = new JsonObject();
        result.add("library_entries", entries);
        return gson.toJson(result);
    }

    @PostMapping(value="/newentry", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public String newEntry(@RequestBody LibraryEntry le) {
        qlm.addEntry(le);
        return le.toJson().toString();
    }

    @RequestMapping(value="/delete/{id}")
    public @ResponseBody long getId(@PathVariable(value="id") long id)  {
        qlm.deleteEntry(id);
        return id;
    }

    @RequestMapping(value="/entry/{id}")
    public @ResponseBody LibraryEntry getEntryById(@PathVariable(value="id") long id)  {
        return qlm.getEntry(id);
    }

    @PostMapping(value="/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public String editEntry(@RequestBody LibraryEntry le, @PathVariable(value="id") long id) {
        qlm.updateEntry(id, le);
        return le.toJson().toString();
    }

}





//    @PostMapping(value="/newentry", consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = {MediaType.APPLICATION_JSON_VALUE  + "; charset=utf-8"})
//    public String newEntry(@RequestBody String submission) throws UnsupportedEncodingException {
//        String json = URLDecoder.decode(submission, String.valueOf(StandardCharsets.UTF_8));
//        Gson gson = new Gson();
//        LibraryEntry le = gson.fromJson(json, LibraryEntry.class);
//        qlm.addEntry(le);
//        return le.toJson().toString();
//    }