package com.danfielden.gloriana;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public final class StringHasher {

    public static void main(String[] args) {
        System.out.println(hashString("gloriana"));
        System.out.println(hashString("salt1"));
        System.out.println(hashString("salt2"));

    }
    static String hashString(String s) {
        return Hashing.sha256().hashString(s, StandardCharsets.UTF_8).toString();
    }
}
