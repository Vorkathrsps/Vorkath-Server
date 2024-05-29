package com.cryptic.model.content.areas.fishing_guild;

import com.cryptic.model.cs2.impl.dialogue.DialogueManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.DOOR_20925;

/**
 * @author Origin
 * april 21, 2020
 */
public class FishingGuild extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            //System.out.println("fishing object " + obj.toString());
            if(obj.getId() == DOOR_20925) {
                int change = player.tile().y >= 3394 ? -1 : 1;
                if (change == 1 && player.getSkills().level(Skills.FISHING) < 68) {
                    player.getDialogueManager().sendStatement("You do not meet the level 68 Fishing requirement to enter the Guild.");
                    return false;
                }

                GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), 3);
                GameObject spawned = new GameObject(obj.getId(), obj.tile(), obj.getType(), 4);
                ObjectManager.replace(old, spawned, 1);

                player.getMovementQueue().walkTo(new Tile(player.tile().x, player.tile().y + change));
                player.lockNoDamage();
                Chain.bound(null).runFn(1, () -> {
                    player.unlock();
                    String plural = change == -1 ? "leave" : "enter";
                    player.message("You "+plural+" the Fishing Guild.");
                });
                return true;
            }
        }
        return false;
    }
}
