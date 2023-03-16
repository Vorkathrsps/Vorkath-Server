package com.aelous.model.content.areas.dungeons.godwars;

import com.aelous.model.World;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.NexCombat.NEX_AREA;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

public class BossRoomDoors extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            // Zamorak
            if (obj.getId() == BIG_DOOR_26505) {
                if (player.tile().y > 5332) {
                    player.teleport(2925, 5331, 2);
                } else if (player.tile().y == 5331) {
                    player.teleport(2925, 5333, 2);
                }
                return true;
            }

            // Bandos
            if (obj.getId() == BIG_DOOR_26503) {
                if (player.tile().x < 2863) {
                    player.teleport(2864, 5354, 2);
                } else if (player.tile().x == 2864) {
                    player.teleport(2862, 5354, 2);
                }
                return true;
            }

            // Saradomin
            if (obj.getId() == BIG_DOOR_26504) {
                if (player.tile().x >= 2909) {
                    player.teleport(2907, 5265, 0);
                } else if (player.tile().x == 2907) {
                    player.teleport(2909, 5265, 0);
                }
                return true;
            }

            // Armadyl
            if (obj.getId() == BIG_DOOR_26502) {
                if (player.tile().y <= 5294) {
                    player.teleport(2839, 5296, 2);
                } else if (player.tile().y == 5296) {
                    player.teleport(2839, 5294, 2);
                }
                return true;
            }

            //Nex
            if(obj.getId() == 42967) {
                if(player.getX() <= 2908) {
                    player.teleport(new Tile(2910, 5203, 0));
                    ZarosGodwars.startEvent();
                }
                return true;
            }
        }

        if(option == 3) {
            if(obj.getId() == 42967) {

                int count = 0;
                for (Player p : World.getWorld().getPlayers()) {
                    if (p != null && p.tile().inArea(NEX_AREA))
                        count++;
                }

                if (count == 0) {
                    DialogueManager.sendStatement(player, "You peek inside the barrier and see no adventurers inside.");
                } else {
                    DialogueManager.sendStatement(player, "You peek inside the barrier and see " + count + " adventurers inside.");
                }
                return true;
            }
        }
        return false;
    }
}
