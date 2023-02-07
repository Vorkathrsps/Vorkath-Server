package com.aelous.model.content.areas.wilderness.content.todays_top_pkers;

/**
 * @author Patrick van Elderen | February, 23, 2021, 16:09
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class KillEntry {

    private final String username;
    private final int kills;

    public KillEntry(String username, int kills) {
        this.username = username;
        this.kills = kills;
    }

    public String getUsername() {
        return username;
    }

    public int getKills() {
        return kills;
    }
}
