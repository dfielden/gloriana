package com.danfielden.gloriana;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public final class StringHasher {


    public static void main(String[] args) {
        System.out.println(hashString("glorianaAdmin" + "salt1"));
        System.out.println(hashString("glorianaGuest" + "salt2"));

    }
    static String hashString(String s) {
        return Hashing.sha256().hashString(s, StandardCharsets.UTF_8).toString();
    }

    static String[] createHashedPassword(String s) {
        final Random rand = new Random();
        String salt = String.format("%x", rand.nextLong());
        return new String[] {hashString(s + salt), salt};
    }
}
