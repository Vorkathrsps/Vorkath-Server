package com.aelous.model.content.areas.wilderness.content.boss_event;

import com.aelous.model.map.position.Tile;
import com.aelous.utility.CustomNpcIdentifiers;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * Boss event data. Contains all the types of boss events that can occur - sequentially - across the server.
 * @author Patrick van Elderen | February, 13, 2021, 09:09
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum BossEvent {

    CORRUPTED_NECH(CustomNpcIdentifiers.CORRUPTED_NECHRYARCH, "Corrupted Nechryarch"),
    BRUTAL_LAVA_DRAGON(CustomNpcIdentifiers.BRUTAL_LAVA_DRAGON_FLYING, "Brutal lava dragon"),
    SKOTIZO(NpcIdentifiers.SKOTIZO, "Skotizo"),
    NOTHING(-1, "Nothing"); // Filler

    public final int npc;
    public final String description;

    BossEvent(int npc, String description) {
        this.npc = npc;
        this.description = description;
    }

    public String spawnLocation(Tile tile) {
        if (tile.equals(new Tile(3166, 4788))) {
            return "east of ::chins";
        } else if (tile.equals(new Tile(3304, 3898))) {
            return "north east of ::gdz";
        } else if (tile.equals(new Tile(3307, 3934))) {
            return "north of ::50s";
        } else if (tile.equals(new Tile(3219,3661))) {
            return "east of ::graves";
        }

        //We shouldn't be getting here
        return "Nothing";
    }
}
