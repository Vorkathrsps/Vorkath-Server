package com.cryptic.model.content.raids;

import com.cryptic.model.content.raids.party.Party;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

/**
 * An abstract handling all the logic we need for raids.
 * @Author Origin
 * @Since October 28, 2021
 */
public abstract class Raids {

    public abstract void startup(Player player);

    public abstract void exit(Player player);

    public abstract void complete(Party party);

    public abstract void clearParty(Player player);

    public abstract boolean death(Player player);

    public abstract Tile respawnTile(Party party, int level);

    public boolean raiding(Player player) {
        return player.raidsParty != null && player.getRaids() != null;
    }

    public abstract void addPoints(Player player, int points);

    public abstract void addDamagePoints(Player player, NPC target, int points);
}
