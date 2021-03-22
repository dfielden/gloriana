package com.danfielden.gloriana;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@SpringBootApplication
@Controller
public class GlorianaApplication {
    private static final String GLORIANA_COOKIE_NAME = "GLORICOOKIE";
    public static final String LOGIN_SUCCESS_RESPONSE_VALUE = "LOGIN_SUCCESS";
    private static final Gson gson = new Gson();
    private static final Random rand = new Random();

    private final QueryLibraryDB ql;
    private final Map<String, GlorianaSessionState> sessions = new ConcurrentHashMap<>(); // cookieValue, GlorianaSessionState

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


    @GetMapping("/")
    public String home(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                System.out.println(cookie.getName() + " = " + cookie.getValue());
            }
        }

        return loginOrHome(req, resp);
    }


    @GetMapping("/login")
    public String login(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        return loginOrHome(req, resp);
    }


    private String loginOrHome(HttpServletRequest req, HttpServletResponse resp) {
        GlorianaSessionState state = getOrCreateSession(req, resp);

        if (state.isLoggedIn()) {
            // already logged in so go to index
            return "index";
        } else {
            return "login";
        }
    }


    @ResponseBody // indicates that we should return in response body rather than render a file with the name 'returnString.html'
    @GetMapping("/entries")
    public String entries(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        GlorianaSessionState state = getOrCreateSession(req, resp);
        if (!state.isLoggedIn()) {
            // TODO this should be an error, not just some un-parseable JSON.
            return "User not authorised to view content.";
        }

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
    public @ResponseBody LibraryEntry getEntryById(
            @PathVariable(value="id") long id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        GlorianaSessionState state = getOrCreateSession(req, resp);
        if (!state.isLoggedIn()) {
            throw new IllegalStateException("User not authorised to view content. Please login");
        }
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
    public @ResponseBody String returnUser(@RequestBody Login login, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            String userName = login.getUsername();
            String enteredPassword = login.getPassword();
            Map userDetails = ql.getuserDetails(userName);
            String salt = (String)userDetails.get("salt");
            String hashedPassword = (String)userDetails.get("hashedPassword");
            String authStatus = (String)userDetails.get("auth");

            if (hashedPassword.equals(GlorianaAuth.hashString(enteredPassword + salt))) {
                // User credentials OK.
                GlorianaSessionState state = getOrCreateSession(req, resp);

                // Update session that user is now logged in.
                state.userName = userName;
                state.authStatus = authStatus.equals("ADMIN") ? AuthStatus.ADMIN_AUTH_STATUS : AuthStatus.GUEST_AUTH_STATUS;

                return LOGIN_SUCCESS_RESPONSE_VALUE;
            } else {
                // User credentials BAD.
                return "Incorrect password. Please try again.";
            }
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @Nonnull
    private synchronized GlorianaSessionState getOrCreateSession(HttpServletRequest req, HttpServletResponse resp) {
        // First, get the Cookie from the request.
        Cookie cookie = findOrSetOurSessionCookie(req, resp);

        // Use the cookie value as the session ID.
        String sessionId = cookie.getValue();

        // Then, look up the corresponding session for this Cookie ID.
        GlorianaSessionState state = sessions.get(sessionId);

        if (state == null) {
            // Create a new session (findOrSetOurSessionCookie probably just created the Cookie, so there is not yet a
            // corresponding session).
            state = new GlorianaSessionState();
            sessions.put(sessionId, state);
        }

        return state;
    }

    @RequestMapping(value="/loginstatus")
    public @ResponseBody String getLoginStatus(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        GlorianaSessionState state = getOrCreateSession(req, resp);

        JsonObject result = new JsonObject();
        result.addProperty("authStatus", state.authStatus.toString());
        return gson.toJson(result);
    }

    @RequestMapping(value="/logout")
    public @ResponseBody String logout(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Cookie c = findOrSetOurSessionCookie(req, resp);
        c.setMaxAge(0);
        resp.addCookie(c);
        HttpSession session = req.getSession();
        session.invalidate();
        req.logout();
        return gson.toJson(AuthStatus.LOGGEDOUT_AUTH_STATUS);
    }

    @RequestMapping(value="/changepw")
    public @ResponseBody String changePassword(HttpServletRequest req, HttpServletResponse resp, String user, String oldPw, String newPw) throws Exception {
        try {
            String currentHashedPw = ql.getuserDetails(user).get("hashedPassword");
            if (!currentHashedPw.equals(GlorianaAuth.hashString(oldPw))) {
                return "Incorrect current password. Please try again";
            }
            // set new password
            String[] newPassword = GlorianaAuth.createHashedPassword(newPw);
            return "hello";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }


    // Private/utils.

    @Nonnull
    private static Cookie findOrSetOurSessionCookie(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (GLORIANA_COOKIE_NAME.equals(c.getName())) {
                    // Found our cookie.
                    return c;
                }
            }
        }

        // No cookie. Set a new one.
        Cookie cookie = new Cookie(GLORIANA_COOKIE_NAME, String.format("%x%xgub", rand.nextLong(), rand.nextLong()));
        resp.addCookie(cookie);
        return cookie;
    }

    /** This object represents all the state we store in the HttpSession. */
    private static final class GlorianaSessionState {
        @Nonnull
        private AuthStatus authStatus = AuthStatus.LOGGEDOUT_AUTH_STATUS;
        @Nullable  // If logged out, this is null.
        private String userName;

        boolean isLoggedIn() {
            return authStatus == AuthStatus.ADMIN_AUTH_STATUS || authStatus == AuthStatus.GUEST_AUTH_STATUS;
        }
    }

    private enum AuthStatus {
        ADMIN_AUTH_STATUS,
        GUEST_AUTH_STATUS,
        LOGGEDOUT_AUTH_STATUS
    }

}