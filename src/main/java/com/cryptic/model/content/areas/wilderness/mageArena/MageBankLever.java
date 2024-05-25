package com.cryptic.model.content.areas.wilderness.mageArena;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;

/**
 * @author Origin | January, 27, 2021, 15:58
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
                var originalId = obj.getId();
                var newId = 5961;
                Chain.bound(null).runFn(1, () -> {
                    player.animate(2140);
                    player.message("You pull the lever...");
                    obj.setId(newId);
                }).then(2, () -> {
                    player.animate(714);
                    player.graphic(111, GraphicHeight.HIGH, 0);
                }).then(4, () -> {
                    var targetTile = new Tile(2539, 4712);
                    obj.setId(originalId);
                    player.teleport(targetTile);
                    player.animate(-1);
                    player.unlock();
                    player.message("...And teleport into the mage's cave.");
                    player.clearAttrib(AttributeKey.LAST_WAS_ATTACKED_TIME);
                    if (player.getCombat().getFightTimer().isRunning()) {
                        player.getCombat().getFightTimer().reset();
                    }
                });
                return true;
            }

            //Inside magebank.. to outside
            if (obj.getId() == 5960) {
                var originalId = obj.getId();
                var newId = 5961;

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
                    obj.setId(newId);
                }).then(2, () -> {
                    player.animate(714);
                    player.graphic(111, GraphicHeight.HIGH, 0);
                }).then(4, () -> {
                    var targetTile = new Tile(3090, 3956);
                    obj.setId(originalId);
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
