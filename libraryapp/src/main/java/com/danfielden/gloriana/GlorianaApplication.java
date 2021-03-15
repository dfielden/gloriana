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
import java.util.*;


@SpringBootApplication
@RestController
public class GlorianaApplication {
    private static final Gson gson = new Gson();
    private final QueryLibraryDB ql;
    private static final int NOT_RECOGNISED = -1;
    private static final int GUEST = 0;
    private static final int ADMIN = 1;

    private final Map<String, Integer> authStatus = new HashMap<>();

    public GlorianaApplication(@Value("${goat}") String database) throws Exception {
        ql = new QueryLibraryDB(new File(database));

        List<String> users = ql.getUserNames();
        for (String user : users) {
            authStatus.put(user, NOT_RECOGNISED);
        }
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
    public String entries() throws Exception {
        JsonArray entries = new JsonArray();
        Collection<LibraryEntry> entriesCollection = ql.getAllEntries().values();
        ArrayList<LibraryEntry> allEntriesList = new ArrayList<>(entriesCollection);
        Collections.sort(allEntriesList, new LibraryEntrySort());

        for (LibraryEntry le : allEntriesList) {
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

    @PostMapping(value="/edit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String editEntry(@RequestBody LibraryEntry le) throws Exception {
        ql.updateEntry(le);
        return le.toJson().toString();
    }

    @PostMapping(value="/searchentries",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchEntries(@RequestBody String s) throws Exception {
        JsonArray entries = new JsonArray();
        Collection<LibraryEntry> searchCollection = ql.searchEntries(s).values();
        ArrayList<LibraryEntry> searchEntriesList = new ArrayList<>(searchCollection);
        Collections.sort(searchEntriesList, new LibraryEntrySort());

        for (LibraryEntry le : searchEntriesList) {
            if (le.getAccompanied() == null) {
                le.setAccompanied("");
            }
            entries.add(le.toJson());
        }
        JsonObject result = new JsonObject();
        result.add("library_entries", entries);
        return gson.toJson(result);
    }



    @PostMapping(value="/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public int returnUser(@RequestBody Login login) throws Exception {
        String user = login.getUsername();
        String enteredPassword = login.getPassword();
        Map userDetails = ql.getuserDetails(user);
        String salt = (String)userDetails.get("salt");
        String hashedPassword = (String)userDetails.get("hashedPassword");
        String userAuth = (String)userDetails.get("auth");


        if (hashedPassword.equals(GlorianaAuth.hashString(enteredPassword + salt))) {
            authStatus.put(user, userAuth.equals("admin") ? ADMIN : GUEST);
        } else {
            authStatus.put(user, NOT_RECOGNISED);
            throw new IllegalArgumentException("Incorrect password. Please try again.");
        }
        return authStatus.get(user);
    }


    @RequestMapping(value="/loginstatus/{user}")
    public @ResponseBody int getLoginStatus(@PathVariable(value="user") String user) throws Exception {
        return authStatus.get(user);
    }

    @RequestMapping(value="/logout/{user}")
    public @ResponseBody int logout(@PathVariable(value="user") String user) throws Exception {
        authStatus.put(user, NOT_RECOGNISED);
        return authStatus.get(user);
    }

}