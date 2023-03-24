package com.aelous.model.content.areas.wilderness.mageArena;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

/**
 * @author Patrick van Elderen | January, 27, 2021, 15:58
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MageBankLever extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            //Outside magebank
            if (obj.getId() == 5959) {
                //Check to see if the player is teleblocked
                if (player.getTimers().has(TimerKey.TELEBLOCK) || player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                    player.teleblockMessage();
                    return true;
                }

                player.lockNoDamage();
                Chain.bound(null).runFn(1, () -> {
                    player.animate(2140);
                    player.message("You pull the lever...");
                    GameObject spawned = new GameObject(5961, obj.tile(), obj.getType(), obj.getRotation());
                    ObjectManager.replace(obj, spawned, 5);
                }).then(2, () -> {
                    player.animate(714);
                    player.graphic(111, GraphicHeight.HIGH, 0);
                }).then(4, () -> {
                    var targetTile = new Tile(2539, 4712);

                    player.teleport(targetTile);
                    player.animate(-1);
                    player.unlock();
                    player.message("...And teleport into the mage's cave.");

                    // So we can tele straight away
                    player.clearAttrib(AttributeKey.LAST_WAS_ATTACKED_TIME);
                    if (player.getCombat().getFightTimer().isRunning()) {
                        player.getCombat().getFightTimer().reset();
                    }
                });
                return true;
            }

            //Inside magebank.. to outside
            if (obj.getId() == 5960) {
                if (!player.getPlayerRights().isAdministrator(player)) {
                    if (player.inventory().count(6685) > 18) {
                        player.message("" + player.inventory().count(6685) + " brews is a little excessive don't you think?");
                        return true;
                    }
                }
                //Check to see if the player is teleblocked
                if (player.getTimers().has(TimerKey.TELEBLOCK) || player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                    player.teleblockMessage();
                    return true;
                }

                Chain.bound(null).runFn(1, () -> {
                    player.lockNoDamage();
                    player.animate(2140);
                    player.message("You pull the lever...");
                    var spawned = new GameObject(5961, obj.tile(), obj.getType(), obj.getRotation());
                    ObjectManager.replace(obj, spawned, 5);
                }).then(2, () -> {
                    player.animate(714);
                    player.graphic(111, GraphicHeight.HIGH, 0);
                }).then(4, () -> {
                    var targetTile = new Tile(3090, 3956);
                    player.teleport(targetTile);
                    player.animate(-1);
                    player.unlock();
                    player.message("...And teleport out of the mage's cave.");
                });
                return true;
            }
        }
        return false;
    }
}
