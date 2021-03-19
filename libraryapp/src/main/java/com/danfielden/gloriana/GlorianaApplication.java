package com.danfielden.gloriana;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;


@SpringBootApplication
@Controller
public class GlorianaApplication {
    private static final Gson gson = new Gson();
    private final QueryLibraryDB ql;
    private final Map<String, GlorianaSessionState> sessions = new HashMap<>(); // cookieValue, GlorianaSessionState

    public static final String LOGIN_SUCCESS_RESPONSE_VALUE = "LOGIN_SUCCESS";
    public static final String LOGIN_FAIL_RESPONSE_VALUE = "LOGIN_FAIL";


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

    @GetMapping("/login")
    public String login(HttpServletRequest req) throws Exception {
        String loginStatus = getLoginStatus(req);
        System.out.println("loginStatus = " + loginStatus);

        // TODO fix this hack - make the getLoginStatus method return pure String not json
        if (loginStatus.equals("\"" + AuthStatus.ADMIN_AUTH_STATUS + "\"") || loginStatus.equals("\"" + AuthStatus.GUEST_AUTH_STATUS + "\"")) {
            // already logged in so redirect to index
            return "index";
        }
        return "login";
    }

    @ResponseBody // indicates that we should return in response body rather than render a file with the name 'returnString.html'
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

    @ResponseBody // indicates that we should return in response body rather than render a file with the name 'returnString.html'
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

    @ResponseBody
    @PostMapping(value="/edit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String editEntry(@RequestBody LibraryEntry le) throws Exception {
        ql.updateEntry(le);
        return le.toJson().toString();
    }

    @ResponseBody
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


    @PostMapping(value="/loginform",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String returnUser(@RequestBody Login login, HttpServletRequest req) throws Exception {


        try {
            String userName = login.getUsername();
            String enteredPassword = login.getPassword();
            Map userDetails = ql.getuserDetails(userName);
            String salt = (String)userDetails.get("salt");
            String hashedPassword = (String)userDetails.get("hashedPassword");
            String authStatus = (String)userDetails.get("auth");

            if (hashedPassword.equals(GlorianaAuth.hashString(enteredPassword + salt))) {
                // User credentials OK.
                GlorianaSessionState state = getSessionFromReq(req);

                // State does not exist - new user
                if (state == null) {
                    System.out.println("state is null");
                    state = new GlorianaSessionState();
                }

                // Update session that user is now logged in.
                state.userName = userName;
                state.authStatus = authStatus.equals("ADMIN") ? AuthStatus.ADMIN_AUTH_STATUS : AuthStatus.GUEST_AUTH_STATUS;

                // Add session to internal memory
                saveSessionToMemory(req, state);
                return LOGIN_SUCCESS_RESPONSE_VALUE;
            } else {
                // User credentials BAD.
                return "Incorrect password. Please try again.";
            }
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @Nullable
    public GlorianaSessionState getSessionFromReq(HttpServletRequest req) {
        Cookie[] cookie = req.getCookies();
        if (cookie != null) {
            for (Cookie c : cookie) {
                GlorianaSessionState state = sessions.get(c.getValue());
                if (state != null) {
                    return state;
                }
            }
        }
        return null;
    }


    @RequestMapping(value="/loginstatus")
    public @ResponseBody String getLoginStatus(HttpServletRequest req) throws Exception {
        GlorianaSessionState state = getSessionFromReq(req);
        JsonObject result = new JsonObject();
        if (state != null) {
            result.addProperty("authStatus", state.authStatus.toString());
            System.out.println(result);
            return gson.toJson(result);
        }
        result.addProperty("authStatus", AuthStatus.LOGGEDOUT_AUTH_STATUS.toString());
        return gson.toJson(result);
    }

    private void saveSessionToMemory(HttpServletRequest req, GlorianaSessionState state) {
        Cookie[] cookie = req.getCookies();
        if (cookie != null) {
            for (Cookie c : cookie) {
                sessions.put(c.getValue(), state);
            }
        }
    }


    @RequestMapping(value="/logout")
    public @ResponseBody String logout(HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        session.invalidate();
        req.logout();
        return gson.toJson(AuthStatus.LOGGEDOUT_AUTH_STATUS);

        // GlorianaSessionState state = (GlorianaSessionState) session.getAttribute("GLORIANA_SESSION_STATE");
    }



    /** This object represents all the state we store in the HttpSession. */
    private static final class GlorianaSessionState {
        private AuthStatus authStatus = AuthStatus.LOGGEDOUT_AUTH_STATUS;
        private String userName;
    }

    private enum AuthStatus {
        ADMIN_AUTH_STATUS,
        GUEST_AUTH_STATUS,
        LOGGEDOUT_AUTH_STATUS
    }

}