package com.danfielden.gloriana;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class GlorianaAuth {

    static String hashString(String s) {
        return Hashing.sha256().hashString(s, StandardCharsets.UTF_8).toString();
    }

    static String[] createHashedPassword(String s) {
        final Random rand = new Random();
        String salt = String.format("%x", rand.nextLong());
        return new String[] {hashString(s + salt), salt};
    }
}
