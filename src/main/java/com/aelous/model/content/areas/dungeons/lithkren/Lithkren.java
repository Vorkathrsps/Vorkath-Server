package com.aelous.model.content.areas.dungeons.lithkren;

import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.NumberUtils;

/**
 * @author Patrick van Elderen | March, 06, 2021, 12:12
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Lithkren extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(obj.getId() == 32080) {
            player.teleport(3549, 10449, 0);
            return true;
        }
        if(obj.getId() == 32081) {
            player.teleport(3556, 4004, 1);
            return true;
        }
        if(obj.getId() == 32082) {
            player.teleport(3561, 4004, 0);
            return true;
        }
        if(obj.getId() == 32084) {
            player.teleport(3555, 4000, 0);
            return true;
        }
        if(obj.getId() == 32144) {
            DialogueManager.sendStatement(player, "You have killed " + NumberUtils.formatNumber(player.<Integer>getAttribOr(AttributeKey.ADAMANT_DRAGONS_KILLED,0)) + " adamant dragons and " + NumberUtils.formatNumber(player.<Integer>getAttribOr(AttributeKey.RUNE_DRAGONS_KILLED,0)) + " rune dragons.");
            return true;
        }
        if(obj.getId() == 32117) {
            player.teleport(1568, 5060, 0);
            return true;
        }
        if(obj.getId() == 32132) {
            player.teleport(3549, 10482, 0);
            return true;
        }
        if(obj.getId() == 32113) {
            if (player.getAbsY() >= 10471)
                player.teleport(player.getAbsX(), 10467, 0);
            else
                player.teleport(player.getAbsX(), 10473, 0);
            return true;
        }
        if(obj.getId() == 32112) {
            player.teleport(3555, 4002, 0);
            return true;
        }
        if(obj.getId() == 32153) {
            player.runFn(1, () -> {
                int x = player.getAbsX();
                int y = player.getAbsY();

                switch (obj.getRotation()) {
                    case 0:
                        if(y > obj.tile().y)
                            y -= 2;
                        else
                            y += 2;
                        break;
                    case 1:
                    case 3:
                        if(x < obj.tile().x)
                            x += 2;
                        else
                            x -= 2;
                    default: break;
                }
                player.lock();
                player.stepAbs(x, y, MovementQueue.StepType.FORCED_WALK);
                final int finalX = x;
                final int finalY = y;
                player.waitForTile(new Tile(finalX, finalY), player::unlock);
            });
            return true;
        }
        return false;
    }
}
