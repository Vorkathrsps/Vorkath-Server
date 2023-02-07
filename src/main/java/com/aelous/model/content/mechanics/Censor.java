package com.aelous.model.content.mechanics;

import java.util.List;

/**
 * @author Patrick van Elderen | March, 03, 2021, 12:28
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Censor {

    private static final List<String> baddies = List.of("nigger", "nigga", "jew", "kanker", "cancer", "kys", "k y s", "k ys", "ky s", "k.y.s", "kill urself", "kill ys", "kill yourself");

    public static boolean containsBadWords(String chatMessage) {
        for(String text : baddies) {
            if(chatMessage.toLowerCase().contains(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Replaces bad words with *****
     */
    public static String starred(String text) {
        for (String bad : baddies) {
            bad = bad.toLowerCase();
            if (text.contains(bad) || text.startsWith(bad)) {
                text = text.replace(bad, "*".repeat(bad.length()));
                return text;
            }
        }
        return null;
    }

}
