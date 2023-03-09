package com.aelous.model.content.areas.dungeons.godwars;

import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

public class GodwarsAltars extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(obj.getId() == ZAMORAK_ALTAR || obj.getId() == SARADOMIN_ALTAR || obj.getId() == ARMADYL_ALTAR || obj.getId() == BANDOS_ALTAR || obj.getId() == 42965) {
            if (option == 1) {
                if (CombatFactory.inCombat(player)) {
                    if (!player.getTimers().has(TimerKey.GODWARS_ALTAR_LOCK)) {
                        player.message("You pray to the gods...");
                        player.getTimers().addOrSet(TimerKey.GODWARS_ALTAR_LOCK, 1000);
                        player.getSkills().replenishSkill(5, player.getSkills().xpLevel(5));
                        player.animate(645);
                        player.message("...and recharged your prayer.");
                    } else {
                        player.message("You cannot use this altar yet!");
                    }
                } else {
                    player.message("You cannot do this while under attack.");
                }
            } else {
                Tile teleportTile = null;
                if (obj.getId() == 26363)
                    teleportTile = new Tile(2925, 5333, 2);
                if (obj.getId() == 26364)
                    teleportTile = new Tile(2909, 5265, 0);
                if (obj.getId() == 26365)
                    teleportTile = new Tile(2839, 5294, 2);
                if (obj.getId() == 26366)
                    teleportTile = new Tile(2862, 5354, 2);
                if (obj.getId() == 42965)
                    teleportTile = new Tile(2904, 5203, 0);

                player.teleport(teleportTile);
            }
            return true;
        }
        return false;
    }
}
