package com.aelous.model.content.areas.fishing_guild;

import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.DOOR_20925;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
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
                    DialogueManager.sendStatement(player,"You do not meet the level 68 Fishing requirement to enter the Guild.");
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
