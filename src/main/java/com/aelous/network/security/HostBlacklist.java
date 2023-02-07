package com.aelous.network.security;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick van Elderen | May, 07, 2021, 13:34
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class HostBlacklist {

    /**
     * TODO
     */
    private static final String BLACKLIST_DIR = "./data/blacklist.txt";

    private static final List<String> blockedHostnames = new ArrayList<>();

    public static List<String> getBlockedHostnames() {
        return blockedHostnames;
    }

    public static boolean isBlocked(String host) {
        return blockedHostnames.contains(host.toLowerCase());
    }

    public static void loadBlacklist() {
        String word;
        try {
            BufferedReader in = new BufferedReader(new FileReader(BLACKLIST_DIR));
            while ((word = in.readLine()) != null)
                blockedHostnames.add(word.toLowerCase());
            in.close();
        } catch (final Exception e) {
            System.out.println("Could not load blacklisted hosts.");
        }
    }
}
