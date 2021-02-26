package com.danfielden.gloriana;

import org.junit.jupiter.api.Test;
import java.util.Random;


import static org.junit.jupiter.api.Assertions.*;

class SearchTest {
    private static final Random random = new Random();

    private final LibraryEntry le = new LibraryEntry(-1,
        "Test1",
        "Test2",
        "Test3",
        "Test4",
        "Test5",
        "Test6",
        "Test7",
        "Test8",
        "Test9",
        "Test10");

    @Test
    public void noMatch() {
        assertFalse(Search.checkLeMatchesSearch(new String[]
            {"Test_1",
            "Te st1",
            "Test11",
            " ",
            String.format("%x", random.nextLong())},
            le)
        );
    }

    @Test
    public void match() {
        assertTrue(Search.checkLeMatchesSearch(new String[]{"10"}, le));
        assertTrue(Search.checkLeMatchesSearch(new String[]{"t"}, le));
        assertTrue(Search.checkLeMatchesSearch(new String[]{"Test1"}, le));
        assertTrue(Search.checkLeMatchesSearch(new String[]{"TEST1"}, le));
        assertTrue(Search.checkLeMatchesSearch(new String[]{"test1"}, le));
        assertTrue(Search.checkLeMatchesSearch(new String[]{"st"}, le));
        assertTrue(Search.checkLeMatchesSearch(new String[]{"sT"}, le));
    }
}